package tornaco.apps.shortx.services.rule.actor.mvel.handle.impl;

import static android.net.TetheringManager.TETHERING_WIFI;

import android.annotation.NonNull;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.TetheringManager;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.elvishew.xlog.XLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import tornaco.apps.shortx.core.util.Logger;
import tornaco.apps.shortx.services.ShortXContext;
import tornaco.apps.shortx.services.rule.actor.mvel.handle.IHW;

@SuppressLint("MissingPermission")
public
class HwImpl implements IHW {
    private final Context context;
    private final ShortXContext s;
    private FlashlightController flashlightController;

    private final Logger logger = new Logger("HwImpl");

    private final FlashlightController.DirectExecutor executor = new FlashlightController.DirectExecutor();

    public HwImpl(Context context, ShortXContext s) {
        this.context = context;
        this.s = s;
    }

    @Override
    public boolean enableWifi() {
        @SuppressLint("WifiManagerPotentialLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.setWifiEnabled(true);
    }

    @Override
    public boolean disableWifi() {
        @SuppressLint("WifiManagerPotentialLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.setWifiEnabled(false);
    }

    @Override
    public boolean isWifiEnabled() {
        @SuppressLint("WifiManagerPotentialLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    @Override
    public boolean enableLocation() {
        return setLocationEnabled(true);
    }

    @Override
    public boolean disableLocation() {
        return setLocationEnabled(false);
    }

    /**
     * Enable or disable location in settings.
     *
     * <p>This will attempt to enable/disable every type of location setting
     * (e.g. high and balanced power).
     *
     * <p>If enabling, a user consent dialog will pop up prompting the user to accept.
     * If the user doesn't accept, network location won't be enabled.
     *
     * @return true if attempt to change setting was successful.
     */
    private boolean setLocationEnabled(boolean enabled) {
        int currentUserId = UserHandle.getCallingUserId();
        if (isUserLocationRestricted(currentUserId)) {
            return false;
        }
        final ContentResolver cr = context.getContentResolver();
        // When enabling location, a user consent dialog will pop up, and the
        // setting won't be fully enabled until the user accepts the agreement.
        int mode = enabled
                ? Settings.Secure.LOCATION_MODE_HIGH_ACCURACY : Settings.Secure.LOCATION_MODE_OFF;
        // QuickSettings always runs as the owner, so specifically set the settings
        // for the current foreground user.
        return Settings.Secure
                .putIntForUser(cr, Settings.Secure.LOCATION_MODE, mode, currentUserId);
    }


    /**
     * Returns true if the current user is restricted from using location.
     */
    private boolean isUserLocationRestricted(int userId) {
        final UserManager um = (UserManager) context.getSystemService(Context.USER_SERVICE);
        return um.hasUserRestriction(
                UserManager.DISALLOW_SHARE_LOCATION,
                new UserHandle(userId));
    }

    /**
     * Returns true if location isn't disabled in settings.
     */
    @Override
    public boolean isLocationEnabled() {
        ContentResolver resolver = context.getContentResolver();
        // QuickSettings always runs as the owner, so specifically retrieve the settings
        // for the current foreground user.
        int mode = Settings.Secure.getIntForUser(resolver, Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF, UserHandle.getCallingUserId());
        return mode != Settings.Secure.LOCATION_MODE_OFF;
    }


    @Override
    public boolean enableBT() {
        BluetoothAdapter bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothadapter.enable();
    }

    @Override
    public boolean disableBT() {
        BluetoothAdapter bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothadapter.disable();
    }

    @Override
    public boolean isBTEnabled() {
        BluetoothAdapter bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothadapter.isEnabled();
    }

    @Override
    public boolean enableNfc() {
        AtomicBoolean res = new AtomicBoolean(false);
        getNfcAdapter().ifPresent(nfcAdapter -> res.set(nfcAdapter.enable()));
        return res.get();
    }

    @Override
    public boolean disableNfc() {
        AtomicBoolean res = new AtomicBoolean(false);
        getNfcAdapter().ifPresent(nfcAdapter -> res.set(nfcAdapter.disable()));
        return res.get();
    }

    @Override
    public boolean isNfcEnabled() {
        return getNfcAdapter().isPresent() && getNfcAdapter().get().isEnabled();
    }

    @Override
    public boolean enabledFlashlight() {
        ensureFlashlightController();
        try {
            flashlightController.setFlashlight(true);
            return true;
        } catch (Throwable e) {
            logger.e(e, "enabledFlashlight fail");
            return false;
        }
    }

    @Override
    public boolean disableFlashlight() {
        ensureFlashlightController();
        try {
            flashlightController.setFlashlight(false);
            return true;
        } catch (Throwable e) {
            logger.e(e, "disableFlashlight fail");
            return false;
        }
    }

    @Override
    public boolean isFlashlightEnabled() {
        ensureFlashlightController();
        try {
            return flashlightController.isEnabled();
        } catch (Throwable e) {
            logger.e(e, "isFlashlightEnabled fail");
            return false;
        }
    }

    @Override
    public boolean isFlashlightAvailable() {
        ensureFlashlightController();
        try {
            return flashlightController.isAvailable();
        } catch (Throwable e) {
            return false;
        }
    }

    @Override
    public boolean isTetheringEnabled() {
        return false;
    }

    @Override
    public boolean enableTethering() {
        TetheringManager tm = (TetheringManager) context.getSystemService(Context.TETHERING_SERVICE);
        tm.startTethering(new TetheringManager.TetheringRequest.Builder(
                        TETHERING_WIFI).setShouldShowEntitlementUi(false).build(),
                executor,
                new TetheringManager.StartTetheringCallback() {
                    @Override
                    public void onTetheringFailed(final int result) {
                        XLog.e("onTetheringFailed: " + result);
                    }
                });
        return true;
    }

    @Override
    public boolean disableTethering() {
        TetheringManager tm = (TetheringManager) context.getSystemService(Context.TETHERING_SERVICE);
        tm.stopTethering(TETHERING_WIFI);
        return true;
    }

    private void ensureFlashlightController() {
        if (flashlightController == null) {
            flashlightController = new FlashlightController(context);
        }
    }

    @NonNull
    private Optional<NfcAdapter> getNfcAdapter() {
        try {
            return Optional.of(NfcAdapter.getNfcAdapter(context));
        } catch (UnsupportedOperationException e) {
            return Optional.empty();
        }
    }
}

// Ref: http://aospxref.com/android-6.0.1_r81/xref/frameworks/base/packages/SystemUI/src/com/android/systemui/statusbar/policy/FlashlightController.java
class FlashlightController {

    private static final int DISPATCH_ERROR = 0;
    private static final int DISPATCH_CHANGED = 1;
    private static final int DISPATCH_AVAILABILITY_CHANGED = 2;

    private final CameraManager mCameraManager;
    /**
     * Call {@link #ensureHandler()} before using
     */
    private Handler mHandler;

    /**
     * Lock on mListeners when accessing
     */
    private final ArrayList<WeakReference<FlashlightListener>> mListeners = new ArrayList<>(1);

    /**
     * Lock on {@code this} when accessing
     */
    private boolean mFlashlightEnabled;

    private final String mCameraId;
    private boolean mTorchAvailable;

    private final Logger logger = new Logger("HwImpl");

    public FlashlightController(Context mContext) {
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);

        String cameraId = null;
        try {
            cameraId = getCameraId();
        } catch (Throwable e) {
            return;
        } finally {
            mCameraId = cameraId;
        }

        if (mCameraId != null) {
            ensureHandler();
            mCameraManager.registerTorchCallback(mTorchCallback, mHandler);
        }
    }

    public void setFlashlight(boolean enabled) {
        logger.d("setFlashlight: " + enabled + ", mFlashlightEnabled= " + mFlashlightEnabled);
        boolean pendingError = false;
        synchronized (this) {
            if (mFlashlightEnabled != enabled) {
                mFlashlightEnabled = enabled;
                try {
                    logger.d("setTorchMode: " + mCameraId + ", " + enabled);
                    mCameraManager.setTorchMode(mCameraId, enabled);
                } catch (CameraAccessException e) {
                    logger.e(e, "setFlashlight");
                    mFlashlightEnabled = false;
                    pendingError = true;
                }
            }
        }
        dispatchModeChanged(mFlashlightEnabled);
        if (pendingError) {
            dispatchError();
        }
    }

    public synchronized boolean isEnabled() {
        return mFlashlightEnabled;
    }

    public synchronized boolean isAvailable() {
        return mTorchAvailable;
    }

    public void addListener(FlashlightListener l) {
        synchronized (mListeners) {
            cleanUpListenersLocked(l);
            mListeners.add(new WeakReference<>(l));
        }
    }

    public void removeListener(FlashlightListener l) {
        synchronized (mListeners) {
            cleanUpListenersLocked(l);
        }
    }

    private synchronized void ensureHandler() {
        if (mHandler == null) {
            HandlerThread thread = new HandlerThread("ShortX-FlashlightCtrl");
            thread.start();
            mHandler = new Handler(thread.getLooper());
        }
    }

    private String getCameraId() throws CameraAccessException {
        String[] ids = mCameraManager.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
            Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
            if (flashAvailable != null && flashAvailable
                    && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                return id;
            }
        }
        return null;
    }

    private void dispatchModeChanged(boolean enabled) {
        dispatchListeners(DISPATCH_CHANGED, enabled);
    }

    private void dispatchError() {
        dispatchListeners(DISPATCH_CHANGED, false /* argument (ignored) */);
    }

    private void dispatchAvailabilityChanged(boolean available) {
        dispatchListeners(DISPATCH_AVAILABILITY_CHANGED, available);
    }

    private void dispatchListeners(int message, boolean argument) {
        synchronized (mListeners) {
            final int N = mListeners.size();
            boolean cleanup = false;
            for (int i = 0; i < N; i++) {
                FlashlightListener l = mListeners.get(i).get();
                if (l != null) {
                    if (message == DISPATCH_ERROR) {
                        l.onFlashlightError();
                    } else if (message == DISPATCH_CHANGED) {
                        l.onFlashlightChanged(argument);
                    } else if (message == DISPATCH_AVAILABILITY_CHANGED) {
                        l.onFlashlightAvailabilityChanged(argument);
                    }
                } else {
                    cleanup = true;
                }
            }
            if (cleanup) {
                cleanUpListenersLocked(null);
            }
        }
    }

    private void cleanUpListenersLocked(FlashlightListener listener) {
        for (int i = mListeners.size() - 1; i >= 0; i--) {
            FlashlightListener found = mListeners.get(i).get();
            if (found == null || found == listener) {
                mListeners.remove(i);
            }
        }
    }

    private final CameraManager.TorchCallback mTorchCallback =
            new CameraManager.TorchCallback() {

                @Override
                public void onTorchModeUnavailable(String cameraId) {
                    if (TextUtils.equals(cameraId, mCameraId)) {
                        setCameraAvailable(false);
                    }
                }

                @Override
                public void onTorchModeChanged(String cameraId, boolean enabled) {
                    if (TextUtils.equals(cameraId, mCameraId)) {
                        setCameraAvailable(true);
                        setTorchMode(enabled);
                    }
                }

                private void setCameraAvailable(boolean available) {
                    boolean changed;
                    synchronized (FlashlightController.this) {
                        changed = mTorchAvailable != available;
                        mTorchAvailable = available;
                    }
                    if (changed) {
                        dispatchAvailabilityChanged(available);
                    }
                }

                private void setTorchMode(boolean enabled) {
                    boolean changed;
                    synchronized (FlashlightController.this) {
                        changed = mFlashlightEnabled != enabled;
                        mFlashlightEnabled = enabled;
                    }
                    if (changed) {
                        dispatchModeChanged(enabled);
                    }
                }
            };

    public interface FlashlightListener {

        /**
         * Called when the flashlight was turned off or on.
         *
         * @param enabled true if the flashlight is currently turned on.
         */
        void onFlashlightChanged(boolean enabled);


        /**
         * Called when there is an error that turns the flashlight off.
         */
        void onFlashlightError();

        /**
         * Called when there is a change in availability of the flashlight functionality
         *
         * @param available true if the flashlight is currently available.
         */
        void onFlashlightAvailabilityChanged(boolean available);
    }

    static class DirectExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            command.run();
        }

        @Override
        public String toString() {
            return "DIRECT_EXECUTOR";
        }
    }
}

