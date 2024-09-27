package tornaco.apps.shortx.ui.addda

import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.func.FuncParameter
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.FUNC_PARAM_REF_PREFIX
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitleWithTip
import tornaco.apps.shortx.ui.base.OutlineBadge
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.StandardSpacer

@Composable
fun FuncParamList(
    params: List<FuncParameter>,
    add: (FuncParameter, override: Boolean) -> Unit,
    remove: (FuncParameter) -> Unit
) {
    MediumTitleWithTip(
        text = LocalI18N.current["ui.da.params"],
        tipText = LocalI18N.current["ui.da.params.description",
            mapOf("arg" to "${FUNC_PARAM_REF_PREFIX}${LocalI18N.current["ui.da.params.name"]}")]
    )
    val editFuncState = rememberNewFuncParameterState {
        add(it, true)
    }
    NewFunctionParameterDialog(state = editFuncState)

    params.forEach { funcParameter ->
        ListItem(
            titleLines = listOfNotNull(
                funcParameter.name,
                LocalI18N.current["ui.da.params.default.value"] + " ${funcParameter.defaultValue}",
                funcParameter.comments.takeIf { it.isNotBlank() }
            ),
            action1 = {
                if (funcParameter.isRequired) {
                    OutlineBadge(text = LocalI18N.current["ui.da.params.is.required"])
                    StandardSpacer()
                }
                IconButton(onClick = {
                    remove(funcParameter)
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            },
            icon = {
                RemixIcon(remixName = Remix.Editor.functions)
            },
            onClick = {
                editFuncState.show(funcParameter)
            }
        )
    }
    if (params.isNotEmpty()) MediumSpacer()

    val addFuncState = rememberNewFuncParameterState {
        add(it, false)
    }
    NewFunctionParameterDialog(state = addFuncState)

    Button(onClick = {
        addFuncState.show()
    }) {
        Text(text = LocalI18N.current["ui.common.add"])
    }
}