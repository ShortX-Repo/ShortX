package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.graphics.Color
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
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.GestureRecordsStore
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.input.IMSFunc
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.postSafety
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.GuideLineView
import tornaco.apps.shortx.services.ui.i18NIns


private const val GUIDELINE_VIEW_SHOWN_TIME = 6000L

class GestureService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "GestureService") {
    private val gestureRecordStore by lazy {
        GestureRecordsStore(systemServiceScope)
    }

    private var gestureDetector: Detector? = null

    private var onKeyEvent: ((KeyEvent) -> Boolean?)? = null
    private var onGestureEvent: ((Pair<Edge, Gesture>) -> Boolean?)? = null

    private val blockingEventDownTimes: MutableSet<Long> = mutableSetOf()

    private val showingHints: MutableSet<GuideLineView> = mutableSetOf()

    private val gestureRecorder by lazy { GestureRecorder(context) }
    private val gesturPlayer by lazy { GesturePlayer(context) }

    override fun start(context: Context) {
        super.start(context)
    }

    override fun systemReady() {
        super.systemReady()
        gestureRecordStore.readBlocking()
        gestureDetector = Detector(context, handler, this).also {
            it.dump()
        }
    }

    fun showEdgeHint(edgeNumber: Int) {
        val edge: Edge? = Edge.forNumber(edgeNumber)
        edge?.let {
            showGideLineViewForArea(it)
        }
    }

    fun onKeyEvent(onKeyEvent: (KeyEvent) -> Boolean?) {
        this.onKeyEvent = onKeyEvent
    }

    fun onGestureEvent(onGestureEvent: (Pair<Edge, Gesture>) -> Boolean?) {
        this.onGestureEvent = onGestureEvent
    }

    private fun showGideLineViewForArea(area: Edge) {
        systemServiceScope.launchSafety {
            // Dismiss first.
            showingHints.forEach {
                kotlin.runCatching { it.detach() }.onFailure { logger.e(it, "detach fail") }
            }
            showingHints.clear()

            val guideLineView =
                GuideLineView(
                    context,
                    listOf(HotSeat(area, Color.RED)),
                    getGestureAreaSize()._dp
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
        if (input.action == MotionEvent.ACTION_UP || input.action == MotionEvent.ACTION_CANCEL
            || input.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_POINTER_UP
        ) {
            blockingEventDownTimes.clear()
        }
        val gestureInterceptRes = gestureDetector?.interceptEvent(input)?.also {
            if (!it) blockingEventDownTimes.add(input.downTime)

            val eventTime = SystemClock.uptimeMillis()
            val cancel =
                MotionEvent.obtain(eventTime, eventTime, MotionEvent.ACTION_CANCEL, 0f, 0f, 0)
            IMSFunc.injectInputEvent(cancel)
        }
        if (blockingEventDownTimes.contains(input.downTime)) {
            return false
        }
        return gestureInterceptRes
    }

    fun handleKeyEvent(event: KeyEvent): Boolean? {
        // Stop play gesture if necessary
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (gesturPlayer.isInjecting) {
                logger.i("Stop inject gesture since VOL key event.")
                systemServiceScope.launchSafety {
                    gesturPlayer.stopInject()
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

    fun handleGestureEvent(area: Edge, gestureEvent: Gesture): Boolean {
        return onGestureEvent?.invoke(area to gestureEvent) ?: false
    }

    fun getGestureAreaSize(): Int = maxOf(store.read(Settings.IntSettings.GestureAreaSizeDp), 12)

    fun setGestureAreaSize(dp: Int) = store.write(Settings.IntSettings.GestureAreaSizeDp, dp)

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
                gesturPlayer.injectGesture(gesture, speed, showGesturePathView)
            }.onFailure {
                if (it is CancellationException) {
                    logger.i("injectGestureRecord canceled.")
                }
                gesturPlayer.stopInject()
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
}