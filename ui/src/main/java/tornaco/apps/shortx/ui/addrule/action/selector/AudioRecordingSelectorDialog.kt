@file:OptIn(
    ExperimentalMaterial3Api::class
)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.AudioSource
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.DropdownItem
import tornaco.apps.shortx.ui.base.DropdownSelector
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TipRow
import tornaco.apps.shortx.ui.base.rememberDropdownSelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme

class AudioRecordingDialogState(
    val onSelected: (Action.AudioRecording) -> Unit
) : CommonDialogState() {
    var src: AudioSource by mutableStateOf(AudioSource.Mic)
    var namePrefix: String by mutableStateOf("Audio")

    fun show(currentSettings: Action.AudioRecording? = null) {
        this.src = currentSettings?.src ?: AudioSource.Mic
        this.namePrefix = currentSettings?.fileNamePrefix ?: "Audio"

        show()
    }
}

@Composable
fun rememberAudioRecordingDialogState(
    onSelected: (Action.AudioRecording) -> Unit
): AudioRecordingDialogState {
    return remember {
        AudioRecordingDialogState(onSelected)
    }
}

@Composable
fun AudioRecordingDialog(state: AudioRecordingDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            AudioRecordingSelector(state)
        }
    }
}

@Composable
private fun AudioRecordingSelector(dialogState: AudioRecordingDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.audio.recording"]) },
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
                                    Action.AudioRecording(
                                        src = dialogState.src,
                                        fileNamePrefix = dialogState.namePrefix
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
                    TipRow(text = LocalI18N.current["ui.action.audio.recording.summary"])
                    StandardSpacer()

                    FieldTitle(text = LocalI18N.current["ui.action.audio.src"])
                    SmallSpacer()
                    val srcDropdownState = rememberDropdownSelectorState()
                    Box {
                        FilledTonalButton(
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ColorDefaults.backgroundSurfaceColor(
                                    1.dp
                                )
                            ), onClick = {
                                srcDropdownState.open()
                            }) {
                            Text(LocalI18N.current[dialogState.src.labelKey()])
                        }
                        DropdownSelector(
                            state = srcDropdownState,
                            items = AudioSource.values().toMutableList().apply {
                                remove(AudioSource.UNRECOGNIZED)
                            }.map {
                                DropdownItem(
                                    labelLines = listOf(LocalI18N.current[it.labelKey()]),
                                    data = it,
                                )
                            }
                        ) {
                            dialogState.src = it.data
                        }
                    }

                    StandardSpacer()
                    ShortXTextField(
                        value = dialogState.namePrefix,
                        label = {
                            Text(text = LocalI18N.current["ui.action.audio.file.prefix"])
                        },
                        showSymbolButton = false,
                        onValueChange = {
                            dialogState.namePrefix = it
                        })
                }
            }
        }
    }

}