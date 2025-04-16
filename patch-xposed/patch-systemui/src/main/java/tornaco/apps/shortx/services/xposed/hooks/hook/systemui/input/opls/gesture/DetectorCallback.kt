package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.rule.DAObserver
import tornaco.apps.shortx.core.rule.RuleObserver
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger

interface DetectorCallback {
    fun handleGestureEvent(area: Array<Edge>, gestureEvent: Gesture): Boolean
    fun getSwipeDistanceThresholdDp(): Int
    fun getGestureAreaWidthDp(): Int
    fun getGestureLengthScale(): Float

    fun obsGestureSettingsChanged(obs: () -> Unit)
    fun obsConfigChanged(obs: () -> Unit)
}

class DetectorCallbackImpl : DetectorCallback {
    private val shortx by lazy { shortXManager }
    private val logger = Logger("DetectorCallbackImpl")

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

    init {
        shortx.registerGestureSettingsListener(object : ICallback.Stub() {
            override fun receive(data: ByteArrayWrapper?) {
                logger.w("GestureSettings changed.")
                readAllSettings()
                afterGestureSettingsChangedObs()
            }
        })

        shortx.registerRuleObs(object : RuleObserver() {
            override fun onRuleChanged(id: String) {
                super.onRuleChanged(id)
                readGestureFireState()
            }

            override fun onRuleSetChanged(id: String) {
                super.onRuleSetChanged(id)
                readGestureFireState()
            }
        })

        shortx.registerDAObs(object : DAObserver() {
            override fun onDAChanged(id: String) {
                super.onDAChanged(id)
                readGestureFireState()
            }

            override fun onDASetChanged(id: String) {
                super.onDASetChanged(id)
                readGestureFireState()
            }
        })

        readAllSettings()
        readGestureFireState()
    }

    private fun readGestureFireState() {
        synchronized(fireableEdgeGestures) {
            fireableEdgeGestures.clear()
            fireableEdgeGestures.addAll(
                allEdgeGestures.filter { gesture ->
                    shortx.shouldFireEdgeGesture(gesture).also {
                        logger.d("shouldFireEdgeGesture? $gesture $it")
                    }
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
}