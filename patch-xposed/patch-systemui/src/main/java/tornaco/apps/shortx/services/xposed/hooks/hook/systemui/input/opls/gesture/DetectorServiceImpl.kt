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

    override val enabledEdgeGestures = mutableSetOf<EdgeGesture>()
    private val interceptMotionPortRects = mutableListOf<RectF>()
    private val interceptMotionLandRects = mutableListOf<RectF>()

    private val requiredFrontApps = mutableSetOf<AppPkg>()
    private val currentFrontApps = mutableSetOf<AppPkg>()
    private var requiredFrontAppAny: Boolean = false
    private var requiredFrontAppMatchQuick: Boolean = false

    val shouldDetectMotion
        get() = isGestureEnabledQuick
                && enabledEdgeGestures.isNotEmpty()
                && (requiredFrontAppAny || requiredFrontAppMatchQuick)

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
                updateQuickCheckDataOnRuleChange()
            }
        })

        shortx.registerDAObs(object : DAObserver() {
            override fun onDAChanged(id: String) {
                super.onDAChanged(id)
                updateQuickCheckDataOnRuleChange()
            }
        })

        shortx.registerPkgSetObs(object : IPkgSetObserver.Stub() {
            override fun onAddOrUpdate(id: String?) {
                updateQuickCheckDataOnRuleChange()
            }

            override fun onDelete(id: String?) {
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
        updateQuickCheckDataOnRuleChange()
    }

    fun shouldInstallMonitor(): Boolean {
        return GlobalSettings.isEdgeGestureEnabled
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

    override fun handleEdgeGestureEvent(areas: List<DetectedArea>, gestureEvent: Gesture): Boolean {
        var res = false
        areas.forEach {
            val handled = onEdgeGestureEvent(it, gestureEvent)
            if (handled) res = true
        }
        return res
    }

    private fun onEdgeGestureEvent(area: DetectedArea, gesture: Gesture): Boolean {
        if (shouldFireEdgeGesture(area, gesture)) {
            logger.d("Let's fire: $area $gesture")
            val data = EdgeGesture.newBuilder()
                .setGesture(gesture)
                .apply {
                    if (area is DetectedArea.PredefinedEdge) {
                        setEdge(area.edge)
                    } else if (area is DetectedArea.CustomRect) {
                        setCustomRect(area.rect)
                    }
                }
                .build()
            shortx.fireEdgeGesture(data)
            return true
        } else return false
    }

    private fun shouldFireEdgeGesture(area: DetectedArea, gesture: Gesture): Boolean {
        return true
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
        val enabledFacts = mutableSetOf<EdgeGesture>()

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
                        enabledFacts.add(eg)

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

        logger.d("updateQuickCheckDataOnRuleChange, enabledFacts: $enabledFacts")
        logger.d("updateQuickCheckDataOnRuleChange, portRects: $portRects")
        logger.d("updateQuickCheckDataOnRuleChange, landRects: $landRects")
        logger.d("updateQuickCheckDataOnRuleChange, requiredFrontAppAny: $requiredFrontAppAny")
        logger.d("updateQuickCheckDataOnRuleChange, requiredFrontAppMatchQuick: $requiredFrontAppMatchQuick")
        logger.d("updateQuickCheckDataOnRuleChange, requiredApps: $requiredApps")

        this.enabledEdgeGestures.clear()
        this.enabledEdgeGestures.addAll(enabledFacts)

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
            requiredFrontAppAny || (currentFrontApps.isNotEmpty() && requiredFrontApps.any {
                currentFrontApps.contains(it)
            })
    }

    fun reportEvent(event: InputEvent) {
        if (isGestureRecording && event is MotionEvent) {
            shortx.onMotionEvent(event)
        }
    }
}