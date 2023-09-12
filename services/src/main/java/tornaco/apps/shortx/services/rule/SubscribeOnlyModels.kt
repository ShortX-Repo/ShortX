package tornaco.apps.shortx.services.rule

import com.google.protobuf.GeneratedMessageV3
import tornaco.apps.shortx.core.proto.action.AudioRecording
import tornaco.apps.shortx.core.proto.action.ClickTile
import tornaco.apps.shortx.core.proto.action.EnableUniversalCopy
import tornaco.apps.shortx.core.proto.action.FindAndClickMatchedView
import tornaco.apps.shortx.core.proto.action.FindAndClickViewById
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.action.InjectGestureRecording
import tornaco.apps.shortx.core.proto.action.InputSwipe
import tornaco.apps.shortx.core.proto.action.InputTap
import tornaco.apps.shortx.core.proto.action.InputText
import tornaco.apps.shortx.core.proto.action.LaunchAppByPkg
import tornaco.apps.shortx.core.proto.action.RemoveNotificationForPackageByPkg
import tornaco.apps.shortx.core.proto.action.SetAppEnabled
import tornaco.apps.shortx.core.proto.action.SetAppEnabledByPkg
import tornaco.apps.shortx.core.proto.action.SetAppSuspend
import tornaco.apps.shortx.core.proto.action.SetAppSuspendByPkg
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.action.StartGestureRecording
import tornaco.apps.shortx.core.proto.action.StopAppByPkg
import tornaco.apps.shortx.core.proto.action.StopGestureRecording
import tornaco.apps.shortx.core.proto.action.SwitchMobileDataSlot
import tornaco.apps.shortx.core.proto.action.TTS
import tornaco.apps.shortx.core.proto.action.ToggleGestureRecording
import tornaco.apps.shortx.core.proto.condition.AppHasAudioFocus
import tornaco.apps.shortx.core.proto.condition.RequireMobileDataEnabled
import tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent
import tornaco.apps.shortx.core.proto.fact.BTConnectedTo
import tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom
import tornaco.apps.shortx.core.proto.fact.FixedInPeriod
import tornaco.apps.shortx.core.proto.fact.HasFoundNodeOnScreen
import tornaco.apps.shortx.core.proto.fact.OnQSTileClick
import tornaco.apps.shortx.core.proto.fact.TaskRemoved
import tornaco.apps.shortx.core.proto.fact.VPNConnected
import tornaco.apps.shortx.core.proto.fact.VPNDisconnected
import kotlin.reflect.KClass


inline fun subscribedOnlyModels(onRes: () -> Unit = {}): Array<KClass<out GeneratedMessageV3>> =
    arrayOf(
        FixedInPeriod::class,
        VPNConnected::class,
        VPNDisconnected::class,
        BTConnectedTo::class,
        BTDisconnectedFrom::class,
        TaskRemoved::class,
        AdvancedKeyEvent::class,
        FixedInPeriod::class,
        OnQSTileClick::class,
        HasFoundNodeOnScreen::class,

        AppHasAudioFocus::class,
        RequireMobileDataEnabled::class,

        LaunchAppByPkg::class,
        StopAppByPkg::class,
        RemoveNotificationForPackageByPkg::class,
        SetAppSuspend::class,
        SetAppSuspendByPkg::class,
        SetAppEnabled::class,
        SetAppEnabledByPkg::class,
        AudioRecording::class,
        ShowOverlayButton::class,
        SwitchMobileDataSlot::class,
        HttpRequest::class,
        InputText::class,
        InputTap::class,
        InputSwipe::class,
        InjectGestureRecording::class,
        ToggleGestureRecording::class,
        StartGestureRecording::class,
        StopGestureRecording::class,
        EnableUniversalCopy::class,
        TTS::class,
        FindAndClickViewById::class,
        FindAndClickMatchedView::class,
        ClickTile::class,
    )