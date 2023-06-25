package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.base.timepicker.BottomTimePicker
import java.time.LocalTime

class AlarmSelectorState(
    initialTime: LocalTime,
    initialRepeat: Array<WeekDay>,
    val selected: (Alarm) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    var time: LocalTime = initialTime

    var repeat = mutableStateOf(initialRepeat.toList())

    fun addRepeat(weekDay: WeekDay) {
        repeat.value = repeat.value.toMutableList().apply {
            add(weekDay)
        }
    }

    fun removeRepeat(weekDay: WeekDay) {
        repeat.value = repeat.value.toMutableList().apply {
            remove(weekDay)
        }
    }

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberAlarmSelectorState(
    initialTime: LocalTime,
    initialRepeat: Array<WeekDay>,
    selected: (Alarm) -> Unit
): AlarmSelectorState {
    return remember {
        AlarmSelectorState(initialTime, initialRepeat, selected)
    }
}

@Composable
fun AlarmSelector(state: AlarmSelectorState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.fact.time.of.a.day"])
        }, buttons = {
            TextButton(onClick = {
                state.selected(
                    Alarm
                        .newBuilder()
                        .setTriggerAt(
                            TimeOfADay
                                .newBuilder()
                                .setHour(state.time.hour)
                                .setMinutes(state.time.minute)
                                .setSeconds(state.time.second)
                                .build()
                        )
                        .setRepeat(
                            RepeatDays
                                .newBuilder()
                                .addAllDays(state.repeat.value)
                                .build()
                        )
                        .build()
                )
                state.dismiss()
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            AlarmContent(state)
        })
    }
}

@Composable
fun AlarmContent(state: AlarmSelectorState) {
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BottomTimePicker(is24TimeFormat = false, currentTime = state.time) {
            state.time = it
        }
        RepeatSelector(state)
    }
}

@Composable
private fun RepeatSelector(state: AlarmSelectorState) {
    Column(
        modifier = Modifier.padding(top = 24.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = LocalI18N.current["ui.fact.time.of.a.day.repeat"],
            style = MaterialTheme.typography.bodySmall
        )
        StandardSpacer()
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            arrayOf(
                WeekDay.SUNDAY,
                WeekDay.MONDAY,
                WeekDay.TUESDAY,
                WeekDay.WEDNESDAY,
                WeekDay.THURSDAY,
                WeekDay.FRIDAY,
                WeekDay.SATURDAY
            ).forEach { weekDay ->
                val isSelected = state.repeat.value.contains(weekDay)
                Column(
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else ColorDefaults.backgroundSurfaceColor())
                        .clickable {
                            if (isSelected) {
                                state.removeRepeat(weekDay)
                            } else {
                                state.addRepeat(weekDay)
                            }
                        }
                        .padding(4.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = weekDay.name.first().toString().uppercase(),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = W600)
                    )
                }
            }
        }
    }
}