@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.function

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.ConfirmDialog
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.rememberConfirmDialogState

@AndroidEntryPoint
class FunctionListActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, FunctionListActivity::class.java))
        }
    }

    @Composable
    override fun Content() {
        FunctionListScreen(back = { finish() })
    }
}

@Composable
fun FunctionListScreen(back: () -> Unit) {
    val vm = hiltViewModel<FunctionListViewModel>()
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    val i18n = LocalI18N.current

    LaunchedEffect(vm) {
        vm.loadState()
    }

    val editorLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        vm.loadState()
    }

    LaunchedEffect(state.promptMessage) {
        state.promptMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vm.consumePromptMessage()
        }
    }

    val deleteDialogState = rememberConfirmDialogState()
    val pendingDelete = state.pendingDelete
    LaunchedEffect(pendingDelete) {
        if (pendingDelete != null) {
            deleteDialogState.show()
        }
    }
    LaunchedEffect(deleteDialogState.isShowing) {
        if (!deleteDialogState.isShowing && pendingDelete != null) {
            vm.dismissDelete()
        }
    }
    ConfirmDialog(
        title = "ui.common.delete",
        state = deleteDialogState,
        data = pendingDelete,
        messageHint = {
            buildString {
                append(i18n["ui.delete.dialog.confirm", mapOf("wht" to (it?.function?.name ?: ""))])

                val refs = it?.references
                if (refs?.hasReferences == true) {
                    append("\n\n")
                    append(i18n["ui.function.delete.referenced"])

                    if (refs.ruleTitles.isNotEmpty()) {
                        append("\n")
                        append(i18n["ui.function.delete.references.rules"])
                        append(": ")
                        append(refs.ruleTitles.joinToString())
                    }

                    if (refs.directActionTitles.isNotEmpty()) {
                        append("\n")
                        append(i18n["ui.function.delete.references.direct.actions"])
                        append(": ")
                        append(refs.directActionTitles.joinToString())
                    }

                    if (refs.functionNames.isNotEmpty()) {
                        append("\n")
                        append(i18n["ui.function.delete.references.functions"])
                        append(": ")
                        append(refs.functionNames.joinToString())
                    }

                    append("\n\n")
                    append(i18n["ui.function.delete.references.continue"])
                }
            }
        },
        onConfirm = {
            vm.confirmDeletePending()
        }
    )

    ShortXAppBarScaffold(
        title = i18n["ui.action.functions"],
        onBackPressed = back,
        actions = {
            IconButton(onClick = {
                editorLauncher.launch(AddFunctionActivity.createIntent(context))
            }) {
                RemixIcon(Remix.System.add_line)
            }
        },
        verticalScroll = false
    ) {
        if (state.functions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(i18n["ui.function.empty"])
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.functions, key = { it.id }) { function ->
                    ListItem(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        titleLines = listOf(
                            function.name,
                            i18n["ui.function.summary", mapOf(
                                "type" to function.returnType,
                                "params" to function.parametersCount.toString(),
                                "actions" to function.actionsCount.toString(),
                            )]
                        ),
                        icon = {
                            RemixIcon(Remix.System.function_fill)
                        },
                        action1 = {
                            IconButton(onClick = {
                                vm.prepareDelete(function.raw)
                            }) {
                                RemixIcon(Remix.System.delete_bin_2_line)
                            }
                        },
                        onClick = {
                            editorLauncher.launch(AddFunctionActivity.editIntent(context, function.id))
                        }
                    )
                }
            }
        }
    }
}