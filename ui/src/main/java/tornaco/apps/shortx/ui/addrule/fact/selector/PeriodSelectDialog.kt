package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.alarm.toLocalTime
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import java.time.LocalTime

class PeriodSelectorState(
    val selected: (Fact.RandomInPeriod) -> Unit
) : CommonDialogState() {
    var start: LocalTime? by mutableStateOf(null)
    var end: LocalTime? by mutableStateOf(null)

    var repeat by mutableStateOf(emptyList<WeekDay>())

    val isValid
        get() = run { start != null && end != null && end!!.isAfter(start!!) }

    fun addRepeat(weekDay: WeekDay) {
        repeat = repeat.toMutableList().apply {
            add(weekDay)
        }
    }

    fun removeRepeat(weekDay: WeekDay) {
        repeat = repeat.toMutableList().apply {
            remove(weekDay)
        }
    }

    fun show(current: Fact.RandomInPeriod? = null) {
        this.start = current?.start?.toLocalTime()
        this.end = current?.end?.toLocalTime()
        this.repeat = current?.repeatDays?.daysList ?: emptyList()
        show()
    }
}

@Composable
fun rememberPeriodSelectorState(
    selected: (Fact.RandomInPeriod) -> Unit
): PeriodSelectorState {
    return remember {
        PeriodSelectorState(selected)
    }
}

@Composable
fun PeriodSelector(state: PeriodSelectorState) {
    if (state.isShowing) {
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.fact.time.of.a.day.random"])
        }, buttons = {
            TextButton(onClick = {
                if (state.isValid) {
                    state.selected(
                        Fact.RandomInPeriod(
                            start = TimeOfADay
                                .newBuilder()
                                .setHour(state.start!!.hour)
                                .setMinutes(state.start!!.minute)
                                .setSeconds(state.start!!.second)
                                .build(), end = TimeOfADay
                                .newBuilder()
                                .setHour(state.end!!.hour)
                                .setMinutes(state.end!!.minute)
                                .setSeconds(state.end!!.second)
                                .build(),
                            repeatDays = RepeatDays
                                .newBuilder()
                                .addAllDays(state.repeat)
                                .build()
                        )
                    )
                    state.dismiss()
                }
            }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }, content = {
            PeriodContent(state)
        })
    }
}

@Composable
fun PeriodContent(state: PeriodSelectorState) {
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start
    ) {
        TimeContent(state)
        SectionSpacer()
        RepeatSelector(state)
    }
}

@Composable
private fun TimeContent(state: PeriodSelectorState) {
    val startSelectState = rememberTimePickerDialogState(onSelected = {
        state.start = it
    })
    TimePickerDialog(dialogState = startSelectState)
    val endSelectState = rememberTimePickerDialogState(onSelected = {
        state.end = it
    })
    TimePickerDialog(dialogState = endSelectState)

    TipRow(text = LocalI18N.current["ui.fact.time.of.a.day.random.summary"])
    StandardSpacer()
    StandardSpacer()

    SmallTitle(text = LocalI18N.current["ui.fact.time.of.a.day.random.start"])
    FilledTonalButton(onClick = { startSelectState.show() }) {
        Text(
            text = state.start?.let { DateUtils.formatLocalTime(it) }
                ?: LocalI18N.current["ui.common.unselected"]
        )
    }

    StandardSpacer()

    SmallTitle(text = LocalI18N.current["ui.fact.time.of.a.day.random.end"])
    FilledTonalButton(onClick = { endSelectState.show() }) {
        Text(
            text = state.end?.let { DateUtils.formatLocalTime(it) }
                ?: LocalI18N.current["ui.common.unselected"]
        )
    }
}


@Composable
private fun RepeatSelector(state: PeriodSelectorState) {
    Column(
        modifier = Modifier.padding(top = 24.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        SmallTitle(
            text = LocalI18N.current["ui.fact.time.of.a.day.repeat"],
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
                val isSelected = state.repeat.contains(weekDay)
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