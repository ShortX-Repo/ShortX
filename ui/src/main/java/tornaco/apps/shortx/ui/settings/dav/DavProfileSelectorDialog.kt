@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package tornaco.apps.shortx.ui.settings.dav

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import tornaco.apps.shortx.core.proto.common.WebDavProfile
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortFullScreenXDialog
import tornaco.apps.shortx.ui.base.ShortXTopAppBar
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.core.i18n.I18N_R

class DavProfileSelectorState(
    val onDavProfileSelected: (WebDavProfile) -> Unit
) : CommonDialogState() {
}

@Composable
fun rememberDavProfileSelectorState(
    onDavProfileSelected: (WebDavProfile) -> Unit
): DavProfileSelectorState {
    return remember {
        DavProfileSelectorState(onDavProfileSelected)
    }
}

@Composable
fun DavProfileSelectorDialog(state: DavProfileSelectorState) {
    if (state.isShowing) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            DavProfileSelector(state)
        }
    }
}

@Composable
private fun DavProfileSelector(selectorState: DavProfileSelectorState) {
    val profiles = remember {
        shortXManager.getAllWebDavProfiles()
    }
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current[I18N_R.UI_SETTINGS_WEBDAV_PROFILE]) },
                        actions = {},
                        navigationIcon = {
                            IconButton(onClick = {
                                selectorState.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        }
                    )
                }) { paddingValues ->
                LazyColumn(contentPadding = paddingValues) {
                    items(profiles) {
                        ListItem(
                            titleLines = listOf(
                                it.alias,
                                it.userName
                            ),
                            onClick = {
                                selectorState.onDavProfileSelected(it)
                                selectorState.dismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}