@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.alarm.FixedSetting
import tornaco.apps.shortx.core.alarm.getTriggerTimeSegments
import tornaco.apps.shortx.core.alarm.toLocalTime
import tornaco.apps.shortx.core.alarm.toProto
import tornaco.apps.shortx.core.common.ofTodayDate
import tornaco.apps.shortx.core.common.sortedDays
import tornaco.apps.shortx.core.common.toTimeOfADay
import tornaco.apps.shortx.core.proto.condition.CalendarDayType
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.proto.fact.WorkdayFilter
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.fact.mapper.label
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.CalendarDayTypeSelector
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.ErrorTipRow
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXFilledTonalButton
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.time.LocalTime
import kotlin.time.Duration.Companion.minutes

class FixedInPeriodSelectorState(
    val selected: (Fact.FixedInPeriod) -> Unit
) : CommonDialogState() {
    var start: LocalTime? by mutableStateOf(null)
    var end: LocalTime? by mutableStateOf(null)
    var repeat by mutableStateOf(emptyList<WeekDay>())
    var fixedSetting: FixedSetting? by mutableStateOf(null)
    var dayType by mutableStateOf(CalendarDayType.AnyDay)

    val isValid
        get() = run {
            checkError()
            start != null && end != null && end!!.isAfter(start!!) && fixedSetting != null
        }

    var errorMessages by mutableStateOf(emptyList<String>())

    fun setStartTime(localTime: LocalTime) {
        this.start = localTime
        checkError()
    }

    fun setEndTime(localTime: LocalTime) {
        this.end = localTime
        checkError()
    }

    private fun checkError() {
        val isStartEndError = (end != null && start != null && !end!!.isAfter(start))
        errorMessages = if (isStartEndError) {
            listOf("ui.fact.time.of.a.day.error.end.before.start")
        } else {
            emptyList()
        }
    }

    fun addRepeat(weekDay: WeekDay) {
        this.repeat = repeat.toMutableList().apply {
            add(weekDay)
        }
    }

    fun removeRepeat(weekDay: WeekDay) {
        this.repeat = repeat.toMutableList().apply {
            remove(weekDay)
        }
    }

    fun setFixedBy(fixedBy: FixedSetting) {
        this.fixedSetting = fixedBy
    }

    fun show(current: Fact.FixedInPeriod? = null) {
        this.start = current?.start?.toLocalTime()
        this.end = current?.end?.toLocalTime()
        this.repeat = current?.repeatDays?.sortedDays ?: emptyList()
        this.fixedSetting = current?.fixedBy
        this.dayType = current?.workdayFilter?.dayType ?: CalendarDayType.AnyDay
        show()
    }
}

@Composable
fun rememberFixedInPeriodSelectorState(
    selected: (Fact.FixedInPeriod) -> Unit
): FixedInPeriodSelectorState {
    return remember {
        FixedInPeriodSelectorState(selected)
    }
}

@Composable
fun FixedPeriodSelector(state: FixedInPeriodSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(text = LocalI18N.current["ui.fact.time.of.a.day.fixed"]) },
                                actions = {
                                    IconButton(onClick = {
                                        if (state.isValid) {
                                            state.selected(
                                                Fact.FixedInPeriod(
                                                    start = state.start!!.toTimeOfADay(),
                                                    end = state.end!!.toTimeOfADay(),
                                                    fixedBy = state.fixedSetting!!,
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
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
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
private fun PeriodContent(state: FixedInPeriodSelectorState) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start
    ) {
        TipRow(text = LocalI18N.current["ui.fact.time.of.a.day.fixed.description"])
        StandardSpacer()

        TimeContent(
            start = state.start,
            end = state.end,
            setStart = { state.setStartTime(it) },
            setEnd = { state.setEndTime(it) }
        )
        SectionSpacer()

        FixedBySelector(state.fixedSetting) { state.setFixedBy(it) }
        SmallSpacer()
        FixedBySummary(state.fixedSetting, state.start, state.end)
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

private fun CalendarDayType.toWorkdayFilterOrDefault(): WorkdayFilter {
    return if (this == CalendarDayType.AnyDay || this == CalendarDayType.UNRECOGNIZED) {
        WorkdayFilter.getDefaultInstance()
    } else {
        WorkdayFilter.newBuilder().setDayType(this).build()
    }
}

@Composable
private fun FixedBySelector(fixedBy: FixedSetting?, setFixedBy: (FixedSetting) -> Unit) {
    val i18N = LocalI18N.current

    val fixedByDropdownState = rememberDropdownSelectorState()

    val timesInputState = rememberTextInputState(
        title = i18N["ui.fact.time.of.a.day.fixed.by.times"],
        message = null,
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onSelected = { input ->
            input.toIntOrNull()?.let {
                setFixedBy(FixedSetting.Times(it))
            }
        })
    TextInputDialog(state = timesInputState)

    val intervalInputState = rememberTextInputState(
        title = i18N["ui.fact.time.of.a.day.fixed.by.interval"],
        message = i18N["ui.fact.time.of.a.day.fixed.by.interval.input.tip"],
        showSymbolButton = false,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onSelected = { input ->
            input.toLongOrNull()?.let {
                setFixedBy(FixedSetting.Interval(it.minutes.inWholeMilliseconds))
            }
        })
    TextInputDialog(state = intervalInputState)

    FieldTitle(text = LocalI18N.current["ui.fact.time.of.a.day.fixed.by.select"])

    Box {
        ShortXFilledTonalButton(onClick = {
            fixedByDropdownState.open()
        }) {
            Text(text = fixedBy?.label(i18N) ?: i18N["ui.common.unselected"])
        }

        DropdownSelector(
            state = fixedByDropdownState,
            items = listOf(
                DropdownItem(
                    labelLines = listOf(i18N["ui.fact.time.of.a.day.fixed.by.times"]),
                    data = "times",
                    icon = {}),
                DropdownItem(
                    labelLines = listOf(i18N["ui.fact.time.of.a.day.fixed.by.interval"]),
                    data = "interval",
                    icon = {}),
            ),
            onSelect = {
                if (it.data == "interval") {
                    intervalInputState.show()
                } else {
                    timesInputState.show()
                }
            })
    }
}


@Composable
private fun FixedBySummary(fixedBy: FixedSetting?, start: LocalTime?, end: LocalTime?) {
    AnimatedVisibility(visible = fixedBy != null) {
        if (fixedBy != null && start != null && end != null) {
            val seg =
                remember(fixedBy, start, end) {
                    getTriggerTimeSegments(
                        fixedBy.toProto(),
                        start.ofTodayDate(),
                        end.ofTodayDate()
                    ).joinToString { DateUtils.formatHHMMSS(it.time) }
                }
            TipBody(text = LocalI18N.current["ui.fact.time.of.a.day.fixed.summary", mapOf("seg" to seg)])
        }
    }
}

