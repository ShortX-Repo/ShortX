package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.content.Context
import android.content.res.Configuration
import android.graphics.RectF
import android.os.SystemClock
import android.view.MotionEvent
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.rule.DAObserver
import tornaco.apps.shortx.core.rule.RuleObserver
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp

interface DetectorService {
    fun handleGestureEvent(area: Array<Edge>, gestureEvent: Gesture): Boolean
    fun getSwipeDistanceThresholdDp(): Int
    fun getGestureAreaWidthDp(): Int
    fun getGestureLengthScale(): Float

    fun obsGestureSettingsChanged(obs: () -> Unit)
    fun obsConfigChanged(obs: () -> Unit)
}

class DetectorServiceImpl(private val context: Context) : DetectorService {
    private val shortx by lazy { shortXManager }
    private val logger = Logger("DetectorService")

    private var swipeDistanceThresholdDp: Int = 24
    private var gestureAreaWidthDp: Int = 28
    private var gestureLengthScale: Float = 1f

    private var afterGestureSettingsChangedObs: () -> Unit = {}
    private var afterConfigChangedObs: () -> Unit = {}

    private val allEdgeGestures = Gesture.entries
        .filter { it != Gesture.UNRECOGNIZED }
        .flatMap { gesture ->
            Edge.entries
                .filter { it != Edge.UNRECOGNIZED }
                .map { edge ->
                    EdgeGesture.newBuilder()
                        .setGesture(gesture)
                        .setEdge(edge)
                        .build()
                }
        }

    private val fireableEdgeGestures = mutableSetOf<EdgeGesture>()

    private val interceptMotionPortRects = mutableListOf<RectF>()
    private val interceptMotionLandRects = mutableListOf<RectF>()

    init {
        shortx.registerGestureSettingsListener(object : ICallback.Stub() {
            override fun receive(data: ByteArrayWrapper?) {
                logger.w("GestureSettings changed.")
                readAllSettings()
                updateShouldInterceptMotionEventRect()
                afterGestureSettingsChangedObs()
            }
        })

        shortx.registerRuleObs(object : RuleObserver() {
            override fun onRuleChanged(id: String) {
                super.onRuleChanged(id)
                readGestureFireState("Rule changed")
                updateShouldInterceptMotionEventRect()
            }
        })

        shortx.registerDAObs(object : DAObserver() {
            override fun onDAChanged(id: String) {
                super.onDAChanged(id)
                readGestureFireState("DA changed")
                updateShouldInterceptMotionEventRect()
            }
        })

        readAllSettings()
        readGestureFireState("Init")
        updateShouldInterceptMotionEventRect()
    }

    private fun readGestureFireState(reason: String) {
        logger.d("readGestureFireState, reason: $reason")
        synchronized(fireableEdgeGestures) {
            fireableEdgeGestures.clear()
            fireableEdgeGestures.addAll(
                allEdgeGestures.filter { gesture ->
                    shortx.shouldFireEdgeGesture(gesture)
                }
            )
            logger.w(
                "readGestureFireState: ${
                    fireableEdgeGestures.joinToString {
                        "${it.edge}-${it.gesture}"
                    }
                }"
            )
        }
    }

    private fun readAllSettings() {
        swipeDistanceThresholdDp = shortx.getSwipeDistanceThresholdDp()
        gestureAreaWidthDp = shortx.getGestureAreaSize()
        gestureLengthScale = shortx.getSwipeLengthScale()

        logger.w("swipeDistanceThresholdDp: $swipeDistanceThresholdDp")
        logger.w("gestureAreaWidthDp: $gestureAreaWidthDp")
        logger.w("gestureLengthScale: $gestureLengthScale")
    }

    override fun getSwipeDistanceThresholdDp(): Int {
        return swipeDistanceThresholdDp
    }


    override fun getGestureAreaWidthDp(): Int {
        return gestureAreaWidthDp
    }

    override fun getGestureLengthScale(): Float {
        return gestureLengthScale
    }

    override fun obsGestureSettingsChanged(obs: () -> Unit) {
        afterGestureSettingsChangedObs = obs
    }

    override fun obsConfigChanged(obs: () -> Unit) {
        afterConfigChangedObs = obs
    }

    override fun handleGestureEvent(area: Array<Edge>, gestureEvent: Gesture): Boolean {
        var res = false
        area.forEach {
            val handled = onGestureEvent(it to gestureEvent)
            if (handled) res = true
        }
        return res
    }

    private fun onGestureEvent(pair: Pair<Edge, Gesture>): Boolean {
        val gesture = EdgeGesture.newBuilder()
            .setEdge(pair.first)
            .setGesture(pair.second)
            .build()
        if (shouldFireGesture(gesture)) {
            logger.w("Let's fire: $pair")
            shortx.fireEdgeGesture(gesture)
            return true
        } else return false
    }

    private fun shouldFireGesture(gesture: EdgeGesture): Boolean {
        return synchronized(fireableEdgeGestures) {
            fireableEdgeGestures.contains(gesture)
        }
    }

    fun shouldStealMotionEvent(input: MotionEvent): Boolean {
        // Check if intercept.
        val startTimeMillis = SystemClock.elapsedRealtimeNanos()
        val currentOrientation = context.resources.configuration.orientation
        val checkRects = if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            interceptMotionPortRects
        } else {
            interceptMotionLandRects
        }
        // onDebugBuild { logger.d("filterMotionEvent, checkRects: $checkRects") }
        val anyContains = checkRects.any { it.contains(input.x, input.y) }
        val timeTaken = SystemClock.elapsedRealtimeNanos() - startTimeMillis
        // logger.d("filterMotionEvent, check take time: $timeTaken(nano)")
        if (anyContains) {
            logger.logOnDebug("shouldStealMotionEvent, steal MotionEvent: $input")
            return true
        }
        return false
    }

    private fun updateShouldInterceptMotionEventRect() {
        val screenSize = shortx.getScreenSize()
        logger.i("updateShouldInterceptMotionEventRect, screenSize: $screenSize")
        val portRects = mutableListOf<RectF>()
        val landRects = mutableListOf<RectF>()

        shortx.getAllRules().filter { it.isEnabled }.forEach {
            it.factsList.forEach { protoFact ->
                val isEG = protoFact is_ EdgeGesture::class.java
                if (isEG) {
                    val eg = protoFact unpack_ EdgeGesture::class.java
                    if (!eg.isDisabled && eg.isIntercept) {
                        portRects.add(
                            eg.edge.getRect(
                                isPortrait = true,
                                screenWidth = screenSize.x.toFloat(),
                                screenHeight = screenSize.y.toFloat(),
                                areaSize = getGestureAreaWidthDp()._dp.toFloat(),
                                areaLengthScale = getGestureLengthScale()
                            )
                        )
                        landRects.add(
                            eg.edge.getRect(
                                isPortrait = false,
                                screenWidth = screenSize.x.toFloat(),
                                screenHeight = screenSize.y.toFloat(),
                                areaSize = getGestureAreaWidthDp()._dp.toFloat(),
                                areaLengthScale = getGestureLengthScale()
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