package tornaco.apps.shortx.services

import android.annotation.SuppressLint
import android.content.Context
import android.view.InputEvent
import android.view.MotionEvent
import tornaco.apps.shortx.GuideLineView
import tornaco.apps.shortx.newHandlerOfNewThread

@SuppressLint("StaticFieldLeak")
object ShortXService : SystemService() {

    private val handler = newHandlerOfNewThread("ShortXService")
    private var listener: SystemGesturesPointerEventListener? = null

    override fun start(context: Context) {
        super.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        listener = SystemGesturesPointerEventListener(context, handler, object :
            SystemGesturesPointerEventListener.CallbacksAdapter() {
            override fun onSwipeFromTop() {
                logger.d("onSwipeFromTop")
            }

            override fun onSwipeFromBottom(x: Int, y: Int) {
                logger.d("onSwipeFromBottom: $x $y")
            }

            override fun onSwipeFromRight(x: Int, y: Int) {
                val screenSize = context.screenSize()
                val matching = allHotSeats.firstOrNull {
                    it.getRect(screenSize.x.toFloat(), screenSize.y.toFloat())
                        .contains(x.toFloat(), y.toFloat())
                }
                logger.d("onSwipeFromRight: $x $y  --> $matching")
            }

            override fun onSwipeFromLeft(x: Int, y: Int) {
                logger.d("onSwipeFromLeft: $x $y")
            }
        })

        GuideLineView(context).attach()
    }

    fun filterInput(event: InputEvent): Boolean? {
        if (event is MotionEvent) {
            listener?.onPointerEvent(event)

            if (event.action == MotionEvent.ACTION_UP) {
                val screenSize = context.screenSize()

                val matching = allHotSeats.firstOrNull {
                    it.getRect(screenSize.x.toFloat(), screenSize.y.toFloat())
                        .contains(event.x, event.y)
                }
                if (matching != null && matching.area == Area.RightBottom) {
                    return false
                }
            }
        }
        return null
    }
}