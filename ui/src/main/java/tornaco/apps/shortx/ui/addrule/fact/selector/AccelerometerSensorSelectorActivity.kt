@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.fact.SensorValueTrend
import tornaco.apps.shortx.core.proto.fact.ValueConstraints
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.toDisplayName
import tornaco.apps.shortx.ui.addrule.fact.mapper.displayNameKey
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SectionSpacer
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState


@AndroidEntryPoint
class AccelerometerSensorSelectorActivity : BaseSelectorActivity<Fact.AccelerometerSensor>() {

    @Composable
    override fun BaseSelectorActivity<Fact.AccelerometerSensor>.SelectorContent(currentData: Fact.AccelerometerSensor?) {
        Selector(currentData)
    }

    @Composable
    private fun Selector(current: Fact.AccelerometerSensor?) {
        val vm = hiltViewModel<AccelerometerSensorSelectorViewModel>()
        LaunchedEffect(vm) {
            vm.initFrom(current)
        }
        val state by vm.state.collectAsState()

        Scaffold(modifier = Modifier,
            topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(LocalI18N.current["ui.fact.sensor.accelerometer"]) },
                    navigationIcon = {
                        IconButton(onClick = {
                            setResult(RESULT_CANCELED)
                            finish()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val action = Fact.AccelerometerSensor(
                                id = state.id,
                                constraintsX = state.constraintsX,
                                trendX = state.trendX,
                                constraintsY = state.constraintsY,
                                trendY = state.trendY,
                                constraintsZ = state.constraintsZ,
                                trendZ = state.trendZ,
                            )
                            setResultAndFinish(action)
                        }) {
                            RemixIcon(remixName = Remix.System.check_line)
                        }

                    },
                )
            }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues)
                    .animateContentSize()
                    .verticalScroll(rememberScrollState())
            ) {
                FieldTitle(text = "X")
                ValueSelector(
                    constraints = state.constraintsX,
                    addConstraint = {
                        vm.addConstraintX(it)
                    },
                    removeConstraint = {
                        vm.removeConstraintX(it)
                    },
                    trend = state.trendX,
                    setTrend = {
                        vm.setTrendX(it)
                    }
                )

                SectionSpacer()

                FieldTitle(text = "Y")
                ValueSelector(
                    constraints = state.constraintsY,
                    addConstraint = {
                        vm.addConstraintY(it)
                    },
                    removeConstraint = {
                        vm.removeConstraintY(it)
                    },
                    trend = state.trendY,
                    setTrend = {
                        vm.setTrendY(it)
                    }
                )

                SectionSpacer()

                FieldTitle(text = "Z")
                ValueSelector(
                    constraints = state.constraintsZ,
                    addConstraint = {
                        vm.addConstraintZ(it)
                    },
                    removeConstraint = {
                        vm.removeConstraintZ(it)
                    },
                    trend = state.trendZ,
                    setTrend = {
                        vm.setTrendZ(it)
                    }
                )
            }
        }
    }
}

@Composable
private fun ValueSelector(
    constraints: List<ValueConstraints>,
    addConstraint: (ValueConstraints) -> Unit,
    removeConstraint: (ValueConstraints) -> Unit,
    trend: SensorValueTrend,
    setTrend: (SensorValueTrend) -> Unit
) {
    val constraintXSelectorState =
        rememberValueConstraintSelectorState { before, after ->
            before?.let { removeConstraint(before) }
            addConstraint(after)
        }
    ValueConstraintSelector(state = constraintXSelectorState)

    FieldTitle(text = LocalI18N.current["ui.fact.sensor.constraint"])
    TinySpacer()
    constraints.forEach {
        ListItem(
            titleLines = listOfNotNull(
                it.op.toDisplayName(),
                it.payload.value.takeIf { payloadValue -> payloadValue.isNotEmpty() }),
            action1 = {
                IconButton(onClick = {
                    removeConstraint(it)
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            },
            onClick = {
                constraintXSelectorState.show(it)
            }
        )
    }
    StandardSpacer()

    Button(onClick = {
        constraintXSelectorState.show()
    }) {
        Text(text = LocalI18N.current["ui.common.add"])
    }

    LargeSpacer()

    FieldTitle(text = LocalI18N.current["ui.fact.sensor.value"])
    SmallSpacer()

    val trendState = rememberDropdownSelectorState()
    Box {
        FilledTonalButton(
            onClick = {
                trendState.open()
            }) {
            Text(LocalI18N.current[trend.displayNameKey()])
        }
        DropdownSelector(
            state = trendState,
            items = SensorValueTrend.entries.toMutableList().apply {
                remove(SensorValueTrend.UNRECOGNIZED)
            }.map {
                DropdownItem(
                    labelLines = listOf(LocalI18N.current[it.displayNameKey()]),
                    data = it,
                )
            }
        ) {
            setTrend(it.data)
        }
    }
}