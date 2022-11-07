package tornaco.apps.shortx.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.action.Action
import tornaco.apps.shortx.core.action.ExpandNotification
import tornaco.apps.shortx.core.action.StartActivity
import tornaco.apps.shortx.core.action.TakeScreenshot
import tornaco.apps.shortx.core.gesture.Area
import tornaco.apps.shortx.core.gesture.GestureEvent
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.base.ExpandableContainer
import tornaco.apps.shortx.ui.base.ExpandableState
import tornaco.apps.shortx.ui.base.requireActivity
import tornaco.apps.shortx.ui.main.actions.ActionsSelector
import tornaco.apps.shortx.ui.main.actions.AllSelectors
import tornaco.apps.shortx.ui.main.actions.rememberActionsSelectorState
import tornaco.apps.shortx.ui.main.actions.rememberStartActivitySelectorState
import tornaco.apps.shortx.ui.theme.ShortXTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel = hiltViewModel<MainViewModel>(LocalContext.current.requireActivity())
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    viewModel.bindLifecycle(lifecycle)
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.loadGestureSettings()
    }

    ShortXTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Scaffold(modifier = Modifier, topBar = {
                TopAppBar(title = {
                    Text(text = "ShortX")
                })
            }) {
                Column(Modifier
                    .padding(it)
                    .padding(vertical = 16.dp)
                    .verticalScroll(rememberScrollState())) {
                    state.areaSettings.forEach { settings ->
                        AreaSettings(settings = settings,
                            availableGestureActions = state.availableGestureActions,
                            setEnabled = { area: Area, enable: Boolean ->
                                viewModel.setGestureEnabledOnArea(area, enable)
                            }, setAction = { gesture, action ->
                                viewModel.setAreaGestureAction(settings.area, gesture, action)
                            })
                    }
                }
            }

        }
    }
}

@Composable
fun AreaSettings(
    settings: AreaSettings,
    availableGestureActions: List<String>,
    setAction: (GestureEvent, Action) -> Unit,
    setEnabled: (Area, Boolean) -> Unit,
) {
    val expandState = remember { mutableStateOf(ExpandableState.Collapsed) }
    ExpandableContainer(expandState = expandState, mainContent = {
        AreaSettingsMain(settings) {
            setEnabled(settings.area, it)
        }
    }, expandContent = {
        AreaSettingsExpanded(settings, availableGestureActions, setAction)
    })
}

@Composable
fun AreaSettingsMain(
    settings: AreaSettings,
    setEnabled: (Boolean) -> Unit,
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = settings.area.name, style = MaterialTheme.typography.titleMedium)
        Switch(checked = settings.isEnabled, onCheckedChange = {
            setEnabled(it)
        })
    }
}

@Composable
fun AreaSettingsExpanded(
    settings: AreaSettings,
    availableGestureActions: List<String>,
    setAction: (GestureEvent, Action) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        settings.gestureActions.forEach { gestureAction ->
            Box {
                val actionSelectorState = rememberActionsSelectorState()
                AreaGestureActionItem(gestureAction) {
                    actionSelectorState.open()
                }

                val startActivityState = rememberStartActivitySelectorState {
                    setAction(gestureAction.gestureEvent, it)
                }

                ActionsSelector(actionSelectorState, availableGestureActions) { actionName ->
                    when (actionName) {
                        TakeScreenshot.NAME -> {
                            setAction(gestureAction.gestureEvent, TakeScreenshot())
                        }
                        ExpandNotification.NAME -> {
                            setAction(gestureAction.gestureEvent, ExpandNotification())
                        }
                        StartActivity.NAME -> {
                            startActivityState.show()
                        }
                    }
                }

                AllSelectors(startActivityState = startActivityState)
            }
        }

    }
}


@Composable
private fun AreaGestureActionItem(
    gestureAction: GestureAction,
    onClick: () -> Unit,
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick()
        }
        .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(painter = painterResource(id = R.drawable.ic_baseline_category_24),
            contentDescription = "")
        Spacer(modifier = Modifier.size(16.dp))
        Column(modifier = Modifier) {
            Text(text = gestureAction.gestureEvent.name,
                style = MaterialTheme.typography.titleMedium)
            Text(text = gestureAction.actionName, style = MaterialTheme.typography.bodyMedium)
            gestureAction.actionDescriptions.forEach {
                Text(text = it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}