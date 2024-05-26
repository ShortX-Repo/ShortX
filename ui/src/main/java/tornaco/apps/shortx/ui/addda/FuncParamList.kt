package tornaco.apps.shortx.ui.addda

import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.func.FuncParameter
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.MediumTitleWithTip
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SmallSpacer

@Composable
fun FuncParamList(
    params: List<FuncParameter>,
    add: (FuncParameter) -> Unit,
    remove: (FuncParameter) -> Unit
) {
    MediumTitleWithTip(
        text = LocalI18N.current["ui.da.params"],
        tipText = LocalI18N.current["ui.da.params.description"]
    )
    SmallSpacer()

    params.forEach {
        ListItem(
            titleLines = listOf(
                it.name,
                LocalI18N.current["ui.da.params.default.value"] + " ${it.defaultValue}"
            ),
            action1 = {
                IconButton(onClick = {
                    remove(it)
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            },
            icon = {
                RemixIcon(remixName = Remix.Development.parentheses_fill)
            }
        )
    }
    MediumSpacer()

    Button(onClick = {
        add(FuncParameter.newBuilder().setName("Test").setDefaultValue("123").build())
    }) {
        Text(text = LocalI18N.current["ui.common.add"])
    }
}