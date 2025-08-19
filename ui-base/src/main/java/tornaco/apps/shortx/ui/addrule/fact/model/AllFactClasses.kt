package tornaco.apps.shortx.ui.addrule.fact.model

val allFactClasses = listOf(
    Fact.CombineKeyEvent::class,
    Fact.KeyEvent::class,
    Fact.AdvancedKeyEvent::class,

    Fact.AnyActivityStarted::class,
    Fact.AnyActivityStopped::class,
    Fact.AnyActivityDestroyed::class,

    Fact.ActivityStarted::class,
    Fact.ActivityStopped::class,
    Fact.ActivityDestroyed::class,
    Fact.EdgeGesture::class,
    Fact.ScreenGesture::class,

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
    Fact.PkgStartRunning::class,
    Fact.PkgStartRunningAny::class,

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
    Fact.MediaStoreDelete::class,

    Fact.OnStartOp::class,
    Fact.OnFinishOp::class,

    Fact.OnQSTileClick::class,
    Fact.OnMenuActionTrigger::class,
    Fact.SystemSettingsChanged::class,
    Fact.CallStateChanged::class,
    Fact.ClipboardContentChanged::class,
    Fact.GlobalVarChanged::class,

    Fact.HasFoundNodeOnScreen::class,
    Fact.AppProcessStarted::class,
    Fact.AppProcessRemoved::class,

    Fact.HeadsetPlug::class,
    Fact.ScreenRotate::class,
    Fact.WindowRotationChange::class,
    Fact.IMEVisibilityChange::class,
    Fact.AppGainWindowFocus::class,
    Fact.AppLostWindowFocus::class,

    Fact.DeepLinkCall::class,
    Fact.Logcat::class,
    Fact.MethodHook::class,

    Fact.BackNavStart::class,
    Fact.BackNavDone::class,

    Fact.UsbDeviceAttached::class,
    Fact.UsbDeviceDetached::class,

    Fact.LightSensor::class,
    Fact.ProximitySensor::class,
    Fact.ProximitySensorStateChanged::class,
    Fact.ShakeDevice::class,
    Fact.AccelerometerSensor::class,
    Fact.Sound::class,

    Fact.CpuAvgAvailability::class,

    Fact.ScreenOnTime::class,
    Fact.SunRiseSet::class,

    Fact.OnStartDynamicShortcut::class,

    Fact.AnyFact::class,
)