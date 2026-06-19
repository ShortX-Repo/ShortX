@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.settings.dav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.proto.common.WebDavProfile
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ClickOutsideClearFocusBox
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R


class DavProfileEditDialogState(val save: (WebDavProfile) -> Unit) : CommonDialogState() {
    var id: String by mutableStateOf("")
    var alias: String by mutableStateOf("")
    var userName: String by mutableStateOf("")
    var password: String by mutableStateOf("")

    fun show(profile: WebDavProfile) {
        this.id = profile.profileId
        this.alias = profile.alias
        this.userName = profile.userName
        this.password = profile.password

        show()
    }
}

@Composable
fun rememberDavProfileEditDialogState(save: (WebDavProfile) -> Unit): DavProfileEditDialogState {
    return remember {
        DavProfileEditDialogState(save)
    }
}

@Composable
fun DavProfileEditDialog(state: DavProfileEditDialogState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = {
            state.dismiss()
        }) {
            ShortXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier,
                        topBar = {
                            ShortXTopAppBar(
                                modifier = Modifier,
                                title = { Text(text = "Webdav") },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        state.dismiss()
                                    }) {
                                        RemixIcon(remixName = Remix.System.close_line)
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
                                        if (state.userName.isNotBlank()
                                            && state.password.isNotBlank()
                                        ) {
                                            state.save(
                                                WebDavProfile.newBuilder()
                                                    .setProfileId(state.id)
                                                    .setUserName(state.userName)
                                                    .setPassword(state.password)
                                                    .setAlias(state.alias)
                                                    .build()
                                            )
                                            state.dismiss()
                                        }
                                    }) {
                                        RemixIcon(remixName = Remix.System.check_line)
                                    }
                                },
                            )
                        }) { paddingValues ->
                        ClickOutsideClearFocusBox {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                                    .padding(16.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                FieldTitle(text = LocalI18N.current[I18N_R.UI_SETTINGS_WEBDAV_PROFILE_ALIAS])
                                SmallSpacer()
                                ShortXTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.alias,
                                    onValueChange = {
                                        state.alias = it
                                    },
                                    showSymbolButton = false
                                )
                                StandardSpacer()

                                FieldTitle(text = LocalI18N.current[I18N_R.UI_SETTINGS_WEBDAV_PROFILE_USER_NAME])
                                SmallSpacer()
                                ShortXTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.userName,
                                    onValueChange = {
                                        state.userName = it
                                    },
                                    showSymbolButton = false
                                )
                                StandardSpacer()

                                FieldTitle(text = LocalI18N.current[I18N_R.UI_SETTINGS_WEBDAV_PROFILE_PASSWORD])
                                SmallSpacer()
                                ShortXTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = state.password,
                                    onValueChange = {
                                        state.password = it
                                    },
                                    showSymbolButton = false
                                )
                                StandardSpacer()
                            }
                        }
                    }
                }
            }
        }
    }
}