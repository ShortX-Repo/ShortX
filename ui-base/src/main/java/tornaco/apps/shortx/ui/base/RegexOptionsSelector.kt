package tornaco.apps.shortx.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
import tornaco.apps.shortx.core.res.Remix

@Composable
fun RegexMatchOptionsDropdown(
    options: RegexMatchOptions,
    onSelectNewOptions: (RegexMatchOptions) -> Unit
) {
    val regexOptionsState = rememberDropdownSelectorState()
    Box {
        FilledTonalButton(
            onClick = {
                regexOptionsState.open()
            }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(LocalI18N.current[options.labelKey()])
                LargeSpacer()
                LargeSpacer()
                RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
            }
        }
        DropdownSelector(
            state = regexOptionsState,
            items = RegexMatchOptions.values().toMutableList().apply {
                remove(RegexMatchOptions.UNRECOGNIZED)
            }.map {
                DropdownItem(
                    labelLines = listOf(LocalI18N.current[it.labelKey()]),
                    data = it,
                )
            }
        ) {
            onSelectNewOptions(it.data)
        }
    }
}