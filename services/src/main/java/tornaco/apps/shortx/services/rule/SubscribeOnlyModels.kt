package tornaco.apps.shortx.services.rule

import com.google.protobuf.GeneratedMessageV3
import tornaco.apps.shortx.core.proto.action.AreaScreenshot
import tornaco.apps.shortx.core.proto.action.AudioRecording
import tornaco.apps.shortx.core.proto.action.BiometricVerify
import tornaco.apps.shortx.core.proto.action.ClickNotification
import tornaco.apps.shortx.core.proto.action.ClickNotificationActionButton
import tornaco.apps.shortx.core.proto.action.ClickTile
import tornaco.apps.shortx.core.proto.action.ConnectWifi
import tornaco.apps.shortx.core.proto.action.DownloadFile
import tornaco.apps.shortx.core.proto.action.EnableUniversalCopy
import tornaco.apps.shortx.core.proto.action.FindAndClickMatchedView
import tornaco.apps.shortx.core.proto.action.FindAndClickViewById
import tornaco.apps.shortx.core.proto.action.ForEachPkgSet
import tornaco.apps.shortx.core.proto.action.GetTextFromScreenNode
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.action.InjectCombineKeyCode
import tornaco.apps.shortx.core.proto.action.InjectGestureRecording
import tornaco.apps.shortx.core.proto.action.InjectKeyCode
import tornaco.apps.shortx.core.proto.action.InputSwipe
import tornaco.apps.shortx.core.proto.action.InputTap
import tornaco.apps.shortx.core.proto.action.InputText
import tornaco.apps.shortx.core.proto.action.LaunchAppByPkg
import tornaco.apps.shortx.core.proto.action.LaunchPinedItem
import tornaco.apps.shortx.core.proto.action.LockDeviceNow
import tornaco.apps.shortx.core.proto.action.MatchRegex
import tornaco.apps.shortx.core.proto.action.PerformContextMenuAction
import tornaco.apps.shortx.core.proto.action.RemoveNotificationForPackageByPkg
import tornaco.apps.shortx.core.proto.action.ReplaceRegex
import tornaco.apps.shortx.core.proto.action.ScrollViewTo
import tornaco.apps.shortx.core.proto.action.SelectScreenArea
import tornaco.apps.shortx.core.proto.action.SendSMS
import tornaco.apps.shortx.core.proto.action.SetAppEnabled
import tornaco.apps.shortx.core.proto.action.SetAppEnabledByPkg
import tornaco.apps.shortx.core.proto.action.SetAppSuspend
import tornaco.apps.shortx.core.proto.action.SetAppSuspendByPkg
import tornaco.apps.shortx.core.proto.action.SetScreenTimeout
import tornaco.apps.shortx.core.proto.action.ShowClipboardView
import tornaco.apps.shortx.core.proto.action.ShowDrawBoard
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.action.StartGestureRecording
import tornaco.apps.shortx.core.proto.action.StopAppByPkg
import tornaco.apps.shortx.core.proto.action.StopGestureRecording
import tornaco.apps.shortx.core.proto.action.SwitchCase
import tornaco.apps.shortx.core.proto.action.SwitchMobileDataSlot
import tornaco.apps.shortx.core.proto.action.TTS
import tornaco.apps.shortx.core.proto.action.Toggle5G
import tornaco.apps.shortx.core.proto.action.ToggleGestureRecording
import tornaco.apps.shortx.core.proto.action.WaitUtilConditionMatch
import tornaco.apps.shortx.core.proto.condition.AppHasAudioFocus
import tornaco.apps.shortx.core.proto.condition.AppHasTask
import tornaco.apps.shortx.core.proto.condition.AppHasTaskByPkg
import tornaco.apps.shortx.core.proto.condition.AppHasWindowFocus
import tornaco.apps.shortx.core.proto.condition.ConnectedWifiSignal
import tornaco.apps.shortx.core.proto.condition.RequireBTConnected
import tornaco.apps.shortx.core.proto.condition.RequireBTDeviceFound
import tornaco.apps.shortx.core.proto.condition.RequireBTDisconnected
import tornaco.apps.shortx.core.proto.condition.RequireFactTag
import tornaco.apps.shortx.core.proto.condition.RequireIMEVisibility
import tornaco.apps.shortx.core.proto.condition.RequireMobileDataEnabled
import tornaco.apps.shortx.core.proto.condition.RequireNotificationPanelExpanded
import tornaco.apps.shortx.core.proto.condition.RequireScreenRotate
import tornaco.apps.shortx.core.proto.condition.RequireTileState
import tornaco.apps.shortx.core.proto.condition.RequireWifiConnected
import tornaco.apps.shortx.core.proto.condition.RequireWifiDisconnected
import tornaco.apps.shortx.core.proto.condition.RequireWindowRotation
import tornaco.apps.shortx.core.proto.condition.TheXXTimeToday
import tornaco.apps.shortx.core.proto.condition.VPNIsConnected
import tornaco.apps.shortx.core.proto.fact.ActivityDestroyed
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.fact.ActivityStopped
import tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent
import tornaco.apps.shortx.core.proto.fact.AppGainWindowFocus
import tornaco.apps.shortx.core.proto.fact.AppLostWindowFocus
import tornaco.apps.shortx.core.proto.fact.BTConnectedTo
import tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom
import tornaco.apps.shortx.core.proto.fact.BatteryTemperatureChanged
import tornaco.apps.shortx.core.proto.fact.ConnectedWifiSignalLevelChanged
import tornaco.apps.shortx.core.proto.fact.FixedInPeriod
import tornaco.apps.shortx.core.proto.fact.HasFoundNodeOnScreen
import tornaco.apps.shortx.core.proto.fact.IMEVisibilityChange
import tornaco.apps.shortx.core.proto.fact.MethodHook
import tornaco.apps.shortx.core.proto.fact.NFCTagDiscover
import tornaco.apps.shortx.core.proto.fact.OnQSTileClick
import tornaco.apps.shortx.core.proto.fact.PkgStartRunning
import tornaco.apps.shortx.core.proto.fact.TaskRemoved
import tornaco.apps.shortx.core.proto.fact.UserPresentAtTheFirstTime
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
        ConnectedWifiSignalLevelChanged::class,
        NFCTagDiscover::class,
        ActivityDestroyed::class,
        ActivityStarted::class,
        ActivityStopped::class,
        UserPresentAtTheFirstTime::class,
        BatteryTemperatureChanged::class,
        MethodHook::class,
        PkgStartRunning::class,

        AppHasAudioFocus::class,
        RequireMobileDataEnabled::class,
        RequireFactTag::class,
        AppHasTask::class,
        AppHasTaskByPkg::class,
        AppHasWindowFocus::class,
        RequireIMEVisibility::class,
        ConnectedWifiSignal::class,
        TheXXTimeToday::class,
        RequireBTDeviceFound::class,
        RequireNotificationPanelExpanded::class,
        RequireWindowRotation::class,
        RequireScreenRotate::class,
        RequireWifiConnected::class,
        RequireWifiDisconnected::class,
        RequireBTConnected::class,
        RequireBTDisconnected::class,
        VPNIsConnected::class,
        RequireTileState::class,

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
        ShowClipboardView::class,
        Toggle5G::class,
        SendSMS::class,
        SwitchCase::class,
        MatchRegex::class,
        ReplaceRegex::class,
        DownloadFile::class,
        InjectKeyCode::class,
        InjectCombineKeyCode::class,
        AreaScreenshot::class,
        SelectScreenArea::class,
        LaunchPinedItem::class,
    )