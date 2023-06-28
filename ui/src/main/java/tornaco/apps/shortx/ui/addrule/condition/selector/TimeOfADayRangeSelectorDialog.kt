package tornaco.apps.shortx.ui.addrule.condition.selector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import tornaco.apps.shortx.core.alarm.toLocalTime
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.common.TimeOfADayRange
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.fact.selector.TimePickerDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberTimePickerDialogState
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import java.time.LocalTime

class TimeOfADayRangeSelectorState(
    val selected: (TimeOfADayRange) -> Unit
) : CommonDialogState() {
    var start: LocalTime? by mutableStateOf(null)
    var end: LocalTime? by mutableStateOf(null)

    val isValid
        get() = run { start != null && end != null && end!!.isAfter(start!!) }


    fun show(current: TimeOfADayRange? = null) {
        this.start = current?.start?.toLocalTime()
        this.end = current?.end?.toLocalTime()
        show()
    }
}

@Composable
fun rememberTimeOfADayRangeSelectorState(
    selected: (TimeOfADayRange) -> Unit
): TimeOfADayRangeSelectorState {
    return remember {
        TimeOfADayRangeSelectorState(selected)
    }
}

@Composable
fun TimeOfADayRangeSelector(state: TimeOfADayRangeSelectorState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.common.time.of.day.selector.title"])
        }, buttons = {
            TextButton(onClick = {
                if (state.isValid) {
                    state.selected(
                        TimeOfADayRange.newBuilder()
                            .setStart(
                                TimeOfADay
                                    .newBuilder()
                                    .setHour(state.start!!.hour)
                                    .setMinutes(state.start!!.minute)
                                    .setSeconds(state.start!!.second)
                                    .build()
                            )
                            .setEnd(
                                TimeOfADay
                                    .newBuilder()
                                    .setHour(state.end!!.hour)
                                    .setMinutes(state.end!!.minute)
                                    .setSeconds(state.end!!.second)
                                    .build()
                            )
                            .build()
                    )
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            TimeOfADayRangeContent(state)
        })
    }
}

@Composable
fun TimeOfADayRangeContent(state: TimeOfADayRangeSelectorState) {
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start
    ) {
        TimeContent(state)
    }
}

@Composable
private fun TimeContent(state: TimeOfADayRangeSelectorState) {
    val startSelectState = rememberTimePickerDialogState(onSelected = {
        state.start = it
    })
    TimePickerDialog(dialogState = startSelectState)
    val endSelectState = rememberTimePickerDialogState(onSelected = {
        state.end = it
    })
    TimePickerDialog(dialogState = endSelectState)

    SmallTitle(text = LocalI18N.current["ui.common.time.of.day.range.start"])
    SmallSpacer()
    FilledTonalButton(onClick = { startSelectState.show() }) {
        Text(
            text = state.start?.let { DateUtils.formatLocalTime(it) }
                ?: LocalI18N.current["ui.common.unselected"]
        )
    }

    StandardSpacer()

    SmallTitle(text = LocalI18N.current["ui.common.time.of.day.range.end"])
    SmallSpacer()
    FilledTonalButton(onClick = { endSelectState.show() }) {
        Text(
            text = state.end?.let { DateUtils.formatLocalTime(it) }
                ?: LocalI18N.current["ui.common.unselected"]
        )
    }
}