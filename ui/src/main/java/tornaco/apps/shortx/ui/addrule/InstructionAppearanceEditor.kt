package tornaco.apps.shortx.ui.addrule

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.res.isBase64Image
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.popupSelectorResult
import tornaco.apps.shortx.ui.base.ColorPickerDialog
import tornaco.apps.shortx.ui.base.ColorPickerDialogState
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIcon
import tornaco.apps.shortx.ui.base.MDIconSelectorActivity
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TinySpacer

@Composable
fun InstructionAppearanceEditor(
    icon: String,
    iconColor: ULong?,
    readOnly: Boolean,
    setIcon: (String) -> Unit,
    setIconColor: (ULong) -> Unit,
) {
    val context = LocalContext.current
    val displayIcon = icon.ifBlank { Remix.Development.command_line }
    val displayColor = Color(iconColor ?: MaterialTheme.colorScheme.primary.value)

    val iconSelector = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            result.popupSelectorResult<MDIcon>()?.let {
                setIcon(it.iconNameOrBase64)
            }
        }
    )
    val colorPickerState = remember(iconColor) {
        ColorPickerDialogState(
            colorInt = displayColor.toArgb(),
            onColorChanged = { setIconColor(Color(it).value) }
        )
    }
    ColorPickerDialog(colorPickerState)

    FieldTitle(text = LocalI18N.current["ui.common.icon"])
    SmallSpacer()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isImage = displayIcon.isBase64Image()
        ColoredIcon(
            size = 40.dp,
            padding = 0.dp,
            color = if (isImage) Color.Transparent else displayColor,
        ) {
            RemixIcon(
                modifier = Modifier.size(if (isImage) 40.dp else 18.dp),
                remixName = displayIcon,
                tint = Color.White
            )
        }
        StandardSpacer()
        Button(
            enabled = !readOnly,
            onClick = {
                iconSelector.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        icon.takeIf { it.isNotBlank() }?.let { MDIcon(it, !it.isBase64Image()) },
                        MDIconSelectorActivity::class.java
                    )
                )
            }
        ) {
            RemixIcon(remixName = Remix.Design.edit_circle_line)
            TinySpacer()
            Text(LocalI18N.current["ui.common.icon"])
        }
        TinySpacer()
        Button(
            enabled = !readOnly,
            onClick = { colorPickerState.show() }
        ) {
            RemixIcon(remixName = Remix.Design.palette_line)
            TinySpacer()
            Text(LocalI18N.current["ui.common.icon.bg"])
        }
    }
}
