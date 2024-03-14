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

        Action.Toggle5G::class -> {
            Remix.Editor.number_5
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

        Action.ExecuteJS::class -> {
            Remix.Development.javascript_fill
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

        Action.LaunchApp::class, Action.LaunchAppByPkg::class -> {
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

        Action.StopAppByPkg::class -> {
            Remix.Media.stop_fill
        }

        Action.StartAppProcess::class, Action.StartAppProcessByPkg::class -> {
            Remix.Media.play_fill
        }

        Action.SetAppInactive::class, Action.SetAppInactiveByPkg::class -> {
            Remix.HealthAndMedical.zzz_fill
        }

        Action.EnableApp::class, Action.EnableAppByPkg::class -> {
            Remix.System.toggle_fill
        }

        Action.DisableApp::class, Action.DisableAppByPkg::class -> {
            Remix.System.toggle_fill
        }

        Action.SuspendApp::class, Action.SuspendAppByPkg::class -> {
            Remix.UserAndFaces.admin_fill
        }

        Action.UnSuspendApp::class, Action.UnSuspendAppByPkg::class -> {
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

        Action.ShowTextFieldDialog::class -> {
            Remix.Editor.input_cursor_move
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

        Action.StartActivityIntentUri::class -> {
            Remix.Business.links_fill
        }

        Action.ExpandNotification::class -> {
            Remix.System.notification_badge_fill
        }

        Action.PostNotification::class -> {
            Remix.Media.notification_fill
        }

        Action.RemoveNotification::class -> {
            Remix.Media.notification_3_fill
        }

        Action.ClickNotification::class -> {
            Remix.System.notification_badge_fill
        }

        Action.ClickNotificationActionButton::class -> {
            Remix.System.function_fill
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
            Remix.System.add_line
        }

        Action.DeleteGV::class -> {
            Remix.System.delete_bin_2_line
        }

        Action.CreateLocalVar::class -> {
            Remix.System.add_line
        }

        Action.WriteLocalVar::class -> {
            Remix.Document.file_edit_fill
        }

        Action.MapNav::class -> {
            Remix.Map.navigation_fill
        }

        Action.MapQueryBus::class -> {
            Remix.Map.bus_2_fill
        }

        Action.FromDA::class -> {
            Remix.Editor.link
        }

        Action.FindAndClickViewByText::class -> {
            Remix.Business.window_2_fill
        }

        Action.FindAndClickViewById::class -> {
            Remix.Business.window_2_fill
        }

        Action.FindAndClickMatchedView::class -> {
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

        Action.GetTextFromScreenNode::class -> {
            Remix.Document.file_text_fill
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

        Action.StartService::class -> {
            Remix.Business.service_line
        }

        Action.RemoveTasks::class, Action.RemoveTasksByPkg::class -> {
            Remix.System.close_circle_fill
        }

        Action.RemoveNotificationForPackage::class, Action.RemoveNotificationForPackageByPkg::class -> {
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

        Action.InjectCombineKeyCode::class -> {
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

        Action.StopAudioRecording::class -> {
            Remix.Media.stop_circle_fill
        }

        Action.SwitchMobileDataSlot::class -> {
            Remix.Device.sim_card_2_fill
        }


        Action.InjectGestureRecording::class,
        Action.StartGestureRecording::class,
        Action.StopGestureRecording::class,
        Action.ToggleGestureRecording::class -> {
            Remix.Editor.sketching
        }

        Action.EnableUniversalCopy::class -> {
            Remix.Document.file_copy_line
        }

        Action.EnableViewIdViewer::class -> {
            Remix.System.search_eye_fill
        }

        Action.TTS::class -> {
            Remix.Communication.speak_line
        }

        Action.SetWallpaper::class -> {
            Remix.Media.landscape_fill
        }

        Action.SetRuleEnabled::class -> {
            Remix.System.toggle_fill
        }

        Action.MediaPlayback::class -> {
            Remix.Media.play_mini_fill
        }

        Action.SetVolume::class -> {
            Remix.Media.volume_up_fill
        }

        Action.BiometricVerify::class -> {
            Remix.System.lock_password_fill
        }

        Action.ClickTile::class -> {
            Remix.Device.server_fill
        }

        Action.AppShortcut::class -> {
            Remix.System.external_link_line
        }

        Action.SetAPMModeEnabled::class -> {
            Remix.Map.flight_takeoff_line
        }

        Action.SetScreenTimeout::class -> {
            Remix.System.brightness_5_fill
        }

        Action.SetScreenRotate::class -> {
            Remix.Design.clockwise_fill
        }

        Action.ScrollViewTo::class -> {
            Remix.Arrows.expand_up_down_fill
        }

        Action.WaitUtilConditionMatch::class -> {
            Remix.System.loader_2_fill
        }

        Action.PerformContextMenuAction::class -> {
            Remix.Design.edit_box_fill
        }

        Action.ShowDrawBoard::class -> {
            Remix.Design.markup_fill
        }

        Action.ShowDrawBoard::class -> {
            Remix.Document.clipboard_fill
        }

        Action.ConnectWifi::class -> {
            Remix.Device.wifi_fill
        }

        Action.DisconnectCurrentWifi::class -> {
            Remix.Device.signal_wifi_line
        }

        Action.StayAwake::class -> {
            Remix.System.eye_2_fill
        }

        Action.ForEachPkgSet::class -> {
            Remix.System.loop_right_fill
        }

        Action.LockDeviceNow::class -> {
            Remix.System.lock_2_fill
        }

        Action.PlayRingtone::class -> {
            Remix.Media.music_2_fill
        }

        Action.DownloadFile::class -> {
            Remix.System.download_2_fill
        }

        Action.SendSMS::class -> {
            Remix.Communication.chat_smile_fill
        }

        Action.SwitchCase::class -> {
            Remix.Editor.flow_chart
        }

        Action.StopAllActions::class -> {
            Remix.Media.stop_circle_fill
        }

        else -> {
            Remix.Document.file_unknow_fill
        }
    }
}
