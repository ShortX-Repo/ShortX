package tornaco.apps.shortx.services.gesture.bpok.gesture

interface ContinuousActionHandler {
    fun onStart(edgeGroup: Int, x: Float, y: Float, time: Long)
    fun onMove(x: Float, y: Float, time: Long)
    fun onFinish(x: Float, y: Float, time: Long)
    fun onCancel()
}
