package tornaco.apps.shortx.services.gesture

import android.content.Context
import android.graphics.Color
import android.os.SystemClock
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.util.ParcelableUtil
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.GestureRecordsStore
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write
import tornaco.apps.shortx.services.input.IMSFunc
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.ui.GuideLineView

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
        if (input.action == MotionEvent.ACTION_UP || input.action == MotionEvent.ACTION_CANCEL) {
            blockingEventDownTimes.clear()
        } else if (blockingEventDownTimes.contains(input.downTime)
        ) {
            return false
        }
        return gestureDetector?.interceptEvent(input)?.also {
            if (!it) blockingEventDownTimes.add(input.downTime)
        }
    }

    fun handleKeyEvent(event: KeyEvent): Boolean? {
        return onKeyEvent?.invoke(event)
    }

    fun handleGestureEvent(area: Edge, gestureEvent: Gesture): Boolean {
        return onGestureEvent?.invoke(area to gestureEvent) ?: false
    }

    fun getGestureAreaSize(): Int = store.read(Settings.IntSettings.GestureAreaSizeDp)

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

    fun injectGestureRecord(id: String) {
        logger.d("injectGestureRecord: $id")
        systemServiceScope.launchSafety {
            val gesture = getGestureRecordById(id)
            logger.d("injectGestureRecord, gesture: $gesture")
            if (gesture != null) {
                val motionEvents = gesture.eventDataList.map {
                    ParcelableUtil.unmarshall(it.toByteArray(), MotionEvent.CREATOR)
                }
                logger.d("injectGestureRecord, event count: ${motionEvents.size}")
                if (motionEvents.isNotEmpty()) {
                    var lastEventTime = motionEvents.first().eventTime
                    val firstDownTime = motionEvents.first().downTime
                    val downTimeDelta = SystemClock.uptimeMillis() - firstDownTime

                    motionEvents.forEach { event ->
                        val delay = event.eventTime - lastEventTime
                        lastEventTime = event.eventTime
                        delay(delay)

                        val newEvent = MotionEvent.obtain(
                            event.downTime + downTimeDelta,
                            SystemClock.uptimeMillis(),
                            event.action,
                            event.x,
                            event.y,
                            event.pressure,
                            event.size,
                            event.metaState,
                            event.xPrecision,
                            event.yPrecision,
                            event.deviceId,
                            event.edgeFlags,
                            event.source,
                            event.displayId
                        )
                        IMSFunc.injectInputEvent(newEvent)
                        newEvent.recycle()
                    }
                }
            }
        }
    }
}