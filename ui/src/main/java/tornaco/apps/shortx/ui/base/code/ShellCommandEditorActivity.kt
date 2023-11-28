@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.base.code


import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.PlayArrow
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.widget.doAfterTextChanged
import androidx.hilt.navigation.compose.hiltViewModel
import com.amrdeveloper.codeview.CodeView
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.Loading
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SwitchBar
import tornaco.apps.shortx.ui.base.TextDisplayActivity
import tornaco.apps.shortx.ui.base.TipBody
import tornaco.apps.shortx.ui.base.code.codeditor.initView
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class ShellCommandEditorActivity : BaseSelectorActivity<Action.ShellCommand>() {
    @Composable
    override fun BaseSelectorActivity<Action.ShellCommand>.SelectorContent(currentAction: Action.ShellCommand?) {
        ShellCommandEditor(currentAction)
    }
}

@Composable
fun ShellCommandEditorActivity.ShellCommandEditor(
    currentAction: Action.ShellCommand?,
) {
    val outputTitle = LocalI18N.current["ui.action.shell"]
    val context = LocalContext.current

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

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier, topBar = {
                ShortXTopAppBar(
                    modifier = Modifier,
                    title = {
                        Column {
                            Text(text = LocalI18N.current["ui.action.shell"])
                            if (!state.isInSystemUI) {
                                TipBody(text = LocalI18N.current["ui.action.shell.summary"])
                            }
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
                        if (!state.isExecuting) {
                            IconButton(
                                onClick = {
                                    vm.execute()
                                }) {
                                Icon(
                                    imageVector = Icons.Outlined.PlayArrow,
                                    contentDescription = "Evaluate"
                                )
                            }
                        }

                        if (state.isExecuting) {
                            Box(modifier = Modifier, contentAlignment = Alignment.Center) {
                                Loading(modifier = Modifier.size(28.dp))

                                IconButton(onClick = {
                                    vm.stopExecute()
                                }) {
                                    RemixIcon(
                                        remixName = Remix.Media.stop_circle_line,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        IconButton(onClick = {
                            setResultAndFinish(
                                Action.ShellCommand(
                                    command = state.command,
                                    singleShot = state.isSingleShot
                                )
                            )
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
                        .verticalScroll(rememberScrollState()),
                ) {
                    SwitchBar(modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                        text = LocalI18N.current["ui.action.shell.single.shot"],
                        tip = LocalI18N.current["ui.action.shell.single.shot.description"],
                        isChecked = state.isSingleShot,
                        onCheckChange = {
                            vm.setSingleShot(it)
                        })

                    val isDarkTheme = isSystemInDarkTheme()
                    AndroidView(modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                        factory = { ctx ->
                            return@AndroidView CodeView(ctx).apply {
                                initView(this, isDarkTheme)
                                setText(currentAction?.command)
                                doAfterTextChanged {
                                    it?.toString()?.let { text ->
                                        vm.updateCommand(text)
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