package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.Md3ExpPullRefreshIndicator
import tornaco.apps.shortx.ui.remote.RemoteExecuteMVELAction

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RemoteActions(addAction: (Action) -> Unit) {
    val vm = hiltViewModel<ActionSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.loadRemoteActions()
    }
    val state by vm.state.collectAsState()

    val refreshing = state.remoteActionsState is RemoteExecuteMVELActionLoadState.Loading
    val refreshState = rememberPullRefreshState(refreshing, onRefresh = {
        vm.loadRemoteActions()
    })

    Box(
        Modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
    ) {
        ActionContent(state, addAction)
        Md3ExpPullRefreshIndicator(
            refreshing,
            refreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ActionContent(
    state: SelectorState, addAction: (Action) -> Unit
) {
    when (val actionState = state.remoteActionsState) {
        RemoteExecuteMVELActionLoadState.Loading -> {
        }

        RemoteExecuteMVELActionLoadState.Error -> {
            LoadError(modifier = Modifier.fillMaxSize())
        }

        is RemoteExecuteMVELActionLoadState.Success -> {
            val uiActionItems = actionState.data.map {
                SelectorItem(
                    type = Action.RemoteMVEL::class,
                    labelAndDescription = it.title to it.description,
                    icon = it.icon,
                    data = it,
                    isForSubscribedOnly = true,
                    iconColor = colorForAction(Action.RemoteMVEL::class)
                )
            }

            SelectorList(actionItems = uiActionItems, itemSelected = { _, data ->
                data?.let {
                    addAction(Action.RemoteMVEL(action = it as RemoteExecuteMVELAction))
                }
            })
        }
    }
}