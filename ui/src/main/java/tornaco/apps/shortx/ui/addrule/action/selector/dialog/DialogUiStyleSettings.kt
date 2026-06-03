package tornaco.apps.shortx.ui.addrule.action.selector.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.ui.addrule.action.selector.ActionThemeModeSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer

@Composable
fun DialogUiStyleSettings(style: DialogUiStyleSettings, update: (DialogUiStyleSettings) -> Unit) {
    Column {
        ActionThemeModeSelector(themeMode = style.themeMode) {
            update(DialogUiStyleSettings.newBuilder(style).setThemeMode(it).build())
        }
        MediumSpacer()
        FieldTitle(text = LocalI18N.current["ui.common.ui.style.font.scale"])
        MediumSpacer()
        Text(text = "${style.fontScale.takeIf { it > 0 } ?: 1f} x")
        Slider(
            valueRange = 0.5f..5f,
            value = style.fontScale.takeIf { it > 0 } ?: 1f,
            onValueChange = {
                update(DialogUiStyleSettings.newBuilder(style).setFontScale(it).build())
            }
        )
    }
}
