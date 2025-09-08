package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.content.Context
import android.content.res.Configuration
import android.graphics.RectF
import android.os.SystemClock
import android.view.InputEvent
import android.view.MotionEvent
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.AppPkgList
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.feature.AppFeature
import tornaco.apps.shortx.core.rule.DAObserver
import tornaco.apps.shortx.core.rule.IPkgSetObserver
import tornaco.apps.shortx.core.rule.RuleObserver
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.data.GlobalSettings

interface DetectorService {
    fun handleEdgeGestureEvent(area: Array<Edge>, gestureEvent: Gesture): Boolean
    fun handleScreenGestureEvent(downX: Float, downY: Float, gestureEvent: Gesture)
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

    var isGestureRecording: Boolean = false
        private set

    private var afterGestureSettingsChangedObs: () -> Unit = {}
    private var afterConfigChangedObs: () -> Unit = {}

    private val allEdgeGestures = Gesture.entries
        .filter { it != Gesture.UNRECOGNIZED }
        .flatMap { gesture ->
            Edge.entries
                .filter { it != Edge.UNRECOGNIZED }
                .map { edge ->
                    edge to gesture
                }
        }

    private val allScreenGestures = Gesture.entries
        .filter { it != Gesture.UNRECOGNIZED }

    private val fireableEdgeGestures = mutableSetOf<Pair<Edge, Gesture>>()
    private val fireableScreenGestures = mutableSetOf<Gesture>()

    private val interceptMotionPortRects = mutableListOf<RectF>()
    private val interceptMotionLandRects = mutableListOf<RectF>()

    private val requiredFrontApps = mutableSetOf<AppPkg>()
    private val currentFrontApps = mutableSetOf<AppPkg>()
    private var requiredFrontAppAny: Boolean = false
    private var requiredFrontAppMatchQuick: Boolean = false

    val shouldDetectMotion
        get() = isGestureEnabledQuick
                && (fireableScreenGestures.isNotEmpty() || (fireableEdgeGestures.isNotEmpty() && requiredFrontAppMatchQuick))

    private var isGestureEnabledQuick = false

    init {
        shortx.registerGestureSettingsListener(object : ICallback.Stub() {
            override fun receive(data: ByteArrayWrapper?) {
                logger.w("GestureSettings changed.")
                readAllSettings()
                updateQuickCheckDataOnRuleChange()
                afterGestureSettingsChangedObs()
            }
        })

        shortx.registerRecorderStateListener(object : ICallback.Stub() {
            override fun receive(data: ByteArrayWrapper?) {
                logger.w("RecorderState changed.")
                isGestureRecording = shortx.isGestureRecording()
                logger.w("RecorderState isGestureRecording: $isGestureRecording")
            }
        })

        shortx.registerRuleObs(object : RuleObserver() {
            override fun onRuleChanged(id: String) {
                super.onRuleChanged(id)
                readGestureFireState("Rule changed")
                updateQuickCheckDataOnRuleChange()
            }
        })

        shortx.registerDAObs(object : DAObserver() {
            override fun onDAChanged(id: String) {
                super.onDAChanged(id)
                readGestureFireState("DA changed")
                updateQuickCheckDataOnRuleChange()
            }
        })

        shortx.registerPkgSetObs(object : IPkgSetObserver.Stub() {
            override fun onAddOrUpdate(id: String?) {
                readGestureFireState("PkgSet changed")
                updateQuickCheckDataOnRuleChange()
            }

            override fun onDelete(id: String?) {
                readGestureFireState("PkgSet changed")
                updateQuickCheckDataOnRuleChange()
            }
        })

        shortx.registerFrontAppChangeListener(object : ICallback.Stub() {
            override fun receive(data: ByteArrayWrapper) {
                logger.w("FrontAppChange.")
                if (requiredFrontAppAny) {
                    requiredFrontAppMatchQuick = true
                } else {
                    kotlin.runCatching {
                        val appPkgList = AppPkgList.parseFrom(data.byteData)
                        synchronized(currentFrontApps) {
                            currentFrontApps.clear()
                            currentFrontApps.addAll(appPkgList.appPkgList)
                            checkFrontAppPkg("FrontAppChange")
                        }
                        logger.logOnDebug("currentFrontApps: $currentFrontApps")
                    }.onFailure {
                        logger.e(it, "FrontAppChange received.")
                    }
                }
                logger.w("requiredFrontAppMatchQuick - $requiredFrontAppMatchQuick")
            }
        })

        readAllSettings()
        readGestureFireState("Init")
        updateQuickCheckDataOnRuleChange()
    }

    fun shouldInstallMonitor(): Boolean {
        return GlobalSettings.isEdgeGestureEnabled
    }

    private fun readGestureFireState(reason: String) {
        logger.d("readGestureFireState, reason: $reason")
        synchronized(fireableEdgeGestures) {
            fireableEdgeGestures.clear()
            fireableEdgeGestures.addAll(
                allEdgeGestures.filter { eg ->
                    shortx.shouldFireEdgeGesture(eg.first.number, eg.second.number)
                }
            )
            logger.w("fireableEdgeGestures: ${fireableEdgeGestures.joinToString()}")
        }

        synchronized(fireableScreenGestures) {
            fireableScreenGestures.clear()
            fireableScreenGestures.addAll(
                allScreenGestures.filter { gesture ->
                    // -1 means we check. @FactMatcherImpl
                    shortx.shouldFireScreenGesture(gesture.number, -1, -1)
                }
            )
            logger.w(
                "fireableScreenGestures: ${
                    fireableScreenGestures.joinToString { it.name }
                }"
            )
        }
    }

    private fun readAllSettings() {
        swipeDistanceThresholdDp = shortx.getSwipeDistanceThresholdDp()
        gestureAreaWidthDp = shortx.getGestureAreaSize()
        gestureLengthScale = shortx.getSwipeLengthScale()
        isGestureRecording = shortx.isGestureRecording()
        GlobalSettings.isEdgeGestureEnabled =
            shortx.isAppFeatureEnabled(AppFeature.AppFeature_EdgeGesture.number)
        isGestureEnabledQuick = GlobalSettings.isEdgeGestureEnabled

        logger.w("swipeDistanceThresholdDp: $swipeDistanceThresholdDp")
        logger.w("gestureAreaWidthDp: $gestureAreaWidthDp")
        logger.w("gestureLengthScale: $gestureLengthScale")
        logger.w("isGestureRecording: $isGestureRecording")
        logger.logOnDebug("isGestureEnabledQuick: $isGestureEnabledQuick")
        logger.logOnDebug("isEdgeGestureEnabled: ${GlobalSettings.isEdgeGestureEnabled}")
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

    override fun handleEdgeGestureEvent(area: Array<Edge>, gestureEvent: Gesture): Boolean {
        var res = false
        area.forEach {
            val handled = onEdgeGestureEvent(it to gestureEvent)
            if (handled) res = true
        }
        return res
    }

    override fun handleScreenGestureEvent(downX: Float, downY: Float, gestureEvent: Gesture) {
        logger.v("handleScreenGestureEvent: $gestureEvent ($downX, $downY)")
        if (shouldFireScreenGesture(gestureEvent)) {
            logger.d("Let's fire: $gestureEvent")
            shortx.fireScreenGesture(gestureEvent.number, downX.toInt(), downY.toInt())
        }
    }

    private fun onEdgeGestureEvent(pair: Pair<Edge, Gesture>): Boolean {
        if (shouldFireEdgeGesture(pair)) {
            logger.d("Let's fire: $pair")
            shortx.fireEdgeGesture(pair.first.number, pair.second.number)
            return true
        } else return false
    }

    private fun shouldFireEdgeGesture(pair: Pair<Edge, Gesture>): Boolean {
        return synchronized(fireableEdgeGestures) {
            if (fireableEdgeGestures.isEmpty()) return false
            fireableEdgeGestures.contains(pair)
        }
    }

    private fun shouldFireScreenGesture(gesture: Gesture): Boolean {
        return synchronized(fireableScreenGestures) {
            if (fireableScreenGestures.isEmpty()) return false
            fireableScreenGestures.contains(gesture)
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

    private fun updateQuickCheckDataOnRuleChange() {
        val screenSize = shortx.getScreenSize()
        logger.i("updateQuickCheckDataOnRuleChange, screenSize: $screenSize")
        val portRects = mutableListOf<RectF>()
        val landRects = mutableListOf<RectF>()
        val requiredApps = mutableSetOf<AppPkg>()
        // Reset.
        requiredFrontAppAny = false

        shortx.getAllRules().filter { it.isEnabled }.forEach { rule ->
            rule.factsList.forEach { protoFact ->
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

                    if (!eg.isDisabled) {
                        // For all.
                        if (eg.appsList.isEmpty() && eg.pkgSetsList.isEmpty()) {
                            requiredFrontAppAny = true
                            requiredFrontAppMatchQuick = true
                        } else {
                            requiredApps.addAll(eg.appsList)
                            val pkgSetApps = eg.pkgSetsList.flatMap {
                                shortx.getPkgSetByLabel(it)?.appPkgsList ?: emptyList()
                            }
                            requiredApps.addAll(pkgSetApps)
                        }
                    }
                }
            }
        }

        logger.d("updateQuickCheckDataOnRuleChange, portRects: $portRects")
        logger.d("updateQuickCheckDataOnRuleChange, landRects: $landRects")
        logger.d("updateQuickCheckDataOnRuleChange, requiredFrontAppAny: $requiredFrontAppAny")
        logger.d("updateQuickCheckDataOnRuleChange, requiredFrontAppMatchQuick: $requiredFrontAppMatchQuick")
        logger.d("updateQuickCheckDataOnRuleChange, requiredApps: $requiredApps")

        this.interceptMotionLandRects.clear()
        this.interceptMotionLandRects.addAll(landRects)

        this.interceptMotionPortRects.clear()
        this.interceptMotionPortRects.addAll(portRects)

        this.requiredFrontApps.clear()
        this.requiredFrontApps.addAll(requiredApps)
        checkFrontAppPkg("updateQuickCheckDataOnRuleChange")
    }

    private fun checkFrontAppPkg(reason: String) {
        logger.d("checkFrontAppPkg: $reason")
        requiredFrontAppMatchQuick =
            requiredFrontAppAny || requiredFrontApps.isEmpty() || (currentFrontApps.isNotEmpty() && requiredFrontApps.any {
                currentFrontApps.contains(it)
            })
    }

    fun reportEvent(event: InputEvent) {
        if (isGestureRecording && event is MotionEvent) {
            shortx.onMotionEvent(event)
        }
    }
}