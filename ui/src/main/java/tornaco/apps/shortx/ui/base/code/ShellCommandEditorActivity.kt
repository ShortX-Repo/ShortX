@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base.code


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
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
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.TextDisplayActivity
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.composeLogger
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

@AndroidEntryPoint
class ShellCommandEditorActivity : BaseSelectorActivity<Action.ShellCommand>() {
    @Composable
    override fun BaseSelectorActivity<Action.ShellCommand>.SelectorContent(currentData: Action.ShellCommand?) {
        ShellCommandEditor(currentData)
    }
}

@Composable
fun ShellCommandEditorActivity.ShellCommandEditor(
    currentAction: Action.ShellCommand?,
) {
    val outputTitle = LocalI18N.current[I18N_R.UI_ACTION_SHELL]
    val context = LocalContext.current
    var ceComposeBinding by remember { mutableStateOf<CEComposeBinding?>(null) }
    var moreMenuExpanded by remember { mutableStateOf(false) }

    val vm = hiltViewModel<ShellCommandEditorViewModel>()
    LaunchedEffect(vm) {
        vm.initWith(currentAction)
    }

    vm.collectSideEffect(sideEffect = {
        if (it.isBlank()) {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        } else {
            TextDisplayActivity.start(context, outputTitle, it)
        }
    })

    val state by vm.collectAsState()

    LaunchedEffect(state.command) {
        ceComposeBinding?.updateText(state.command)
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = {
                        Text("")
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
                            setResultAndFinish(
                                Action.ShellCommand(
                                    command = state.command,
                                    singleShot = state.isSingleShot
                                )
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Done"
                            )
                        }

                        IconButton(onClick = {
                            if (state.isExecuting) {
                                vm.stopExecute()
                            } else {
                                vm.execute()
                            }
                        }) {
                            if (state.isExecuting) {
                                Icon(
                                    imageVector = Icons.Outlined.Stop,
                                    contentDescription = LocalI18N.current[I18N_R.UI_COMMON_STOP]
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = LocalI18N.current[I18N_R.UI_COMMON_RUN]
                                )
                            }
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
                                    text = { Text(LocalI18N.current[I18N_R.UI_ACTION_SHELL_SINGLE_SHOT]) },
                                    onClick = {
                                        moreMenuExpanded = false
                                        vm.setSingleShot(!state.isSingleShot)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(LocalI18N.current[I18N_R.UI_CODE_EDITOR_WORDWRAP]) },
                                    onClick = {
                                        moreMenuExpanded = false
                                        ceComposeBinding?.toggleWordwrap()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(LocalI18N.current[I18N_R.UI_CODE_EDITOR_WHITESPACE]) },
                                    onClick = {
                                        moreMenuExpanded = false
                                        ceComposeBinding?.toggleNonPrintable()
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
                    if (!state.isInSystemUI) {
                        Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                            TipBody(text = LocalI18N.current[I18N_R.UI_ACTION_SHELL_SUMMARY])
                        }
                    }

                    AndroidViewBinding(
                        factory = { inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean ->
                            CodeEditorComposeBinding.inflate(inflater, parent, attachToParent)
                                .apply {
                                    CEComposeBinding(
                                        binding = this,
                                        showToolbar = true,
                                        initialText = currentAction?.command ?: "",
                                        languageScopeName = "source.shell",
                                        autoCompleteEnabled = false,
                                        showWordwrapInBottomBar = false,
                                        showNonPrintableInBottomBar = false,
                                        footerTitle = outputTitle
                                    ) {
                                        composeLogger.logOnDebug("Content change: $it")
                                        vm.updateCommand(it)
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
