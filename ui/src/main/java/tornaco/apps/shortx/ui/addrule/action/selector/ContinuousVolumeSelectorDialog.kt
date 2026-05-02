@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import android.media.AudioManager
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.mapper.streamTypeLabelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MediumSpacer
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme
import kotlin.math.roundToInt

class ContinuousVolumeDialogState(
    val onSelected: (Action.ContinuousAdjustVolume) -> Unit
) : CommonDialogState() {
    var type by mutableIntStateOf(AudioManager.STREAM_MUSIC)
    var sensitivityScale by mutableFloatStateOf(1f)

    fun show(current: Action.ContinuousAdjustVolume? = null) {
        type = current?.type ?: AudioManager.STREAM_MUSIC
        sensitivityScale = current?.sensitivityScale ?: 1f
        show()
    }
}

@Composable
fun rememberContinuousVolumeDialogState(
    onSelected: (Action.ContinuousAdjustVolume) -> Unit
): ContinuousVolumeDialogState {
    return remember {
        ContinuousVolumeDialogState(onSelected)
    }
}

@Composable
fun ContinuousVolumeDialog(state: ContinuousVolumeDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ContinuousVolumeSelector(state)
        }
    }
}

@Composable
private fun ContinuousVolumeSelector(dialogState: ContinuousVolumeDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    ShortXTopAppBar(
                        title = {
                            Text(
                                text = LocalI18N.current.get(
                                    key = "ui.action.continuous.adjust.volume",
                                    fallback = "Continuous volume"
                                )
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { dialogState.dismiss() }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(
                                    Action.ContinuousAdjustVolume(
                                        type = dialogState.type,
                                        sensitivityScale = dialogState.sensitivityScale,
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
                    val typeDropdownState = rememberDropdownSelectorState()
                    FieldTitle(text = LocalI18N.current["ui.action.audio.stream.type"])
                    MediumSpacer()
                    Box(modifier = Modifier.fillMaxWidth()) {
                        FilledTonalButton(onClick = { typeDropdownState.open() }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = LocalI18N.current[dialogState.type.streamTypeLabelKey()])
                                StandardSpacer()
                                RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                            }
                        }
                        DropdownSelector(
                            state = typeDropdownState,
                            items = listOf(
                                AudioManager.STREAM_MUSIC,
                                AudioManager.STREAM_SYSTEM,
                                AudioManager.STREAM_ALARM,
                                AudioManager.STREAM_NOTIFICATION,
                                AudioManager.STREAM_RING,
                                AudioManager.STREAM_VOICE_CALL,
                            ).map {
                                DropdownItem(
                                    labelLines = listOf(LocalI18N.current[it.streamTypeLabelKey()]),
                                    data = it,
                                )
                            },
                            onSelect = {
                                dialogState.type = it.data
                            }
                        )
                    }

                    StandardSpacer()
                    FieldTitle(text = "Sensitivity ${"%.2f".format(dialogState.sensitivityScale)}x")
                    MediumSpacer()
                    TipBody(text = "Higher values change the selected stream faster during the gesture.")
                    Slider(
                        value = dialogState.sensitivityScale,
                        valueRange = 0.25f..3f,
                        onValueChange = {
                            dialogState.sensitivityScale = (it * 20).roundToInt() / 20f
                        }
                    )
                }
            }
        }
    }
}