package tornaco.apps.shortx.services.location;

import android.annotation.Nullable;

/**
 * Callback for when the twilight state has changed.
 */
public interface TwilightListener {
    /**
     * Called when the twilight state has changed.
     */
    void onTwilightStateChanged(@Nullable TwilightState state);
}