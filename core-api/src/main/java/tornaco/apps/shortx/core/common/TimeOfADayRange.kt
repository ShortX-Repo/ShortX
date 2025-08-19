package tornaco.apps.shortx.core.common

import tornaco.apps.shortx.core.proto.common.TimeOfADayRange

fun TimeOfADayRange.timeOfADayRangeToString() =
    "${start.timeOfADayToString()} - ${end.timeOfADayToString()}"