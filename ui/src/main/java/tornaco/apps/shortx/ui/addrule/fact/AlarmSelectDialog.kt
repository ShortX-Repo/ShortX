package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.ui.base.*

class AlarmSelectorState(val selected: (Alarm) -> Unit) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    var h = mutableStateOf(0)
    var m = mutableStateOf(0)
    var s = mutableStateOf(0)

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberAlarmSelectorState(selected: (Alarm) -> Unit): AlarmSelectorState {
    return remember {
        AlarmSelectorState(selected)
    }
}

@Composable
fun AlarmSelector(state: AlarmSelectorState) {
    if (state.isShow) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = "Time of a day")
        }, buttons = {
            TextButton(onClick = {
                state.selected(
                    Alarm.newBuilder()
                        .setTriggerAt(
                            TimeOfADay.newBuilder()
                                .setHour(state.h.value)
                                .setMinutes(state.m.value)
                                .setSeconds(state.s.value)
                                .build()
                        )
                        .setRepeat(RepeatDays.newBuilder().addDays(WeekDay.MONDAY).build())
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmContent(state: AlarmSelectorState) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Hour(state)
        Minute(state)
        Second(state)
        Text(
            text = "Min interval", style = MaterialTheme.typography.labelSmall
        )

        TinySpacer()
        Text(
            text = "Tag", style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun Hour(durationState: AlarmSelectorState) {
    Field(
        durationState.h,
        "Hour",
        0,
        24
    )
}

@Composable
private fun Minute(durationState: AlarmSelectorState) {
    Field(
        durationState.m,
        "Minute",
        15,
        59
    )
}

@Composable
private fun Second(durationState: AlarmSelectorState) {
    Field(
        durationState.s,
        "Second",
        0,
        59
    )
}

@Composable
private fun Field(fieldValue: MutableState<Int>, label: String, min: Int = 0, max: Int) {
    var value by fieldValue
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = label)
        LargeSpacer()

        // -
        IconButton(onClick = {
            if (value > min) {
                value -= 1
            } else {
                value = max
            }
        }) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "-"
            )
        }

        StandardSpacer()
        Text(text = "$value")
        StandardSpacer()

        // +
        IconButton(onClick = {
            if (value < max) {
                value += 1
            } else {
                value = min
            }
        }) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "+"
            )
        }
    }
}