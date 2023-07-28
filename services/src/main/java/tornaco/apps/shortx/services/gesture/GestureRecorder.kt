package tornaco.apps.shortx.services.gesture

import android.app.ActivityManager
import android.content.Context
import android.graphics.PixelFormat
import android.os.SystemClock
import android.view.MotionEvent
import android.view.MotionEvent.PointerCoords
import android.view.MotionEvent.PointerProperties
import android.view.WindowManager
import com.google.protobuf.ByteString
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.ParcelableUtil
import tornaco.apps.shortx.services.input.IMSFunc
import tornaco.apps.shortx.services.ui.PointerLocationView
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

class GestureRecorder(
    private val context: Context,
) {
    private val logger = Logger("GestureRecorder")
    private val isStart = AtomicBoolean(false)
    private val eventCache = mutableListOf<ByteArray>()

    private var mPointerLocationView: PointerLocationView? = null

    val isRecording get() = isStart.get()

    fun start() {
        require(!isStart.get()) {
            "Already started!"
        }
        eventCache.clear()
        isStart.set(true)
        enablePointerLocation()
    }

    fun add(motionEvent: MotionEvent) {
        eventCache.add(ParcelableUtil.marshall(motionEvent))
    }

    fun stop(): GestureRecord? {
        isStart.set(false)
        disablePointerLocation()

        if (eventCache.isEmpty()) {
            return null
        }
        val record = GestureRecord.newBuilder().setId(UUID.randomUUID().toString())
            .setLabel(DateUtils.formatForFileName(System.currentTimeMillis()))
            .addAllEventData(eventCache.map {
                ByteString.copyFrom(it)
            })
            .build()

        eventCache.clear()
        return record
    }

    fun onPointerEvent(input: MotionEvent) {
        if (isRecording) {
            mPointerLocationView?.onPointerEvent(input)
            add(input)
        }
    }

    suspend fun injectGesture(gesture: GestureRecord) {
        stop()
        enablePointerLocation()

        val motionEvents = gesture.eventDataList.map {
            ParcelableUtil.unmarshall(it.toByteArray(), MotionEvent.CREATOR)
        }
        logger.d("injectGesture: ${gesture.label}, event size: ${gesture.eventDataList.size}")
        if (motionEvents.isNotEmpty()) {
            var lastEventTime = motionEvents.first().eventTime
            val firstDownTime = motionEvents.first().downTime
            val downTimeDelta = SystemClock.uptimeMillis() - firstDownTime

            motionEvents.forEach { event ->
                val delay = event.eventTime - lastEventTime
                lastEventTime = event.eventTime
                delay(delay)

                val pointerProps = arrayOfNulls<PointerProperties>(event.pointerCount)
                val pointerCoords = arrayOfNulls<PointerCoords>(event.pointerCount)
                for (i in 0 until event.pointerCount) {
                    val prop = PointerProperties()
                    val coords = PointerCoords()

                    event.getPointerProperties(i, prop)
                    pointerProps[i] = prop

                    event.getPointerCoords(i, coords)
                    pointerCoords[i] = coords
                }

                val newEvent = MotionEvent.obtain(
                    event.downTime + downTimeDelta,
                    SystemClock.uptimeMillis(),
                    event.action,
                    event.pointerCount,
                    pointerProps,
                    pointerCoords,
                    event.metaState,
                    event.buttonState,
                    event.xPrecision,
                    event.yPrecision,
                    event.deviceId,
                    event.edgeFlags,
                    event.source,
                    event.displayId,
                    event.flags
                )

                IMSFunc.injectInputEvent(newEvent)

                // Dispatch to us because we can not catch injected event..
                mPointerLocationView?.onPointerEvent(newEvent)

                newEvent.recycle()
            }
        }

        disablePointerLocation()
    }

    private fun enablePointerLocation() {
        if (mPointerLocationView != null) {
            return
        }
        mPointerLocationView = PointerLocationView(context)
        mPointerLocationView!!.setPrintCoords(false)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY
        lp.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        if (OsUtils.isROrAbove()) {
            lp.fitInsetsTypes = 0
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
        if (ActivityManager.isHighEndGfx()) {
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            lp.privateFlags =
                lp.privateFlags or WindowManager.LayoutParams.PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED
        }
        lp.format = PixelFormat.TRANSLUCENT
        lp.title = "GestureRecorder-ShortX"
        lp.inputFeatures =
            lp.inputFeatures or WindowManager.LayoutParams.INPUT_FEATURE_NO_INPUT_CHANNEL
        val wm: WindowManager = context.getSystemService(WindowManager::class.java)
        wm.addView(mPointerLocationView, lp)
    }

    private fun disablePointerLocation() {
        if (mPointerLocationView == null) {
            return
        }
        val wm: WindowManager = context.getSystemService(WindowManager::class.java)
        wm.removeView(mPointerLocationView)

        mPointerLocationView = null
    }
}