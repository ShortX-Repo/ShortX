package tornaco.apps.shortx.ui.addrule.action.mapper

import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import kotlin.reflect.KClass

fun imageVectorForAction(action: Action): String {
    return if (action is Action.NoAction) {
        action.icon
    } else imageVectorForAction(action::class)
}


fun imageVectorForAction(action: KClass<out Action>): String {
    return when (action) {
        Action.ShowToast::class -> {
            Remix.Communication.chat_2_fill
        }

        Action.ShowDanmu::class -> {
            Remix.Communication.chat_1_fill
        }

        Action.ShellCommand::class -> {
            Remix.Development.terminal_box_fill
        }

        Action.InputText::class -> {
            Remix.Editor.text
        }

        Action.Delay::class -> {
            Remix.System.timer_fill
        }

        Action.TakeScreenshot::class -> {
            Remix.Design.screenshot_fill
        }

        Action.MVEL::class -> {
            Remix.Development.code_fill
        }

        Action.HideAllOverlay::class -> {
            Remix.Business.window_fill
        }

        Action.HideOverlay::class -> {
            Remix.Business.window_fill
        }

        Action.ShowOverlay::class -> {
            Remix.Business.window_2_fill
        }

        Action.LaunchApp::class -> {
            Remix.Arrows.arrow_right_up_fill
        }

        Action.DisableBT::class -> {
            Remix.Device.bluetooth_fill
        }

        Action.DisableDND::class -> {
            Remix.Weather.moon_fill
        }

        Action.DisableLocation::class -> {
            Remix.Device.gps_fill
        }

        Action.DisableDarkMode::class -> {
            Remix.Weather.sun_fill
        }

        Action.DisableData::class -> {
            Remix.Arrows.arrow_up_down_fill
        }

        Action.DisableFlashLight::class -> {
            Remix.Others.lightbulb_fill
        }

        Action.DisableHotSpot::class -> {
            Remix.Device.hotspot_fill
        }

        Action.DisableNFC::class -> {
            Remix.Device.nfc_fill
        }

        Action.DisableWifi::class -> {
            Remix.Device.wifi_off_fill
        }

        Action.DisableSensorsOff::class -> {
            Remix.Device.sensor_fill
        }

        Action.EnableBT::class -> {
            Remix.Device.bluetooth_connect_fill
        }

        Action.EnableDND::class -> {
            Remix.Weather.moon_fill
        }

        Action.EnableLocation::class -> {
            Remix.Device.gps_fill
        }

        Action.EnableDarkMode::class -> {
            Remix.Weather.sun_fill
        }

        Action.EnableData::class -> {
            Remix.Arrows.arrow_up_down_fill
        }

        Action.EnableFlashLight::class -> {
            Remix.Others.lightbulb_fill
        }

        Action.EnableHotSpot::class -> {
            Remix.Device.hotspot_fill
        }

        Action.EnableNFC::class -> {
            Remix.Device.nfc_fill
        }

        Action.EnableWifi::class -> {
            Remix.Device.wifi_fill
        }

        Action.EnableSensorsOff::class -> {
            Remix.Device.sensor_line
        }

        Action.StopApp::class -> {
            Remix.Media.stop_fill
        }

        Action.StartAppProcess::class -> {
            Remix.Media.play_fill
        }

        Action.SetAppInactive::class -> {
            Remix.HealthAndMedical.zzz_fill
        }

        Action.EnableApp::class -> {
            Remix.System.toggle_fill
        }

        Action.DisableApp::class -> {
            Remix.System.toggle_fill
        }

        Action.SuspendApp::class -> {
            Remix.UserAndFaces.admin_fill
        }

        Action.UnSuspendApp::class -> {
            Remix.UserAndFaces.admin_fill
        }

        Action.Brk::class -> {
            Remix.System.close_circle_fill
        }

        Action.ShowAlertDialog::class -> {
            Remix.Communication.chat_4_fill
        }

        Action.ShowMenuDialog::class -> {
            Remix.System.menu_3_line
        }

        Action.StartActivity::class -> {
            Remix.Logos.android_fill
        }

        Action.StartActivityIntent::class -> {
            Remix.Logos.android_fill
        }

        Action.StartActivityUrlSchema::class -> {
            Remix.Business.links_fill
        }

        Action.ExpandNotification::class -> {
            Remix.System.notification_badge_fill
        }

        Action.PostNotification::class -> {
            Remix.Media.notification_fill
        }

        Action.RemoteMVEL::class -> {
            Remix.Business.cloud_fill
        }

        Action.WhileLoop::class -> {
            Remix.System.loop_left_fill
        }

        Action.WriteGV::class -> {
            Remix.Document.file_edit_fill
        }

        Action.CreateGV::class -> {
            Remix.Document.file_edit_fill
        }

        Action.DeleteGV::class -> {
            Remix.Document.file_edit_fill
        }

        Action.MapNav::class -> {
            Remix.Map.navigation_fill
        }

        Action.FromDA::class -> {
            Remix.Editor.link
        }

        Action.FindAndClickViewByText::class -> {
            Remix.Business.window_2_fill
        }

        Action.InputTap::class -> {
            Remix.System.touch_fill
        }

        Action.InputSwipe::class -> {
            Remix.System.swipe_fill
        }

        Action.WaitForIdle::class -> {
            Remix.System.hourglass_fill
        }

        Action.IfThenElse::class -> {
            Remix.Editor.text_wrap
        }

        Action.NoAction::class -> {
            Remix.Media.album_fill
        }

        Action.ReadClipboard::class -> {
            Remix.Document.clipboard_fill
        }

        Action.WriteClipboard::class -> {
            Remix.Document.clipboard_fill
        }

        Action.CreatePkgSet::class -> {
            Remix.Document.folder_4_line
        }

        Action.SetRingerMode::class -> {
            Remix.Media.volume_vibrate_fill
        }

        Action.SetBrightness::class -> {
            Remix.System.brightness_5_fill
        }

        Action.EnableAutoBrightness::class -> {
            Remix.System.brightness_auto_fill
        }

        Action.DisableAutoBrightness::class -> {
            Remix.System.brightness_7_fill
        }

        Action.StopService::class -> {
            Remix.Media.stop_line
        }

        Action.RemoveTasks::class -> {
            Remix.System.close_circle_fill
        }

        Action.RemoveNotificationForPackage::class -> {
            Remix.System.notification_badge_fill
        }

        Action.KillProcessByName::class -> {
            Remix.Media.stop_circle_fill
        }

        Action.HttpRequest::class -> {
            Remix.Business.global_line
        }

        Action.InjectKeyCode::class -> {
            Remix.Device.keyboard_fill
        }

        Action.SleepScreen::class -> {
            Remix.Device.shut_down_line
        }

        Action.WakeupScreen::class -> {
            Remix.Weather.sun_line
        }

        Action.StartLastApp::class -> {
            Remix.Arrows.arrow_left_circle_fill
        }

        Action.StopCurrentApp::class -> {
            Remix.Media.stop_fill
        }

        Action.AdjustVolume::class -> {
            Remix.Media.volume_up_line
        }

        Action.Vibrate::class -> {
            Remix.Media.volume_vibrate_line
        }

        Action.AudioRecording::class -> {
            Remix.Media.mic_2_fill
        }

        Action.SwitchMobileDataSlot::class -> {
            Remix.Device.sim_card_2_fill
        }

        Action.CreateLocalVar::class -> {
            Remix.Document.file_edit_fill
        }

        Action.WriteLocalVar::class -> {
            Remix.Document.file_edit_fill
        }

        Action.StartGestureRecording::class,
        Action.StopGestureRecording::class,
        Action.ToggleGestureRecording::class -> {
            Remix.Editor.sketching
        }

        else -> {
            Remix.Document.file_unknow_fill
        }
    }
}
