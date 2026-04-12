package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.gesture

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.RectF
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
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.AppPkgList
import tornaco.apps.shortx.core.proto.fact.CornerSwipeToCenter
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
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
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.data.GlobalSettings
import kotlin.math.roundToInt

class DetectorServiceImpl(private val context: Context) : DetectorService {
    private val shortx by lazy { shortXManager }
    private val logger = Logger("DetectorService")

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    private var swipeDistanceThresholdDp: Int = 24
    private var gestureAreaWidthDp: Int = 28
    private var gestureLengthScale: Float = 1f

    var isGestureRecording: Boolean = false
        private set

    private var afterGestureSettingsChangedObs: () -> Unit = {}
    private var afterConfigChangedObs: () -> Unit = {}
    private var afterUserDataChangedObs: () -> Unit = {}

    override val enabledEdgeGestures = mutableSetOf<EdgeGesture>()
    override val enabledCornerSwipeToCenterFacts = mutableSetOf<CornerSwipeToCenter>()
    private val interceptCornerSwipeToCenterFacts = mutableSetOf<CornerSwipeToCenter>()
    private val interceptMotionPortRects = mutableListOf<RectF>()
    private val interceptMotionLandRects = mutableListOf<RectF>()
    private var currentScreenSize: Point? = null

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
        isGestureRecording = shortx.isGestureRecording()
        GlobalSettings.isEdgeGestureEnabled =
            shortx.isAppFeatureEnabled(AppFeature.AppFeature_EdgeGesture.number)
        isGestureEnabledQuick = GlobalSettings.isEdgeGestureEnabled
        
        // 读取 forceDisable 和 ruleFeature 状态
        isRuleFeatureDisabled = !shortx.isRuleFeatureEnabled()

        logger.w("swipeDistanceThresholdDp: $swipeDistanceThresholdDp")
        logger.w("gestureAreaWidthDp: $gestureAreaWidthDp")
        logger.w("gestureLengthScale: $gestureLengthScale")
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

    override fun obsGestureSettingsChanged(obs: () -> Unit) {
        afterGestureSettingsChangedObs = obs
    }

    override fun obsConfigChanged(obs: () -> Unit) {
        afterConfigChangedObs = obs
    }

    override fun obsUserDataChanged(obs: () -> Unit) {
        afterUserDataChangedObs = obs
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
            // 首先检查gesture是否匹配，不匹配则提前返回false
            if (enabledGesture.gesture != gesture) {
                return@any false
            }

            // gesture匹配后再检查区域是否匹配
            when (area) {
                is DetectedArea.PredefinedEdge -> {
                    // 对于预定义边缘，检查edge是否匹配且customRect为默认值
                    enabledGesture.edge == area.edge &&
                            (enabledGesture.customRect == null || enabledGesture.customRect == tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance())
                }

                is DetectedArea.CustomRect -> {
                    // 对于自定义矩形，检查customRect是否匹配
                    enabledGesture.customRect != null &&
                            enabledGesture.customRect != tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance() &&
                            enabledGesture.customRect == area.rect
                }
            }
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
        val cornerContains = if (!anyContains) shouldStealCornerSwipeMotionEvent(input.x, input.y) else false
        val timeTaken = SystemClock.elapsedRealtimeNanos() - startTimeMillis
        // logger.d("filterMotionEvent, check take time: $timeTaken(nano)")
        if (anyContains || cornerContains) {
            logger.logOnDebug("shouldStealMotionEvent, steal MotionEvent: $input")
            return true
        }
        return false
    }

    private fun shouldStealCornerSwipeMotionEvent(x: Float, y: Float): Boolean {
        if (interceptCornerSwipeToCenterFacts.isEmpty()) return false
        val screenSize = currentScreenSize ?: return false
        val width = screenSize.x.toFloat()
        val height = screenSize.y.toFloat()
        return interceptCornerSwipeToCenterFacts.any { cornerSwipe ->
            !cornerSwipe.isDisabled && CornerSwipeToCenterGeometry.contains(
                cornerSwipe.corner,
                x,
                y,
                width,
                height
            )
        }
    }

    private fun updateQuickCheckDataOnRuleChange() {
        val screenSize = shortx.getScreenSize()
        currentScreenSize = screenSize
        logger.i("updateQuickCheckDataOnRuleChange, screenSize: $screenSize")

        // 复用现有集合而不是创建新的
        interceptMotionPortRects.clear()
        interceptMotionLandRects.clear()
        enabledEdgeGestures.clear()
        enabledCornerSwipeToCenterFacts.clear()
        interceptCornerSwipeToCenterFacts.clear()
        requiredFrontApps.clear()

        // 使用临时集合来收集数据，避免频繁的clear/add操作
        val tempPortRects = mutableListOf<RectF>()
        val tempLandRects = mutableListOf<RectF>()
        val tempEnabledFacts = mutableSetOf<EdgeGesture>()
        val tempEnabledCornerFacts = mutableSetOf<CornerSwipeToCenter>()
        val tempInterceptCornerFacts = mutableSetOf<CornerSwipeToCenter>()
        val tempRequiredApps = mutableSetOf<AppPkg>()

        // Reset.
        requiredFrontAppAny = false

        shortx.getAllRules().filter { it.isEnabled }.forEach { rule ->
            rule.factsList.forEach { protoFact ->
                val isEG = protoFact is_ EdgeGesture::class.java
                if (isEG) {
                    val eg = protoFact unpack_ EdgeGesture::class.java
                    if (!eg.isDisabled && eg.isIntercept) {
                        // 优先检查是否有自定义矩形区域
                        if (eg.customRect != null && eg.customRect != tornaco.apps.shortx.core.proto.common.Rect.getDefaultInstance()) {
                            // 使用自定义矩形区域
                            val customRectF = RectF(
                                eg.customRect.left.toFloat(),
                                eg.customRect.top.toFloat(),
                                eg.customRect.right.toFloat(),
                                eg.customRect.bottom.toFloat()
                            )
                            // 自定义矩形区域在横竖屏下都使用相同的坐标
                            tempPortRects.add(customRectF)
                            tempLandRects.add(customRectF)
                        } else if (eg.edge != Edge.UNRECOGNIZED) {
                            // 使用预定义的边缘区域计算
                            tempPortRects.add(
                                eg.edge.getRect(
                                    isPortrait = true,
                                    screenWidth = screenSize.x.toFloat(),
                                    screenHeight = screenSize.y.toFloat(),
                                    areaSize = getGestureAreaWidthDp()._dp.toFloat(),
                                    areaLengthScale = getGestureLengthScale()
                                )
                            )
                            tempLandRects.add(
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

                    if (!eg.isDisabled) {
                        tempEnabledFacts.add(eg)

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
                        if (cornerSwipe.hasIsIntercept() && cornerSwipe.isIntercept) {
                            tempInterceptCornerFacts.add(cornerSwipe)
                        }
                        requiredFrontAppAny = true
                        requiredFrontAppMatchQuick = true
                    }
                }
            }
        }

        // 一次性更新所有集合
        enabledEdgeGestures.addAll(tempEnabledFacts)
        enabledCornerSwipeToCenterFacts.addAll(tempEnabledCornerFacts)
        interceptCornerSwipeToCenterFacts.addAll(tempInterceptCornerFacts)
        interceptMotionLandRects.addAll(tempLandRects)
        interceptMotionPortRects.addAll(tempPortRects)
        requiredFrontApps.addAll(tempRequiredApps)

        checkFrontAppPkg("updateQuickCheckDataOnRuleChange")

        logger.d("updateQuickCheckDataOnRuleChange, enabledEdgeGestures: $enabledEdgeGestures")
        logger.d("updateQuickCheckDataOnRuleChange, enabledCornerSwipeToCenterFacts: $enabledCornerSwipeToCenterFacts")
        logger.d("updateQuickCheckDataOnRuleChange, interceptCornerSwipeToCenterFacts: $interceptCornerSwipeToCenterFacts")
        logger.d("updateQuickCheckDataOnRuleChange, interceptMotionLandRects: $interceptMotionLandRects")
        logger.d("updateQuickCheckDataOnRuleChange, interceptMotionPortRects: $interceptMotionPortRects")
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
}