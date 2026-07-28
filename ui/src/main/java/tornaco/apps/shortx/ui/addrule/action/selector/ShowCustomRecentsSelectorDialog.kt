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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import tornaco.apps.shortx.core.proto.action.RecentsScrollOrientation
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
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.theme.ShortXTheme
import kotlin.math.roundToInt

class ShowCustomRecentsDialogState(
    val onSelected: (Action.ShowCustomRecents) -> Unit
) : CommonDialogState() {
    var layoutStyle by mutableStateOf(RecentsLayoutStyle.RecentsLayoutStyle_Carousel)
    var scrollOrientation by mutableStateOf(RecentsScrollOrientation.RecentsScrollOrientation_Horizontal)
    var gridColumns by mutableIntStateOf(3)
    var bgAlpha by mutableFloatStateOf(0.6f)
    var backgroundBlurRadius by mutableIntStateOf(0)
    var backgroundImagePath by mutableStateOf("")

    fun show(current: Action.ShowCustomRecents? = null) {
        layoutStyle = current?.layoutStyle ?: RecentsLayoutStyle.RecentsLayoutStyle_Carousel
        scrollOrientation = current?.scrollOrientation ?: RecentsScrollOrientation.RecentsScrollOrientation_Horizontal
        gridColumns = current?.gridColumns ?: 3
        bgAlpha = if (current != null) {
            ((current.backgroundColor.toLong() and 0xFF000000L) ushr 24).toFloat() / 255f
        } else 0.6f
        backgroundBlurRadius = current?.backgroundBlurRadius ?: 0
        backgroundImagePath = current?.backgroundImagePath ?: ""
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
                                        scrollOrientation = dialogState.scrollOrientation,
                                        gridColumns = dialogState.gridColumns,
                                        backgroundColor = dialogState.buildBackgroundColor(),
                                        backgroundBlurRadius = dialogState.backgroundBlurRadius,
                                        backgroundImagePath = dialogState.backgroundImagePath,
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
                        .padding(16.dp)
                        .padding(paddingValues)
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Layout style
                    FieldTitle(text = i18N["ui.action.recents.style"])
                    MediumSpacer()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RecentsLayoutStyle.entries
                            .filterNot { it == RecentsLayoutStyle.UNRECOGNIZED }
                            .forEach { style ->
                                FilterChip(
                                    selected = dialogState.layoutStyle == style,
                                    onClick = { dialogState.layoutStyle = style },
                                    label = { Text(i18N[style.labelKey()]) },
                                )
                            }
                    }

                    // Scroll orientation (only for Carousel)
                    if (dialogState.layoutStyle == RecentsLayoutStyle.RecentsLayoutStyle_Carousel) {
                        StandardSpacer()
                        FieldTitle(text = i18N["ui.action.recents.scroll"])
                        MediumSpacer()
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            RecentsScrollOrientation.entries
                                .filterNot { it == RecentsScrollOrientation.UNRECOGNIZED }
                                .forEach { orientation ->
                                    FilterChip(
                                        selected = dialogState.scrollOrientation == orientation,
                                        onClick = { dialogState.scrollOrientation = orientation },
                                        label = { Text(i18N[orientation.labelKey()]) },
                                    )
                                }
                        }
                    }

                    // Grid columns (only for Grid)
                    if (dialogState.layoutStyle == RecentsLayoutStyle.RecentsLayoutStyle_Grid) {
                        StandardSpacer()
                        FieldTitle(text = "${i18N["ui.action.recents.grid.columns"]}: ${dialogState.gridColumns}")
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

                    // Background settings
                    StandardSpacer()
                    FieldTitle(text = "${i18N["ui.action.recents.bg.color"]}: ${(dialogState.bgAlpha * 100).roundToInt()}%")
                    MediumSpacer()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = dialogState.bgAlpha))
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
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

                    StandardSpacer()
                    FieldTitle(text = "${i18N["ui.action.recents.bg.blur"]}: ${dialogState.backgroundBlurRadius}")
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
    }
}
