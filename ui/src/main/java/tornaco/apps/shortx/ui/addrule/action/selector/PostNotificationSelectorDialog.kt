@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.action.selector

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme


class PostNotificationDialogState(
    val onSelected: (Action.PostNotification) -> Unit
) {
    var isShowing by mutableStateOf(false)

    var currentSettings: Action.PostNotification? = null

    fun show(currentSettings: Action.PostNotification? = null) {
        this.currentSettings = currentSettings
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberPostNotificationDialogState(
    onSelected: (Action.PostNotification) -> Unit
): PostNotificationDialogState {
    return remember {
        PostNotificationDialogState(onSelected)
    }
}

@Composable
fun PostNotificationSelectorDialog(state: PostNotificationDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            PostNotificationSelector(state)
        }
    }
}

@Composable
private fun PostNotificationSelector(dialogState: PostNotificationDialogState) {
    val vm = hiltViewModel<PostNotificationSelectorViewModel>().apply { resetState() }
    LaunchedEffect(dialogState.currentSettings) {
        dialogState.currentSettings?.let {
            vm.initFrom(it)
        }
    }

    val state by vm.state.collectAsState()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(LocalI18N.current["ui.action.post.notification"]) },
                        navigationIcon = {
                            IconButton(onClick = {
                                dialogState.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                dialogState.onSelected(state.data)
                                dialogState.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Done")
                            }

                        },
                    )
                }) {
                Content(paddingValues = it, vm = vm)
            }
        }
    }

}

@Composable
private fun Content(paddingValues: PaddingValues, vm: PostNotificationSelectorViewModel) {
    val state by vm.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)
            .animateContentSize()
            .verticalScroll(rememberScrollState())
    ) {

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(LocalI18N.current["ui.action.post.notification.tag"])
            TipBody(LocalI18N.current["ui.action.post.notification.tag.description"])
            OutlinedTextField(value = state.data.tag, onValueChange = {
                vm.updateTag(it)
            })
        }

        //   "ui.action.post.notification.title": "标题",
        //  "ui.action.post.notification.message": "内容",
        //  "ui.action.post.notification.small.icon": "状态栏图标",
        //  "ui.action.post.notification.large.icon": "大图标（可选）",
        //  "ui.action.post.notification.important": "重要通知",
        //  "ui.action.post.notification.sound": "播放声音",
        //  "ui.action.post.notification.vibrate": "振动",

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(LocalI18N.current["ui.action.post.notification.title"])
            OutlinedTextField(value = state.data.title, onValueChange = {
                vm.updateTitle(it)
            })
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(LocalI18N.current["ui.action.post.notification.message"])
            OutlinedTextField(value = state.data.message, onValueChange = {
                vm.updateMessage(it)
            })
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(LocalI18N.current["ui.action.post.notification.small.icon"])
            val iconSelector = rememberMDIconSelectorState {
                vm.updateSmallIcon(it)
            }
            MDIconSelectorDialog(state = iconSelector)
            TextButton(onClick = {
                iconSelector.show()
            }) {
                RemixIcon(state.data.smallIcon)
            }
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(LocalI18N.current["ui.action.post.notification.large.icon"])
            val iconSelector = rememberMDIconSelectorState {
                vm.updateLargeIcon(it)
            }
            MDIconSelectorDialog(state = iconSelector)
            TextButton(onClick = {
                iconSelector.show()
            }) {
                if (state.data.largeIcon.isNotEmpty()) {
                    RemixIcon(state.data.largeIcon)
                }
            }
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(text = LocalI18N.current["ui.action.post.notification.important"])
            Switch(checked = state.data.isImportant, onCheckedChange = {
                vm.setImportant(it)
            })
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(text = LocalI18N.current["ui.action.post.notification.sound"])
            Switch(checked = state.data.sound, onCheckedChange = {
                vm.setSound(it)
            })
        }

        StandardSpacer()
        Column(verticalArrangement = Arrangement.Center) {
            SmallTitle(text = LocalI18N.current["ui.action.post.notification.vibrate"])
            Switch(checked = state.data.vibrate, onCheckedChange = {
                vm.setVibrate(it)
            })
        }

    }
}