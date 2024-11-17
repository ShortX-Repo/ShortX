package tornaco.apps.shortx.ui.addrule.action.selector.webdav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.FieldTitle
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXAppBarScaffold
import tornaco.apps.shortx.ui.base.ShortXTextField
import tornaco.apps.shortx.ui.base.SmallSpacer
import tornaco.apps.shortx.ui.settings.dav.DavProfileSelectorDialog
import tornaco.apps.shortx.ui.settings.dav.rememberDavProfileSelectorState

@AndroidEntryPoint
class WebDavListSelectorActivity : BaseSelectorActivity<Action.WebDavList>() {
    @Composable
    override fun BaseSelectorActivity<Action.WebDavList>.SelectorContent(currentData: Action.WebDavList?) {
        Selector(currentData)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSelectorActivity<Action.WebDavList>.Selector(currentAction: Action.WebDavList?) {
    val vm = hiltViewModel<WebDavSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.init(currentAction)
    }

    val dialogState by vm.state.collectAsState()

    ShortXAppBarScaffold(
        title = LocalI18N.current["ui.action.web.dav.list"],
        onBackPressed = {
            cancelAndFinish()
        },
        actions = {
            IconButton(onClick = {
                setResultAndFinish(
                    Action.WebDavList(
                        id = dialogState.id,
                        url = dialogState.url,
                        depth = dialogState.depth,
                        profile = dialogState.profile
                    )
                )
            }) {
                RemixIcon(remixName = Remix.System.check_line)
            }

        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FieldTitle(text = "Url")
            SmallSpacer()
            ShortXTextField(
                modifier = Modifier.fillMaxWidth(),
                value = dialogState.url,
                onValueChange = {
                    vm.inputUrl(it)
                })
            LargeSpacer()

            FieldTitle(text = LocalI18N.current["ui.settings.webdav.profile"])
            SmallSpacer()
            val webdavProfileSelectorState = rememberDavProfileSelectorState {
                vm.setProfile(it)
            }
            DavProfileSelectorDialog(webdavProfileSelectorState)
            Button(onClick = {
                webdavProfileSelectorState.show()
            }) {
                Text(text = if (dialogState.profile == null) {
                    LocalI18N.current["ui.common.unselected"]
                } else {
                    dialogState.profile!!.let {
                        it.alias ?: it.userName
                    }
                })
            }
            LargeSpacer()
        }
    }
}