package tornaco.apps.shortx.services.rule.fact

import android.content.Context
import tornaco.apps.shortx.services.ShortXContext

internal fun allTriggers(context: Context, shortXContext: ShortXContext) = listOf(
    ScreenStateTrigger(context, shortXContext),
    AlarmTrigger(context, shortXContext),
)