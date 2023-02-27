package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.wifi.WifiStateTrigger

internal fun allTriggers(context: Context, shortXContext: ShortXContext) = listOf(
    ScreenStateTrigger(context, shortXContext),
    AlarmTrigger(context, shortXContext),
    WifiStateTrigger(context, shortXContext),
)