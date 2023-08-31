package tornaco.apps.shortx.services.gesture

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.SystemClock
import android.view.MotionEvent
import android.view.WindowManager
import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.ScreenOrientation
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.ParcelableUtil
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.input.IMSFunc
import tornaco.apps.shortx.services.ui.PointerLocationView
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.util.copyUse
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

    suspend fun start() {
        if (isStart.get()) {
            logger.e("Already started!")
            return
        }
        eventCache.clear()
        isStart.set(true)
        enablePointerLocation()
    }

    fun add(motionEvent: MotionEvent) {
        eventCache.add(ParcelableUtil.marshall(motionEvent))
    }

    suspend fun stop(): GestureRecord? {
        isStart.set(false)
        disablePointerLocation()

        if (eventCache.isEmpty()) {
            return null
        }
        val screenOrientation = context.resources.configuration.orientation
        val firstEvent = ParcelableUtil.unmarshall(eventCache.first(), MotionEvent.CREATOR)
        val lastEvent = ParcelableUtil.unmarshall(eventCache.last(), MotionEvent.CREATOR)
        val duration = maxOf(lastEvent.eventTime - firstEvent.eventTime, 0L)
        val record = GestureRecord
            .newBuilder()
            .setId(UUID.randomUUID().toString())
            .setLabel(i18NIns["ui.settings.gesture.record"])
            .setRecordingTimeMillis(System.currentTimeMillis())
            .setDuration(duration)
            .setScreenOrientation(
                (screenOrientation == Configuration.ORIENTATION_PORTRAIT).select(
                    ScreenOrientation.ScreenOrientation_P,
                    ScreenOrientation.ScreenOrientation_L
                )
            )
            .addAllEventData(eventCache.map {
                ByteString.copyFrom(it)
            })
            .build()

        eventCache.clear()
        return record
    }

    suspend fun onPointerEvent(input: MotionEvent) {
        if (isRecording) {
            withContext(Dispatchers.Main) {
                mPointerLocationView?.onPointerEvent(input)
            }
            add(input)
        }
    }

    suspend fun injectGesture(
        gesture: GestureRecord,
        speed: Float,
        showGesturePathView: Boolean,
    ) {
        stop()
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
                    if (isActive) {
                        val delay = event.eventTime - lastEventTime
                        lastEventTime = event.eventTime
                        delay((delay * speedScale).toLong())

                        event.copyUse(
                            event.downTime + downTimeDelta,
                            SystemClock.uptimeMillis()
                        ) {
                            IMSFunc.injectInputEvent(it)
                            // Dispatch to us because we can not catch injected event..
                            withContext(Dispatchers.Main) {
                                mPointerLocationView?.onPointerEvent(it)
                            }
                        }
                    }
                }
            }
        }
        disablePointerLocation()
    }

    private suspend fun enablePointerLocation() = withContext(Dispatchers.Main) {
        if (mPointerLocationView != null) {
            return@withContext
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