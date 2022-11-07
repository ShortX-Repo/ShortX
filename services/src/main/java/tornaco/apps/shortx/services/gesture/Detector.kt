package tornaco.apps.shortx.services.gesture

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.VibrationEffect
import android.view.GestureDetector
import android.view.MotionEvent
import tornaco.apps.shortx.core.action.Action
import tornaco.apps.shortx.core.action.NoAction
import tornaco.apps.shortx.core.action.toActionOrThrow
import tornaco.apps.shortx.core.gesture.Area
import tornaco.apps.shortx.core.gesture.GestureEvent
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.screenSize
import tornaco.apps.shortx.core.util.vibrate
import tornaco.apps.shortx.services.actor.Supervisor
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope

class Detector(
    private val context: Context,
    private val handler: Handler,
    private val service: GestureService,
) {
    private val logger = Logger("Detector")
    private val supervisor: Supervisor by lazy {
        Supervisor(context = context, handler = handler, beforeExecute = {
            vibrateOnExecuteAction()
        })
    }

    private val screenSize by lazy {
        context.screenSize()
    }

    private val gestureDetector: GestureDetector =
        GestureDetector(context, object : OnGestureListenerAdapter() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val area = detectArea(e) ?: return false
                if (!service.isGestureEnabledOnArea(area)) return false
                val action = getGestureAction(area, GestureEvent.DoubleTap)
                if (action is NoAction) return false
                supervisor.execute(action)
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                logger.d("onSingleTapConfirmed")
                return super.onSingleTapConfirmed(e)
            }
        }, handler)

    fun onEvent(event: MotionEvent): Boolean? {
        if (event.isTouchEvent) {
            if (gestureDetector.onTouchEvent(event)) {
                return false
            }
        }
        return null
    }

    private fun detectArea(event: MotionEvent): Area? {
        val downX = event.x
        val downY = event.y
        val area = Area.values().firstOrNull {
            it.getRect(screenSize.x.toFloat(), screenSize.y.toFloat()).contains(downX, downY)
        }
        return area
    }

    private fun getGestureAction(area: Area, gestureEvent: GestureEvent): Action {
        return service.getGestureAction(area.name, gestureEvent.name).toActionOrThrow()
    }

    @SuppressLint("MissingPermission")
    private fun vibrateOnExecuteAction() {
        service.systemServiceScope.launchSafety {
            context.vibrate.vibrate(VibrationEffect.createOneShot(30,
                VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}

open class OnGestureListenerAdapter : GestureDetector.SimpleOnGestureListener()