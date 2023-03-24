package tornaco.apps.shortx.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.ClipboardUtils

enum class HomeBottomRoute {
    Rules, DirectActions
}

sealed class BottomNavItem(
    var title: String, var icon: ImageVector, var screenRoute: HomeBottomRoute
) {
    object Rules : BottomNavItem("ui.feat.label.rule", Icons.Filled.AutoMode, HomeBottomRoute.Rules)

    object DirectActions :
        BottomNavItem("ui.feat.label.da", Icons.Filled.TouchApp, HomeBottomRoute.DirectActions)
}

@Composable
fun HomeScreen() {
    val appNavController = LocalNavHostController.current
    val goToSettings: () -> Unit = {
        appNavController.navigate(AppRoute.Settings.name)
    }
    val goToAddRule: () -> Unit = {
        appNavController.navigate(AppRoute.AddRule.name)
    }
    val goToEditRule: (String) -> Unit = {
        appNavController.navigate("${AppRoute.AddRule.name}?ruleId=$it")
    }
    val goToAddDA: () -> Unit = {
        appNavController.navigate(AppRoute.AddDA.name)
    }
    val goToEditDA: (String) -> Unit = {
        appNavController.navigate("${AppRoute.AddDA.name}?daId=$it")
    }
    HomeScreen(
        goToSettings = goToSettings,
        goToAddRule = goToAddRule,
        goToEditRule = goToEditRule,
        goToAddDA = goToAddDA,
        goToEditDA = goToEditDA
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    goToSettings: () -> Unit,
    goToAddRule: () -> Unit,
    goToEditRule: (String) -> Unit,
    goToAddDA: () -> Unit,
    goToEditDA: (String) -> Unit,
) {
    val vm = hiltViewModel<MainViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadRules()
        vm.loadDA()
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var isShowActiveDialog by remember {
        mutableStateOf(false)
    }

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    if (state.isActivated) {
                        MediumTopAppBar(
                            title = {
                                Text(
                                    text = if (currentRoute == HomeBottomRoute.DirectActions.name) {
                                        LocalI18N.current["ui.feat.label.da"]
                                    } else {
                                        LocalI18N.current["ui.feat.label.rule"]
                                    }
                                )
                            },
                            actions = {
                                AppBarActions(goToSettings)
                            },
                            scrollBehavior = scrollBehavior,
                        )
                    } else {
                        TopAppBar(title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = stringResource(id = tornaco.apps.shortx.ui.R.string.ui_app_name))
                                SmallSpacer()
                                ClickableBadge(text = LocalI18N.current["ui.settings.core.info.na"]) {
                                    isShowActiveDialog = true
                                }
                            }
                        }, actions = {
                            AppBarActions(goToSettings)
                        })
                    }
                },
                bottomBar = {
                    BottomNavigationContent(navController)
                },
                floatingActionButton = {
                    val context = LocalContext.current
                    Fab(onClipboardClick = {
                        when (navController.currentBackStackEntry?.destination?.route) {
                            HomeBottomRoute.Rules.name -> {
                                vm.parseShareContentRule(ClipboardUtils.readClipboard(context))
                                    ?.let {
                                        vm.importRule(it)
                                    }
                            }
                            HomeBottomRoute.DirectActions.name -> {
                                vm.parseShareContentDA(ClipboardUtils.readClipboard(context))?.let {
                                    vm.importDA(it)
                                }
                            }
                            else -> {}
                        }
                    }, onFileClick = {

                    }, onCreateClick = {
                        when (navController.currentBackStackEntry?.destination?.route) {
                            HomeBottomRoute.Rules.name -> {
                                goToAddRule()
                            }
                            HomeBottomRoute.DirectActions.name -> {
                                goToAddDA()
                            }
                            else -> {}
                        }
                    })
                }) { paddingValues ->

                val shareDialogState = rememberShareDialogState("share.shortx")
                ShareDialog(state = shareDialogState)

                Column(
                    modifier = Modifier.padding(paddingValues),
                ) {

                    if (state.isFrameworkUpdated) {
                        Card(
                            modifier = Modifier
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    LocalI18N.current["ui.framework.updated"],
                                )

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    FilledTonalButton(
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = MaterialTheme.colorScheme.onError
                                        ),
                                        onClick = {
                                            vm.reboot()
                                        }) {
                                        Text(text = LocalI18N.current["ui.common.reboot"])
                                    }
                                }
                            }
                        }
                    }

                    BottomNavigationGraph(navController = navController,
                        state = state,
                        deleteRule = { rule ->
                            vm.deleteRule(rule.id)
                        },
                        editRule = { rule ->
                            goToEditRule(rule.id)
                        },
                        shareRule = { um ->
                            vm.ruleToShareString(um)?.let {
                                shareDialogState.show(it)
                            }
                        },
                        setRuleEnabled = { id, enable ->
                            vm.setRuleEnabled(id, enable)
                        },
                        onRuleClick = { rule ->
                            vm.setRuleEnabled(rule.id, !rule.isEnabled)
                        },
                        onDAClick = { da ->
                            vm.executeDA(da)
                        },
                        onDeleteDA = { da ->
                            vm.deleteDA(da.id)
                        },
                        onEditDA = { da ->
                            goToEditDA(da.id)
                        },
                        stopAllJobs = {
                            vm.stopJobs(it)
                        },
                        onShareDA = { um ->
                            vm.daToShareString(um)?.let {
                                shareDialogState.show(it)
                            }
                        })

                    if (isShowActiveDialog) {
                        ActiveDialog(onDismissRequest = {
                            isShowActiveDialog = false
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBarActions(goToSettings: () -> Unit) {
    IconButton(onClick = {
        goToSettings()
    }) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Settings"
        )
    }
}

@Composable
private fun Fab(
    onClipboardClick: () -> Unit,
    onFileClick: () -> Unit,
    onCreateClick: () -> Unit,
) {
    val expandFbItemList: MutableList<MultiFabItem> = mutableListOf(
        MultiFabItem(
            id = "file", icon = Icons.Outlined.FileCopy, label = "File"
        ), MultiFabItem(
            id = "clipboard",
            icon = Icons.Outlined.ContentPaste,
            label = LocalI18N.current["ui.common.share.clipboard"]
        ), MultiFabItem(
            id = "new", icon = Icons.Outlined.Add, label = "New"
        )
    )
    MultiFloatingActionButton(
        srcIcon = Icons.Outlined.Add, showLabels = true, items = expandFbItemList
    ) {
        when (it.id) {
            "clipboard" -> {
                onClipboardClick()
            }
            "file" -> {
                onFileClick()
            }
            "new" -> {
                onCreateClick()
            }
        }
    }
}

@Composable
private fun BottomNavigationContent(
    navController: NavHostController,
) {
    val items = listOf(
        BottomNavItem.DirectActions, BottomNavItem.Rules
    )
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(icon = {
                Icon(imageVector = item.icon, contentDescription = item.title)
            },
                label = {
                    Text(text = LocalI18N.current[item.title], fontSize = 9.sp)
                },
                alwaysShowLabel = true,
                selected = currentRoute == item.screenRoute.name,
                onClick = {
                    navController.navigate(item.screenRoute.name) {
                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}


@Composable
private fun BottomNavigationGraph(
    navController: NavHostController,
    state: MainState,
    deleteRule: (RuleUM) -> Unit,
    editRule: (RuleUM) -> Unit,
    shareRule: (RuleUM) -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
    onRuleClick: (RuleUM) -> Unit,
    onDeleteDA: (DirectActionUM) -> Unit,
    onEditDA: (DirectActionUM) -> Unit,
    onShareDA: (DirectActionUM) -> Unit,
    onDAClick: (DirectActionUM) -> Unit,
    stopAllJobs: (DirectActionUM) -> Unit
) {
    NavHost(navController, startDestination = BottomNavItem.DirectActions.screenRoute.name) {
        composable(BottomNavItem.Rules.screenRoute.name) {
            RuleScreen(
                state = state,
                setRuleEnabled = setRuleEnabled,
                onDelete = deleteRule,
                onEdit = editRule,
                onShare = shareRule,
                onRuleClick = onRuleClick
            )
        }

        composable(BottomNavItem.DirectActions.screenRoute.name) {
            DirectActionListScreen(
                state = state,
                onDelete = onDeleteDA,
                onEdit = onEditDA,
                onShare = onShareDA,
                onDirectActionClick = onDAClick,
                stopAllJobs = stopAllJobs
            )
        }
    }
}


@Composable
private fun ActiveDialog(
    onDismissRequest: () -> Unit,
) {
    ShortXAlertDialog(title = {
        Text(text = LocalI18N.current["ui.settings.core.info.na"])
    }, text = {
        Text(text = LocalI18N.current["ui.active.dialog.message"])
    }, onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) {
            Text(text = stringResource(android.R.string.ok))
        }
    })
}
