package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.wifi.WifiStateTrigger

internal fun allTriggers(context: Context, shortXContext: ShortXContext) = mutableListOf(
    RuleHooksTrigger(context, shortXContext),
    ScreenStateTrigger(context, shortXContext),
    AlarmTrigger(context, shortXContext),
    FixedInPeriodTrigger(context, shortXContext),
    RandomInPeriodTrigger(context, shortXContext),
    WifiStateTrigger(context, shortXContext),
    BTStateTrigger(context, shortXContext),
    LocationStateTrigger(context, shortXContext),
    UiModeStateTrigger(context, shortXContext),
    ConnectivityStateTrigger(context, shortXContext),
    TorchStateTrigger(context, shortXContext),
    DNDStateTrigger(context, shortXContext),
    PackageInstallTrigger(context, shortXContext),
    AudioFocusTrigger(context, shortXContext),
    BatteryLevelTrigger(context, shortXContext),
    ChargeStateTrigger(context, shortXContext),
    BatteryTemperatureTrigger(context, shortXContext),
    BroadcastTrigger(context, shortXContext),
    StartOpTrigger(context, shortXContext),
    SystemSettingsTrigger(context, shortXContext),
    CallStateTrigger(context, shortXContext),
    ClipboardTrigger(context, shortXContext),
    GlobalVarTrigger(context, shortXContext),
    AccessibilityNodeOnScreenTrigger(context, shortXContext),
    HeadsetPlugTrigger(context, shortXContext),
)