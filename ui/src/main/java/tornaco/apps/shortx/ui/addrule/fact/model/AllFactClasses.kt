package tornaco.apps.shortx.ui.addrule.fact.model

val allFactClasses = listOf(
    Fact.KeyEvent::class,
    Fact.AnyActivityStarted::class,
    Fact.ActivityStarted::class,
    Fact.EdgeGesture::class,

    Fact.ScreenOn::class,
    Fact.ScreenOff::class,
    Fact.UserPresent::class,
    Fact.SystemReady::class,

    Fact.Alarm::class,
    Fact.RandomInPeriod::class,
    Fact.AppBecomeBackground::class,
    Fact.AppBecomeForeground::class,

    Fact.WifiStatusChanged::class,
    Fact.WifiConnectedTo::class,
    Fact.WifiDisconnectedFrom::class,

    Fact.BTStatusChanged::class,
    Fact.MobileDataStatusChanged::class,
    Fact.LocationStatusChanged::class,
    Fact.NFCStatusChanged::class,
    Fact.DNDStatusChanged::class,
    Fact.HotSpotStatusChanged::class,
    Fact.FlashLightStatusChanged::class,
    Fact.DarkModeStatusChanged::class,

    Fact.NotificationPosted::class,
    Fact.NotificationRemoved::class,
    Fact.NotificationUpdated::class,

    Fact.NotificationPostedAny::class,
    Fact.NotificationRemovedAny::class,
    Fact.NotificationUpdatedAny::class,

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

    Fact.BatteryLevelChanged::class,
    Fact.ChargerPlug::class,
    Fact.ChargerUnplug::class,

    Fact.Broadcast::class,

    Fact.VPNConnected::class,
    Fact.VPNDisconnected::class,
)