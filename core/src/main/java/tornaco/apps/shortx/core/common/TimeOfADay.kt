package tornaco.apps.shortx.core.common

import tornaco.apps.shortx.core.alarm.toLocalTime
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.util.DateUtils

fun TimeOfADay.timeOfADayToString(): String = DateUtils.formatLocalTime(toLocalTime())