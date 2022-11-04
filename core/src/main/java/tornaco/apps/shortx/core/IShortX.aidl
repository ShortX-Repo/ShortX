package tornaco.apps.shortx.core;

interface IShortX {
    Version version();
    String fingerprint();

    boolean isEdgeGestureEnabled();
    void setEdgeGestureEnabled(boolean enable);
}