@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base.code


import android.graphics.Color
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import com.amrdeveloper.codeview.CodeView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util._sp
import tornaco.apps.shortx.ui.addrule.action.selector.*
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.base.code.codeditor.syntax.LanguageManager
import tornaco.apps.shortx.ui.base.code.codeditor.syntax.LanguageName
import tornaco.apps.shortx.ui.base.code.codeditor.syntax.ThemeName
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils
import tornaco.apps.shortx.ui.util.TypefaceHelper

data class Code(val src: String, val note: String)

class CodeEditorState(
    val title: String,
    val message: String?,
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
    execute: suspend (String) -> String,
    onSelected: (Code) -> Unit
): CodeEditorState {
    val scope = rememberCoroutineScope()
    return remember {
        CodeEditorState(title, message, scope, execute, onSelected)
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
private fun CodeEditor(
    state: CodeEditorState
) {
    val noteDialogState = rememberTextSelectorState(title = LocalI18N.current["ui.common.note"]) {
        state.setNote(it)
    }
    InputTextSelector(state = noteDialogState)

    val outputDialogState = rememberOutputDialogState(title = state.title)
    OutputDialog(state = outputDialogState)

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = { Text(text = state.note.ifBlank { state.title }) },
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
                            state.onSelected(Code(src = state.currentText, note = state.note))
                            state.dismiss()
                        }) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = "Done")
                        }
                    },
                )
            }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    state.message?.let {
                        Text(text = it, style = MaterialTheme.typography.titleSmall)
                        StandardSpacer()
                    }
                    AndroidView(modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .imePadding(),
                        factory = { ctx ->
                            return@AndroidView CodeView(ctx).apply {
                                initView(this)
                                setText(state.currentText)
                                doAfterTextChanged {
                                    it?.toString()?.let { text ->
                                        state.currentText = text
                                    }
                                }
                            }
                        },
                        update = { view ->

                        })
                }
            }
        }
    }
}


private fun initView(
    codeView: CodeView
) {
    setupCodeView(codeView)
    setupLanguageAutoComplete(codeView)
}

private fun setupCodeView(codeView: CodeView) {
    codeView.background = null
    codeView.textSize = 14f
    codeView.gravity = Gravity.TOP or Gravity.START
    codeView.typeface = TypefaceHelper.jetbrainsMonoMedium(codeView.context)

    // Setup Line number feature
    codeView.setEnableLineNumber(true)
    codeView.setLineNumberTextColor(Color.GRAY)
    codeView.setLineNumberTextSize(12._sp)

    // Setup Auto indenting feature
    codeView.setTabLength(4)
    codeView.setEnableAutoIndentation(true)

    // Setup the language and theme with SyntaxManager helper class
    val languageManager = LanguageManager(codeView.context, codeView)
    languageManager.applyTheme(LanguageName.JSON, ThemeName.DYNAMIC)

    // Setup auto pair complete
    val pairCompleteMap: MutableMap<Char, Char> = HashMap()
    pairCompleteMap['{'] = '}'
    pairCompleteMap['['] = ']'
    pairCompleteMap['('] = ')'
    pairCompleteMap['<'] = '>'
    pairCompleteMap['"'] = '"'
    pairCompleteMap['\''] = '\''
    codeView.setPairCompleteMap(pairCompleteMap)
    codeView.enablePairComplete(true)
    codeView.enablePairCompleteCenterCursor(true)
    codeView.setHorizontallyScrolling(false)
}

private fun setupLanguageAutoComplete(codeView: CodeView) {
    val languageManager = LanguageManager(codeView.context, codeView)
    val languageKeywords: Array<String> = languageManager.getLanguageKeywords(LanguageName.JSON)

    val layoutId = tornaco.apps.shortx.ui.R.layout.ui_list_item_suggestion

    val viewId = tornaco.apps.shortx.ui.R.id.suggestItemTextView
    val adapter = ArrayAdapter(codeView.context, layoutId, viewId, languageKeywords)

    codeView.setAdapter(adapter)
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
