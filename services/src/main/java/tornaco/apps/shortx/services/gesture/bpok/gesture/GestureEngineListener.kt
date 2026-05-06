package tornaco.apps.shortx.services.gesture.bpok.gesture

import tornaco.apps.shortx.core.proto.fact.Edge

interface GestureEngineListener {
    fun onEdgeTouchDown(region: Edge, x: Float, y: Float)
    fun onDirectionResolved(direction: Direction, gestureType: GestureType?)
    fun onSwipeProgress(region: Edge, direction: Direction, progress: Float)
    fun onGestureTriggered(region: Edge, gestureType: GestureType)
    fun onGestureCancelled()
}
