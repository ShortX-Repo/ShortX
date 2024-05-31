@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.proto.fact.MethodHookExpressions
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.settings.log.hook.MethodHookLogActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme


@AndroidEntryPoint
class MethodHookSelectorActivity : BaseSelectorActivity<Fact.MethodHook>() {

    @Composable
    override fun BaseSelectorActivity<Fact.MethodHook>.SelectorContent(currentData: Fact.MethodHook?) {
        CombineKeyEventSelector(currentData)
    }

    @Composable
    private fun CombineKeyEventSelector(currentFact: Fact.MethodHook?) {
        val context = LocalContext.current
        val vm = hiltViewModel<MethodHookSelectorViewModel>()
        LaunchedEffect(vm) {
            vm.initFrom(currentFact)
        }
        val state by vm.state.collectAsState()

        ShortXTheme {
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
                Scaffold(modifier = Modifier,
                    topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = { Text(LocalI18N.current["Method hook"]) },
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
                                    MethodHookLogActivity.Starter.start(context)
                                }) {
                                    RemixIcon(remixName = Remix.Document.file_text_line)
                                }

                                IconButton(onClick = {
                                    setResultAndFinish(state.toFact())
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
                        FieldTitle(
                            text = LocalI18N.current["Lifecycle"]
                        )
                        SmallSpacer()
                        val lifecycleDropdownState = rememberDropdownSelectorState()
                        Box(modifier = Modifier.fillMaxWidth()) {
                            FilledTonalButton(onClick = {
                                lifecycleDropdownState.open()
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = state.lifecycle.name)
                                    StandardSpacer()
                                    RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                                }
                            }
                            DropdownSelector(
                                state = lifecycleDropdownState,
                                items = MethodHookLifecycle.entries
                                    .filterNot { it == MethodHookLifecycle.UNRECOGNIZED }.map {
                                        DropdownItem(
                                            labelLines = listOf(
                                                LocalI18N.current[it.name]
                                            ),
                                            data = it
                                        )
                                    },
                                onSelect = {
                                    vm.setLifecycle(it.data)
                                })
                        }
                        StandardSpacer()

                        AnimatedVisibility(state.lifecycle != MethodHookLifecycle.InitZygote) {
                            Column {
                                FieldTitleWithTip(
                                    text = LocalI18N.current["Package name"],
                                    LocalI18N.current["Remember to enable target App scope in Xposed Manager"]
                                )
                                SmallSpacer()
                                ShortXTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.packageName,
                                    onValueChange = { vm.setPackageName(it) })
                                StandardSpacer()
                            }
                        }

                        FieldTitle(
                            text = LocalI18N.current["Class name"]
                        )
                        SmallSpacer()
                        ShortXTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.className,
                            onValueChange = { vm.setClassName(it) })
                        StandardSpacer()

                        FieldTitle(
                            text = LocalI18N.current["Method name"]
                        )
                        SmallSpacer()
                        ShortXTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.methodName,
                            onValueChange = { vm.setMethodName(it) })
                        StandardSpacer()

                        Constraints(
                            constraints = state.constraints,
                            add = {
                                vm.addConstraint(it)
                            },
                            update = { c, index ->
                                vm.updateConstraint(c, index)
                            },
                            remove = {
                                vm.removeConstraint(it)
                            })

                        Expressions(
                            expressions = state.expressions,
                            add = { vm.addExpression(it) },
                            remove = { vm.removeExpression(it) }
                        )
                        StandardSpacer()
                    }
                }
            }
        }
    }

    @Composable
    private fun Expressions(
        expressions: List<MethodHookExpressions>,
        add: (MethodHookExpressions) -> Unit,
        remove: (MethodHookExpressions) -> Unit
    ) {
        val editState = rememberNewMethodHookExpressionState {
            add(it)
        }
        NewMethodHookExpressionDialog(editState)

        FieldTitleWithTip(
            text = "Context data expressions",
            tipText = "Add an MVEL expression to parse the Method hook param, and the parsed result will be set as Context data"
        )
        SmallSpacer()
        expressions.forEach {
            ListItem(
                titleLines = listOf(it.contextKey, it.expressionMVEL),
                icon = {
                    RemixIcon(remixName = Remix.Development.code_line)
                },
                action1 = {
                    IconButton(onClick = {
                        remove(it)
                    }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                onClick = {
                    editState.show(it)
                })
        }

        if (expressions.isNotEmpty()) MediumSpacer()

        val addState = rememberNewMethodHookExpressionState {
            add(it)
        }
        NewMethodHookExpressionDialog(state = addState)

        Button(onClick = {
            addState.show()
        }) {
            Text(text = LocalI18N.current["ui.common.add"])
        }
    }


    @Composable
    private fun Constraints(
        constraints: List<Fact.MethodHook.Constraint>,
        add: (Fact.MethodHook.Constraint) -> Unit,
        update: (Fact.MethodHook.Constraint, Int) -> Unit,
        remove: (Fact.MethodHook.Constraint) -> Unit
    ) {
        FieldTitleWithTip(
            text = "Constraints",
            tipText = "Add constraints to filter the target Method"
        )
        SmallSpacer()
        constraints.forEach {
            ListItem(
                titleLines = listOf(it.toString()),
                icon = {
                    RemixIcon(remixName = Remix.Development.code_line)
                },
                action1 = {
                    IconButton(onClick = {
                        remove(it)
                    }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                onClick = {
                })
        }

        if (constraints.isNotEmpty()) MediumSpacer()

        val dropdownState = rememberDropdownSelectorState()
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                dropdownState.open()
            }) {
                Text(text = LocalI18N.current["ui.common.add"])
            }
            DropdownSelector(
                state = dropdownState,
                items = listOf(
                    DropdownItem(
                        labelLines = listOf("Method parameter types"),
                        data = Fact.MethodHook.Constraint.ParamsType::class.java
                    ),
                    DropdownItem(
                        labelLines = listOf("Method parameter count"),
                        data = Fact.MethodHook.Constraint.ParamsCount::class.java
                    ),
                    DropdownItem(
                        labelLines = listOf("Method return type"),
                        data = Fact.MethodHook.Constraint.ReturnType::class.java
                    )
                ),
                onSelect = {

                })
        }
    }
}