@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.animation.AnimatedVisibility
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
import tornaco.apps.shortx.core.alarm.toLocalTime
import tornaco.apps.shortx.core.common.sortedDays
import tornaco.apps.shortx.core.common.toTimeOfADay
import tornaco.apps.shortx.core.proto.condition.CalendarDayType
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.proto.fact.WorkdayFilter
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.condition.mapper.labelKey as calendarDayTypeLabelKey
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ErrorTipRow
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXFilledTonalButton
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.time.LocalTime
import tornaco.apps.shortx.core.i18n.I18N_R

class RandomInPeriodSelectorState(
    val selected: (Fact.RandomInPeriod) -> Unit
) : CommonDialogState() {
    var start: LocalTime? by mutableStateOf(null)
    var end: LocalTime? by mutableStateOf(null)

    var repeat by mutableStateOf(emptyList<WeekDay>())
    var dayType by mutableStateOf(CalendarDayType.AnyDay)

    val isValid
        get() = run {
            checkError()
            start != null && end != null && end!!.isAfter(start!!)
        }

    var errorMessages by mutableStateOf(emptyList<String>())

    private fun checkError() {
        val isStartEndError = (end != null && start != null && !end!!.isAfter(start))
        errorMessages = if (isStartEndError) {
            listOf(I18N_R.UI_FACT_TIME_OF_A_DAY_ERROR_END_BEFORE_START)
        } else {
            emptyList()
        }
    }

    fun setStartTime(localTime: LocalTime) {
        this.start = localTime
        checkError()
    }

    fun setEndTime(localTime: LocalTime) {
        this.end = localTime
        checkError()
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

    fun show(current: Fact.RandomInPeriod? = null) {
        this.start = current?.start?.toLocalTime()
        this.end = current?.end?.toLocalTime()
        this.repeat = current?.repeatDays?.sortedDays ?: emptyList()
        this.dayType = current?.workdayFilter?.dayType ?: CalendarDayType.AnyDay
        show()
    }
}

@Composable
fun rememberRandomInPeriodSelectorState(
    selected: (Fact.RandomInPeriod) -> Unit
): RandomInPeriodSelectorState {
    return remember {
        RandomInPeriodSelectorState(selected)
    }
}

@Composable
fun PeriodSelector(state: RandomInPeriodSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(topBar = {
                        ShortXTopAppBar(
                            title = { Text(text = LocalI18N.current[I18N_R.UI_FACT_TIME_OF_A_DAY_RANDOM]) },
                            navigationIcon = {
                                IconButton(onClick = { state.dismiss() }) {
                                    RemixIcon(remixName = Remix.System.close_line)
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    if (state.isValid) {
                                        state.selected(
                                            Fact.RandomInPeriod(
                                                start = state.start!!.toTimeOfADay(),
                                                end = state.end!!.toTimeOfADay(),
                                                repeatDays = RepeatDays
                                                    .newBuilder()
                                                    .addAllDays(state.repeat)
                                                    .build(),
                                                workdayFilter = state.dayType.toWorkdayFilterOrDefault()
                                            )
                                        )
                                        state.dismiss()
                                    }
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
                            PeriodContent(state)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodContent(state: RandomInPeriodSelectorState) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start
    ) {
        TipRow(text = LocalI18N.current[I18N_R.UI_FACT_TIME_OF_A_DAY_RANDOM_SUMMARY])
        StandardSpacer()

        TimeContent(
            start = state.start,
            end = state.end,
            setStart = { state.setStartTime(it) },
            setEnd = { state.setEndTime(it) }
        )
        SectionSpacer()
        RepeatSelector(
            repeat = state.repeat,
            addRepeat = { state.addRepeat(it) },
            removeRepeat = { state.removeRepeat(it) })
        CalendarDayTypeSelector(
            selected = state.dayType,
            onSelected = { state.dayType = it }
        )

        AnimatedVisibility(state.errorMessages.isNotEmpty()) {
            Column {
                StandardSpacer()
                state.errorMessages.forEach {
                    ErrorTipRow(text = LocalI18N.current[it])
                }
            }
        }
    }
}

@Composable
fun TimeContent(
    start: LocalTime?,
    end: LocalTime?,
    setStart: (LocalTime) -> Unit,
    setEnd: (LocalTime) -> Unit
) {
    val startSelectState = rememberTimePickerDialogState(onSelected = {
        setStart(it)
    })
    TimePickerDialog(dialogState = startSelectState)
    val endSelectState = rememberTimePickerDialogState(onSelected = {
        setEnd(it)
    })
    TimePickerDialog(dialogState = endSelectState)

    FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_TIME_OF_A_DAY_RANDOM_START])
    ShortXFilledTonalButton(onClick = { startSelectState.show() }) {
        Text(
            text = start?.let { DateUtils.formatLocalTime(it) }
                ?: LocalI18N.current[I18N_R.UI_COMMON_UNSELECTED]
        )
    }

    StandardSpacer()

    FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_TIME_OF_A_DAY_RANDOM_END])
    ShortXFilledTonalButton(onClick = { endSelectState.show() }) {
        Text(
            text = end?.let { DateUtils.formatLocalTime(it) }
                ?: LocalI18N.current[I18N_R.UI_COMMON_UNSELECTED]
        )
    }
}


@Composable
fun RepeatSelector(
    repeat: List<WeekDay>,
    addRepeat: (WeekDay) -> Unit,
    removeRepeat: (WeekDay) -> Unit,
) {
    Column(
        modifier = Modifier.padding(top = 24.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        FieldTitle(
            text = LocalI18N.current[I18N_R.UI_FACT_TIME_OF_A_DAY_REPEAT],
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
                val isSelected = repeat.contains(weekDay)
                Column(
                    modifier = Modifier
                        .width(36.dp)
                        .height(36.dp)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else ColorDefaults.backgroundSurfaceColor())
                        .clickable {
                            if (isSelected) {
                                removeRepeat(weekDay)
                            } else {
                                addRepeat(weekDay)
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

@Composable
fun CalendarDayTypeSelector(
    selected: CalendarDayType,
    onSelected: (CalendarDayType) -> Unit,
) {
    Column(
        modifier = Modifier.padding(bottom = 32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        FieldTitle(text = LocalI18N.current[I18N_R.UI_CONDITIONS_CALENDAR_DAY_MATCH])
        StandardSpacer()
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(
                CalendarDayType.AnyDay,
                CalendarDayType.WorkdayOnly,
                CalendarDayType.HolidayOnly,
                CalendarDayType.WeekendOnly,
                CalendarDayType.TransferWorkdayOnly,
            ).forEach { dayType ->
                val isSelected = selected == dayType
                Text(
                    text = LocalI18N.current[dayType.calendarDayTypeLabelKey()],
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                ColorDefaults.backgroundSurfaceColor()
                            }
                        )
                        .clickable { onSelected(dayType) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = W600)
                )
            }
        }
        StandardSpacer()
        CalendarDayTypeExplanation()
    }
}

private fun CalendarDayType.toWorkdayFilterOrDefault(): WorkdayFilter {
    return if (this == CalendarDayType.AnyDay || this == CalendarDayType.UNRECOGNIZED) {
        WorkdayFilter.getDefaultInstance()
    } else {
        WorkdayFilter.newBuilder().setDayType(this).build()
    }
}
