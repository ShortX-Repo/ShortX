package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.content.res.Configuration
import android.graphics.RectF
import android.os.SystemClock
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildPropExt.onDebugBuild
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.GestureRecordsStore
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.input.IMSFunc
import tornaco.apps.shortx.services.input.opls.OplusInputMonitorHelper
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.GuideLineView
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.util.SingleKeyGestureDetector
import tornaco.apps.shortx.services.watchdog.launchSafety
import tornaco.apps.shortx.services.watchdog.postSafety
import tornaco.apps.shortx.services.wm.ColorPalette
import kotlin.math.abs


private const val GUIDELINE_VIEW_SHOWN_TIME = 6000L

class GestureService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "GestureService") {
    private val gestureRecordStore by lazy {
        GestureRecordsStore(systemServiceScope)
    }

    private var gestureDetector: Detector? = null

    private var onKeyEvent: ((KeyEvent) -> Boolean?)? = null
    private var onGestureEvent: ((Pair<Edge, Gesture>) -> Boolean?) = {
        false
    }

    private val showingHints: MutableSet<GuideLineView> = mutableSetOf()

    private val gestureRecorder by lazy { GestureRecorder(context) }
    private val gesturePlayer by lazy { GesturePlayer(context) }

    private val interceptMotionPortRects = mutableListOf<RectF>()
    private val interceptMotionLandRects = mutableListOf<RectF>()

    private val inputMonitorHelper by lazy {
        OplusInputMonitorHelper.getInstance().apply {
            init(context)
        }
    }

    override fun start(context: Context) {
        super.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        gestureRecordStore.readBlocking()
        gestureDetector = Detector(context, handler, this).also {
            it.dump()
        }

        updateShouldInterceptMotionEventRect()

        inputMonitorHelper.enableInputReceiver({ inputEvent ->
            // logger.d("SHORTX_INPUT_ OplusInputMonitorHelper: $inputEvent")
            val res = filterInput(inputEvent)
            val consumed = res != null && !res
            consumed
        }, false)
    }

    fun showEdgeHint(edgeNumber: List<Int>) {
        showGideLineViewForArea(edgeNumber.mapNotNull { Edge.forNumber(it) })
    }

    fun onKeyEvent(onKeyEvent: (KeyEvent) -> Boolean?) {
        this.onKeyEvent = onKeyEvent
    }

    fun onGestureEvent(onGestureEvent: (Pair<Edge, Gesture>) -> Boolean?) {
        this.onGestureEvent = onGestureEvent
    }

    private fun showGideLineViewForArea(area: List<Edge>) {
        systemServiceScope.launchSafety {
            // Dismiss first.
            showingHints.forEach {
                kotlin.runCatching { it.detach() }.onFailure { logger.e(it, "detach fail") }
            }
            showingHints.clear()

            val guideLineView =
                GuideLineView(
                    context,
                    area.mapIndexed { index, area ->
                        HotSeat(area, ColorPalette[index % ColorPalette.size])
                    },
                    getGestureAreaWidthDp()._dp,
                    getGestureLengthScale()
                ).apply { attach() }
            showingHints.add(guideLineView)
            delay(GUIDELINE_VIEW_SHOWN_TIME)
            guideLineView.detach()
            showingHints.remove(guideLineView)
        }
    }

    fun filterInput(input: InputEvent?): Boolean? {
        if (!isSystemReady) return null
        if (input == null) return null
        if (input is MotionEvent) {
            if (gestureRecorder.isRecording) {
                runBlocking { gestureRecorder.onPointerEvent(input) }
            }
            return filterMotionEvent(input)
        }
        if (input is KeyEvent) {
            // Below code is handle by PhoneWindowManager.
            if (!arrayOf(
                    KeyEvent.KEYCODE_HOME,
                    KeyEvent.KEYCODE_BACK,
                    KeyEvent.KEYCODE_POWER,
                ).contains(input.keyCode)
            ) {
                return handleKeyEvent(input)
            }
        }
        return null
    }

    private fun filterMotionEvent(input: MotionEvent): Boolean? {
        val nonNullGestureDetector = gestureDetector
        if (nonNullGestureDetector == null) {
            logger.e("gestureDetector is null.")
            return null
        }

        // Call nonNullGestureDetector first.
        val gestureInterceptRes = nonNullGestureDetector.interceptEvent(input)

        // Check if intercept.
        val startTimeMillis = SystemClock.elapsedRealtimeNanos()
        val currentOrientation = context.resources.configuration.orientation
        val checkRects = if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            interceptMotionPortRects
        } else {
            interceptMotionLandRects
        }
        // onDebugBuild { logger.d("filterMotionEvent, checkRects: $checkRects") }
        val anyIntercept = checkRects.any { it.contains(input.x, input.y) }
        val timeTaken = SystemClock.elapsedRealtimeNanos() - startTimeMillis
        onDebugBuild {
            // logger.d("filterMotionEvent, check take time: $timeTaken(nano)")
        }
        if (anyIntercept) {
            onDebugBuild {
                logger.d("filterMotionEvent, intercept MotionEvent: $input")
            }
            return false
        }

        if (gestureInterceptRes != null) {
            // False, means block this event
            if (!gestureInterceptRes) {
                logger.w("Send ACTION_CANCEL since we interceptEvent!")
                val eventTime = SystemClock.uptimeMillis()
                val cancel =
                    MotionEvent.obtain(eventTime, eventTime, MotionEvent.ACTION_CANCEL, 0f, 0f, 0)
                IMSFunc.injectInputEvent(cancel)
            }
        }

        return gestureInterceptRes
    }

    fun handleKeyEvent(event: KeyEvent): Boolean? {
        // update last key.
        shortXContext.input.updateLastInputKey(event.keyCode, "handleKeyEvent")

        // Stop play gesture if necessary
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (gesturePlayer.isInjecting) {
                logger.i("Stop inject gesture since VOL key event.")
                systemServiceScope.launchSafety {
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

        return onKeyEvent?.invoke(event)
    }

    fun handleGestureEvent(area: Array<Edge>, gestureEvent: Gesture): Boolean {
        var res = false
        area.forEach {
            val handled = onGestureEvent(it to gestureEvent) ?: false
            if (handled) res = true
        }
        return res
    }

    fun getGestureLengthScale(): Float =
        minOf(store.read(Settings.FloatSettings.GestureAreaLengthScale), 1f)

    fun setGestureLengthScale(scale: Float) {
        store.write(Settings.FloatSettings.GestureAreaLengthScale, minOf(1f, abs(scale)))
        updateShouldInterceptMotionEventRect()
    }

    fun getGestureAreaWidthDp(): Int =
        maxOf(store.read(Settings.IntSettings.GestureAreaWidthDp), 12)

    fun setGestureAreaWidthDp(dp: Int) {
        store.write(Settings.IntSettings.GestureAreaWidthDp, dp)
        updateShouldInterceptMotionEventRect()
    }

    fun getSwipeDistanceThresholdDp(): Int =
        maxOf(store.read(Settings.IntSettings.SwipeDistanceThresholdDp), 1)

    fun setSwipeDistanceThresholdDp(thr: Int) {
        store.write(Settings.IntSettings.SwipeDistanceThresholdDp, thr)
        gestureDetector?.setSwipeDistanceThresholdDp(thr)
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
        systemServiceScope.launchSafety {
            logger.d("startGestureRecording")
            gestureRecorder.start()
        }
    }

    fun stopGestureRecording(save: Boolean) {
        systemServiceScope.launchSafety {
            gestureRecorder.stop()?.let {
                if (save) {
                    logger.d("stopGestureRecording: ${it.label}")
                    addGestureRecord(it)

                    handler.postSafety {
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

    fun onRuleChanged() {
        updateShouldInterceptMotionEventRect()
    }

    private fun updateShouldInterceptMotionEventRect() {
        val screenSize = shortXContext.wm.screenSize
        val portRects = mutableListOf<RectF>()
        val landRects = mutableListOf<RectF>()

        shortXContext.rule.getAllEnabledRules().forEach {
            it.factsList.forEach { protoFact ->
                val isEG = protoFact is_ EdgeGesture::class.java
                if (isEG) {
                    val eg = protoFact unpack_ EdgeGesture::class.java
                    if (eg.isIntercept) {
                        portRects.add(
                            eg.edge.getRect(
                                screenSize.x.toFloat(),
                                screenSize.y.toFloat(),
                                getGestureAreaWidthDp()._dp.toFloat(),
                                getGestureLengthScale()
                            )
                        )
                        landRects.add(
                            eg.edge.getRectL(
                                screenSize.x.toFloat(),
                                screenSize.y.toFloat(),
                                getGestureAreaWidthDp()._dp.toFloat(),
                                getGestureLengthScale()
                            )
                        )
                    }
                }
            }
        }

        logger.d("updateShouldInterceptMotionEventRect, portRects: $portRects")
        logger.d("updateShouldInterceptMotionEventRect, landRects: $landRects")

        this.interceptMotionLandRects.clear()
        this.interceptMotionLandRects.addAll(landRects)

        this.interceptMotionPortRects.clear()
        this.interceptMotionPortRects.addAll(portRects)
    }
}