package tornaco.apps.shortx.core;

interface IShortX {
    Version version();
    String fingerprint();

    boolean isEdgeGestureEnabled();
    void setEdgeGestureEnabled(boolean enable);

    boolean isEdgeGestureEnabledOnArea(String areaName);
    void setEdgeGestureEnabledOnArea(String areaName, boolean enable);
}