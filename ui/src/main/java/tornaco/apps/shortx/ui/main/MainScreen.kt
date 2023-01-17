package tornaco.apps.shortx.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.theme.ShortXTheme

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
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
        vm.loadDirections()
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
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
                            IconButton(onClick = {
                                goToSettings()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Settings"
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                },
                bottomBar = {
                    BottomNavigationContent(navController)
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        when (navController.currentBackStackEntry?.destination?.route) {
                            HomeBottomRoute.Rules.name -> {
                                goToAddRule()
                            }
                            HomeBottomRoute.DirectActions.name -> {
                                goToAddDA()
                            }
                            else -> {}
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "add")
                    }
                }) {
                Column(
                    modifier = Modifier.padding(it),
                ) {
                    BottomNavigationGraph(navController = navController,
                        state = state,
                        deleteRule = { rule ->
                            vm.deleteRule(rule.id)
                        },
                        editRule = { rule ->
                            goToEditRule(rule.id)
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
                        })
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationContent(
    navController: NavHostController,
) {
    val items = listOf(
        BottomNavItem.DirectActions,
        BottomNavItem.Rules
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
    setRuleEnabled: (String, Boolean) -> Unit,
    onRuleClick: (RuleUM) -> Unit,
    onDeleteDA: (DirectActionUM) -> Unit,
    onEditDA: (DirectActionUM) -> Unit,
    onDAClick: (DirectActionUM) -> Unit,
) {
    NavHost(navController, startDestination = BottomNavItem.DirectActions.screenRoute.name) {
        composable(BottomNavItem.Rules.screenRoute.name) {
            RuleScreen(
                state = state,
                setRuleEnabled = setRuleEnabled,
                onDelete = deleteRule,
                onEdit = editRule,
                onRuleClick = onRuleClick
            )
        }

        composable(BottomNavItem.DirectActions.screenRoute.name) {
            DirectActionListScreen(
                state = state,
                onDelete = onDeleteDA,
                onEdit = onEditDA,
                onDirectActionClick = onDAClick
            )
        }
    }
}