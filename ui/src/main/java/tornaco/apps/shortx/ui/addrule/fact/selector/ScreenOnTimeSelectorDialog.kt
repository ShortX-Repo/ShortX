package tornaco.apps.shortx.ui.addrule.fact.selector


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.fact.ScreenOnTimeStartFrom
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.fact.mapper.factLabelKey
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.suggestion.SuggestedNavEntry
import tornaco.apps.shortx.ui.theme.ShortXTheme

class ScreenOnTimeSelectorState(
    val onSelected: (Fact.ScreenOnTime) -> Unit
) : CommonDialogState() {
    var timeInSeconds by mutableStateOf("60")
    var from by mutableStateOf(ScreenOnTimeStartFrom.ScreenOnTimeStartFrom_LastScreenOff)

    fun show(initialValue: Fact.ScreenOnTime? = null) {
        this.timeInSeconds = initialValue?.timeInSeconds ?: "60"
        this.from = initialValue?.from ?: ScreenOnTimeStartFrom.ScreenOnTimeStartFrom_LastScreenOff

        show()
    }
}

@Composable
fun rememberScreenOnTimeSelectorState(
    onSelected: (Fact.ScreenOnTime) -> Unit
): ScreenOnTimeSelectorState {
    return remember {
        ScreenOnTimeSelectorState(
            onSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenOnTimeSelectorDialog(state: ScreenOnTimeSelectorState) {
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
                                title = { Text(text = LocalI18N.current["ui.fact.screen.on.time"]) },
                                actions = {
                                    IconButton(onClick = {
                                        state.onSelected(
                                            Fact.ScreenOnTime(
                                                timeInSeconds = state.timeInSeconds,
                                                from = state.from
                                            )
                                        )
                                        state.dismiss()
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
                                .padding(paddingValues)
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            FieldTitle(text = LocalI18N.current["ui.fact.screen.on.time"])
                            MediumSpacer()
                            ShortXTextField(state.timeInSeconds, onValueChange = {})


                            LargeSpacer()
                            val fromState = rememberDropdownSelectorState()
                            Box {
                                FilledTonalButton(
                                    onClick = {
                                        fromState.open()
                                    }) {
                                    Text(LocalI18N.current[state.from.factLabelKey()])
                                }
                                DropdownSelector(
                                    state = fromState,
                                    items = ScreenOnTimeStartFrom.entries.toMutableList().apply {
                                        remove(ScreenOnTimeStartFrom.UNRECOGNIZED)
                                    }.map {
                                        DropdownItem(
                                            labelLines = listOf(LocalI18N.current[it.factLabelKey()]),
                                            data = it,
                                        )
                                    }
                                ) {
                                    state.from = it.data
                                }
                            }

                            SectionSpacer()
                            SectionSpacer()
                            SuggestedNavEntry()
                        }
                    }
                }
            }
        }
    }
}
