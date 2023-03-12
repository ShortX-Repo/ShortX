package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.base.MDIcons
import tornaco.apps.shortx.ui.remote.RemoteExecuteMVELAction

@Composable
fun RemoteActions(addAction: (Action) -> Unit) {
    val vm = hiltViewModel<ActionSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.loadRemoteActions()
    }
    val state = vm.state.collectAsState()
    val uiActionItems = state.value.remoteActions.map {
        Item(
            type = Action.RemoteMVEL::class,
            labelAndDescription = it.title to it.description,
            icon = MDIcons.outlined(it.icon),
            data = it
        )
    }

    val navHost = LocalNavHostController.current
    ActionList(
        actionItems = uiActionItems,
        itemSelected = { _, data ->
            data?.let {
                addAction(Action.RemoteMVEL(it as RemoteExecuteMVELAction))
            }
        })
}