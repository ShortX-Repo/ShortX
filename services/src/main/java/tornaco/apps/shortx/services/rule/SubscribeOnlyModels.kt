package tornaco.apps.shortx.services.rule

import com.google.protobuf.GeneratedMessageV3
import tornaco.apps.shortx.core.proto.action.AudioRecording
import tornaco.apps.shortx.core.proto.action.BiometricVerify
import tornaco.apps.shortx.core.proto.action.ClickNotification
import tornaco.apps.shortx.core.proto.action.ClickNotificationActionButton
import tornaco.apps.shortx.core.proto.action.ClickTile
import tornaco.apps.shortx.core.proto.action.ConnectWifi
import tornaco.apps.shortx.core.proto.action.EnableUniversalCopy
import tornaco.apps.shortx.core.proto.action.FindAndClickMatchedView
import tornaco.apps.shortx.core.proto.action.FindAndClickViewById
import tornaco.apps.shortx.core.proto.action.ForEachPkgSet
import tornaco.apps.shortx.core.proto.action.GetTextFromScreenNode
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.action.InjectGestureRecording
import tornaco.apps.shortx.core.proto.action.InputSwipe
import tornaco.apps.shortx.core.proto.action.InputTap
import tornaco.apps.shortx.core.proto.action.InputText
import tornaco.apps.shortx.core.proto.action.LaunchAppByPkg
import tornaco.apps.shortx.core.proto.action.LockDeviceNow
import tornaco.apps.shortx.core.proto.action.PerformContextMenuAction
import tornaco.apps.shortx.core.proto.action.RemoveNotificationForPackageByPkg
import tornaco.apps.shortx.core.proto.action.ScrollViewTo
import tornaco.apps.shortx.core.proto.action.SetAppEnabled
import tornaco.apps.shortx.core.proto.action.SetAppEnabledByPkg
import tornaco.apps.shortx.core.proto.action.SetAppSuspend
import tornaco.apps.shortx.core.proto.action.SetAppSuspendByPkg
import tornaco.apps.shortx.core.proto.action.SetScreenTimeout
import tornaco.apps.shortx.core.proto.action.ShowDrawBoard
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.action.StartGestureRecording
import tornaco.apps.shortx.core.proto.action.StopAppByPkg
import tornaco.apps.shortx.core.proto.action.StopGestureRecording
import tornaco.apps.shortx.core.proto.action.SwitchMobileDataSlot
import tornaco.apps.shortx.core.proto.action.TTS
import tornaco.apps.shortx.core.proto.action.ToggleGestureRecording
import tornaco.apps.shortx.core.proto.action.WaitUtilConditionMatch
import tornaco.apps.shortx.core.proto.condition.AppHasAudioFocus
import tornaco.apps.shortx.core.proto.condition.AppHasTask
import tornaco.apps.shortx.core.proto.condition.AppHasWindowFocus
import tornaco.apps.shortx.core.proto.condition.RequireFactTag
import tornaco.apps.shortx.core.proto.condition.RequireIMEVisibility
import tornaco.apps.shortx.core.proto.condition.RequireMobileDataEnabled
import tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent
import tornaco.apps.shortx.core.proto.fact.AppGainWindowFocus
import tornaco.apps.shortx.core.proto.fact.AppLostWindowFocus
import tornaco.apps.shortx.core.proto.fact.BTConnectedTo
import tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom
import tornaco.apps.shortx.core.proto.fact.FixedInPeriod
import tornaco.apps.shortx.core.proto.fact.HasFoundNodeOnScreen
import tornaco.apps.shortx.core.proto.fact.IMEVisibilityChange
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
        AppGainWindowFocus::class,
        AppLostWindowFocus::class,
        IMEVisibilityChange::class,

        AppHasAudioFocus::class,
        RequireMobileDataEnabled::class,
        RequireFactTag::class,
        AppHasTask::class,
        AppHasWindowFocus::class,
        RequireIMEVisibility::class,

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
        BiometricVerify::class,
        GetTextFromScreenNode::class,
        ForEachPkgSet::class,
        LockDeviceNow::class,
        ConnectWifi::class,
        ScrollViewTo::class,
        ShowDrawBoard::class,
        PerformContextMenuAction::class,
        WaitUtilConditionMatch::class,
        SetScreenTimeout::class,
        ClickNotification::class,
        ClickNotificationActionButton::class,
    )