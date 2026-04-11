package tornaco.apps.shortx.ui.addrule.fact.mapper

import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import kotlin.reflect.KClass


fun imageVectorForFact(fact: KClass<out Fact>): String {
    return when (fact) {
        Fact.AnyFact::class -> Remix.Logos.blender_fill
        Fact.AppProcessStarted::class -> Remix.Logos.android_fill
        Fact.AppProcessRemoved::class -> Remix.Logos.android_fill
        Fact.ActivityStarted::class -> Remix.Logos.android_fill
        Fact.ActivityStopped::class -> Remix.Logos.android_fill
        Fact.ActivityDestroyed::class -> Remix.Logos.android_fill
        Fact.AnyActivityStarted::class -> Remix.Logos.android_fill
        Fact.AnyActivityStopped::class -> Remix.Logos.android_fill
        Fact.AnyActivityDestroyed::class -> Remix.Logos.android_fill
        Fact.EdgeGesture::class -> Remix.Editor.sketching
        Fact.CornerSwipeToCenter::class -> Remix.Editor.sketching
        Fact.ScreenGesture::class -> Remix.Editor.sketching
        Fact.CombineKeyEvent::class -> Remix.Hardware.f_n_key
        Fact.KeyEvent::class -> Remix.Device.keyboard_fill
        Fact.AdvancedKeyEvent::class -> Remix.Device.keyboard_fill
        Fact.ScreenOff::class -> Remix.Device.smartphone_fill
        Fact.ScreenOn::class -> Remix.Device.smartphone_fill
        Fact.UserPresent::class -> Remix.Device.phone_lock_fill
        Fact.UserPresentAtTheFirstTime::class -> Remix.Device.phone_lock_fill
        Fact.SystemReady::class -> Remix.System.login_circle_fill
        Fact.Alarm::class -> Remix.System.alarm_fill
        Fact.RandomInPeriod::class -> Remix.System.alarm_fill
        Fact.FixedInPeriod::class -> Remix.System.alarm_fill
        Fact.AppBecomeBackground::class -> Remix.System.refresh_fill
        Fact.AppBecomeForeground::class -> Remix.System.star_fill
        Fact.WifiStatusChanged::class -> Remix.Device.wifi_fill
        Fact.ConnectedWifiSignalLevelChanged::class -> Remix.Device.signal_wifi_fill
        Fact.WifiConnectedTo::class -> Remix.Device.signal_wifi_fill
        Fact.WifiDisconnectedFrom::class -> Remix.Device.signal_wifi_line
        Fact.BTStatusChanged::class -> Remix.Device.bluetooth_fill
        Fact.BTConnectedTo::class -> Remix.Device.bluetooth_fill
        Fact.BTDisconnectedFrom::class -> Remix.Device.bluetooth_fill
        Fact.LocationStatusChanged::class -> Remix.Device.gps_fill
        Fact.NFCStatusChanged::class -> Remix.Device.nfc_fill
        Fact.MobileDataStatusChanged::class -> Remix.Arrows.arrow_up_down_fill
        Fact.HotSpotStatusChanged::class -> Remix.Device.hotspot_fill
        Fact.DarkModeStatusChanged::class -> Remix.Weather.sun_fill
        Fact.FlashLightStatusChanged::class -> Remix.Others.lightbulb_fill
        Fact.NotificationPosted::class -> Remix.System.notification_badge_fill
        Fact.NotificationUpdated::class -> Remix.System.notification_badge_fill
        Fact.NotificationRemoved::class -> Remix.System.notification_badge_fill
        Fact.TaskRemoved::class -> Remix.System.checkbox_multiple_blank_fill
        Fact.TaskRemovedAny::class -> Remix.System.checkbox_multiple_blank_fill
        Fact.PkgStopRunning::class -> Remix.UserAndFaces.skull_2_fill
        Fact.PkgStopRunningAny::class -> Remix.UserAndFaces.skull_2_fill
        Fact.PkgStartRunning::class -> Remix.Base.loading
        Fact.PkgStartRunningAny::class -> Remix.Base.loading
        Fact.AppAdded::class -> Remix.Device.install_fill
        Fact.AppRemoved::class -> Remix.Device.uninstall_fill
        Fact.AppUpdated::class -> Remix.System.refresh_fill
        Fact.AudioFocusGain::class -> Remix.Media.music_2_fill
        Fact.AudioFocusLost::class -> Remix.Media.music_2_fill
        Fact.AudioFocusChanged::class -> Remix.Media.music_2_fill
        Fact.BatteryLevelChanged::class -> Remix.Device.battery_low_fill
        Fact.BatteryTemperatureChanged::class -> Remix.Weather.celsius_line
        Fact.ChargerPlug::class -> Remix.Device.battery_charge_line
        Fact.ChargerUnplug::class -> Remix.Device.battery_fill
        Fact.Broadcast::class -> Remix.Media.broadcast_fill
        Fact.VPNConnected::class -> Remix.Others.key_line
        Fact.VPNDisconnected::class -> Remix.Others.key_line
        Fact.MediaStoreInsert::class -> Remix.Media.landscape_line
        Fact.MediaStoreDelete::class -> Remix.Media.landscape_fill
        Fact.OnStartOp::class -> Remix.System.alarm_warning_line
        Fact.OnFinishOp::class -> Remix.System.check_line
        Fact.OnQSTileClick::class -> Remix.Media.equalizer_fill
        Fact.OnMenuActionTrigger::class -> Remix.System.menu_5_fill
        Fact.SystemSettingsChanged::class -> Remix.System.settings_6_fill
        Fact.CallStateChanged::class -> Remix.Device.phone_fill
        Fact.GlobalVarChanged::class -> Remix.Finance.exchange_line
        Fact.HasFoundNodeOnScreen::class -> Remix.Design.focus_3_line
        Fact.HeadsetPlug::class -> Remix.Media.headphone_fill
        Fact.ScreenRotate::class -> Remix.Device.smartphone_fill
        Fact.WindowRotationChange::class -> Remix.Device.smartphone_fill
        Fact.APMStatusChanged::class -> Remix.Map.flight_takeoff_fill
        Fact.IMEVisibilityChange::class -> Remix.Device.keyboard_fill
        Fact.AppGainWindowFocus::class -> Remix.Design.focus_2_fill
        Fact.AppLostWindowFocus::class -> Remix.Design.focus_2_line
        Fact.DeepLinkCall::class -> Remix.System.external_link_fill
        Fact.NFCTagDiscover::class -> Remix.Device.nfc_fill
        Fact.Logcat::class -> Remix.Document.newspaper_fill
        Fact.MethodHook::class -> Remix.Development.code_box_fill
        Fact.BackNavStart::class -> Remix.Arrows.arrow_left_line
        Fact.BackNavDone::class -> Remix.Arrows.arrow_left_line
        Fact.UsbDeviceAttached::class -> Remix.Device.usb_fill
        Fact.UsbDeviceDetached::class -> Remix.Device.usb_fill
        Fact.LightSensor::class -> Remix.Others.lightbulb_fill
        Fact.AccelerometerSensor::class -> Remix.Others.fireworks
        Fact.ProximitySensor::class -> Remix.Hardware.phone
        Fact.ProximitySensorStateChanged::class -> Remix.Hardware.phone
        Fact.ShakeDevice::class -> Remix.Others.weixin_shake
        Fact.Sound::class -> Remix.Media.sound_module_fill
        Fact.ScreenOnTime::class -> Remix.Time.big_clock
        Fact.SunRiseSet::class -> Remix.Weather.sun_fill
        Fact.CpuAvgAvailability::class -> Remix.Hardware.cpu

        else -> {
            Remix.Document.file_unknow_fill
        }
    }
}
