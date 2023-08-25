package tornaco.apps.shortx.services.rule.actor

import android.content.Context
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.ActionEvaluator
import tornaco.apps.shortx.services.rule.ConditionEvaluator

data class HandleParams(
    val androidContext: Context,
    val shortXContext: ShortXContext,
    val evaluateContext: EvaluateContext,
    val action: ProtoAny,
    val contextData: ContextData,
    val localVars: LocalVars,

    val recursiveExecute: suspend (
        context: EvaluateContext,
        action: ProtoAny,
        contextData: ContextData,
        localVars: LocalVars
    ) -> ActionEvaluator.EvaluateResult,
    val conditionEvaluator: ConditionEvaluator
)

interface Actor {
    fun actionsCanHandle(): Class<out Message>
    suspend fun handle(params: HandleParams)

    fun init(androidContext: Context, shortXContext: ShortXContext) {
        logger.i("Actor $this init")
    }

    val logger get() = Logger("Actor-${actionsCanHandle().simpleName}")
}

interface ActorProvider {
    val actor: Actor
}

enum class PrebuiltActor : ActorProvider {
    NoAction {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.NoAction

    },
    StartActivity {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StartActivity

    },
    StartActivityIntent {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StartActivityIntent

    },
    StartActivityUrlSchema {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StartActivityUrlSchema

    },
    TakeScreenshot {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.TakeScreenshot
    },
    ExpandNotification {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.ExpandNotification
    },
    ShellCommand {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.ShellCommand
    },
    InputText {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.InputText
    },
    ShowToast {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.ShowToast
    },
    Delay {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.Delay
    },
    ExecuteMVEL {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.ExecuteMVEL
    },
    RemoteExecuteMVEL {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.RemoteExecuteMVEL
    },
    HttpRequest {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.HttpRequest
    },
    ShowOverlayButton {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.ShowOverlayButton
    },
    HideOverlayButton {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.HideOverlayButton
    },
    LaunchApp {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.LaunchApp
    },
    LaunchAppByPkg {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.LaunchAppByPkg
    },
    StopApp {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StopApp
    },
    StopAppByPkg {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StopAppByPkg
    },
    StartAppProcess {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StartAppProcess
    },
    StartAppProcessByPkg {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StartAppProcessByPkg
    },
    SetAppInactive {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetAppInactive
    },
    SetAppInactiveByPkg {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetAppInactiveByPkg
    },
    SetAppEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetAppEnabled
    },
    SetAppEnabledByPkg {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetAppEnabledByPkg
    },
    SetAppSuspend {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetAppSuspend
    },
    SetAppSuspendByPkg {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetAppSuspendByPkg
    },
    SetSensorOffEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetSensorsOffEnabled
    },
    SetWifiEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetWifiEnabled
    },
    SetHotSpotEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetHotSpotEnabled
    },
    SetBTEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetBTEnabled
    },
    SetLocationEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetLocationEnabled
    },
    SetNFCEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetNFCEnabled
    },
    SetFlashLightEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetFlashLightEnabled
    },
    SetDarkModeEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetDarkModeEnabled
    },
    SetDataEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetDataEnabled
    },
    SetDNDEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetDNDEnabled
    },
    BreakActionExecute {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.BreakActionExecute
    },
    ShowAlertDialog {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.ShowAlertDialog
    },
    ShowMenuDialog {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.ShowMenuDialog
    },
    ShowDanmu {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.ShowDanmu
    },
    WhileLoop {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.WhileLoop
    },
    CreatePkgSet {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.CreatePkgSet
    },
    WriteGlobalVar {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.WriteGlobalVar
    },
    CreateGlobalVar {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.CreateGlobalVar
    },
    DeleteGlobalVar {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.DeleteGlobalVar
    },
    AddToGlobalVar {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.AddToGlobalVar
    },
    MapNav {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.MapNav
    },
    MapQueryBus {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.MapQueryBus
    },
    PostNotification {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.PostNotification
    },
    FromDA {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.FromDA
    },
    FindAndClickViewByText {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.FindAndClickViewByText
    },
    InputTap {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.InputTap
    },
    InputSwipe {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.InputSwipe
    },
    WaitForIdle {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.WaitForIdle
    },
    IfThenElse {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.IfThenElse
    },
    ReadClipboard {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.ReadClipboard
    },
    WriteClipboard {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.WriteClipboard
    },
    SetRingerMode {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetRingerMode
    },
    SetBrightness {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetBrightness
    },
    SetAutoBrightness {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetAutoBrightness
    },
    StopService {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StopService
    },
    RemoveTasks {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.RemoveTasks
    },
    RemoveTasksByPkg {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.RemoveTasksByPkg
    },
    KillProcessByName {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.KillProcessByName
    },
    RemoveNotificationForPackage {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.RemoveNotificationForPackage
    },

    RemoveNotificationForPackageByPkg {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.RemoveNotificationForPackageByPkg
    },
    InjectKeyCode {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.InjectKeyCode
    },
    SleepScreen {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SleepScreen
    },
    WakeupScreen {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.WakeupScreen
    },
    StartLastApp {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StartLastApp
    },
    StartPreviousApp {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StartPreviousApp
    },
    StartNextApp {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StartNextApp
    },
    StopCurrentApp {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StopCurrentApp
    },
    AdjustVolume {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.AdjustVolume
    },
    Vibrate {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.Vibrate
    },
    AudioRecording {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.AudioRecording
    },
    StopAudioRecording {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StopAudioRecording
    },
    SwitchMobileDataSlot {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SwitchMobileDataSlot
    },
    CreateLocalVar {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.CreateLocalVar
    },
    WriteLocalVar {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.WriteLocalVar
    },
    StartGestureRecording {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StartGestureRecording
    },
    StopGestureRecording {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.StopGestureRecording
    },
    ToggleGestureRecording {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.ToggleGestureRecording
    },
    InjectGestureRecording {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.InjectGestureRecording
    },
    EnableUniversalCopy {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.EnableUniversalCopy
    },
    ShowTextFieldDialog {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.ShowTextFieldDialog
    },
    TTS {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.TTS
    },
    SetWallpaper {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetWallpaper
    },
    SetRuleEnabled {
        override val actor: Actor
            get() = tornaco.apps.shortx.services.rule.actor.impl.SetRuleEnabled
    },


    ;


}

fun prebuiltActorFor(action: ProtoAny): Actor? {
    return PrebuiltActor.values().firstOrNull { action is_ it.actor.actionsCanHandle() }?.actor
}