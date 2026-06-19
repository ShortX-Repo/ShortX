@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact.selector

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.fact.MethodHookExpressions
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.FieldTitleWithTip
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.settings.log.hook.MethodHookLogActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.BrowserUtils
import tornaco.apps.shortx.core.i18n.I18N_R


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
                Scaffold(
                    modifier = Modifier,
                    topBar = {
                        ShortXTopAppBar(
                            modifier = Modifier,
                            title = {
                                Column {
                                    Text(LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK])
                                    LinkText(text = LocalI18N.current[I18N_R.UI_USER_GUIDE]) {
                                        BrowserUtils.launch(context, BuildProp.METHOD_HOOK_WIKI_URL)
                                    }
                                }
                            },
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
                            .verticalScroll(rememberScrollState())
                    ) {
                        FieldTitleWithTip(
                            text = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_LIFECYCLE],
                            tipText = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_XPOSED_TIP]
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
                                FieldTitle(text = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_PACKAGE])
                                SmallSpacer()
                                ShortXTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.packageName,
                                    onValueChange = { vm.setPackageName(it) })
                                StandardSpacer()
                            }
                        }

                        FieldTitle(
                            text = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_CLASS]
                        )
                        SmallSpacer()
                        ShortXTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.className,
                            onValueChange = { vm.setClassName(it) })
                        StandardSpacer()

                        FieldTitle(
                            text = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD]
                        )
                        SmallSpacer()
                        ShortXTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.methodName,
                            onValueChange = { vm.setMethodName(it) })
                        StandardSpacer()


                        FieldTitle(
                            text = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_BEFORE_AFTER]
                        )
                        SmallSpacer()
                        Box {
                            val isBeforeState = rememberDropdownSelectorState()
                            fun getBeforeAfterLabel(before: Boolean): String {
                                return before.select("Before method", "After method")
                            }

                            DropdownSelector(
                                state = isBeforeState,
                                items = listOf(
                                    DropdownItem(
                                        labelLines = listOf(getBeforeAfterLabel(true)),
                                        data = "before"
                                    ),
                                    DropdownItem(
                                        labelLines = listOf(getBeforeAfterLabel(false)),
                                        data = "after"
                                    )
                                )
                            ) {
                                vm.setBeforeMethod(it.data == "before")
                            }

                            FilledTonalButton(onClick = {
                                isBeforeState.open()
                            }) {
                                Row(verticalAlignment = CenterVertically) {
                                    Text(text = getBeforeAfterLabel(state.beforeMethod))
                                    StandardSpacer()
                                    RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                                }
                            }
                        }
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
            text = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_CONTEXT_DATA_EXPRESSION],
            tipText = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_CONTEXT_DATA_EXPRESSION_DESCRIPTION]
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
            Text(text = LocalI18N.current[I18N_R.UI_COMMON_ADD])
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
            text = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS],
            tipText = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_DESCRIPTION]
        )
        SmallSpacer()
        constraints.forEachIndexed { index, constraint ->
            val updateParamsTypeDialogState = rememberTextInputState(
                title = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_TYPE],
                message = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_TYPE_TIP]
            ) { input ->
                val types = input.split(",").filter { it.isNotBlank() }.map { it.trim() }
                if (types.isNotEmpty()) {
                    update(Fact.MethodHook.Constraint.ParamsType(types), index)
                }
            }
            TextInputDialog(state = updateParamsTypeDialogState)

            val updateParamsCountDialogState = rememberTextInputState(
                title = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_COUNT],
                message = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_COUNT_TIP]
            ) {
                val countInt = it.toIntOrNull() ?: 0
                update(Fact.MethodHook.Constraint.ParamsCount(countInt), index)
            }
            TextInputDialog(state = updateParamsCountDialogState)

            val updateReturnTypeDialogState = rememberTextInputState(
                title = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_RETURN_TYPE],
                message = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_RETURN_TYPE_TIP]
            ) { input ->
                val types =
                    input.split(",").filter { it.isNotBlank() }.map { it.trim() }.firstOrNull()
                        ?: "void"
                if (types.isNotEmpty()) {
                    update(Fact.MethodHook.Constraint.ReturnType(types), index)
                }
            }
            TextInputDialog(state = updateReturnTypeDialogState)

            ListItem(
                titleLines = when (constraint) {
                    is Fact.MethodHook.Constraint.ParamsCount -> {
                        listOf(
                            LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_COUNT],
                            constraint.count.toString()
                        )
                    }

                    is Fact.MethodHook.Constraint.ParamsType -> {
                        composeLogger.d("ParamsType- $constraint")
                        listOf(
                            LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_TYPE],
                            constraint.types.joinToString(",")
                        )
                    }

                    is Fact.MethodHook.Constraint.ReturnType -> {
                        listOf(
                            LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_RETURN_TYPE],
                            constraint.type
                        )
                    }
                },
                icon = {
                    RemixIcon(remixName = Remix.Development.code_line)
                },
                action1 = {
                    IconButton(onClick = {
                        remove(constraint)
                    }) {
                        RemixIcon(remixName = Remix.System.close_line)
                    }
                },
                onClick = {
                    when (constraint) {
                        is Fact.MethodHook.Constraint.ParamsCount -> {
                            updateParamsCountDialogState.show(constraint.count.toString())
                        }

                        is Fact.MethodHook.Constraint.ParamsType -> {
                            updateParamsTypeDialogState.show(constraint.types.joinToString(","))
                        }

                        is Fact.MethodHook.Constraint.ReturnType -> {
                            updateReturnTypeDialogState.show(constraint.type)
                        }
                    }
                })
        }

        if (constraints.isNotEmpty()) MediumSpacer()

        val dropdownState = rememberDropdownSelectorState()
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                dropdownState.open()
            }) {
                Text(text = LocalI18N.current[I18N_R.UI_COMMON_ADD])
            }

            val addParamsTypeDialogState = rememberTextInputState(
                title = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_TYPE],
                message = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_TYPE_TIP]
            ) { input ->
                val types = input.split(",").filter { it.isNotBlank() }.map { it.trim() }
                composeLogger.d("types- $types")
                if (types.isNotEmpty()) {
                    add(Fact.MethodHook.Constraint.ParamsType(types))
                }
            }
            TextInputDialog(state = addParamsTypeDialogState)

            val addParamsCountDialogState = rememberTextInputState(
                title = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_COUNT],
                message = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_COUNT_TIP]
            ) {
                val countInt = it.toIntOrNull() ?: 0
                add(Fact.MethodHook.Constraint.ParamsCount(countInt))
            }
            TextInputDialog(state = addParamsCountDialogState)

            val addReturnTypeDialogState = rememberTextInputState(
                title = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_RETURN_TYPE],
                message = LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_RETURN_TYPE_TIP]
            ) { input ->
                val types =
                    input.split(",").filter { it.isNotBlank() }.map { it.trim() }.firstOrNull()
                        ?: "void"
                if (types.isNotEmpty()) {
                    add(Fact.MethodHook.Constraint.ReturnType(types))
                }
            }
            TextInputDialog(state = addReturnTypeDialogState)

            DropdownSelector(
                state = dropdownState,
                items = listOf(
                    DropdownItem(
                        labelLines = listOf(LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_TYPE]),
                        data = Fact.MethodHook.Constraint.ParamsType::class.java
                    ),
                    DropdownItem(
                        labelLines = listOf(LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_PARAMETER_COUNT]),
                        data = Fact.MethodHook.Constraint.ParamsCount::class.java
                    ),
                    DropdownItem(
                        labelLines = listOf(LocalI18N.current[I18N_R.UI_FACT_METHOD_HOOK_METHOD_CONSTRAINTS_RETURN_TYPE]),
                        data = Fact.MethodHook.Constraint.ReturnType::class.java
                    )
                ),
                onSelect = {
                    when (it.data) {
                        Fact.MethodHook.Constraint.ParamsType::class.java -> {
                            addParamsTypeDialogState.show()
                        }

                        Fact.MethodHook.Constraint.ParamsCount::class.java -> {
                            addParamsCountDialogState.show()
                        }

                        else -> {
                            addReturnTypeDialogState.show()
                        }
                    }
                })
        }
    }
}