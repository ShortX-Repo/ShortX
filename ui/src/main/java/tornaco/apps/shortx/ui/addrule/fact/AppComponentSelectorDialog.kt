@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.common.AppComponentInfo
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.core.unKnownPkg
import tornaco.apps.shortx.ui.addrule.action.selector.*
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class ComponentType {
    Activity,
    Service,
    Receiver,
    Provider
}

class AppComponentSelectorState(
    val types: List<ComponentType>,
    val onAppComponentSelected: (List<AppComponentInfo>) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    var pkg: AppPkg = unKnownPkg

    private var _selectedAppComponents by mutableStateOf(emptyList<AppComponentInfo>())
    val selectedAppComponents get() = _selectedAppComponents


    fun show(pkg: AppPkg) {
        this.pkg = pkg
        _isShow = true
    }

    fun dismiss() {
        _isShow = false
    }

    fun addAppComponent(activity: AppComponentInfo) {
        _selectedAppComponents = selectedAppComponents.toMutableList().apply {
            if (contains(activity).not()) {
                add(activity)
            }
        }
    }

    fun removeAppComponent(activity: AppComponentInfo) {
        _selectedAppComponents = selectedAppComponents.toMutableList().apply {
            remove(activity)
        }
    }

}

@Composable
fun rememberAppComponentSelectorState(
    types: List<ComponentType>,
    onAppComponentSelected: (List<AppComponentInfo>) -> Unit
): AppComponentSelectorState {
    return remember {
        AppComponentSelectorState(types, onAppComponentSelected)
    }
}

@Composable
fun AppComponentSelectorDialog(state: AppComponentSelectorState) {
    if (state.isShow) {
        ShortFullScreenXDialog(onDismissRequest = { state.dismiss() }) {
            AppComponentSelector(state)
        }
    }
}

@Composable
private fun AppComponentSelector(state: AppComponentSelectorState) {
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(modifier = Modifier,
                topBar = {
                    TopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.title.select.action"]) },
                        actions = {
                            IconButton(onClick = {
                                state.onAppComponentSelected(state.selectedAppComponents)
                                state.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = "Done")
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                            }
                        }
                    )
                }) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    var selectedTabIndex by remember {
                        mutableStateOf(0)
                    }
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        val titles = state.types.map { it.name }
                        titles.forEachIndexed { index, title ->
                            Tab(selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title, maxLines = 2, overflow = TextOverflow.Ellipsis
                                    )
                                })
                        }
                    }

                    when (selectedTabIndex) {
                        ComponentType.Activity.ordinal -> {
                            ActivityListContent(state)
                        }
                        ComponentType.Service.ordinal -> {
                            ServiceListContent(state)
                        }
                        ComponentType.Receiver.ordinal -> {}
                        ComponentType.Provider.ordinal -> {}
                    }
                }

            }
        }
    }
}

@Composable
private fun ActivityListContent(selectorState: AppComponentSelectorState) {
    val vm = hiltViewModel<AppComponentSelectorViewModel>().apply { resetState() }
    LaunchedEffect(vm) {
        vm.loadActivities(selectorState.pkg)
    }
    val state by vm.state.collectAsState()
    val components = state.activities

    ComponentList(components, selectorState)
}

@Composable
private fun ServiceListContent(selectorState: AppComponentSelectorState) {
    val vm = hiltViewModel<AppComponentSelectorViewModel>().apply { resetState() }
    LaunchedEffect(vm) {
        vm.loadServices(selectorState.pkg)
    }
    val state by vm.state.collectAsState()
    val components = state.services

    ComponentList(components, selectorState)
}

@Composable
private fun ComponentList(
    components: List<AppComponentInfo>,
    selectorState: AppComponentSelectorState
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(components, key = {
            it.component.toString()
        }) {
            val isSelected = remember(selectorState.selectedAppComponents) {
                selectorState.selectedAppComponents.contains(it)
            }
            AppComponentItem(
                app = it,
                isSelected = isSelected,
                onCheckedChange = { appComponentInfo: AppComponentInfo, selected: Boolean ->
                    if (selected) {
                        selectorState.addAppComponent(appComponentInfo)
                    } else {
                        selectorState.removeAppComponent(appComponentInfo)
                    }
                })
        }
    }
}

@Composable
private fun AppComponentItem(
    app: AppComponentInfo, isSelected: Boolean, onCheckedChange: (AppComponentInfo, Boolean) -> Unit
) {
    Box(modifier = Modifier
        .clip(ShortXCardRoundedCornerShape)
        .clickable {
            onCheckedChange(app, !isSelected)
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .heightIn(min = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    AppIcon(modifier = Modifier.size(38.dp), app.component.pkg)
                    Spacer(modifier = Modifier.size(12.dp))

                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = app.label, fontSize = 14.sp
                        )
                        SmallSpacer()
                        Text(
                            text = app.component.toComponentName().flattenToShortString(),
                            fontSize = 12.sp
                        )
                    }
                }
                Checkbox(checked = isSelected, onCheckedChange = {
                    onCheckedChange(app, it)
                })
            }
        }
    }
}