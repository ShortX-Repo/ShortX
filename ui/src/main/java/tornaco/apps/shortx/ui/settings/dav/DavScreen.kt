@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.settings.dav

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tornaco.apps.shortx.core.proto.common.WebDavProfile
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ListItem
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import java.util.UUID

@Composable
fun DavScreen(back: () -> Unit) {
    val vm = hiltViewModel<DavSettingsViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()

    val addDialog = rememberDavProfileEditDialogState {
        vm.addProfile(it)
    }
    DavProfileEditDialog(addDialog)

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.settings.webdav.profile"],
        onBackPressed = {
            back()
        },
        actions = {
            IconButton(onClick = {
                addDialog.show(
                    WebDavProfile.newBuilder().setProfileId(UUID.randomUUID().toString()).build()
                )
            }) {
                RemixIcon(Remix.System.add_fill)
            }
        }) {
        state.profiles.forEach { profile ->
            val editDialog = rememberDavProfileEditDialogState {
                vm.addProfile(it)
            }
            DavProfileEditDialog(editDialog)

            ListItem(
                titleLines = listOf(
                    profile.alias,
                    profile.userName,
                    profile.password
                ),
                action1 = {
                    IconButton(onClick = {
                        vm.removeProfile(profile.profileId)
                    }) {
                        RemixIcon(Remix.System.delete_bin_fill)
                    }
                    IconButton(onClick = {
                        editDialog.show(profile)
                    }) {
                        RemixIcon(Remix.Edit.edit)
                    }
                }
            )
        }
    }
}