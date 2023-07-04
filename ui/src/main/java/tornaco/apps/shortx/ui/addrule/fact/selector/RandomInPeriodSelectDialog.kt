package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.verticalScroll
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
import tornaco.apps.shortx.core.common.sortedDays
import tornaco.apps.shortx.core.common.toTimeOfADay
import tornaco.apps.shortx.core.proto.fact.RepeatDays
import tornaco.apps.shortx.core.proto.fact.WeekDay
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DialogTitle
import tornaco.apps.shortx.ui.base.ErrorTipRow
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXDialog
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import java.time.LocalTime

class RandomInPeriodSelectorState(
    val selected: (Fact.RandomInPeriod) -> Unit
) : CommonDialogState() {
    var start: LocalTime? by mutableStateOf(null)
    var end: LocalTime? by mutableStateOf(null)

    var repeat by mutableStateOf(emptyList<WeekDay>())

    val isValid
        get() = run {
            checkError()
            start != null && end != null && end!!.isAfter(start!!)
        }

    var errorMessages by mutableStateOf(emptyList<String>())

    private fun checkError() {
        val isStartEndError = (end != null && start != null && !end!!.isAfter(start))
        errorMessages = if (isStartEndError) {
            listOf("ui.fact.time.of.a.day.error.end.before.start")
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
        ShortXDialog(onDismissRequest = { state.dismiss() }, title = {
            DialogTitle(text = LocalI18N.current["ui.fact.time.of.a.day.random"])
        }, buttons = {
            TextButton(onClick = {
                if (state.isValid) {
                    state.selected(
                        Fact.RandomInPeriod(
                            start = state.start!!.toTimeOfADay(),
                            end = state.end!!.toTimeOfADay(),
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
private fun PeriodContent(state: RandomInPeriodSelectorState) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start
    ) {
        TipRow(text = LocalI18N.current["ui.fact.time.of.a.day.random.summary"])
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

    FieldTitle(text = LocalI18N.current["ui.fact.time.of.a.day.random.start"])
    FilledTonalButton(onClick = { startSelectState.show() }) {
        Text(
            text = start?.let { DateUtils.formatLocalTime(it) }
                ?: LocalI18N.current["ui.common.unselected"]
        )
    }

    StandardSpacer()

    FieldTitle(text = LocalI18N.current["ui.fact.time.of.a.day.random.end"])
    FilledTonalButton(onClick = { endSelectState.show() }) {
        Text(
            text = end?.let { DateUtils.formatLocalTime(it) }
                ?: LocalI18N.current["ui.common.unselected"]
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