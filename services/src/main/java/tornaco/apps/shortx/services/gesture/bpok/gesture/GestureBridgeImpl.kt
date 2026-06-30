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
import tornaco.apps.shortx.core.proto.action.ShowPieMenu
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.AppPkgList
import tornaco.apps.shortx.core.proto.fact.CornerSwipeToCenter
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.EdgeGestureStep
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.ScreenCorner
import tornaco.apps.shortx.core.proto.fact.normalizedForSingleChildStep
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
import tornaco.apps.shortx.services.gesture.bpok.gesture.handlers.PieMenuContinuousHandler
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
    private val areaGestureProfiles = linkedMapOf<DetectedArea, AreaGestureProfile>()

    private val requiredFrontApps = mutableSetOf<AppPkg>()
    private val currentFrontApps = mutableSetOf<AppPkg>()
    private var requiredFrontAppAny: Boolean = false
    private var requiredFrontAppMatchQuick: Boolean = false

    private var isIMEShown = false

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

        shortx.registerIMEVisibilityChangeListener(object : ICallback.Stub() {
            override fun receive(data: ByteArrayWrapper) {
                logger.w("IMEVisibilityChange.")
                kotlin.runCatching {
                    val isShown = data.byteData?.let {
                        tornaco.apps.shortx.core.proto.fact.IMEVisibilityChange.parseFrom(it).isShown
                    } ?: false
                    isIMEShown = isShown
                    logger.logOnDebug("isIMEShown: $isIMEShown")
                }.onFailure {
                    logger.e(it, "IMEVisibilityChange received.")
                }
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
        coordinateMode: PredefinedEdgeCoordinateMode?,
    ): ContinuousGestureBinding? {
        return continuousGestureBindings.firstOrNull { binding ->
            !binding.rule.isDisabled &&
                    binding.rule.childStepsCount == 0 &&
                    binding.rule.gesture == gestureEvent &&
                    (!gestureEvent.requiresDirectionalCoordinateMode() ||
                            (coordinateMode != null && binding.rule.expectedDirectionCoordinateMode() == coordinateMode)) &&
                    !(binding.rule.hasDisableWhenIME() && binding.rule.disableWhenIME && isIMEShown) &&
                    areas.any { area -> matchedTriggerArea(area, binding.rule) != null }
        }
    }

    override fun getAreaGestureProfile(areas: List<DetectedArea>): AreaGestureProfile {
        if (areas.isEmpty()) return AreaGestureProfile(false, false, false, false)

        var hasAnyTrigger = false
        var hasTapTrigger = false
        var hasLongPressTrigger = false
        var hasSwipeTrigger = false

        areas.forEach { area ->
            val profile = areaProfileForDetectedArea(area) ?: return@forEach
            hasAnyTrigger = hasAnyTrigger || profile.hasAnyTrigger
            hasTapTrigger = hasTapTrigger || profile.hasTapTrigger
            hasLongPressTrigger = hasLongPressTrigger || profile.hasLongPressTrigger
            hasSwipeTrigger = hasSwipeTrigger || profile.hasSwipeTrigger
        }

        return AreaGestureProfile(
            hasAnyTrigger = hasAnyTrigger,
            hasTapTrigger = hasTapTrigger,
            hasLongPressTrigger = hasLongPressTrigger,
            hasSwipeTrigger = hasSwipeTrigger,
        )
    }

    private fun areaProfileForDetectedArea(area: DetectedArea): AreaGestureProfile? {
        return when (area) {
            is DetectedArea.PredefinedEdge -> {
                areaGestureProfiles[area]
                    ?: run {
                        val parentEdge = getParentEdgeRegion(area.edge)
                        if (parentEdge != area.edge) {
                            areaGestureProfiles[DetectedArea.PredefinedEdge(parentEdge, area.coordinateMode)]
                        } else {
                            null
                        }
                    }
            }

            is DetectedArea.CustomRect -> areaGestureProfiles[area]
        }
    }

    override fun shouldDisableForIME(areas: List<DetectedArea>): Boolean {
        if (!isIMEShown) return false
        if (areas.isEmpty()) return false

        return enabledEdgeGestures.any { enabledGesture ->
            enabledGesture.hasDisableWhenIME() && enabledGesture.disableWhenIME &&
                    areas.any { area -> matchedTriggerArea(area, enabledGesture) != null }
        }
    }

    override fun handleEdgeGestureEvent(
        areas: List<DetectedArea>,
        gestureEvent: Gesture,
        downX: Float,
        downY: Float,
        coordinateMode: PredefinedEdgeCoordinateMode?,
    ): Boolean {
        val matches = enabledEdgeGestures.mapNotNull { enabledGesture ->
            if (enabledGesture.isDisabled || enabledGesture.childStepsCount > 0 || enabledGesture.gesture != gestureEvent) {
                return@mapNotNull null
            }
            if (gestureEvent.requiresDirectionalCoordinateMode() &&
                (coordinateMode == null || enabledGesture.expectedDirectionCoordinateMode() != coordinateMode)
            ) {
                return@mapNotNull null
            }

            if (enabledGesture.hasDisableWhenIME() && enabledGesture.disableWhenIME && isIMEShown) {
                logger.logOnDebug("Edge gesture disabled due to IME shown: $enabledGesture")
                return@mapNotNull null
            }

            val matchedArea = areas.firstNotNullOfOrNull { area ->
                matchedTriggerArea(area, enabledGesture)
            } ?: return@mapNotNull null

            enabledGesture to matchedArea
        }

        matches.forEach { (_, matchedArea) ->
            fireEdgeGestureEvent(matchedArea, gestureEvent, downX, downY)
        }
        return matches.isNotEmpty()
    }

    override fun findMatchingEdgeGestureSequences(
        areas: List<DetectedArea>,
        gestureEvent: Gesture,
        coordinateMode: PredefinedEdgeCoordinateMode?,
    ): List<EdgeGestureSequenceCandidate> {
        val allSequenceRules = enabledEdgeGestures.filter {
            !it.isDisabled &&
                    it.gesture == gestureEvent &&
                    it.childStepsCount > 0 &&
                    (!gestureEvent.requiresDirectionalCoordinateMode() ||
                            (coordinateMode != null && it.expectedDirectionCoordinateMode() == coordinateMode))
        }
        val matched = allSequenceRules.mapNotNull { enabledGesture ->
            if (enabledGesture.hasDisableWhenIME() && enabledGesture.disableWhenIME && isIMEShown) {
                logger.logOnDebug("Edge gesture sequence disabled due to IME shown: $enabledGesture")
                return@mapNotNull null
            }

            val matchedArea = areas.firstNotNullOfOrNull { area ->
                matchedTriggerArea(area, enabledGesture)
            } ?: return@mapNotNull null

            EdgeGestureSequenceCandidate(area = matchedArea, rule = enabledGesture)
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

    private fun fireEdgeGestureEvent(
        area: DetectedArea,
        gesture: Gesture,
        downX: Float,
        downY: Float,
    ) {
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
    }

    private fun matchedTriggerArea(area: DetectedArea, rule: EdgeGesture): DetectedArea? {
        return when (area) {
            is DetectedArea.PredefinedEdge -> {
                val hasCustomRect = rule.hasCustomRect() &&
                        rule.customRect != tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance()
                val expectedMode = if (rule.usesAbsolutePredefinedArea()) {
                    PredefinedEdgeCoordinateMode.AbsolutePortrait
                } else {
                    PredefinedEdgeCoordinateMode.RelativeToDisplay
                }
                if (hasCustomRect) {
                    null
                } else if (area.coordinateMode != expectedMode) {
                    null
                } else if (rule.edge == area.edge) {
                    area
                } else {
                    val parentEdge = getParentEdgeRegion(area.edge)
                    if (parentEdge != area.edge && rule.edge == parentEdge) {
                        DetectedArea.PredefinedEdge(parentEdge, area.coordinateMode)
                    } else {
                        null
                    }
                }
            }

            is DetectedArea.CustomRect -> {
                if (rule.hasCustomRect() &&
                    rule.customRect != tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance() &&
                    rule.customRect == area.rect
                ) {
                    area
                } else {
                    null
                }
            }
        }
    }

    private fun getParentEdgeRegion(edge: Edge): Edge {
        return when (edge) {
            Edge.LeftTop, Edge.LeftCenter, Edge.LeftBottom -> Edge.LeftAll
            Edge.RightTop, Edge.RightCenter, Edge.RightBottom -> Edge.RightAll
            Edge.TopLeft, Edge.TopCenter, Edge.TopRight -> Edge.TopAll
            Edge.BottomLeft, Edge.BottomCenter, Edge.BottomRight -> Edge.BottomAll
            Edge.LeftAll, Edge.RightAll, Edge.TopAll, Edge.BottomAll, Edge.UNRECOGNIZED -> edge
        }
    }

    private fun updateQuickCheckDataOnRuleChange() {
        val screenSize = shortx.getScreenSize()
        logger.i("updateQuickCheckDataOnRuleChange, screenSize: $screenSize")

        enabledEdgeGestures.clear()
        enabledCornerSwipeToCenterFacts.clear()
        continuousGestureBindings.clear()
        areaGestureProfiles.clear()
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
                    val eg = (protoFact unpack_ EdgeGesture::class.java)
                        .normalizedForSingleChildStep()
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
                                        releaseButton = when {
                                            action.releaseButton != tornaco.apps.shortx.core.proto.action.GesturePointerReleaseButton.GesturePointerReleaseButtonNone -> action.releaseButton
                                            action.clickOnRelease -> tornaco.apps.shortx.core.proto.action.GesturePointerReleaseButton.GesturePointerReleaseButtonPrimary
                                            else -> tornaco.apps.shortx.core.proto.action.GesturePointerReleaseButton.GesturePointerReleaseButtonNone
                                        },
                                        dragButton = action.dragButton,
                                        dragHoldDurationMs = action.dragHoldDurationMs.takeIf { it > 0 } ?: 350,
                                        scrollMode = action.scrollMode,
                                        scrollScale = action.scrollScale.takeIf { it > 0f } ?: 1f,
                                        cancelRegionRadiusDp = action.cancelRegionRadiusDp.takeIf { it > 0 } ?: 48,
                                        enableCenterActionSwitch = action.enableCenterActionSwitch,
                                    )
                                }

                                actionProto is_ ContinuousAdjustVolume::class.java -> {
                                    val action = actionProto unpack_ ContinuousAdjustVolume::class.java
                                    ContinuousGestureActionPayload.Volume(
                                        streamType = action.streamType,
                                        sensitivityScale = action.sensitivityScale.takeIf { it > 0f } ?: 1f,
                                    )
                                }

                                actionProto is_ ShowPieMenu::class.java -> {
                                    val action = actionProto unpack_ ShowPieMenu::class.java
                                    ContinuousGestureActionPayload.PieMenu(
                                        items = action.itemsList,
                                        pieColor = action.pieColorArgb.takeIf { it != 0 } ?: 0xFFCDD7DE.toInt(),
                                        iconColor = action.iconColorArgb.takeIf { it != 0 } ?: 0xFF1C1B1F.toInt(),
                                        scalePercent = action.scalePercent.takeIf { it > 0 } ?: 100,
                                        arcSpanDegrees = action.arcSpanDegrees.toFloat().takeIf { it > 0f } ?: 160f,
                                        ringCount = action.ringCount.takeIf { it > 0 } ?: 1,
                                        labelSizeSp = action.labelSizeSp.takeIf { it > 0 } ?: 11,
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
        areaGestureProfiles.putAll(buildAreaGestureProfiles(tempEnabledFacts))
        requiredFrontApps.addAll(tempRequiredApps)

        checkFrontAppPkg("updateQuickCheckDataOnRuleChange")

        logger.d("updateQuickCheckDataOnRuleChange, enabledEdgeGestures: $enabledEdgeGestures")
        logger.d("updateQuickCheckDataOnRuleChange, enabledCornerSwipeToCenterFacts: $enabledCornerSwipeToCenterFacts")
        logger.d("updateQuickCheckDataOnRuleChange, continuousGestureBindings: $continuousGestureBindings")
        logger.d("updateQuickCheckDataOnRuleChange, areaGestureProfiles: $areaGestureProfiles")
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
                context = context,
                sensitivityScale = payload.sensitivityScale,
                releaseButton = payload.releaseButton,
                dragButton = payload.dragButton,
                dragHoldDurationMs = payload.dragHoldDurationMs,
                scrollMode = payload.scrollMode,
                scrollScale = payload.scrollScale,
                cancelRegionRadiusDp = payload.cancelRegionRadiusDp,
                enableCenterActionSwitch = payload.enableCenterActionSwitch,
            )

            is ContinuousGestureActionPayload.Volume -> VolumeContinuousHandler(
                context = context,
                streamType = payload.streamType,
                sensitivityScale = payload.sensitivityScale,
            )

            is ContinuousGestureActionPayload.PieMenu -> PieMenuContinuousHandler(
                context = context,
                items = payload.items,
                pieColor = payload.pieColor,
                iconColor = payload.iconColor,
                scalePercent = payload.scalePercent,
                arcSpanDegrees = payload.arcSpanDegrees,
                ringCount = payload.ringCount,
                labelSizeSp = payload.labelSizeSp,
            )
        }
    }

    private fun buildAreaGestureProfiles(rules: Set<EdgeGesture>): Map<DetectedArea, AreaGestureProfile> {
        val profiles = linkedMapOf<DetectedArea, MutableAreaGestureProfile>()
        rules.forEach { rule ->
            val area = areaKeyForRule(rule)
            val profile = profiles.getOrPut(area) { MutableAreaGestureProfile() }
            profile.hasAnyTrigger = true
            when (rule.gesture) {
                Gesture.SingleTap,
                Gesture.DoubleTap -> profile.hasTapTrigger = true

                Gesture.LongTap -> profile.hasLongPressTrigger = true

                Gesture.SwipeFromLeft,
                Gesture.SwipeFromRight,
                Gesture.SwipeFromTop,
                Gesture.SwipeFromBottom -> profile.hasSwipeTrigger = true

                else -> Unit
            }
        }
        return profiles.mapValues { (_, profile) ->
            AreaGestureProfile(
                hasAnyTrigger = profile.hasAnyTrigger,
                hasTapTrigger = profile.hasTapTrigger,
                hasLongPressTrigger = profile.hasLongPressTrigger,
                hasSwipeTrigger = profile.hasSwipeTrigger,
            )
        }
    }

    private fun areaKeyForRule(rule: EdgeGesture): DetectedArea {
        val hasCustomRect = rule.hasCustomRect() &&
                rule.customRect != tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance()
        return if (hasCustomRect) {
            DetectedArea.CustomRect(rule.customRect)
        } else {
            DetectedArea.PredefinedEdge(rule.edge, rule.expectedDirectionCoordinateMode())
        }
    }

    private class MutableAreaGestureProfile {
        var hasAnyTrigger: Boolean = false
        var hasTapTrigger: Boolean = false
        var hasLongPressTrigger: Boolean = false
        var hasSwipeTrigger: Boolean = false
    }
}
