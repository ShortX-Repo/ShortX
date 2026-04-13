@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base.code


import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import io.github.rosemoe.sora.app.CEComposeBinding
import io.github.rosemoe.sora.app.databinding.CodeEditorComposeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.LinkText
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXAlertDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.WarningCard
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.BrowserUtils
import tornaco.apps.shortx.ui.util.ClipboardUtils

data class Code(val src: String, val note: String)

open class CodeEditorState(
    val title: String,
    val message: String?,
    val guideTextAndLink: Pair<String, String>?,
    val languageScopeName: String?,
    val scope: CoroutineScope,
    val execute: suspend (String) -> String,
    val onSelected: (Code) -> Unit
) : CommonDialogState() {
    private var _note by mutableStateOf("")
    val note get() = _note

    private var _isExecuting by mutableStateOf(false)
    val isExecuting get() = _isExecuting

    var currentText: String = ""

    fun show(initialText: String? = "") {
        this.currentText = initialText ?: ""
        this._note = ""

        show()
    }

    fun setNote(note: String) {
        _note = note
    }

    fun setIsExecuting(executing: Boolean) {
        _isExecuting = executing
    }
}

@Composable
fun rememberCodeEditorState(
    title: String,
    message: String? = null,
    guideTextAndLink: Pair<String, String>? = null,
    languageScopeName: String? = null,
    execute: suspend (String) -> String,
    onSelected: (Code) -> Unit
): CodeEditorState {
    val scope = rememberCoroutineScope()
    return remember {
        CodeEditorState(title, message, guideTextAndLink, languageScopeName, scope, execute, onSelected)
    }
}

@Composable
fun CodeEditorDialog(state: CodeEditorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            CodeEditor(state)
        }
    }
}

@Composable
fun CodeEditor(
    state: CodeEditorState,
    onSave: () -> Unit = {
        state.onSelected(Code(src = state.currentText, note = state.note))
        state.dismiss()
    },
    headerContent: @Composable () -> Unit = {}
) {
    val outputDialogState = rememberOutputDialogState(title = state.title)
    OutputDialog(state = outputDialogState)
    val context = LocalContext.current
    var ceComposeBinding by remember { mutableStateOf<CEComposeBinding?>(null) }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = {
                        Column {
                            Text(text = state.note.ifBlank { state.title })
                            state.guideTextAndLink?.let {
                                LinkText(text = it.first) {
                                    BrowserUtils.launch(context, it.second)
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            state.dismiss()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        AnimatedVisibility(visible = !state.isExecuting) {
                            IconButton(
                                onClick = {
                                    state.scope.launch {
                                        state.setIsExecuting(true)
                                        val output = withContext(Dispatchers.IO) {
                                            state.execute(state.currentText)
                                        }
                                        state.setIsExecuting(false)
                                        outputDialogState.show(output)
                                    }
                                }) {
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = "Evaluate"
                                )
                            }
                        }

                        AnimatedVisibility(visible = state.isExecuting) {
                            Loading(modifier = Modifier.size(24.dp))
                        }

                        IconButton(onClick = {
                            ceComposeBinding?.toggleSearchPanel()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search and replace"
                            )
                        }

                        IconButton(onClick = {
                            onSave()
                        }) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = "Done")
                        }
                    },
                )
            }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        state.message?.let {
                            WarningCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(text = it, style = MaterialTheme.typography.titleSmall)
                                    StandardSpacer()
                                }
                            }
                        }

                        headerContent()
                    }

                    AndroidViewBinding(
                        factory = CodeEditorComposeBinding::inflate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .imePadding(),
                        update = {
                            if (ceComposeBinding == null) {
                                CEComposeBinding(
                                    binding = this,
                                    showToolbar = true,
                                    initialText = state.currentText,
                                    languageScopeName = state.languageScopeName,
                                    autoCompleteEnabled = false
                                ) { text ->
                                    state.currentText = text
                                }.also {
                                    ceComposeBinding = it
                                    it.onCreate()
                                }
                            } else {
                                ceComposeBinding?.updateText(state.currentText)
                            }
                        },
                        onRelease = {
                            ceComposeBinding?.onDestroy()
                            ceComposeBinding = null
                        }
                    )
                }
            }
        }
    }
}

class OutputDialogState(val title: String) : CommonDialogState() {
    var output: String by mutableStateOf("")

    fun show(out: String) {
        this.output = out
        show()
    }
}


@Composable
fun rememberOutputDialogState(
    title: String,
): OutputDialogState {
    return remember {
        OutputDialogState(title)
    }
}

@Composable
fun OutputDialog(state: OutputDialogState) {
    val context = LocalContext.current
    val i18N = LocalI18N.current
    if (state.isShowing) {
        ShortXAlertDialog(
            onDismissRequest = {
                state.dismiss()
            },
            confirmButton = {
                TextButton(onClick = {
                    ClipboardUtils.copyToClipboard(
                        context,
                        "output",
                        state.output
                    )
                    Toast.makeText(
                        context,
                        i18N["ui.common.copied.to.clipboard"],
                        Toast.LENGTH_LONG
                    ).show()

                    state.dismiss()
                }) {
                    Text(text = LocalI18N.current["ui.common.copy"])
                }
            },
            title = {
                Text(text = state.title)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = state.output)
                }
            })
    }
}
