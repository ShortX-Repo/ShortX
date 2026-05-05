package tornaco.apps.shortx.services.gesture.bpok.gesture

import android.content.Context
import android.os.SystemClock
import android.view.InputEvent
import android.view.MotionEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.IForceDisableListener
import tornaco.apps.shortx.core.IRuleFeatureStateListener
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.action.ContinuousAdjustBrightness
import tornaco.apps.shortx.core.proto.action.ContinuousGesturePointer
import tornaco.apps.shortx.core.proto.action.ContinuousAdjustVolume
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.AppPkgList
import tornaco.apps.shortx.core.proto.fact.CornerSwipeToCenter
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.EdgeGestureStep
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.ScreenCorner
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
import tornaco.apps.shortx.services.gesture.bpok.gesture.handlers.BrightnessContinuousHandler
import tornaco.apps.shortx.services.gesture.bpok.gesture.handlers.GesturePointerContinuousHandler
import tornaco.apps.shortx.services.gesture.bpok.gesture.handlers.VolumeContinuousHandler
import kotlin.math.roundToInt

class GestureBridgeImpl(private val context: Context) : GestureBridge {
    private val shortx by lazy { shortXManager }
    private val logger = Logger("GestureBridge")

    companion object {
        private const val DIAG_PREFIX = "[SX_EDGE_DIAG]"
    }

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    private var swipeDistanceThresholdDp: Int = 24
    private var gestureAreaWidthDp: Int = 28
    private var gestureLengthScale: Float = 1f
    private var cornerGestureRegionRadiusRatio: Float =
        CornerSwipeToCenterGeometry.DEFAULT_CORNER_REGION_RADIUS_RATIO
    private var cornerSwipeMinDistanceDp: Int = 72
    private var edgeGestureSequenceHoverDurationMs: Int = 300
    private var edgeGestureSequenceHoverRadiusDp: Int = 80
    private var edgeGestureSequenceChildSwipeMinDistanceDp: Int = 72
    private var edgeGestureSequenceChildSwipeMaxAngleDeviationDeg: Int = 35
    private var edgeGestureSequenceChildSwipeTriggerOnRelease: Boolean = false

    var isGestureRecording: Boolean = false
        private set

    private var afterGestureSettingsChangedObs: () -> Unit = {}
    private var afterConfigChangedObs: () -> Unit = {}
    private var afterUserDataChangedObs: () -> Unit = {}

    override val enabledEdgeGestures = mutableSetOf<EdgeGesture>()
    override val enabledCornerSwipeToCenterFacts = mutableSetOf<CornerSwipeToCenter>()
    private val continuousGestureBindings = mutableListOf<ContinuousGestureBinding>()

    private val requiredFrontApps = mutableSetOf<AppPkg>()
    private val currentFrontApps = mutableSetOf<AppPkg>()
    private var requiredFrontAppAny: Boolean = false
    private var requiredFrontAppMatchQuick: Boolean = false

    val shouldDetectMotion
        get() = !isRuleFeatureDisabled
                && isGestureEnabledQuick
                && (enabledEdgeGestures.isNotEmpty() || enabledCornerSwipeToCenterFacts.isNotEmpty())
                && (requiredFrontAppAny || requiredFrontAppMatchQuick)

    private var isGestureEnabledQuick = false
    private var isRuleFeatureDisabled = false

    init {
        shortx.registerGestureSettingsListener(object : ICallback.Stub() {
            override fun receive(data: ByteArrayWrapper?) {
                logger.w("GestureSettings changed.")
                readAllSettings()
                updateQuickCheckDataOnRuleChange()
                afterUserDataChangedObs()
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
                afterUserDataChangedObs()
            }
        })

        shortx.registerDAObs(object : DAObserver() {
            override fun onDAChanged(id: String) {
                super.onDAChanged(id)
                updateQuickCheckDataOnRuleChange()
                afterUserDataChangedObs()
            }
        })

        shortx.registerPkgSetObs(object : IPkgSetObserver.Stub() {
            override fun onAddOrUpdate(id: String?) {
                updateQuickCheckDataOnRuleChange()
                afterUserDataChangedObs()
            }

            override fun onDelete(id: String?) {
                updateQuickCheckDataOnRuleChange()
                afterUserDataChangedObs()
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

        shortx.registerForceDisableListener(object : IForceDisableListener.Stub() {
            override fun onForceDisableStateChanged(isDisabled: Boolean) {
                logger.w("ForceDisable state changed: $isDisabled")
            }
        })

        shortx.registerRuleFeatureStateListener(object : IRuleFeatureStateListener.Stub() {
            override fun onRuleFeatureStateChanged(isEnabled: Boolean) {
                isRuleFeatureDisabled = !isEnabled
                logger.w("Rule feature state changed: enabled=$isEnabled")
            }
        })

        val initDataFunc = {
            readAllSettings()
            updateQuickCheckDataOnRuleChange()
            afterUserDataChangedObs()
        }

        runCatching {
            ShortXManagerNative.shortX?.registerSystemReadyListener(object : ICallback.Stub() {
                override fun receive(p0: ByteArrayWrapper?) {
                    logger.w("System ready.")
                    scope.launch {
                        // Wait PkgPool.
                        delay(6000)
                        initDataFunc()
                    }
                }
            })
        }.onFailure {
            logger.e(it, "registerSystemReadyListener")
        }

        initDataFunc()
    }

    fun shouldInstallMonitor(): Boolean {
        return GlobalSettings.isEdgeGestureEnabled
    }

    private fun readAllSettings() {
        swipeDistanceThresholdDp = shortx.getSwipeDistanceThresholdDp()
        gestureAreaWidthDp = shortx.getGestureAreaSize()
        gestureLengthScale = shortx.getSwipeLengthScale()
        cornerGestureRegionRadiusRatio =
            shortx.getCornerGestureRegionRadiusRatioPercent().coerceIn(6, 40) / 100f
        cornerSwipeMinDistanceDp = shortx.getCornerGestureMinSwipeDistanceDp().coerceIn(24, 200)
        edgeGestureSequenceHoverDurationMs = shortx.getEdgeGestureSequenceHoverDurationMs()
        edgeGestureSequenceHoverRadiusDp = shortx.getEdgeGestureSequenceHoverRadiusDp()
        edgeGestureSequenceChildSwipeMinDistanceDp =
            shortx.getEdgeGestureSequenceChildSwipeMinDistanceDp()
        edgeGestureSequenceChildSwipeMaxAngleDeviationDeg =
            shortx.getEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg()
        edgeGestureSequenceChildSwipeTriggerOnRelease =
            shortx.isEdgeGestureSequenceChildSwipeTriggerOnRelease()
        isGestureRecording = shortx.isGestureRecording()
        GlobalSettings.isEdgeGestureEnabled =
            shortx.isAppFeatureEnabled(AppFeature.AppFeature_EdgeGesture.number)
        isGestureEnabledQuick = GlobalSettings.isEdgeGestureEnabled

        // 读取 forceDisable 和 ruleFeature 状态
        isRuleFeatureDisabled = !shortx.isRuleFeatureEnabled()

        logger.w("swipeDistanceThresholdDp: $swipeDistanceThresholdDp")
        logger.w("gestureAreaWidthDp: $gestureAreaWidthDp")
        logger.w("gestureLengthScale: $gestureLengthScale")
        logger.w("cornerGestureRegionRadiusRatio: $cornerGestureRegionRadiusRatio")
        logger.w("cornerSwipeMinDistanceDp: $cornerSwipeMinDistanceDp")
        logger.w("edgeGestureSequenceHoverDurationMs: $edgeGestureSequenceHoverDurationMs")
        logger.w("edgeGestureSequenceHoverRadiusDp: $edgeGestureSequenceHoverRadiusDp")
        logger.w("edgeGestureSequenceChildSwipeMinDistanceDp: $edgeGestureSequenceChildSwipeMinDistanceDp")
        logger.w("edgeGestureSequenceChildSwipeMaxAngleDeviationDeg: $edgeGestureSequenceChildSwipeMaxAngleDeviationDeg")
        logger.w("edgeGestureSequenceChildSwipeTriggerOnRelease: $edgeGestureSequenceChildSwipeTriggerOnRelease")
        logger.w("isGestureRecording: $isGestureRecording")
        logger.logOnDebug("isGestureEnabledQuick: $isGestureEnabledQuick")
        logger.logOnDebug("isEdgeGestureEnabled: ${GlobalSettings.isEdgeGestureEnabled}")
        logger.logOnDebug("isRuleFeatureDisabled: $isRuleFeatureDisabled")
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

    override fun getCornerGestureRegionRadiusRatio(): Float {
        return cornerGestureRegionRadiusRatio
    }

    override fun getCornerSwipeMinDistanceDp(): Int {
        return cornerSwipeMinDistanceDp
    }

    override fun getEdgeGestureSequenceHoverDurationMs(): Int {
        return edgeGestureSequenceHoverDurationMs
    }

    override fun getEdgeGestureSequenceHoverRadiusDp(): Int {
        return edgeGestureSequenceHoverRadiusDp
    }

    override fun getEdgeGestureSequenceChildSwipeMinDistanceDp(): Int {
        return edgeGestureSequenceChildSwipeMinDistanceDp
    }

    override fun getEdgeGestureSequenceChildSwipeMaxAngleDeviationDeg(): Int {
        return edgeGestureSequenceChildSwipeMaxAngleDeviationDeg
    }

    override fun isEdgeGestureSequenceChildSwipeTriggerOnRelease(): Boolean {
        return edgeGestureSequenceChildSwipeTriggerOnRelease
    }

    override fun obsGestureSettingsChanged(obs: () -> Unit) {
        afterGestureSettingsChangedObs = obs
    }

    override fun obsConfigChanged(obs: () -> Unit) {
        afterConfigChangedObs = obs
    }

    override fun obsUserDataChanged(obs: () -> Unit) {
        afterUserDataChangedObs = obs
    }

    override fun findContinuousGestureBinding(
        areas: List<DetectedArea>,
        gestureEvent: Gesture,
    ): ContinuousGestureBinding? {
        return continuousGestureBindings.firstOrNull { binding ->
            !binding.rule.isDisabled &&
                    binding.rule.childStepsCount == 0 &&
                    binding.rule.gesture == gestureEvent &&
                    areas.any { area -> areaMatchesRule(area, binding.rule) }
        }
    }

    override fun handleEdgeGestureEvent(
        areas: List<DetectedArea>,
        gestureEvent: Gesture,
        downX: Float,
        downY: Float,
    ): Boolean {
        var res = false
        areas.forEach {
            val handled = onEdgeGestureEvent(it, gestureEvent, downX, downY)
            if (handled) res = true
        }
        return res
    }

    override fun findMatchingEdgeGestureSequences(
        areas: List<DetectedArea>,
        gestureEvent: Gesture,
    ): List<EdgeGestureSequenceCandidate> {
        val allSequenceRules = enabledEdgeGestures.filter {
            !it.isDisabled && it.gesture == gestureEvent && it.childStepsCount > 0
        }
        val matched = areas.flatMap { area ->
            enabledEdgeGestures.filter { enabledGesture ->
                !enabledGesture.isDisabled &&
                        enabledGesture.gesture == gestureEvent &&
                        enabledGesture.childStepsCount > 0 &&
                        areaMatchesRule(area, enabledGesture)
            }.map {
                EdgeGestureSequenceCandidate(area = area, rule = it)
            }
        }
        logger.logOnDebug(
            "$DIAG_PREFIX findMatchingEdgeGestureSequences: gesture=$gestureEvent, areas=$areas, totalSequenceRules=${allSequenceRules.size}, matched=${matched.map { candidate -> "${candidate.area}:${candidate.rule.childStepsList.map { it.type }}" }}"
        )
        return matched
    }

    override fun fireEdgeGestureSequence(
        area: DetectedArea,
        gesture: Gesture,
        childSteps: List<EdgeGestureStep>,
        downX: Float,
        downY: Float,
    ): Boolean {
        val data = EdgeGesture.newBuilder()
            .setGesture(gesture)
            .addAllChildSteps(childSteps)
            .apply {
                if (area is DetectedArea.PredefinedEdge) {
                    setEdge(area.edge)
                    setIsTriggerCustom(false)
                } else if (area is DetectedArea.CustomRect) {
                    setCustomRect(area.rect)
                    setIsTriggerCustom(true)
                }
            }
            .setDownX(downX.roundToInt())
            .setDownY(downY.roundToInt())
            .build()
        logger.d("$DIAG_PREFIX fireEdgeGestureSequence: area=$area, gesture=$gesture, childSteps=${childSteps.map { it.type }}, down=($downX,$downY)")
        shortx.fireEdgeGesture(data)
        return true
    }

    override fun handleCornerSwipeToCenterEvent(
        corner: ScreenCorner,
        downX: Float,
        downY: Float,
    ): Boolean {
        val shouldFire = enabledCornerSwipeToCenterFacts.any {
            !it.isDisabled && it.corner == corner
        }
        if (!shouldFire) return false

        val data = CornerSwipeToCenter.newBuilder()
            .setCorner(corner)
            .setDownX(downX.roundToInt())
            .setDownY(downY.roundToInt())
            .build()
        shortx.fireCornerSwipeToCenter(data)
        return true
    }

    private fun onEdgeGestureEvent(
        area: DetectedArea,
        gesture: Gesture,
        downX: Float,
        downY: Float,
    ): Boolean {
        if (shouldFireEdgeGesture(area, gesture)) {
            logger.d("Let's fire: $area $gesture")
            val data = EdgeGesture.newBuilder()
                .setGesture(gesture)
                .apply {
                    if (area is DetectedArea.PredefinedEdge) {
                        setEdge(area.edge)
                        setIsTriggerCustom(false)
                    } else if (area is DetectedArea.CustomRect) {
                        setCustomRect(area.rect)
                        setIsTriggerCustom(true)
                    }
                }
                .setDownX(downX.roundToInt())
                .setDownY(downY.roundToInt())
                .build()
            shortx.fireEdgeGesture(data)
            return true
        } else return false
    }

    private fun shouldFireEdgeGesture(area: DetectedArea, gesture: Gesture): Boolean {
        // 检查enabledEdgeGestures中是否包含匹配的EdgeGesture
        return enabledEdgeGestures.any { enabledGesture ->
            if (enabledGesture.childStepsCount > 0) {
                return@any false
            }
            // 首先检查gesture是否匹配，不匹配则提前返回false
            if (enabledGesture.gesture != gesture) {
                return@any false
            }

            // gesture匹配后再检查区域是否匹配
            areaMatchesRule(area, enabledGesture)
        }
    }

    private fun areaMatchesRule(area: DetectedArea, rule: EdgeGesture): Boolean {
        return when (area) {
            is DetectedArea.PredefinedEdge -> {
                rule.edge == area.edge &&
                        (!rule.hasCustomRect() || rule.customRect == tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance())
            }

            is DetectedArea.CustomRect -> {
                rule.hasCustomRect() &&
                        rule.customRect != tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance() &&
                        rule.customRect == area.rect
            }
        }
    }

    private fun updateQuickCheckDataOnRuleChange() {
        val screenSize = shortx.getScreenSize()
        logger.i("updateQuickCheckDataOnRuleChange, screenSize: $screenSize")

        enabledEdgeGestures.clear()
        enabledCornerSwipeToCenterFacts.clear()
        continuousGestureBindings.clear()
        requiredFrontApps.clear()

        val tempEnabledFacts = mutableSetOf<EdgeGesture>()
        val tempEnabledCornerFacts = mutableSetOf<CornerSwipeToCenter>()
        val tempContinuousGestureBindings = mutableListOf<ContinuousGestureBinding>()
        val tempRequiredApps = mutableSetOf<AppPkg>()

        // Reset.
        requiredFrontAppAny = false

        shortx.getAllRules().filter { it.isEnabled }.forEach { rule ->
            rule.factsList.forEach { protoFact ->
                val isEG = protoFact is_ EdgeGesture::class.java
                if (isEG) {
                    val eg = protoFact unpack_ EdgeGesture::class.java
                    if (!eg.isDisabled) {
                        tempEnabledFacts.add(eg)
                        val continuousPayload = rule.actionsList.firstNotNullOfOrNull { actionProto ->
                            when {
                                actionProto is_ ContinuousAdjustBrightness::class.java -> {
                                    val action = actionProto unpack_ ContinuousAdjustBrightness::class.java
                                    ContinuousGestureActionPayload.Brightness(
                                        sensitivityScale = action.sensitivityScale.takeIf { it > 0f } ?: 1f,
                                        minValue = action.minValue.coerceAtLeast(0),
                                        maxValue = action.maxValue.takeIf { it > action.minValue } ?: 255,
                                    )
                                }

                                actionProto is_ ContinuousGesturePointer::class.java -> {
                                    val action = actionProto unpack_ ContinuousGesturePointer::class.java
                                    ContinuousGestureActionPayload.Pointer(
                                        sensitivityScale = action.sensitivityScale.takeIf { it > 0f } ?: 1f,
                                        clickOnRelease = action.clickOnRelease,
                                    )
                                }

                                actionProto is_ ContinuousAdjustVolume::class.java -> {
                                    val action = actionProto unpack_ ContinuousAdjustVolume::class.java
                                    ContinuousGestureActionPayload.Volume(
                                        streamType = action.streamType,
                                        sensitivityScale = action.sensitivityScale.takeIf { it > 0f } ?: 1f,
                                    )
                                }

                                else -> null
                            }
                        }
                        if (continuousPayload != null) {
                            tempContinuousGestureBindings.add(
                                ContinuousGestureBinding(
                                    rule = eg,
                                    payload = continuousPayload,
                                )
                            )
                        }

                        // For all.
                        if (eg.appsList.isEmpty() && eg.pkgSetsList.isEmpty()) {
                            requiredFrontAppAny = true
                            requiredFrontAppMatchQuick = true
                        } else {
                            tempRequiredApps.addAll(eg.appsList)
                            val pkgSetApps = eg.pkgSetsList.flatMap {
                                shortx.getPkgSetByLabel(it)?.appPkgsList ?: emptyList()
                            }
                            tempRequiredApps.addAll(pkgSetApps)
                        }
                    }
                    return@forEach
                }

                val isCornerSwipeToCenter = protoFact is_ CornerSwipeToCenter::class.java
                if (isCornerSwipeToCenter) {
                    val cornerSwipe = protoFact unpack_ CornerSwipeToCenter::class.java
                    if (!cornerSwipe.isDisabled) {
                        tempEnabledCornerFacts.add(cornerSwipe)
                        requiredFrontAppAny = true
                        requiredFrontAppMatchQuick = true
                    }
                }
            }
        }

        // 一次性更新所有集合
        enabledEdgeGestures.addAll(tempEnabledFacts)
        enabledCornerSwipeToCenterFacts.addAll(tempEnabledCornerFacts)
        continuousGestureBindings.addAll(tempContinuousGestureBindings)
        requiredFrontApps.addAll(tempRequiredApps)

        checkFrontAppPkg("updateQuickCheckDataOnRuleChange")

        logger.d("updateQuickCheckDataOnRuleChange, enabledEdgeGestures: $enabledEdgeGestures")
        logger.d("updateQuickCheckDataOnRuleChange, enabledCornerSwipeToCenterFacts: $enabledCornerSwipeToCenterFacts")
        logger.d("updateQuickCheckDataOnRuleChange, continuousGestureBindings: $continuousGestureBindings")
        logger.d("updateQuickCheckDataOnRuleChange, requiredFrontApps: $requiredFrontApps")
        logger.d("updateQuickCheckDataOnRuleChange, requiredFrontAppMatchQuick: $requiredFrontAppMatchQuick")
        logger.d("updateQuickCheckDataOnRuleChange, requiredFrontAppAny: $requiredFrontAppAny")

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

    override fun createContinuousActionHandler(payload: ContinuousGestureActionPayload): ContinuousActionHandler? {
        return when (payload) {
            is ContinuousGestureActionPayload.Brightness -> BrightnessContinuousHandler(
                context = context,
                sensitivityScale = payload.sensitivityScale,
                minValue = payload.minValue,
                maxValue = payload.maxValue,
            )

            is ContinuousGestureActionPayload.Pointer -> GesturePointerContinuousHandler(
                sensitivityScale = payload.sensitivityScale,
                clickOnRelease = payload.clickOnRelease,
            )

            is ContinuousGestureActionPayload.Volume -> VolumeContinuousHandler(
                context = context,
                streamType = payload.streamType,
                sensitivityScale = payload.sensitivityScale,
            )
        }
    }
}