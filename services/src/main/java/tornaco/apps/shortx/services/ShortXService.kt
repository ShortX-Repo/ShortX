package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context
import android.view.InputEvent
import android.view.MotionEvent
import tornaco.apps.shortx.services.SystemGesturesPointerEventListener.Callbacks

@SuppressLint("StaticFieldLeak")
object ShortXService : SystemService() {

    private var listener: SystemGesturesPointerEventListener? = null

    override fun start(context: Context) {
        super.start(context)
        listener = SystemGesturesPointerEventListener(context, object : Callbacks {
            override fun onSwipeFromTop() {
                logger.d("onSwipeFromTop")
            }

            override fun onSwipeFromBottom(x: Int, y: Int) {
                logger.d("onSwipeFromBottom: $x $y")
            }

            override fun onSwipeFromRight() {
                logger.d("onSwipeFromRight")
            }

            override fun onSwipeFromLeft() {
                logger.d("onSwipeFromLeft")
            }

            override fun onFling(durationMs: Int) {

            }

            override fun onDown() {

            }

            override fun onUpOrCancel() {

            }

            override fun onMouseHoverAtTop() {

            }

            override fun onMouseHoverAtBottom() {

            }

            override fun onMouseLeaveFromEdge() {

            }

            override fun onDebug() {

            }
        })
    }

    fun filterInput(event: InputEvent): Boolean {
        if (event is MotionEvent) {
            listener?.onPointerEvent(event)
        }
        return false
    }
}