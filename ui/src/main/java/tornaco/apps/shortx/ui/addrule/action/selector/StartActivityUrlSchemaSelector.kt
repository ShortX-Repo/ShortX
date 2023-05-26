@file:OptIn(
    ExperimentalMaterial3Api::class
)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.os.isMainUser
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallTitle
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.i18NIns
import tornaco.apps.shortx.ui.theme.ShortXTheme

class StartActivityUrlSchemaDialogState(
    val onSelected: (Action.StartActivityUrlSchema) -> Unit
) {
    var isShowing by mutableStateOf(false)

    var urlSchema by mutableStateOf("")

    var userId by mutableStateOf(0)
    var userLabel by mutableStateOf("N/A")

    fun show(current: Action.StartActivityUrlSchema? = null) {
        this.urlSchema = current?.urlSchema ?: ""
        updateUser(current?.userId ?: myUserId)

        isShowing = true
    }

    fun updateUser(id: Int) {
        this.userId = id
        this.userLabel = if (userId.isMainUser()) {
            i18NIns["ui.common.user.main"]
        } else {
            shortXManager.getAllUsers().firstOrNull { it.userId == userId }?.name ?: "N/A"
        }
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberStartActivityUrlSchemaDialogState(
    onSelected: (Action.StartActivityUrlSchema) -> Unit
): StartActivityUrlSchemaDialogState {
    return remember {
        StartActivityUrlSchemaDialogState(onSelected)
    }
}

@Composable
fun StartActivityUrlSchemaDialog(state: StartActivityUrlSchemaDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            ShowDialogSelector(state)
        }
    }
}

@Composable
private fun ShowDialogSelector(dialogState: StartActivityUrlSchemaDialogState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.start.activity.intent"]) },
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
                                    Action.StartActivityUrlSchema(
                                        urlSchema = dialogState.urlSchema,
                                        userId = dialogState.userId
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
                    SmallTitle(text = "URL Schema")
                    ShortXTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = dialogState.urlSchema,
                        onValueChange = {
                            dialogState.urlSchema = it
                        })
                    StandardSpacer()

                    SmallTitle(text = LocalI18N.current["ui.common.user.id"])
                    val userSelectorState = rememberUserSelectorDialogState(onSelected = {
                        dialogState.updateUser(it.userId)
                    })
                    UserSelectorDialog(state = userSelectorState)
                    FilledTonalButton(onClick = {
                        userSelectorState.show()
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = dialogState.userLabel)
                            StandardSpacer()
                            RemixIcon(remixName = Remix.Arrows.arrow_drop_down_fill)
                        }
                    }
                }
            }
        }
    }

}
