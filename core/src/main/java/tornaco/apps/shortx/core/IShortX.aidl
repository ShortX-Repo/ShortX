package tornaco.apps.shortx.core;

interface IShortX {
    Version version();
    String fingerprint();

    boolean isGestureEnabled();
    void setGestureEnabled(boolean enable);

    boolean isGestureEnabledOnArea(String areaName);
    void setGestureEnabledOnArea(String areaName, boolean enable);

    void setGestureAction(String areaName, String gestureEvent, in Bundle data);
    Bundle getGestureAction(String areaName, String gestureEvent);
}