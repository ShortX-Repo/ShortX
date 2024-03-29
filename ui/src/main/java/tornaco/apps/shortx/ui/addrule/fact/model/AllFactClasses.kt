package tornaco.apps.shortx.ui.addrule.fact.model

val allFactClasses = listOf(
    Fact.KeyEvent::class,
    Fact.AdvancedKeyEvent::class,

    Fact.AnyActivityStarted::class,
    Fact.ActivityStarted::class,
    Fact.ActivityStopped::class,
    Fact.EdgeGesture::class,

    Fact.ScreenOn::class,
    Fact.ScreenOff::class,
    Fact.UserPresent::class,
    Fact.UserPresentAtTheFirstTime::class,
    Fact.SystemReady::class,

    Fact.Alarm::class,
    Fact.RandomInPeriod::class,
    Fact.FixedInPeriod::class,

    Fact.AppBecomeBackground::class,
    Fact.AppBecomeForeground::class,

    Fact.WifiStatusChanged::class,
    Fact.WifiConnectedTo::class,
    Fact.WifiDisconnectedFrom::class,
    Fact.ConnectedWifiSignalLevelChanged::class,

    Fact.BTStatusChanged::class,
    Fact.BTConnectedTo::class,
    Fact.BTDisconnectedFrom::class,

    Fact.MobileDataStatusChanged::class,
    Fact.LocationStatusChanged::class,
    Fact.NFCStatusChanged::class,
    Fact.DNDStatusChanged::class,
    Fact.HotSpotStatusChanged::class,
    Fact.FlashLightStatusChanged::class,
    Fact.DarkModeStatusChanged::class,
    Fact.APMStatusChanged::class,

    Fact.NFCTagDiscover::class,

    Fact.NotificationPosted::class,
    Fact.NotificationRemoved::class,
    Fact.NotificationUpdated::class,

    Fact.TaskRemoved::class,
    Fact.TaskRemovedAny::class,

    Fact.AppAdded::class,
    Fact.AppRemoved::class,
    Fact.AppUpdated::class,

    Fact.PkgStopRunning::class,
    Fact.PkgStopRunningAny::class,

    Fact.AudioFocusGain::class,
    Fact.AudioFocusLost::class,
    Fact.AudioFocusChanged::class,

    Fact.BatteryTemperatureChanged::class,
    Fact.BatteryLevelChanged::class,
    Fact.ChargerPlug::class,
    Fact.ChargerUnplug::class,

    Fact.Broadcast::class,

    Fact.VPNConnected::class,
    Fact.VPNDisconnected::class,

    Fact.MediaStoreInsert::class,

    Fact.OnStartOp::class,
    Fact.OnFinishOp::class,

    Fact.OnQSTileClick::class,
    Fact.SystemSettingsChanged::class,
    Fact.CallStateChanged::class,
    Fact.ClipboardContentChanged::class,
    Fact.GlobalVarChanged::class,

    Fact.HasFoundNodeOnScreen::class,
    Fact.AppProcessStarted::class,

    Fact.HeadsetPlug::class,
    Fact.ScreenRotate::class,
    Fact.WindowRotationChange::class,
    Fact.IMEVisibilityChange::class,
    Fact.AppGainWindowFocus::class,
    Fact.AppLostWindowFocus::class,

    Fact.DeepLinkCall::class,


    Fact.AnyFact::class,
)