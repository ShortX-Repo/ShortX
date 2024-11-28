package tornaco.apps.shortx.services.location;

import android.annotation.NonNull;
import android.os.Handler;

/**
 * This class provides sunrise/sunset information based on the device's current location.
 */
public interface TwilightManager {
    /**
     * Register a listener to be notified whenever the twilight state changes.
     *
     * @param listener the {@link TwilightListener} to be notified
     * @param handler  the {@link Handler} to use to notify the listener
     */
    void registerListener(@NonNull TwilightListener listener, @NonNull Handler handler);

    /**
     * Unregisters a previously registered listener.
     *
     * @param listener the {@link TwilightListener} to be unregistered
     */
    void unregisterListener(@NonNull TwilightListener listener);

    /**
     * Returns the last {@link TwilightState}, or {@code null} if not available.
     */
    TwilightState getLastTwilightState();
}