package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.content.res.Configuration
import android.view.MotionEvent
import com.google.protobuf.ByteString
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.ScreenOrientation
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.ParcelableUtil
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.services.ui.i18NIns
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

class GestureRecorder(
    context: Context,
) : WithPointerLocation(context) {
    private val logger = Logger("GestureRecorder")
    private val isStart = AtomicBoolean(false)
    private val eventCache = mutableListOf<ByteArray>()

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
            injectOnPointerEvent(input)
            add(input)
        }
    }
}