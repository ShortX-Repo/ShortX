package tornaco.apps.shortx.core;

/**
 * Listener for monitoring ForceDisable state changes.
 * This interface allows clients to be notified when the ForceDisable
 * functionality is enabled or disabled.
 */
interface IForceDisableListener {
    /**
     * Called when the ForceDisable state changes.
     * 
     * @param enabled true if ForceDisable is enabled, false otherwise
     */
    void onForceDisableStateChanged(boolean enabled);
}