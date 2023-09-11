package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.os.SystemClock
import android.view.MotionEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.ParcelableUtil
import tornaco.apps.shortx.services.input.IMSFunc
import tornaco.apps.shortx.services.util.copyUse
import java.util.concurrent.atomic.AtomicBoolean

class GesturePlayer(
    context: Context,
) : WithPointerLocation(context) {
    private val logger = Logger("GesturePlayer")
    private val isStart = AtomicBoolean(false)

    val isInjecting get() = isStart.get()

    suspend fun stopInject() {
        logger.d("stopInject")
        isStart.set(false)
        disablePointerLocation()
    }

    suspend fun injectGesture(
        gesture: GestureRecord,
        speed: Float,
        showGesturePathView: Boolean,
    ) {
        logger.d("injectGesture")
        if (isStart.compareAndSet(false, true)) {
            logger.d("injectGesture in.")

            if (showGesturePathView) {
                enablePointerLocation()
            }

            withContext(Dispatchers.IO) {
                val motionEvents = gesture.eventDataList.map {
                    ParcelableUtil.unmarshall(it.toByteArray(), MotionEvent.CREATOR)
                }
                val speedScale = 1f / (if (speed <= 0) 1f else speed)
                logger.d("injectGesture: ${gesture.label}, speed: $speedScale, event size: ${gesture.eventDataList.size}")
                if (motionEvents.isNotEmpty()) {
                    var lastEventTime = motionEvents.first().eventTime
                    val firstDownTime = motionEvents.first().downTime
                    val downTimeDelta = SystemClock.uptimeMillis() - firstDownTime

                    motionEvents.forEach { event ->
                        if (isActive && isStart.get()) {
                            val delay = event.eventTime - lastEventTime
                            lastEventTime = event.eventTime
                            delay((delay * speedScale).toLong())

                            event.copyUse(
                                event.downTime + downTimeDelta,
                                SystemClock.uptimeMillis()
                            ) {
                                IMSFunc.injectInputEvent(it)
                                // Dispatch to us because we can not catch injected event..
                                injectOnPointerEvent(it)
                            }
                        }
                    }
                }
            }
            disablePointerLocation()
            isStart.set(false)
        }
    }
}