@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.common.toTimeOfADay
import tornaco.apps.shortx.core.proto.condition.CalendarDayType
import tornaco.apps.shortx.core.proto.common.TimeOfADay
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.proto.fact.WorkdayFilter
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.fact.mapper.toDisplayTime
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.time.LocalTime
import tornaco.apps.shortx.core.i18n.I18N_R

class AlarmSelectorState(
    val selected: (Alarm) -> Unit
) : CommonDialogState() {
    var time: LocalTime by mutableStateOf(LocalTime.now())
    var repeat by mutableStateOf(emptyList<WeekDay>())
    var dayType by mutableStateOf(CalendarDayType.AnyDay)

    fun show(
        initialTime: LocalTime,
        initialRepeat: List<WeekDay>,
        initialDayType: CalendarDayType,
    ) {
        this.time = initialTime
        this.repeat = initialRepeat
        this.dayType = initialDayType

        super.show()
    }

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
}

@Composable
fun rememberAlarmSelectorState(
    selected: (Alarm) -> Unit
): AlarmSelectorState {
    return remember {
        AlarmSelectorState(selected)
    }
}

@Composable
fun AlarmSelector(state: AlarmSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = {
                        ShortXTopAppBar(
                            title = { Text(text = LocalI18N.current[I18N_R.UI_FACT_TIME_OF_A_DAY]) },
                            navigationIcon = {
                                IconButton(onClick = { state.dismiss() }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            actions = {
                                IconButton(onClick = {
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
                                                    .addAllDays(state.repeat)
                                                    .build()
                                            )
                                            .apply {
                                                state.dayType.toWorkdayFilterOrNull()
                                                    ?.let { setWorkdayFilter(it) }
                                            }
                                            .build()
                                    )
                                    state.dismiss()
                                }) {
                                    RemixIcon(remixName = Remix.System.check_line)
                                }
                            }
                        )
                    }) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .padding(paddingValues)
                        ) {
                            AlarmContent(state)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmContent(state: AlarmSelectorState) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val startSelectState = rememberTimePickerDialogState(
            initialHour = state.time.hour,
            initialMinute = state.time.minute,
            onSelected = {
                state.time = it
            })
        TimePickerDialog(dialogState = startSelectState)

        Button(onClick = {
            startSelectState.show()
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RemixIcon(remixName = Remix.System.alarm_fill)
                SmallSpacer()
                Text(text = state.time.toTimeOfADay().toDisplayTime())
            }
        }

        SectionSpacer()
        RepeatSelector(state)
        CalendarDayTypeSelector(
            selected = state.dayType,
            onSelected = { state.dayType = it }
        )
    }
}

@Composable
private fun RepeatSelector(state: AlarmSelectorState) {
    Column(
        modifier = Modifier.padding(bottom = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = LocalI18N.current[I18N_R.UI_FACT_TIME_OF_A_DAY_REPEAT],
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

private fun CalendarDayType.toWorkdayFilterOrNull(): WorkdayFilter? {
    if (this == CalendarDayType.AnyDay || this == CalendarDayType.UNRECOGNIZED) {
        return null
    }
    return WorkdayFilter.newBuilder().setDayType(this).build()
}
