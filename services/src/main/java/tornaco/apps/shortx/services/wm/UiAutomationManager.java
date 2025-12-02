package tornaco.apps.shortx.services.wm;

import android.accessibilityservice.AccessibilityGestureEvent;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.IAccessibilityServiceConnection;
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.app.UiAutomation;
import android.app.UiAutomationConnection;
import android.content.pm.ParceledListSlice;
import android.graphics.Bitmap;
import android.graphics.Region;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.SparseArray;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.test.uiautomator.imp.adapter.IUiAutomation;

import java.util.List;
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

    private UiAutomation.OnAccessibilityEventListener mOnAccessibilityEventListener;
    private final Handler mLocalCallbackHandler;


    // Copy from Android SDK AccessibilityService
    private int mGestureStatusCallbackSequence;
    private SparseArray<GestureResultCallbackInfo> mGestureStatusCallbackInfos;
    private final Object mLock = new Object();

    public UiAutomationManager(Handler localCallbackHandler) {
        this.mLocalCallbackHandler = localCallbackHandler;
    }

    public boolean isConnected() {
        return connectionId.get() != AccessibilityInteractionClient.NO_ID;
    }

    public void connect() {
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

    public void disconnect() {
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
        if (connectionId.get() == AccessibilityInteractionClient.NO_ID) {
            LOGGER.e("performGlobalAction, not connected...");
            return false;
        }
        final IAccessibilityServiceConnection connection;
        synchronized (syncLock) {
            connection = AccessibilityInteractionClient.getConnection(connectionId.get());
        }

        if (connection != null) {
            try {
                return connection.performGlobalAction(action);
            } catch (RemoteException re) {
                LOGGER.e(re, "Error while calling performGlobalAction");
            }
        }
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

        AccessibilityEvent cloneEvent = AccessibilityEvent.obtain(event);
        mLocalCallbackHandler.post(() -> {
            if (mOnAccessibilityEventListener != null) {
                mOnAccessibilityEventListener.onAccessibilityEvent(cloneEvent);
                cloneEvent.recycle();
            }
        });
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onGesture(AccessibilityGestureEvent gestureEvent) {

    }

    @Override
    public void clearAccessibilityCache() {

    }

    @Override
    public void onKeyEvent(KeyEvent event, int sequence) {

    }

    @Override
    public void onMagnificationChanged(int displayId, Region region, float scale, float centerX, float centerY) {

    }

    @Override
    public void onSoftKeyboardShowModeChanged(int showMode) {

    }

    @Override
    public void onPerformGestureResult(int sequence, boolean completedSuccessfully) {
        if (mGestureStatusCallbackInfos == null) {
            return;
        }
        GestureResultCallbackInfo callbackInfo;
        synchronized (mLock) {
            callbackInfo = mGestureStatusCallbackInfos.get(sequence);
            mGestureStatusCallbackInfos.remove(sequence);
        }
        final GestureResultCallbackInfo finalCallbackInfo = callbackInfo;
        if ((callbackInfo != null) && (callbackInfo.gestureDescription != null)
                && (callbackInfo.callback != null)) {
            if (callbackInfo.handler != null) {
                callbackInfo.handler.post(() -> {
                    if (completedSuccessfully) {
                        finalCallbackInfo.callback
                                .onCompleted(finalCallbackInfo.gestureDescription);
                    } else {
                        finalCallbackInfo.callback
                                .onCancelled(finalCallbackInfo.gestureDescription);
                    }
                });
                return;
            }
            if (completedSuccessfully) {
                callbackInfo.callback.onCompleted(callbackInfo.gestureDescription);
            } else {
                callbackInfo.callback.onCancelled(callbackInfo.gestureDescription);
            }
        }
    }

    @Override
    public void onFingerprintCapturingGesturesChanged(boolean capturing) {

    }

    @Override
    public void onFingerprintGesture(int gesture) {

    }

    @Override
    public void onAccessibilityButtonClicked(int displayId) {

    }

    @Override
    public void onAccessibilityButtonAvailabilityChanged(boolean available) {

    }

    @Override
    public void onSystemActionsChanged() {

    }

    public boolean dispatchGesture(@NonNull GestureDescription gesture,
                                   @Nullable AccessibilityService.GestureResultCallback callback,
                                   @Nullable Handler handler) {
        if (connectionId.get() == AccessibilityInteractionClient.NO_ID) {
            LOGGER.e("getServiceInfo, not connected...");
            return false;
        }
        final IAccessibilityServiceConnection connection;
        synchronized (syncLock) {
            connection = AccessibilityInteractionClient.getConnection(connectionId.get());
        }
        // Calling out without a lock held.
        if (connection != null) {
            int sampleTimeMs = calculateGestureSampleTimeMs(0);
            List<GestureDescription.GestureStep> steps =
                    GestureDescription.MotionEventGenerator.getGestureStepsFromGestureDescription(gesture, sampleTimeMs);
            try {
                synchronized (mLock) {
                    mGestureStatusCallbackSequence++;
                    if (callback != null) {
                        if (mGestureStatusCallbackInfos == null) {
                            mGestureStatusCallbackInfos = new SparseArray<>();
                        }
                        GestureResultCallbackInfo callbackInfo = new GestureResultCallbackInfo(gesture,
                                callback, handler);
                        mGestureStatusCallbackInfos.put(mGestureStatusCallbackSequence, callbackInfo);
                    }
                    connection.dispatchGesture(mGestureStatusCallbackSequence,
                            new ParceledListSlice<>(steps), 0); // gesture.getDisplayId()
                }
                return true;
            } catch (Throwable re) {
                LOGGER.e(re, "dispatchGesture error");
            }
        }
        return false;
    }

    private int calculateGestureSampleTimeMs(int displayId) {
//        if (getApplicationInfo().targetSdkVersion <= Build.VERSION_CODES.Q) {
//            return 100;
//        }
//        Display display = getSystemService(DisplayManager.class).getDisplay(
//                displayId);
//        if (display == null) {
//            return 100;
//        }
//        int msPerSecond = 1000;
//        int sampleTimeMs = (int) (msPerSecond / display.getRefreshRate());
//        if (sampleTimeMs < 1) {
//            // Should be impossible, but do not return 0.
//            return 100;
//        }
//        return sampleTimeMs;
        return 100;
    }

}
