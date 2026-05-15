@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base.code.js


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.rosemoe.sora.app.CEComposeBinding
import io.github.rosemoe.sora.app.databinding.CodeEditorComposeBinding
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.CodeType
import tornaco.apps.shortx.core.proto.common.CoroutineContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.mapper.labelKey
import tornaco.apps.shortx.ui.addrule.action.mapper.summaryKey
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MenuDialog
import tornaco.apps.shortx.ui.base.MenuDialogItem
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTextButton
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TextDisplayActivity
import tornaco.apps.shortx.ui.base.code.ParcelableCode
import tornaco.apps.shortx.ui.base.rememberMenuDialogState
import tornaco.apps.shortx.ui.code.CodeLibSelectorActivity
import tornaco.apps.shortx.ui.code.popupCodeLibraryItemResult
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.settings.log.js.JSLogActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class JSEditorActivity : BaseSelectorActivity<ParcelableCode>() {
    @Composable
    override fun BaseSelectorActivity<ParcelableCode>.SelectorContent(currentData: ParcelableCode?) {
        ExecuteJSEditor(currentData)
    }
}

@Composable
fun JSEditorActivity.ExecuteJSEditor(
    currentAction: ParcelableCode?,
) {
    val outputTitle = LocalI18N.current["ui.title.js"]
    val context = LocalContext.current
    var ceComposeBinding by remember { mutableStateOf<CEComposeBinding?>(null) }
    var moreMenuExpanded by remember { mutableStateOf(false) }

    val vm = hiltViewModel<JSEditorViewModel>()
    val codeLibSelectorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            result.popupCodeLibraryItemResult()?.let { item ->
                if (item.type == CodeType.CodeType_JAVASCRIPT) {
                    vm.updateExpression(item.content)
                } else {
                    Toast.makeText(context, "请选择 JavaScript 代码", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
    val state by vm.collectAsState()

    LaunchedEffect(vm) {
        vm.initWith(currentAction)
    }

    LaunchedEffect(state.expression) {
        ceComposeBinding?.updateText(state.expression)
    }

    vm.collectSideEffect(sideEffect = {
        if (it.isBlank()) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        } else {
            TextDisplayActivity.start(context, outputTitle, it)
        }
    })

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val contextMenuState = rememberMenuDialogState<CoroutineContext>(
                title = LocalI18N.current["ui.common.coroutine.context"],
                menuItems = CoroutineContext.entries.filterNot { it == CoroutineContext.UNRECOGNIZED }
                    .map {
                        MenuDialogItem(
                            id = it.name,
                            title = LocalI18N.current[it.labelKey()],
                            summary = LocalI18N.current[it.summaryKey()]
                        )
                    },
                onItemSelected = { _, id ->
                    val type = CoroutineContext.valueOf(id)
                    vm.setCoroutineContext(type)
                }
            )
            MenuDialog(state = contextMenuState)
            Scaffold(modifier = Modifier, topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = {
                        ShortXTextButton(onClick = {
                            contextMenuState.show()
                        }) {
                            Text(LocalI18N.current[state.coroutineContext.labelKey()])
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            cancelAndFinish()
                        }) {
                            RemixIcon(remixName = Remix.System.close_line)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            JSLogActivity.Starter.start(context)
                        }) {
                            RemixIcon(remixName = Remix.Document.file_text_line)
                        }

                        IconButton(
                            enabled = !state.isExecuting,
                            onClick = {
                                vm.execute()
                            }
                        ) {
                            if (state.isExecuting) {
                                Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                                    Loading(modifier = Modifier.size(20.dp))
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = LocalI18N.current["ui.common.run"]
                                )
                            }
                        }

                        IconButton(onClick = {
                            setResultAndFinish(
                                ParcelableCode(
                                    expression = state.expression,
                                    coroutineContext = state.coroutineContext,
                                    parameterInputs = state.parameterInputs
                                )
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Done"
                            )
                        }

                        Box {
                            IconButton(onClick = { moreMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = "More actions"
                                )
                            }
                            DropdownMenu(
                                expanded = moreMenuExpanded,
                                onDismissRequest = { moreMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(LocalI18N.current[state.coroutineContext.labelKey()]) },
                                    onClick = {
                                        moreMenuExpanded = false
                                        contextMenuState.show()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(LocalI18N.current["ui.code.editor.from.code.library"]) },
                                    onClick = {
                                        moreMenuExpanded = false
                                        codeLibSelectorLauncher.launch(
                                            CodeLibSelectorActivity.newIntent(
                                                context = context,
                                                codeType = CodeType.CodeType_JAVASCRIPT
                                            )
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(LocalI18N.current["ui.code.editor.wordwrap"]) },
                                    onClick = {
                                        moreMenuExpanded = false
                                        ceComposeBinding?.toggleWordwrap()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(LocalI18N.current["ui.code.editor.whitespace"]) },
                                    onClick = {
                                        moreMenuExpanded = false
                                        ceComposeBinding?.toggleNonPrintable()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(LocalI18N.current["ui.code.editor.log"]) },
                                    onClick = {
                                        moreMenuExpanded = false
                                        JSLogActivity.Starter.start(context)
                                    }
                                )
                            }
                        }
                    },
                )
            }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    AndroidViewBinding(
                        factory = { inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean ->
                            CodeEditorComposeBinding.inflate(inflater, parent, attachToParent)
                                .apply {
                                    CEComposeBinding(
                                        binding = this,
                                        showToolbar = true,
                                        initialText = currentAction?.expression ?: "",
                                        languageScopeName = "source.js",
                                        autoCompleteEnabled = false,
                                        showWordwrapInBottomBar = false,
                                        showNonPrintableInBottomBar = false,
                                        footerTitle = "JS"
                                    ) {
                                        composeLogger.logOnDebug("Content change: $it")
                                        vm.updateExpression(it)
                                    }.also {
                                        ceComposeBinding = it
                                        it.onCreate()
                                    }
                                }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding(),
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
