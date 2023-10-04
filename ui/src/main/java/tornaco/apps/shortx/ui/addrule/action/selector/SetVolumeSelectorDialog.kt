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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
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


class SetVolumeDialogState(
    val onSelected: (Action.SetVolume) -> Unit
) : CommonDialogState() {
    var type by mutableIntStateOf(AudioManager.STREAM_MUSIC)
    var index by mutableIntStateOf(0)

    fun show(current: Action.SetVolume? = null) {
        this.type = current?.type ?: AudioManager.STREAM_MUSIC
        this.index = current?.index ?: 6

        show()
    }
}

@Composable
fun rememberSetVolumeDialogState(
    onSelected: (Action.SetVolume) -> Unit
): SetVolumeDialogState {
    return remember {
        SetVolumeDialogState(onSelected)
    }
}

@Composable
fun SetVolumeDialog(state: SetVolumeDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            SetVolumeSelector(state)
        }
    }
}

@Composable
private fun SetVolumeSelector(dialogState: SetVolumeDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.action.set.volume"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(
                                    Action.SetVolume(
                                        type = dialogState.type,
                                        index = dialogState.index
                                    )
                                )
                                dialogState.dismiss()
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
                        .animateContentSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    val typeDropdownState = rememberDropdownSelectorState()
                    FieldTitle(text = LocalI18N.current["ui.action.audio.stream.type"])
                    MediumSpacer()
                    Box(modifier = Modifier.fillMaxWidth()) {
                        FilledTonalButton(onClick = {
                            typeDropdownState.open()
                        }) {
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
                                    labelLines = listOfNotNull(
                                        LocalI18N.current[it.streamTypeLabelKey()],
                                    ),
                                    data = it
                                )
                            },
                            onSelect = {
                                dialogState.type = it.data
                                dialogState.index = shortXManager.getStreamVolume(it.data)
                            })
                    }
                    StandardSpacer()

                    FieldTitle(text = LocalI18N.current["ui.action.set.volume"] + " ${dialogState.index}")
                    MediumSpacer()
                    val min =
                        remember(dialogState.type) {
                            shortXManager.getStreamMinVolume(dialogState.type).toFloat()
                        }
                    val max =
                        remember(dialogState.type) {
                            shortXManager.getStreamMaxVolume(dialogState.type).toFloat()
                        }
                    TipBody(text = "$min - $max")
                    Slider(
                        valueRange = min..max,
                        value = dialogState.index.toFloat(),
                        onValueChange = {
                            dialogState.index = (it.roundToInt())
                        })
                }
            }
        }
    }


}