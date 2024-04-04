package tornaco.apps.shortx.services.wm;

import android.accessibilityservice.AccessibilityGestureEvent;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.IAccessibilityServiceConnection;
import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.graphics.Bitmap;
import android.graphics.Region;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.test.uiautomator.imp.adapter.IUiAutomation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import tornaco.apps.shortx.core.util.Logger;
import tornaco.apps.shortx.core.util.OsUtils;
import util.Reflections;

public class UiAutomationManager extends IAccessibilityServiceClient.Stub implements IUiAutomation {
    public static final int FLAG_PREFETCH_DESCENDANTS_HYBRID = 0x00000004;

    private static final Logger LOGGER = new Logger("UiAutomationManager");

    private final AtomicInteger connectionId = new AtomicInteger(AccessibilityInteractionClient.NO_ID);
    private UiAutomationConnection connection;

    private final Object syncLock = new Object();

    private final ArrayList<AccessibilityEvent> mEventQueue = new ArrayList<>();
    private boolean mWaitingForEventDelivery;
    private long mLastEventTimeMillis;

    private UiAutomation.OnAccessibilityEventListener mOnAccessibilityEventListener;
    private final Handler mLocalCallbackHandler;

    public UiAutomationManager(Handler localCallbackHandler) {
        this.mLocalCallbackHandler = localCallbackHandler;
    }

    boolean isConnected() {
        return connectionId.get() != AccessibilityInteractionClient.NO_ID;
    }

    void connect() {
        synchronized (syncLock) {
            LOGGER.w("UiAutomationManager connect");
            if (connectionId.get() != AccessibilityInteractionClient.NO_ID) {
                // Already has a connection.
                LOGGER.w("connect Already has a connection.");
                return;
            }
            connection = new UiAutomationConnection();
            connection.connect(this, UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES);
        }
    }

    void disconnect() {
        synchronized (syncLock) {
            LOGGER.w("UiAutomationManager disconnect");
            if (connectionId.get() == AccessibilityInteractionClient.NO_ID) {
                // no connection.
                LOGGER.w("disconnect no connection");
                return;
            }
            if (connection == null) {
                LOGGER.w("disconnect connection == null.");
                connectionId.set(AccessibilityInteractionClient.NO_ID);
                connection = null;
                return;
            }
            if (!connection.isBinderAlive()) {
                LOGGER.w("disconnect isBinderAlive == false");
                connectionId.set(AccessibilityInteractionClient.NO_ID);
                connection = null;
                return;
            }

            LOGGER.w("disconnect now.");
            try {
                connection.disconnect();
                connectionId.set(AccessibilityInteractionClient.NO_ID);
                connection = null;
            } catch (Throwable e) {
                LOGGER.e(e, "disconnect got error");
            }
        }
    }

    @Override
    public List<AccessibilityWindowInfo> getWindows() {
        return null;
    }

    public AccessibilityNodeInfo getRootInActiveWindow() {
        synchronized (syncLock) {
            if (connectionId.get() == AccessibilityInteractionClient.NO_ID) {
                LOGGER.e("getRootInActiveWindow, not connected...");
                return null;
            }
            if (OsUtils.isTOrAbove()) {
                return (AccessibilityNodeInfo) Reflections.callMethod(AccessibilityInteractionClient.getInstance(), "getRootInActiveWindow",
                        connectionId.get(), FLAG_PREFETCH_DESCENDANTS_HYBRID);
            } else {
                return AccessibilityInteractionClient.getInstance().getRootInActiveWindow(connectionId.get());
            }
        }
    }

    public void performAccessibilityAction(long sourceNodeId, int windowId, int action) {
        AccessibilityInteractionClient.getInstance().performAccessibilityAction(connectionId.get(), windowId, sourceNodeId, action, null);
    }

    @Override
    public ParcelFileDescriptor executeShellCommand(String cmd) {
        return null;
    }

    @Override
    public Bitmap takeScreenshot() {
        return null;
    }

    @Override
    public AccessibilityEvent executeAndWaitForEvent(Runnable command,
                                                     UiAutomation.AccessibilityEventFilter filter,
                                                     long timeoutMillis) throws TimeoutException {
        // Acquire the lock and prepare for receiving events.
        synchronized (syncLock) {
            if (connectionId.get() == AccessibilityInteractionClient.NO_ID) {
                LOGGER.e("executeAndWaitForEvent, not connected...");
                return null;
            }
            mEventQueue.clear();
            // Prepare to wait for an event.
            mWaitingForEventDelivery = true;
        }

        // Note: We have to release the lock since calling out with this lock held
        // can bite. We will correctly filter out events from other interactions,
        // so starting to collect events before running the action is just fine.

        // We will ignore events from previous interactions.
        final long executionStartTimeMillis = SystemClock.uptimeMillis();
        // Execute the command *without* the lock being held.
        command.run();

        List<AccessibilityEvent> receivedEvents = new ArrayList<>();

        // Acquire the lock and wait for the event.
        try {
            // Wait for the event.
            final long startTimeMillis = SystemClock.uptimeMillis();
            while (true) {
                List<AccessibilityEvent> localEvents;
                synchronized (syncLock) {
                    localEvents = new ArrayList<>(mEventQueue);
                    mEventQueue.clear();
                }
                // Drain the event queue
                while (!localEvents.isEmpty()) {
                    AccessibilityEvent event = localEvents.remove(0);
                    // Ignore events from previous interactions.
                    if (event.getEventTime() < executionStartTimeMillis) {
                        continue;
                    }
                    if (filter.accept(event)) {
                        return event;
                    }
                    receivedEvents.add(event);
                }
                // Check if timed out and if not wait.
                final long elapsedTimeMillis = SystemClock.uptimeMillis() - startTimeMillis;
                final long remainingTimeMillis = timeoutMillis - elapsedTimeMillis;
                if (remainingTimeMillis <= 0) {
                    throw new TimeoutException("Expected event not received within: "
                            + timeoutMillis + " ms among: " + receivedEvents);
                }
                synchronized (syncLock) {
                    if (mEventQueue.isEmpty()) {
                        try {
                            syncLock.wait(remainingTimeMillis);
                        } catch (InterruptedException ie) {
                            /* ignore */
                        }
                    }
                }
            }
        } finally {
            int size = receivedEvents.size();
            for (int i = 0; i < size; i++) {
                receivedEvents.get(i).recycle();
            }

            synchronized (syncLock) {
                mWaitingForEventDelivery = false;
                mEventQueue.clear();
                syncLock.notifyAll();
            }
        }
    }

    @Override
    public AccessibilityServiceInfo getServiceInfo() {
        if (connectionId.get() == AccessibilityInteractionClient.NO_ID) {
            LOGGER.e("getServiceInfo, not connected...");
            return null;
        }
        final IAccessibilityServiceConnection connection;
        synchronized (syncLock) {
            connection = AccessibilityInteractionClient.getConnection(connectionId.get());
        }
        // Calling out without a lock held.
        if (connection != null) {
            try {
                return connection.getServiceInfo();
            } catch (RemoteException re) {
                LOGGER.e(re, "Error while getting AccessibilityServiceInfo");
            }
        }
        return null;
    }

    @Override
    public void setServiceInfo(AccessibilityServiceInfo info) {

    }

    public boolean injectInputEvent(InputEvent event, boolean sync) {
        synchronized (syncLock) {
            if (connectionId.get() == AccessibilityInteractionClient.NO_ID) {
                LOGGER.e("injectInputEvent, not connected...");
                return false;
            }
            try {
                return UiAutomationConnectionCompat.injectInputEvent(connection, event, sync);
            } catch (Throwable throwable) {
                LOGGER.e(throwable, "Error while injecting input event!");
                return false;
            }
        }
    }

    @Override
    public boolean performGlobalAction(int action) {
        return false;
    }

    @Override
    public void setRotation(int rotation) {

    }

    @Override
    public void setOnAccessibilityEventListener(UiAutomation.OnAccessibilityEventListener listener) {
        this.mOnAccessibilityEventListener = listener;
    }

    @Override
    public void waitForIdle(long idleTimeoutMillis, long globalTimeoutMillis) throws TimeoutException {
        synchronized (syncLock) {
            if (connectionId.get() == AccessibilityInteractionClient.NO_ID) {
                LOGGER.e("onAccessibilityEvent, not connected...");
                return;
            }

            final long startTimeMillis = SystemClock.uptimeMillis();
            if (mLastEventTimeMillis <= 0) {
                mLastEventTimeMillis = startTimeMillis;
            }

            while (true) {
                final long currentTimeMillis = SystemClock.uptimeMillis();
                // Did we get idle state within the global timeout?
                final long elapsedGlobalTimeMillis = currentTimeMillis - startTimeMillis;
                final long remainingGlobalTimeMillis =
                        globalTimeoutMillis - elapsedGlobalTimeMillis;
                if (remainingGlobalTimeMillis <= 0) {
                    throw new TimeoutException("No idle state with idle timeout: "
                            + idleTimeoutMillis + " within global timeout: "
                            + globalTimeoutMillis);
                }
                // Did we get an idle state within the idle timeout?
                final long elapsedIdleTimeMillis = currentTimeMillis - mLastEventTimeMillis;
                final long remainingIdleTimeMillis = idleTimeoutMillis - elapsedIdleTimeMillis;
                if (remainingIdleTimeMillis <= 0) {
                    return;
                }
                try {
                    syncLock.wait(remainingIdleTimeMillis);
                } catch (InterruptedException ie) {
                    /* ignore */
                }
            }
        }
    }

    @Override
    public void init(IAccessibilityServiceConnection connection, int id, IBinder iBinder) {
        synchronized (syncLock) {
            LOGGER.w("IAccessibilityServiceConnection init " + connection + id + iBinder);
            if (connection != null) {
                try {
                    connectionId.set(id);
                    if (OsUtils.isTOrAbove()) {
                        Reflections.callStaticMethod(AccessibilityInteractionClient.class,
                                "addConnection",
                                id, connection, false);
                    } else {
                        Reflections.callStaticMethod(AccessibilityInteractionClient.class,
                                "addConnection",
                                id, connection);
                    }

                } catch (Throwable e) {
                    LOGGER.e(e, "UiAutomationManager#init addConnection");
                }
            }
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event, boolean b) {
        if (connectionId.get() == AccessibilityInteractionClient.NO_ID) {
            LOGGER.e("onAccessibilityEvent, not connected...");
            return;
        }

        // TODO Refactor UI auto
        // See anr_wms.log
        if (event.getPackageName() == "android") return;

        synchronized (syncLock) {
            mLastEventTimeMillis = event.getEventTime();
            if (mWaitingForEventDelivery) {
                mEventQueue.add(AccessibilityEvent.obtain(event));
            }
            syncLock.notifyAll();
        }

        if (mOnAccessibilityEventListener != null) {
            AccessibilityEvent cloneEvent = AccessibilityEvent.obtain(event);
            mLocalCallbackHandler.post(() -> {
                mOnAccessibilityEventListener.onAccessibilityEvent(cloneEvent);
                cloneEvent.recycle();
            });
        }
    }

    @Override
    public void onInterrupt() throws RemoteException {

    }

    @Override
    public void onGesture(AccessibilityGestureEvent gestureEvent) throws RemoteException {

    }

    @Override
    public void clearAccessibilityCache() throws RemoteException {

    }

    @Override
    public void onKeyEvent(KeyEvent event, int sequence) throws RemoteException {

    }

    @Override
    public void onMagnificationChanged(int displayId, Region region, float scale, float centerX, float centerY) throws RemoteException {

    }

    @Override
    public void onSoftKeyboardShowModeChanged(int showMode) throws RemoteException {

    }

    @Override
    public void onPerformGestureResult(int sequence, boolean completedSuccessfully) throws RemoteException {

    }

    @Override
    public void onFingerprintCapturingGesturesChanged(boolean capturing) throws RemoteException {

    }

    @Override
    public void onFingerprintGesture(int gesture) throws RemoteException {

    }

    @Override
    public void onAccessibilityButtonClicked(int displayId) throws RemoteException {

    }

    @Override
    public void onAccessibilityButtonAvailabilityChanged(boolean available) throws RemoteException {

    }

    @Override
    public void onSystemActionsChanged() throws RemoteException {

    }
}
