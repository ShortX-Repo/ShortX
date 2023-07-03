package tornaco.apps.shortx.core.common

import tornaco.apps.shortx.core.proto.fact.RepeatDays

val RepeatDays.sortedDays get() = daysList.sortedBy { it.ordinal }