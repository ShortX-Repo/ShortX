package tornaco.apps.shortx.core;

/**
 * Listener for monitoring Rule Feature state changes.
 * This interface allows clients to be notified when the Rule Feature
 * functionality is enabled or disabled.
 */
interface IRuleFeatureStateListener {
    /**
     * Called when the Rule Feature state changes.
     * 
     * @param enabled true if Rule Feature is enabled, false otherwise
     */
    void onRuleFeatureStateChanged(boolean enabled);
}