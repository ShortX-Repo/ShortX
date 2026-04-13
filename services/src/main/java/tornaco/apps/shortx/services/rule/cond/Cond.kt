package tornaco.apps.shortx.services.rule.cond

import android.content.Context
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.LoggerCache

interface Cond {
    val logger get() = LoggerCache.getLogger("Cond-${condClass().simpleName}")

    fun condClass(): Class<out Message>

    fun init(androidContext: Context, shortXContext: ShortXContext) {
        logger.i("Cond $this init")
    }

    suspend fun evaluate(param: CondParam): Result

    data class CondParam(
        val context: Context,
        val shortXContext: ShortXContext,
        val evaluateContext: EvaluateContext,
        val condition: ProtoAny,
        val contextData: ContextData,
        val localVars: LocalVars,
        val parameterInputs: List<FuncParameterInput>
    )

    data class Result(
        val match: Boolean,
        val reason: String? = null
    )

    fun Boolean.toResult(reason: String? = null) = Result(this, reason)
}

interface CondProvider {
    val cond: Cond
}

enum class PrebuiltCond : CondProvider {
    CurrentActivity {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.CurrentActivity
    },
    CurrentPkgList {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.CurrentPkgList
    },
    CurrentPkgListByPkg {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.CurrentPkgListByPkg
    },
    True {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.True
    },
    False {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.False
    },
    MatchMVEL {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.MatchMVEL
    },
    MatchJS {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.MatchJS
    },
    EvaluateGlobalVar {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.EvaluateGlobalVar
    },
    EvaluateContextVar {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.EvaluateContextVar
    },
    EvaluateLocalVar {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.EvaluateLocalVar
    },
    EvaluateScreenOnTime {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.EvaluateScreenOnTime
    },
    BatteryPercent {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.BatteryPercent
    },
    AvailableMemory {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.AvailableMemory
    },
    ConnectedWifiSignal {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.ConnectedWifiSignal
    },
    ChargeState {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.ChargeState
    },
    PlugState {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.PlugState
    },
    AppIsRunning {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.AppIsRunning
    },
    AppIsNotRunning {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.AppIsNotRunning
    },
    AppHasAudioFocus {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.AppHasAudioFocus
    },
    AppHasWindowFocus {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.AppHasWindowFocus
    },
    ServiceIsRunning {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.ServiceIsRunning
    },
    ScreenIsOn {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.ScreenIsOn
    },
    VPNIsConnected {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.VPNIsConnected
    },
    TimeInRange {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.TimeInRange
    },
    CalendarDayMatch {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.CalendarDayMatch
    },
    RequireWifiConnected {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireWifiConnected
    },
    RequireWifiDisconnectedConnected {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireWifiDisconnected
    },
    RequireBTConnected {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireBTConnected
    },
    RequireBTDisconnected {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireBTDisconnected
    },
    RequireBTDeviceFound {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireBTDeviceFound
    },
    RequireMobileDataEnabled {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireMobileDataEnabled
    },
    AppHasNotification {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.AppHasNotification
    },
    KeyguardIsLocked {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.KeyguardIsLocked
    },
    ScreenOrientationIsPort {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.ScreenOrientationIsPort
    },
    IsInCall {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.IsInCall
    },
    IsRinging {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.IsRinging
    },
    HasNodeOnScreen {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.HasNodeOnScreen
    },
    IsRuleEnabled {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.IsRuleEnabled
    },
    IsHeadsetPlug {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.IsHeadsetPlug
    },
    RequireScreenRotate {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireScreenRotate
    },
    RequireWindowRotation {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireWindowRotation
    },
    RequireDelay {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireDelay
    },
    RequireSensorOff {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireSensorOff
    },

    RequireTileState {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireTileState
    },

    RequireFactTag {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireFactTag
    },
    AppHasTask {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.AppHasTask
    },
    AppHasTaskByPkg {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.AppHasTaskByPkg
    },
    RequireAPMMode {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireAPMMode
    },
    RequireIMEVisibility {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireIMEVisibility
    },
    RequireNotificationPanelExpanded {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireNotificationPanelExpanded
    },
    TheXXTimeToday {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.TheXXTimeToday
    },
    RequireRingerMode {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.RequireRingerMode
    },
    HasFoundPointsByColor {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.HasFoundPointsByColor
    },
    TextFromScreenNodeMatches {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.TextFromScreenNodeMatches
    },
    PluginCond {
        override val cond: Cond
            get() = tornaco.apps.shortx.services.rule.cond.impl.PluginCond
    },
}

fun prebuiltCondFor(condition: ProtoAny): Cond? {
    return PrebuiltCond.entries.firstOrNull { condition is_ it.cond.condClass() }?.cond
}