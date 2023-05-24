@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.settings.SettingsScreen
import tornaco.apps.shortx.ui.theme.ShortXTheme

enum class HomeBottomRoute {
    Rules, DirectActions
}

sealed class BottomNavItem(
    var title: String, var icon: String, var screenRoute: HomeBottomRoute
) {
    object Rules :
        BottomNavItem("ui.feat.label.rule", Remix.Weather.meteor_line, HomeBottomRoute.Rules)

    object DirectActions :
        BottomNavItem(
            "ui.feat.label.da",
            Remix.Development.command_line,
            HomeBottomRoute.DirectActions
        )
}


@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    ShortXTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.fillMaxWidth(fraction = 0.93f)
                    ) {
                        Spacer(Modifier.height(12.dp))

                        val appNavController = LocalNavHostController.current
                        SettingsScreen(
                            back = { scope.launch { drawerState.close() } },
                            goToPkgSetList = {
                                appNavController.navigate(AppRoute.PkgSetList.name)
                            },
                            goToGV = {
                                appNavController.navigate(AppRoute.GlobalVarList.name)
                            },
                            goToDanmuUISettings = {
                                appNavController.navigate(AppRoute.DanmuUISettings.name)
                            },
                            viewOpenSourceLicense = {
                                appNavController.navigate(AppRoute.ViewOpenSourceLicense.name)
                            })

                        BackHandler(enabled = drawerState.isOpen) {
                            scope.launch { drawerState.close() }
                        }
                    }
                },
                content = {
                    CompositionLocalProvider(LocalDrawerState provides drawerState) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f, fill = false)
                            ) {
                                BottomNavigationGraph(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    navController = navController
                                )
                            }

                            BottomNavigationContent(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                navController = navController
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun BottomNavigationContent(
    modifier: Modifier,
    navController: NavHostController,
) {
    val items = listOf(
        BottomNavItem.DirectActions, BottomNavItem.Rules
    )
    NavigationBar(
        modifier = modifier
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(icon = {
                RemixIcon(remixName = item.icon)
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
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = BottomNavItem.DirectActions.screenRoute.name
    ) {
        composable(BottomNavItem.Rules.screenRoute.name) {
            RuleScreen()
        }

        composable(BottomNavItem.DirectActions.screenRoute.name) {
            DirectActionListScreen()
        }
    }
}
