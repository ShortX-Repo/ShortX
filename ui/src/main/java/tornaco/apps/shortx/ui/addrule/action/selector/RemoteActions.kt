package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.MDIcons
import tornaco.apps.shortx.ui.remote.RemoteExecuteMVELAction

@Composable
fun RemoteActions(addAction: (Action) -> Unit) {
    val vm = hiltViewModel<ActionSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.loadRemoteActions()
    }
    val state by vm.state.collectAsState()

    when (val actionState = state.remoteActionsState) {
        RemoteExecuteMVELActionLoadState.Loading -> {
            val uiActionItems = mutableListOf<Item>().apply {
                repeat(2) {
                    add(
                        Item(
                            type = Action.RemoteMVEL::class,
                            labelAndDescription = "" to "",
                            icon = Icons.Outlined.Apps,
                        )
                    )
                }
            }

            ActionList(
                childModifier = Modifier.placeholder(
                    visible = true,
                    color = ColorDefaults.backgroundSurfaceColor(),
                    highlight = PlaceholderHighlight.shimmer(highlightColor = Color.White)
                ),
                actionItems = uiActionItems,
                itemSelected = { _, _ -> })
        }
        RemoteExecuteMVELActionLoadState.Error -> {
            LoadError(modifier = Modifier.fillMaxSize())
        }
        is RemoteExecuteMVELActionLoadState.Success -> {
            val uiActionItems = actionState.data.map {
                Item(
                    type = Action.RemoteMVEL::class,
                    labelAndDescription = it.title to it.description,
                    icon = MDIcons.outlined(it.icon),
                    data = it
                )
            }

            ActionList(
                actionItems = uiActionItems,
                itemSelected = { _, data ->
                    data?.let {
                        addAction(Action.RemoteMVEL(it as RemoteExecuteMVELAction))
                    }
                })
        }
    }
}