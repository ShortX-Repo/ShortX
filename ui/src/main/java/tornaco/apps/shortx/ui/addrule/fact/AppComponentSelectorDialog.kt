@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppComponentInfo
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.core.unKnownPkg
import tornaco.apps.shortx.ui.addrule.action.selector.*
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ComponentRule

enum class ComponentType {
    Activity,
    Service,
    Receiver,
    Provider
}

class AppComponentSelectorState(
    val types: List<ComponentType>,
    private val maxItemCount: Int = Int.MAX_VALUE,
    val onAppComponentSelected: (List<AppComponentInfo>) -> Unit
) {
    private var _isShow by mutableStateOf(false)
    val isShow get() = _isShow

    var pkg: AppPkg = unKnownPkg

    private var _selectedAppComponents by mutableStateOf(emptyList<AppComponentInfo>())
    val selectedAppComponents get() = _selectedAppComponents

    fun show(pkg: AppPkg) {
        this.pkg = pkg
        _selectedAppComponents = emptyList()
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

            if (size > maxItemCount) {
                removeAt(0)
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
    maxItemCount: Int = Int.MAX_VALUE,
    onAppComponentSelected: (List<AppComponentInfo>) -> Unit
): AppComponentSelectorState {
    return remember {
        AppComponentSelectorState(types, maxItemCount, onAppComponentSelected)
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
                    ShortXTopAppBar(
                        modifier = Modifier,
                        title = { Text(text = LocalI18N.current["ui.title.select.component"]) },
                        actions = {
                            IconButton(onClick = {
                                state.onAppComponentSelected(state.selectedAppComponents)
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.check_line)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                state.dismiss()
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
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

                        ComponentType.Receiver.ordinal -> {
                            ReceiverListContent(state)
                        }

                        ComponentType.Provider.ordinal -> {
                            ProviderListContent(state)
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun ActivityListContent(selectorState: AppComponentSelectorState) {
    val vm = hiltViewModel<AppComponentSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.loadActivities(selectorState.pkg)
    }
    val state by vm.state.collectAsState()
    val components = state.activities

    if (state.isLoading) {
        Loading(modifier = Modifier.fillMaxSize())
    } else {
        ComponentList(components, selectorState)
    }

}

@Composable
private fun ServiceListContent(selectorState: AppComponentSelectorState) {
    val vm = hiltViewModel<AppComponentSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.loadServices(selectorState.pkg)
    }
    val state by vm.state.collectAsState()
    val components = state.services

    if (state.isLoading) {
        Loading(modifier = Modifier.fillMaxSize())
    } else {
        ComponentList(components, selectorState)
    }
}

@Composable
private fun ReceiverListContent(selectorState: AppComponentSelectorState) {
    val vm = hiltViewModel<AppComponentSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.loadReceivers(selectorState.pkg)
    }
    val state by vm.state.collectAsState()
    val components = state.receivers

    if (state.isLoading) {
        Loading(modifier = Modifier.fillMaxSize())
    } else {
        ComponentList(components, selectorState)
    }
}

@Composable
private fun ProviderListContent(selectorState: AppComponentSelectorState) {
    val vm = hiltViewModel<AppComponentSelectorViewModel>()
    LaunchedEffect(vm) {
        vm.loadProviders(selectorState.pkg)
    }
    val state by vm.state.collectAsState()
    val components = state.providers

    if (state.isLoading) {
        Loading(modifier = Modifier.fillMaxSize())
    } else {
        ComponentList(components, selectorState)
    }
}

@Composable
private fun ComponentList(
    components: List<AppComponentInfoWithRule>,
    selectorState: AppComponentSelectorState
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(components, key = {
            it.info.component.toString()
        }) {
            val isSelected = remember(selectorState.selectedAppComponents) {
                selectorState.selectedAppComponents.contains(it.info)
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
    app: AppComponentInfoWithRule,
    isSelected: Boolean,
    onCheckedChange: (AppComponentInfo, Boolean) -> Unit
) {
    ShortXCard(modifier = Modifier
        .padding(horizontal = 12.dp, vertical = 4.dp),
        onClick = {
            onCheckedChange(app.info, !isSelected)
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 64.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
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
                        AppIcon(modifier = Modifier.size(38.dp), app.info.component.pkg)
                        Spacer(modifier = Modifier.size(12.dp))

                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = app.info.label, fontSize = 14.sp
                            )
                            SmallSpacer()
                            Text(
                                text = app.info.component.toComponentName().flattenToShortString(),
                                fontSize = 12.sp
                            )
                        }
                    }
                    Checkbox(checked = isSelected, onCheckedChange = {
                        onCheckedChange(app.info, it)
                    })
                }


                app.rule?.let {
                    val ruleInfoDialogState = rememberComponentRuleInfoDialogState(
                        ruleLabel = it.label,
                        ruleUrl = it.descriptionUrl ?: ""
                    )
                    ComponentRuleInfoDialog(state = ruleInfoDialogState)

                    val context = LocalContext.current
                    FilledTonalButton(
                        contentPadding = PaddingValues(vertical = 6.dp, horizontal = 16.dp),
                        enabled = it.descriptionUrl != null,
                        onClick = {
                            it.descriptionUrl?.let {
                                ruleInfoDialogState.show()
                            }
                        }) {
                        Icon(
                            painter = painterResource(id = it.iconRes),
                            contentDescription = null,
                            tint = if (it.isSimpleColorIcon) LocalContentColor.current else Color.Unspecified
                        )
                        TinySpacer()
                        Text(text = it.label)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun AppComponentItemPreview() {
    AppComponentItem(
        app = AppComponentInfoWithRule(
            info = AppComponentInfo.newBuilder().setLabel("Label").setComponent(
                AppComponent.newBuilder().setPkg(
                    AppPkg.newBuilder().setUserId(0).setPkgName("com.xx.xx").build()
                ).setClassName("com.xx.xx.Class").build()
            ).build(),
            rule = ComponentRule(
                label = "AD",
                iconRes = github.tornaco.shortx.icons.R.drawable.ic_remix_24_hours_line,
                descriptionUrl = null,
                isSimpleColorIcon = false,
                regexName = "Regex"
            )
        ),
        isSelected = true,
        onCheckedChange = { info, checked -> })
}