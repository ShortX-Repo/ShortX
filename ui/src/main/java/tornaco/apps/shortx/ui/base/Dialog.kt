/*
 * (C) Copyright 2022 Thanox
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package tornaco.apps.shortx.ui.base

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import tornaco.apps.shortx.core.shortXManager


@Composable
fun ShortFullScreenXDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(
                topStart = 28.dp,
                topEnd = 28.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            ),
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            BackHandler {
                onDismissRequest()
            }
            content()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShortXAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
    iconContentColor: Color = AlertDialogDefaults.iconContentColor,
    titleContentColor: Color = AlertDialogDefaults.titleContentColor,
    textContentColor: Color = AlertDialogDefaults.textContentColor,
    properties: DialogProperties = DialogProperties(),
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        modifier = Modifier.fillMaxWidth(fraction = 0.82f),
        dismissButton = dismissButton,
        icon = icon,
        title = title,
        text = text,
        shape = shape,
        containerColor = containerColor,
        tonalElevation = tonalElevation,
        iconContentColor = iconContentColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        properties = DialogProperties(
            dismissOnBackPress = properties.dismissOnBackPress,
            dismissOnClickOutside = properties.dismissOnClickOutside,
            securePolicy = properties.securePolicy,
            usePlatformDefaultWidth = false
        )
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShortXDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    title: @Composable () -> Unit = {},
    buttons: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = properties.dismissOnBackPress,
            dismissOnClickOutside = properties.dismissOnClickOutside,
            securePolicy = properties.securePolicy,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(fraction = 0.82f),
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                title()
                Spacer(modifier = Modifier.size(16.dp))
                content()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    buttons()
                }
            }

        }
    }
}

class DeleteConfirmDialogState {
    var isShowing by mutableStateOf(false)

    fun show() {
        isShowing = true
    }

    fun dismiss() {
        isShowing = false
    }
}

@Composable
fun rememberDeleteConfirmDialogState(): DeleteConfirmDialogState {
    return remember {
        DeleteConfirmDialogState()
    }
}

@Composable
fun <T> DeleteConfirmDialog(
    titleKey: I18NKey = "ui.common.delete",
    state: DeleteConfirmDialogState,
    data: T,
    messageHint: (T) -> String = { "" },
    onDelete: (T) -> Unit
) {
    if (state.isShowing) {
        val i18N = LocalI18N.current
        ShortXAlertDialog(
            onDismissRequest = {
                state.dismiss()
            },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(data)
                    state.dismiss()
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { state.dismiss() }) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
            title = {
                Text(text = i18N[titleKey])
            },
            text = {
                Text(text = i18N["ui.delete.dialog.confirm", mapOf("wht" to messageHint(data))])
            })
    }
}


open class CommonDialogState {
    private var _isShow by mutableStateOf(false)
    val isShowing get() = _isShow

    fun show() {
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }
}

@Composable
fun rememberRebootConfirmDialogState(): CommonDialogState {
    return remember {
        CommonDialogState()
    }
}

@Composable
fun RebootConfirmDialog(
    state: CommonDialogState,
) {
    if (state.isShowing) {
        val i18N = LocalI18N.current
        ShortXAlertDialog(
            onDismissRequest = {
                state.dismiss()
            },
            confirmButton = {
                TextButton(onClick = {
                    shortXManager.reboot("RebootConfirmDialog")
                    state.dismiss()
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { state.dismiss() }) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
            title = {
                Text(text = i18N["ui.common.reboot"])
            },
            text = {
                Text(text = i18N["ui.common.reboot.message"])
            })
    }
}


data class MenuDialogItem(val title: String, val iconName: String? = null, val onClick: () -> Unit)

class MenuDialogState(
    val title: String,
    val menuItems: List<MenuDialogItem>
) :
    CommonDialogState()

@Composable
fun rememberMenuDialogState(
    title: String,
    menuItems: List<MenuDialogItem>
): MenuDialogState {
    return remember {
        MenuDialogState(title, menuItems)
    }
}

@Composable
fun MenuDialog(
    state: MenuDialogState,
) {
    if (state.isShowing) {
        ShortXDialog(
            onDismissRequest = {
                state.dismiss()
            },

            title = {
                Text(text = state.title)
            }) {

            state.menuItems.forEach { dialogItem ->
                ListItem(
                    title = dialogItem.title,
                    icon = {
                        dialogItem.iconName?.let {
                            RemixIcon(remixName = it)
                        }
                    },
                    onClick = {
                        dialogItem.onClick()
                        state.dismiss()
                    })
            }

        }
    }
}


class TipDialogState(
    val title: String,
    val tip: String,
) : CommonDialogState()

@Composable
fun rememberTipDialogState(
    title: String,
    tip: String,
): TipDialogState {
    return remember {
        TipDialogState(title, tip)
    }
}

@Composable
fun TipDialog(state: TipDialogState) {
    if (state.isShowing) {
        ShortXAlertDialog(
            onDismissRequest = {
                state.dismiss()
            },
            confirmButton = {
                TextButton(onClick = {
                    state.dismiss()
                }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            },
            title = {
                Text(text = state.title)
            },
            text = {
                Text(text = state.tip)
            })
    }
}