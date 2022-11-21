package tornaco.apps.shortx.core;

interface IShortX {
    Version version();
    String fingerprint();

    boolean isGestureEnabled();
    void setGestureEnabled(boolean enable);

    boolean isGestureEnabledOnArea(String areaName);
    void setGestureEnabledOnArea(String areaName, boolean enable);

    void setGestureActions(String areaName, String gestureEvent, in List<ActionBytes> data);
    List<ActionBytes> getGestureActions(String areaName, String gestureEvent);
}