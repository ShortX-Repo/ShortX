package tornaco.apps.shortx.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import tornaco.apps.shortx.ui.R
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class HomeBottomRoute {
    Rules, Actions
}

sealed class BottomNavItem(
    var title: String,
    var icon: ImageVector,
    var screenRoute: HomeBottomRoute
) {
    object Rules :
        BottomNavItem("Rules", Icons.Filled.List, HomeBottomRoute.Rules)

    object Actions :
        BottomNavItem("Actions", Icons.Filled.AppShortcut, HomeBottomRoute.Actions)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(goToSettings: () -> Unit, goToAddRule: () -> Unit, goToEditRule: (String) -> Unit) {
    val vm = hiltViewModel<MainViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadRules()
    }

    val navController = rememberNavController()

    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    MediumTopAppBar(
                        title = { Text(text = stringResource(id = R.string.ui_app_name)) },
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
                            HomeBottomRoute.Actions.name -> {

                            }
                            else -> {}
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "add")
                    }
                }) {
                Column(
                    modifier = Modifier
                        .padding(it),
                ) {
                    BottomNavigationGraph(navController = navController,
                        state = state,
                        deleteRule = { rule ->
                            vm.delete(rule.id)
                        },
                        setRuleEnabled = { id, enable ->
                            vm.setRuleEnabled(id, enable)
                        },
                        onRuleClick = { rule ->
                            goToEditRule(rule.id)
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
        BottomNavItem.Rules,
        BottomNavItem.Actions
    )
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {
                    Text(text = item.title, fontSize = 9.sp)
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
                }
            )
        }
    }
}


@Composable
private fun BottomNavigationGraph(
    navController: NavHostController,
    state: MainState,
    deleteRule: (Rule) -> Unit,
    setRuleEnabled: (String, Boolean) -> Unit,
    onRuleClick: (Rule) -> Unit,
) {
    NavHost(navController, startDestination = BottomNavItem.Rules.screenRoute.name) {
        composable(BottomNavItem.Rules.screenRoute.name) {
            RuleScreen(
                state = state,
                setRuleEnabled = setRuleEnabled,
                onDelete = deleteRule,
                onRuleClick = onRuleClick
            )
        }

        composable(BottomNavItem.Actions.screenRoute.name) {

        }
    }
}