package tornaco.apps.shortx.ui.addrule.condition.mapper

import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import kotlin.reflect.KClass


fun iconForCondition(condition: KClass<out Condition>): String {
    return when (condition) {
        Condition.CurrentForegroundApp::class -> {
            Remix.Logos.android_fill
        }

        Condition.CurrentForegroundAppByPkg::class -> {
            Remix.Logos.android_fill
        }

        Condition.AppIsRunning::class -> {
            Remix.System.refresh_fill
        }

        Condition.AppIsNotRunning::class -> {
            Remix.Media.stop_line
        }

        Condition.ServiceIsRunning::class -> {
            Remix.System.refresh_fill
        }

        Condition.AppHasAudioFocus::class -> {
            Remix.Media.music_2_fill
        }

        Condition.AppHasTask::class -> {
            Remix.Document.task_fill
        }

        Condition.MVEL::class -> {
            Remix.Development.code_fill
        }

        Condition.JS::class -> {
            Remix.Development.javascript_fill
        }

        Condition.TRUE::class -> {
            Remix.System.check_fill
        }

        Condition.EvaluateGlobalVar::class -> {
            Remix.Document.file_list_fill
        }

        Condition.EvaluateContextVar::class -> {
            Remix.Document.file_list_fill
        }

        Condition.EvaluateLocalVar::class -> {
            Remix.Document.file_list_fill
        }

        Condition.BatteryPercent::class -> {
            Remix.Device.battery_2_fill
        }

        Condition.ChargeState::class -> {
            Remix.Device.battery_charge_line
        }

        Condition.PlugState::class -> {
            Remix.Others.plug_2_fill
        }

        Condition.ScreenIsOn::class -> {
            Remix.Device.smartphone_line
        }

        Condition.VPNIsConnected::class -> {
            Remix.Others.key_2_fill
        }

        Condition.TimeInRange::class -> {
            Remix.System.time_fill
        }

        Condition.RequireWifiConnected::class -> {
            Remix.Device.wifi_fill
        }

        Condition.RequireWifiDisconnected::class -> {
            Remix.Device.signal_wifi_line
        }

        Condition.RequireBTConnected::class -> {
            Remix.Device.bluetooth_fill
        }


        Condition.RequireBTDeviceFound::class -> {
            Remix.Device.bluetooth_line
        }

        Condition.RequireBTDisconnected::class -> {
            Remix.Device.bluetooth_fill
        }

        Condition.RequireMobileDataEnabled::class -> {
            Remix.Arrows.arrow_up_down_line
        }

        Condition.AppHasNotification::class -> {
            Remix.System.notification_badge_line
        }

        Condition.KeyguardIsLocked::class -> {
            Remix.System.lock_line
        }

        Condition.IsInCall::class -> {
            Remix.Device.phone_line
        }

        Condition.IsRinging::class -> {
            Remix.Media.volume_vibrate_line
        }

        Condition.HasNodeOnScreen::class -> {
            Remix.Design.focus_3_line
        }

        Condition.IsRuleEnabled::class -> {
            Remix.System.toggle_fill
        }

        Condition.IsHeadsetPlug::class -> {
            Remix.Media.headphone_fill
        }

        Condition.RequireScreenRotate::class -> {
            Remix.Device.smartphone_fill
        }

        Condition.RequireWindowRotation::class -> {
            Remix.Device.smartphone_fill
        }

        Condition.RequireDelay::class -> {
            Remix.System.alarm_fill
        }

        Condition.RequireSensorOff::class -> {
            Remix.Device.sensor_fill
        }

        Condition.CurrentActivity::class -> {
            Remix.Logos.android_fill
        }

        Condition.RequireTileState::class -> {
            Remix.Device.server_fill
        }

        Condition.RequireFactTag::class -> {
            Remix.Finance.price_tag_3_fill
        }

        Condition.RequireAPMMode::class -> {
            Remix.Map.flight_takeoff_fill
        }

        Condition.RequireIMEVisibility::class -> {
            Remix.Device.keyboard_fill
        }

        Condition.AppHasWindowFocus::class -> {
            Remix.Design.focus_2_fill
        }

        Condition.ConnectedWifiSignal::class -> {
            Remix.Device.signal_wifi_2_line
        }

        Condition.TheXXTimeToday::class -> {
            Remix.Editor.number_8
        }

        else -> {
            Remix.Document.file_unknow_fill
        }
    }
}

fun iconForOp(op: Op): String {
    return when (op) {
        Op.All -> {
            Remix.System.check_double_fill
        }

        Op.Any -> {
            Remix.Editor.asterisk
        }

        Op.None -> {
            Remix.System.checkbox_blank_circle_fill
        }

        is Op.MVEL -> {
            Remix.Development.code_fill
        }
    }
}