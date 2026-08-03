@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.action.RecentsLayoutStyle
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.theme.ShortXTheme
import kotlin.math.roundToInt

class ShowCustomRecentsDialogState(
    val onSelected: (Action.ShowCustomRecents) -> Unit
) : CommonDialogState() {
    var layoutStyle by mutableStateOf(RecentsLayoutStyle.RecentsLayoutStyle_TiltStack)
    var gridColumns by mutableIntStateOf(3)
    var bgAlpha by mutableFloatStateOf(0.6f)
    var backgroundBlurRadius by mutableIntStateOf(0)
    var backgroundImagePath by mutableStateOf("")
    var miniMode by mutableStateOf(false)
    var showAppName by mutableStateOf(true)
    var arcArrangement by mutableStateOf(false)
    var centerActiveApp by mutableStateOf(false)

    fun show(current: Action.ShowCustomRecents? = null) {
        layoutStyle = current?.layoutStyle ?: RecentsLayoutStyle.RecentsLayoutStyle_TiltStack
        gridColumns = current?.gridColumns ?: 3
        bgAlpha = if (current != null) {
            ((current.backgroundColor.toLong() and 0xFF000000L) ushr 24).toFloat() / 255f
        } else 0.6f
        backgroundBlurRadius = current?.backgroundBlurRadius ?: 0
        backgroundImagePath = current?.backgroundImagePath ?: ""
        miniMode = current?.miniMode ?: false
        showAppName = current?.showAppName ?: true
        arcArrangement = current?.arcArrangement ?: false
        centerActiveApp = current?.centerActiveApp ?: false
        show()
    }

    fun buildBackgroundColor(): Int {
        val alpha = (bgAlpha * 255).roundToInt().coerceIn(0, 255)
        return (alpha.toLong() shl 24).toInt()
    }
}

@Composable
fun rememberShowCustomRecentsDialogState(
    onSelected: (Action.ShowCustomRecents) -> Unit
): ShowCustomRecentsDialogState {
    return remember {
        ShowCustomRecentsDialogState(onSelected)
    }
}

@Composable
fun ShowCustomRecentsDialog(state: ShowCustomRecentsDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShowCustomRecentsSelector(state)
        }
    }
}

@Composable
private fun ShowCustomRecentsSelector(dialogState: ShowCustomRecentsDialogState) {
    val i18N = LocalI18N.current
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    ShortXTopAppBar(
                        title = {
                            Text(text = i18N["ui.action.show.custom.recents"])
                        },
                        navigationIcon = {
                            IconButton(onClick = { dialogState.dismiss() }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(
                                    Action.ShowCustomRecents(
                                        layoutStyle = dialogState.layoutStyle,
                                        gridColumns = dialogState.gridColumns,
                                        backgroundColor = dialogState.buildBackgroundColor(),
                                        backgroundBlurRadius = dialogState.backgroundBlurRadius,
                                        backgroundImagePath = dialogState.backgroundImagePath,
                                        miniMode = dialogState.miniMode,
                                        showAppName = dialogState.showAppName,
                                        arcArrangement = dialogState.arcArrangement,
                                        centerActiveApp = dialogState.centerActiveApp,
                                    )
                                )
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // ── Layout type section ──
                    SectionHeader(text = i18N["ui.action.recents.style"])

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            RecentsLayoutStyle.entries
                                .filterNot { it == RecentsLayoutStyle.UNRECOGNIZED }
                                .forEach { style ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { dialogState.layoutStyle = style }
                                            .padding(vertical = 4.dp),
                                    ) {
                                        RadioButton(
                                            selected = dialogState.layoutStyle == style,
                                            onClick = { dialogState.layoutStyle = style },
                                        )
                                        Text(
                                            text = i18N[style.labelKey()],
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.padding(start = 8.dp),
                                        )
                                    }
                                }
                        }
                    }

                    StandardSpacer()

                    // ── Detail settings section ──
                    SectionHeader(text = i18N["ui.action.recents.detail"])

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            // Mini mode — for TiltStack, Grid, SlimList
                            if (dialogState.layoutStyle != RecentsLayoutStyle.RecentsLayoutStyle_VerticalList) {
                                SettingSwitch(
                                    title = i18N["ui.action.recents.minimode"],
                                    description = i18N["ui.action.recents.minimode.desc"],
                                    checked = dialogState.miniMode,
                                    onCheckedChange = { dialogState.miniMode = it },
                                )
                                SettingDivider()
                            }

                            // Arc arrangement — only for VerticalList
                            if (dialogState.layoutStyle == RecentsLayoutStyle.RecentsLayoutStyle_VerticalList) {
                                SettingSwitch(
                                    title = i18N["ui.action.recents.arc"],
                                    description = i18N["ui.action.recents.arc.desc"],
                                    checked = dialogState.arcArrangement,
                                    onCheckedChange = { dialogState.arcArrangement = it },
                                )
                                SettingDivider()
                            }

                            // Center active app — for TiltStack
                            if (dialogState.layoutStyle == RecentsLayoutStyle.RecentsLayoutStyle_TiltStack) {
                                SettingSwitch(
                                    title = i18N["ui.action.recents.centeractive"],
                                    description = i18N["ui.action.recents.centeractive.desc"],
                                    checked = dialogState.centerActiveApp,
                                    onCheckedChange = { dialogState.centerActiveApp = it },
                                )
                                SettingDivider()
                            }

                            // Show app name — for TiltStack, Grid, VerticalList
                            if (dialogState.layoutStyle != RecentsLayoutStyle.RecentsLayoutStyle_SlimList) {
                                SettingSwitch(
                                    title = i18N["ui.action.recents.showappname"],
                                    description = i18N["ui.action.recents.showappname.desc"],
                                    checked = dialogState.showAppName,
                                    onCheckedChange = { dialogState.showAppName = it },
                                )
                            }

                            // Grid columns — only for Grid
                            if (dialogState.layoutStyle == RecentsLayoutStyle.RecentsLayoutStyle_Grid) {
                                SettingDivider()
                                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                                    Text(
                                        text = "${i18N["ui.action.recents.grid.columns"]}: ${dialogState.gridColumns}",
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    MediumSpacer()
                                    Slider(
                                        value = dialogState.gridColumns.toFloat(),
                                        valueRange = 2f..5f,
                                        steps = 2,
                                        onValueChange = {
                                            dialogState.gridColumns = it.roundToInt()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    StandardSpacer()

                    // ── Background settings ──
                    SectionHeader(text = i18N["ui.action.recents.bg.color"])

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = dialogState.bgAlpha))
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline,
                                            CircleShape
                                        )
                                )
                                Text(
                                    text = "${(dialogState.bgAlpha * 100).roundToInt()}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Slider(
                                    value = dialogState.bgAlpha,
                                    valueRange = 0f..1f,
                                    onValueChange = {
                                        dialogState.bgAlpha = (it * 20).roundToInt() / 20f
                                    },
                                    modifier = Modifier.weight(1f),
                                )
                            }

                            SettingDivider()

                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = "${i18N["ui.action.recents.bg.blur"]}: ${dialogState.backgroundBlurRadius}",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                MediumSpacer()
                                Slider(
                                    value = dialogState.backgroundBlurRadius.toFloat(),
                                    valueRange = 0f..50f,
                                    onValueChange = {
                                        dialogState.backgroundBlurRadius = it.roundToInt()
                                    }
                                )
                            }
                        }
                    }

                    StandardSpacer()
                    StandardSpacer()
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun SettingDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
        thickness = 0.5.dp,
    )
}
