package tornaco.apps.shortx.services.gesture

import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.input.InternalInputFlags.FLAG_SHORTX_INJECTION
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.ScreenCorner
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.emptyByteArrayWrapper
import tornaco.apps.shortx.core.util.RemoteCallbackListExt
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.GestureRecordsStore
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.gesture.bpok.gesture.HotSeat
import tornaco.apps.shortx.services.policy.SingleKeyGestureDetector
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.GuideLineView
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.watchdog.postSafety
import tornaco.apps.shortx.services.wm.ColorPalette
import kotlin.math.abs


private const val GUIDELINE_VIEW_SHOWN_TIME = 6000L

class GestureService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "GestureService") {
    companion object {
        const val EDGE_GESTURE_SEQUENCE_HOVER_DURATION_MS_MIN = 100
        const val EDGE_GESTURE_SEQUENCE_HOVER_DURATION_MS_MAX = 1200
        const val EDGE_GESTURE_SEQUENCE_HOVER_RADIUS_DP_MIN = 8
        const val EDGE_GESTURE_SEQUENCE_HOVER_RADIUS_DP_MAX = 96
        const val EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MIN_DISTANCE_DP_MIN = 24
        const val EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MIN_DISTANCE_DP_MAX = 200
        const val EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MAX_ANGLE_DEVIATION_DEG_MIN = 10
        const val EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MAX_ANGLE_DEVIATION_DEG_MAX = 60
    }

    private val gestureRecordStore by lazy {
        GestureRecordsStore(systemServiceScope)
    }

    private var gestureDetector: ShortXGestureMain? = null

    private val showingHints: MutableSet<GuideLineView> = mutableSetOf()

    private val gestureRecorder by lazy { GestureRecorder(context) }
    private val gesturePlayer by lazy { GesturePlayer(context) }

    private val gestureSettingsListeners = RemoteCallbackListExt<ICallback>()

    override fun systemReady() {
        super.systemReady()
        gestureRecordStore.readBlocking()

        gestureDetector = ShortXGestureMain(context)
    }

    fun showEdgeHint(edgeNumber: List<Int>) {
        showGideLineViewForArea(edgeNumber.mapNotNull { Edge.forNumber(it) })
    }

    fun showCornerHint(cornerNumber: List<Int>) {
        showGuideLineViewForCorner(cornerNumber.mapNotNull { ScreenCorner.forNumber(it) })
    }

    fun showScreenRectHint(rect: Rect) {
        showGideLineViewForRect(listOf(rect))
    }

    fun filterInput(input: InputEvent?): Boolean? {
        shortXContext.input.setHasReceivedInputFilterCallback()
        if (!isSystemReady) return null
        if (input == null) return null
        if (input is MotionEvent) {
            if ((input.flags and FLAG_SHORTX_INJECTION) != 0) {
                logger.logOnDebug("Ignore injected MotionEvent: $input")
                return null
            }
            if (gestureRecorder.isRecording) {
                runBlocking { gestureRecorder.onMotionEvent(input) }
            }
            if ((input.flags and FLAG_SHORTX_INJECTION) == 0) {
                // Not our injected event, stop play
                stopGesturePlayer()
            }
            return filterMotionEvent(input)
        }
        return null
    }

    private fun filterMotionEvent(input: MotionEvent): Boolean? {
        val nonNullGestureDetector = gestureDetector
        if (nonNullGestureDetector == null) {
            logger.e("gestureDetector is null.")
            return null
        }
        val gestureInterceptRes = nonNullGestureDetector.filterMotionEvent(input)
        return gestureInterceptRes
    }

    fun getEdgeGestureSequenceHoverDurationMs(): Int =
        store.read(Settings.IntSettings.EdgeGestureSequenceHoverDurationMs)
            .coerceIn(
                EDGE_GESTURE_SEQUENCE_HOVER_DURATION_MS_MIN,
                EDGE_GESTURE_SEQUENCE_HOVER_DURATION_MS_MAX,
            )

    fun setEdgeGestureSequenceHoverDurationMs(durationMs: Int) {
        store.write(
            Settings.IntSettings.EdgeGestureSequenceHoverDurationMs,
            durationMs.coerceIn(
                EDGE_GESTURE_SEQUENCE_HOVER_DURATION_MS_MIN,
                EDGE_GESTURE_SEQUENCE_HOVER_DURATION_MS_MAX,
            )
        )
        notifyGestureSettingsChanged()
    }

    fun getEdgeGestureSequenceHoverRadiusDp(): Int =
        store.read(Settings.IntSettings.EdgeGestureSequenceHoverRadiusDp)
            .coerceIn(
                EDGE_GESTURE_SEQUENCE_HOVER_RADIUS_DP_MIN,
                EDGE_GESTURE_SEQUENCE_HOVER_RADIUS_DP_MAX,
            )

    fun setEdgeGestureSequenceHoverRadiusDp(radiusDp: Int) {
        store.write(
            Settings.IntSettings.EdgeGestureSequenceHoverRadiusDp,
            radiusDp.coerceIn(
                EDGE_GESTURE_SEQUENCE_HOVER_RADIUS_DP_MIN,
                EDGE_GESTURE_SEQUENCE_HOVER_RADIUS_DP_MAX,
            )
        )
        notifyGestureSettingsChanged()
    }

    fun getEdgeGestureSequenceChildSwipeMinDistanceDp(): Int =
        store.read(Settings.IntSettings.EdgeGestureSequenceChildSwipeMinDistanceDp)
            .coerceIn(
                EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MIN_DISTANCE_DP_MIN,
                EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MIN_DISTANCE_DP_MAX,
            )

    fun setEdgeGestureSequenceChildSwipeMinDistanceDp(distanceDp: Int) {
        store.write(
            Settings.IntSettings.EdgeGestureSequenceChildSwipeMinDistanceDp,
            distanceDp.coerceIn(
                EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MIN_DISTANCE_DP_MIN,
                EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MIN_DISTANCE_DP_MAX,
            )
        )
        notifyGestureSettingsChanged()
    }

    fun getEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg(): Int =
        store.read(Settings.IntSettings.EdgeGestureSequenceChildSwipeMaxAngleDeviationDeg)
            .coerceIn(
                EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MAX_ANGLE_DEVIATION_DEG_MIN,
                EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MAX_ANGLE_DEVIATION_DEG_MAX,
            )

    fun setEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg(angleDeg: Int) {
        store.write(
            Settings.IntSettings.EdgeGestureSequenceChildSwipeMaxAngleDeviationDeg,
            angleDeg.coerceIn(
                EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MAX_ANGLE_DEVIATION_DEG_MIN,
                EDGE_GESTURE_SEQUENCE_CHILD_SWIPE_MAX_ANGLE_DEVIATION_DEG_MAX,
            )
        )
        notifyGestureSettingsChanged()
    }

    fun isEdgeGestureSequenceChildSwipeTriggerOnRelease(): Boolean =
        store.read(Settings.BoolSettings.EdgeGestureSequenceChildSwipeTriggerOnRelease)

    fun setEdgeGestureSequenceChildSwipeTriggerOnRelease(enabled: Boolean) {
        store.write(Settings.BoolSettings.EdgeGestureSequenceChildSwipeTriggerOnRelease, enabled)
        notifyGestureSettingsChanged()
    }

    fun onRuleChanged() {
        logger.logOnDebug { "onRuleChanged" }
    }

    private fun showGideLineViewForArea(area: List<Edge>) {
        systemServiceScope.launchSafety("showGideLineViewForArea") {
            // Dismiss first.
            showingHints.forEach { hint ->
                kotlin.runCatching { hint.detach() }.onFailure { logger.e(it, "detach fail") }
            }
            showingHints.clear()

            val guideLineView =
                GuideLineView(
                    context,
                    area.mapIndexed { index, area ->
                        HotSeat(area, ColorPalette[index % ColorPalette.size])
                    },
                    getGestureAreaWidthDp()._dp,
                    getGestureLengthScale(),
                    emptyList()
                ).apply { attach() }
            showingHints.add(guideLineView)
            delay(GUIDELINE_VIEW_SHOWN_TIME)
            guideLineView.detach()
            showingHints.remove(guideLineView)
        }
    }

    private fun showGideLineViewForRect(rects: List<Rect>) {
        systemServiceScope.launchSafety("showGideLineViewForRect") {
            // Dismiss first.
            showingHints.forEach { hint ->
                kotlin.runCatching { hint.detach() }.onFailure { logger.e(it, "detach fail") }
            }
            showingHints.clear()

            val guideLineView =
                GuideLineView(
                    context,
                    emptyList(),
                    0,
                    0f,
                    rects
                ).apply { attach() }
            showingHints.add(guideLineView)
            delay(GUIDELINE_VIEW_SHOWN_TIME)
            guideLineView.detach()
            showingHints.remove(guideLineView)
        }
    }

    private fun showGuideLineViewForCorner(corners: List<ScreenCorner>) {
        systemServiceScope.launchSafety("showGuideLineViewForCorner") {
            showingHints.forEach { hint ->
                kotlin.runCatching { hint.detach() }.onFailure { logger.e(it, "detach fail") }
            }
            showingHints.clear()

            val guideLineView = GuideLineView(
                context,
                emptyList(),
                0,
                0f,
                emptyList(),
                corners,
                getCornerGestureRegionRadiusRatio()
            ).apply { attach() }
            showingHints.add(guideLineView)
            delay(GUIDELINE_VIEW_SHOWN_TIME)
            guideLineView.detach()
            showingHints.remove(guideLineView)
        }
    }

    fun reportMotionEvent(event: MotionEvent) {
        logger.e(
            "reportMotionEvent, should never be called, system-ui gesture solution is deprecated. $event"
        )
    }

    // Return true, means consumed, should intercept this event.
    fun interceptKeyBeforeQueueing(event: KeyEvent, interactive: Boolean): Boolean? {
        // update last key.
        shortXContext.input.updateLastInputKey(event.keyCode, "handleKeyEvent")

        // Stop play gesture if necessary
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            stopGesturePlayer()
        }
        return shortXContext.rule.interceptKeyBeforeQueueing(event, interactive)
    }

    private fun stopGesturePlayer() {
        if (gesturePlayer.isInjecting) {
            logger.w("Stop inject gesture since VOL key event.")
            systemServiceScope.launchSafety("handleKeyEvent") {
                gesturePlayer.stopInject()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        i18NIns["ui.action.gesture.record.inject.stopped"],
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun getGestureLengthScale(): Float =
        minOf(store.read(Settings.FloatSettings.GestureAreaLengthScale), 1f)

    fun setGestureLengthScale(scale: Float) {
        store.write(Settings.FloatSettings.GestureAreaLengthScale, minOf(1f, abs(scale)))
        notifyGestureSettingsChanged()
    }

    fun getGestureAreaWidthDp(): Int =
        maxOf(store.read(Settings.IntSettings.GestureAreaWidthDp), 12)

    fun setGestureAreaWidthDp(dp: Int) {
        store.write(Settings.IntSettings.GestureAreaWidthDp, dp)
        notifyGestureSettingsChanged()
    }

    fun getSwipeDistanceThresholdDp(): Int =
        maxOf(store.read(Settings.IntSettings.SwipeDistanceThresholdDp), 1)

    fun setSwipeDistanceThresholdDp(thr: Int) {
        store.write(Settings.IntSettings.SwipeDistanceThresholdDp, thr)
        notifyGestureSettingsChanged()
    }

    fun getCornerGestureRegionRadiusRatio(): Float =
        (store.read(Settings.IntSettings.CornerGestureRegionRadiusRatioPercent)
            .coerceIn(6, 40)) / 100f

    fun setCornerGestureRegionRadiusRatioPercent(percent: Int) {
        store.write(
            Settings.IntSettings.CornerGestureRegionRadiusRatioPercent,
            percent.coerceIn(6, 40)
        )
        notifyGestureSettingsChanged()
    }

    fun getCornerGestureRegionRadiusRatioPercent(): Int =
        (getCornerGestureRegionRadiusRatio() * 100).toInt()

    fun getCornerGestureMinSwipeDistanceDp(): Int =
        store.read(Settings.IntSettings.CornerGestureMinSwipeDistanceDp).coerceIn(24, 200)

    fun setCornerGestureMinSwipeDistanceDp(dp: Int) {
        store.write(Settings.IntSettings.CornerGestureMinSwipeDistanceDp, dp.coerceIn(24, 200))
        notifyGestureSettingsChanged()
    }


    fun getKeyDetectorLongPressTimeoutMS() =
        store.read(Settings.IntSettings.KeyDetectorLongPressTimeoutMS)

    fun setKeyDetectorLongPressTimeoutMS(timeout: Int) {
        store.write(Settings.IntSettings.KeyDetectorLongPressTimeoutMS, timeout)
        SingleKeyGestureDetector.sDefaultLongPressTimeout =
            shortXContext.gesture.getKeyDetectorLongPressTimeoutMS().toLong().apply {
                logger.i("setKeyDetectorLongPressTimeoutMS, sDefaultLongPressTimeout: $this")
            }
    }

    fun addGestureRecord(record: GestureRecord) {
        gestureRecordStore.addGestureRecord(record)
    }

    fun getGestureRecordById(id: String): GestureRecord? {
        return gestureRecordStore.getGestureRecord(id)
    }

    fun deleteGestureRecordById(id: String) {
        gestureRecordStore.deleteGestureRecord(id)
    }

    fun getAllGestureRecords(withEventData: Boolean): List<GestureRecord> {
        return gestureRecordStore.getAllGestureRecords().map {
            if (withEventData) {
                it
            } else {
                GestureRecord.newBuilder(it).clearEventData().build()
            }
        }
    }

    suspend fun injectGestureRecord(
        id: String,
        speed: Float,
        showGesturePathView: Boolean,
        onComplete: SynchronousResultReceiver?
    ) {
        logger.d("injectGestureRecord: $id $speed $onComplete")
        val gesture = getGestureRecordById(id)
        logger.d("injectGestureRecord, gesture: $gesture")
        if (gesture != null) {
            runCatching {
                gesturePlayer.injectGesture(gesture, speed, showGesturePathView)
            }.onFailure {
                if (it is CancellationException) {
                    logger.i("injectGestureRecord canceled.")
                }
                gesturePlayer.stopInject()
                throw it
            }
        }
        onComplete?.send(ByteArrayWrapper(byteArrayOf()))
    }

    fun startGestureRecording() {
        systemServiceScope.launchSafety("startGestureRecording") {
            logger.d("startGestureRecording")
            gestureRecorder.start()
        }
    }

    fun stopGestureRecording(save: Boolean) {
        systemServiceScope.launchSafety("stopGestureRecording") {
            gestureRecorder.stop()?.let {
                if (save) {
                    logger.d("stopGestureRecording: ${it.label}")
                    addGestureRecord(it)

                    handler.postSafety("showDanmu") {
                        shortXContext.ui.showDanmu(
                            null,
                            i18NIns["ui.settings.gesture.record"] + i18NIns["ui.common.saved"]
                        )
                    }
                }
            }
        }
    }

    fun isGestureRecording(): Boolean {
        return gestureRecorder.isRecording
    }

    fun registerGestureSettingsListener(listener: ICallback) {
        synchronized(gestureSettingsListeners) {
            gestureSettingsListeners.register(listener)
        }
    }

    fun unregisterGestureSettingsListener(listener: ICallback) {
        synchronized(gestureSettingsListeners) {
            gestureSettingsListeners.unregister(listener)
        }
    }

    fun notifyGestureSettingsChanged() {
        logger.logOnDebug("notifyGestureSettingsChanged.")
        synchronized(gestureSettingsListeners) {
            gestureSettingsListeners.broadcast { item ->
                runCatching {
                    item.receive(emptyByteArrayWrapper)
                }.onFailure {
                    logger.e(it, "notifyGestureSettingsChanged")
                }
            }
        }
    }

    fun registerRecorderStateListener(listener: ICallback) {
        gestureRecorder.registerRecorderStateListener(listener)
    }

    fun unregisterRecorderStateListener(listener: ICallback) {
        gestureRecorder.unregisterRecorderStateListener(listener)
    }
}
