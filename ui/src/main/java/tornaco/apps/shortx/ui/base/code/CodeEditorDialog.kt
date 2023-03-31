@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base.code


import android.graphics.Color
import android.view.Gravity
import android.widget.ArrayAdapter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import com.amrdeveloper.codeview.CodeView
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util._sp
import tornaco.apps.shortx.ui.addrule.action.selector.*
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.base.code.codeditor.syntax.LanguageManager
import tornaco.apps.shortx.ui.base.code.codeditor.syntax.LanguageName
import tornaco.apps.shortx.ui.base.code.codeditor.syntax.ThemeName
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.TypefaceHelper

class CodeEditorState(
    val title: String,
    val message: String?,
    val onSelected: (String) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    private var _output by mutableStateOf("")
    val output get() = _output

    var currentText: String = ""

    fun show(initialText: String = "") {
        this.currentText = initialText
        this._output = ""
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }

    fun setOutput(output: String) {
        _output = output
    }
}

@Composable
fun rememberCodeEditorState(
    title: String,
    message: String? = null,
    onSelected: (String) -> Unit
): CodeEditorState {
    return remember {
        CodeEditorState(title, message, onSelected)
    }
}

@Composable
fun CodeEditorDialog(state: CodeEditorState) {
    if (state.isShow) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            CodeEditor(state)
        }
    }
}

@Composable
private fun CodeEditor(
    state: CodeEditorState
) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                TopAppBar(
                    modifier = Modifier,
                    title = { Text(text = state.title) },
                    navigationIcon = {
                        IconButton(onClick = {
                            state.dismiss()
                        }) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            state.setOutput(shortXManager.executeMVEL(state.currentText))
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.PlayArrow,
                                contentDescription = "Evaluate"
                            )
                        }

                        IconButton(onClick = {
                            state.onSelected(state.currentText)
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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    state.message?.let {
                        Text(text = it, style = MaterialTheme.typography.titleSmall)
                        StandardSpacer()
                    }

                    AnimatedVisibility(visible = state.output.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 72.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                modifier = Modifier.padding(16.dp),
                                text = state.output,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        StandardSpacer()
                    }

                    AndroidView(modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .imePadding(),
                        factory = { context ->
                            return@AndroidView CodeView(context).apply {
                                initView(this)
                                setText(state.currentText)
                                doAfterTextChanged {
                                    it?.toString()?.let { text ->
                                        state.currentText = text
                                        state.setOutput("")
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