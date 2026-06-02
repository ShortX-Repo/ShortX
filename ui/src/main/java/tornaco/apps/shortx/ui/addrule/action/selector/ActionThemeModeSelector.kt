package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.normalized
import tornaco.apps.shortx.core.res.Remix

@Composable
fun ActionThemeModeSelector(
    themeMode: UiThemeMode,
    updateThemeMode: (UiThemeMode) -> Unit,
) {
    val i18n = LocalI18N.current
    val dropdownState = rememberDropdownSelectorState()
    val current = themeMode.normalized()

    FieldTitle(text = i18n["ui.settings.theme.mode"])
    StandardSpacer()

    Box(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
        FilledTonalButton(onClick = { dropdownState.open() }) {
            Row {
                Text(text = i18n[current.labelKey()])
                StandardSpacer()
                RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
            }
        }

        DropdownSelector(
            state = dropdownState,
            items = listOf(
                UiThemeMode.UiThemeMode_System,
                UiThemeMode.UiThemeMode_Light,
                UiThemeMode.UiThemeMode_Dark,
            ).map {
                DropdownItem(labelLines = listOf(i18n[it.labelKey()]), data = it)
            },
            onSelect = { updateThemeMode(it.data) },
        )
    }
}

private fun UiThemeMode.labelKey(): String {
    return when (normalized()) {
        UiThemeMode.UiThemeMode_Light -> "ui.settings.theme.mode.light"
        UiThemeMode.UiThemeMode_Dark -> "ui.settings.theme.mode.dark"
        UiThemeMode.UNRECOGNIZED, UiThemeMode.UiThemeMode_System -> "ui.settings.theme.mode.system"
    }
}
