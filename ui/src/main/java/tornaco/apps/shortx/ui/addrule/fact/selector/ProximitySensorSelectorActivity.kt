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
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState


@AndroidEntryPoint
class ProximitySensorSelectorActivity : BaseSelectorActivity<Fact.ProximitySensor>() {

    @Composable
    override fun BaseSelectorActivity<Fact.ProximitySensor>.SelectorContent(currentData: Fact.ProximitySensor?) {
        CombineKeyEventSelector(currentData)
    }

    @Composable
    private fun CombineKeyEventSelector(current: Fact.ProximitySensor?) {
        val vm = hiltViewModel<ProximitySensorSelectorViewModel>()
        LaunchedEffect(vm) {
            vm.initFrom(current)
        }
        val state by vm.state.collectAsState()

        Scaffold(modifier = Modifier,
            topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(LocalI18N.current["ui.fact.sensor.proximity"]) },
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
                            val action = Fact.ProximitySensor(
                                id = state.id,
                                constraints = state.constraints,
                                trend = state.trend
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
                val constraintSelectorState =
                    rememberValueConstraintSelectorState { before, after ->
                        before?.let { vm.removeConstraint(before) }
                        vm.addConstraint(after)
                    }
                ValueConstraintSelector(state = constraintSelectorState)

                FieldTitle(text = LocalI18N.current["ui.fact.sensor.constraint"])
                TinySpacer()
                state.constraints.forEach {
                    ListItem(
                        titleLines = listOfNotNull(
                            it.op.toDisplayName(),
                            it.payload.value.takeIf { payloadValue -> payloadValue.isNotEmpty() }),
                        action1 = {
                            IconButton(onClick = {
                                vm.removeConstraint(it)
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        onClick = {
                            constraintSelectorState.show(it)
                        }
                    )
                }
                StandardSpacer()

                Button(onClick = {
                    constraintSelectorState.show()
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
                        Text(LocalI18N.current[state.trend.displayNameKey()])
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
                        vm.setTrend(it.data)
                    }
                }
            }
        }
    }
}