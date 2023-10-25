@file:OptIn(ExperimentalMaterial3Api::class)

package tornaco.apps.shortx.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ErrorMessageDialog
import tornaco.apps.shortx.ui.base.LocalDrawerState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.rememberErrorMessageDialogState
import tornaco.apps.shortx.ui.discover.DiscoverScreen
import tornaco.apps.shortx.ui.gesture.GestureRecordListActivity
import tornaco.apps.shortx.ui.gvar.GlobalVarListActivity
import tornaco.apps.shortx.ui.main.da.DirectActionListScreen
import tornaco.apps.shortx.ui.main.rule.RuleScreen
import tornaco.apps.shortx.ui.pkgset.PkgSetListActivity
import tornaco.apps.shortx.ui.record.AudioRecordActivity
import tornaco.apps.shortx.ui.record.EvaRecordActivity
import tornaco.apps.shortx.ui.record.SettingsAccessRecordActivity
import tornaco.apps.shortx.ui.settings.SettingsScreen
import tornaco.apps.shortx.ui.settings.danmu.DanmuUISettingsActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.AssetUtils

enum class HomeBottomRoute {
    Rules, DirectActions, Discover
}

sealed class BottomNavItem(
    var title: String, var icon: String, var screenRoute: HomeBottomRoute
) {
    data object Rules :
        BottomNavItem("ui.feat.label.rule", Remix.Weather.meteor_line, HomeBottomRoute.Rules)

    data object DirectActions :
        BottomNavItem(
            "ui.feat.label.da",
            Remix.Development.command_line,
            HomeBottomRoute.DirectActions
        )

    data object Discover :
        BottomNavItem(
            "ui.feat.label.discover",
            Remix.Map.compass_discover_line,
            HomeBottomRoute.Discover
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
                        drawerTonalElevation = 0.5.dp
                    ) {
                        StandardSpacer()

                        val appNavController = LocalNavHostController.current
                        val context = LocalContext.current

                        val statementDialog =
                            rememberErrorMessageDialogState(title = LocalI18N.current["ui.settings.privacy.statement"])
                        ErrorMessageDialog(state = statementDialog)

                        SettingsScreen(
                            back = { scope.launch { drawerState.close() } },
                            goToPkgSetList = {
                                PkgSetListActivity.start(context)
                            },
                            goToGV = {
                                GlobalVarListActivity.start(context)
                            },
                            goToDanmuUISettings = {
                                DanmuUISettingsActivity.start(context)
                            },
                            goToEvaRecord = {
                                EvaRecordActivity.start(context)
                            },
                            goToAudioRecord = {
                                AudioRecordActivity.start(context)
                            },
                            goToGestureRecord = {
                                GestureRecordListActivity.start(context)
                            },
                            goToSettingsCatcherRecord = {
                                SettingsAccessRecordActivity.start(context)
                            },
                            viewOpenSourceLicense = {
                                appNavController.navigate(AppRoute.ViewOpenSourceLicense.name)
                            },
                            viewPrivacyStatement = {
                                statementDialog.show(
                                    AssetUtils.readFileToString(
                                        context,
                                        "privacy/privacy_agreement.html"
                                    )
                                )
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

        composable(BottomNavItem.Discover.screenRoute.name) {
            DiscoverScreen()
        }
    }
}
