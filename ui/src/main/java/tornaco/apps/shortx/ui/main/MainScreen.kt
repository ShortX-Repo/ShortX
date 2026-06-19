package tornaco.apps.shortx.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.LocalDrawerState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXNavigationBar
import tornaco.apps.shortx.ui.base.ShortXNavigationBarItem
import tornaco.apps.shortx.ui.base.StandardSpacer
import tornaco.apps.shortx.ui.base.TextDisplayActivity
import tornaco.apps.shortx.ui.base.rememberShortXBottomSheetState
import tornaco.apps.shortx.ui.changelog.ChangeLogBottomSheet
import tornaco.apps.shortx.ui.discover.DiscoverScreen
import tornaco.apps.shortx.ui.featoggle.FeatureToggleActivity
import tornaco.apps.shortx.ui.function.FunctionListActivity
import tornaco.apps.shortx.ui.gesture.GestureRecordListActivity
import tornaco.apps.shortx.ui.gvar.GlobalVarListActivity
import tornaco.apps.shortx.ui.contextdata.ContextVarListActivity
import tornaco.apps.shortx.ui.main.da.DirectActionListScreen
import tornaco.apps.shortx.ui.main.rule.RuleScreen
import tornaco.apps.shortx.ui.main.toggle.ToggleListScreen
import tornaco.apps.shortx.ui.pkgset.PkgSetListActivity
import tornaco.apps.shortx.ui.record.AudioRecordActivity
import tornaco.apps.shortx.ui.record.EvaRecordActivity
import tornaco.apps.shortx.ui.record.SettingsAccessRecordActivity
import tornaco.apps.shortx.ui.settings.SettingsScreen
import tornaco.apps.shortx.ui.settings.ShellCommandHelpActivity
import tornaco.apps.shortx.ui.settings.danmu.DanmuUISettingsActivity
import tornaco.apps.shortx.ui.settings.dav.DavSettingsActivity
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.settings.statusbar.StatusBarClipUISettingsActivity
import tornaco.apps.shortx.ui.shortcut.ShortcutStubManageActivity
import tornaco.apps.shortx.ui.theme.ExperimentalYellow
import tornaco.apps.shortx.ui.theme.ShortXTheme
import tornaco.apps.shortx.ui.util.AssetUtils
import tornaco.apps.shortx.core.i18n.I18N_R

enum class HomeBottomRoute {
    Rules, DirectActions, Toggles, Discover
}

sealed class BottomNavItem(
    val title: String,
    val icon: String,
    val screenRoute: HomeBottomRoute,
    val isExperimental: Boolean = false,
) {
    data object Rules :
        BottomNavItem(I18N_R.UI_FEAT_LABEL_RULE, Remix.Weather.meteor_line, HomeBottomRoute.Rules)

    data object DirectActions :
        BottomNavItem(
            I18N_R.UI_FEAT_LABEL_DA,
            Remix.Development.command_line,
            HomeBottomRoute.DirectActions
        )

    data object Toggles :
        BottomNavItem(
            I18N_R.UI_FEAT_LABEL_TOGGLES,
            Remix.System.toggle_fill,
            HomeBottomRoute.Toggles,
            true
        )

    data object Discover :
        BottomNavItem(
            I18N_R.UI_FEAT_LABEL_DISCOVER,
            Remix.Map.compass_discover_line,
            HomeBottomRoute.Discover
        )
}


@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    ShortXTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Box(
                    Modifier.fillMaxSize()
                ) {
                    StandardSpacer()

                    val appNavController = LocalNavHostController.current
                    val context = LocalContext.current

                    val statementTitle = LocalI18N.current[I18N_R.UI_SETTINGS_PRIVACY_STATEMENT]

                    SettingsScreen(
                        back = { scope.launch { drawerState.close() } },
                        goToPkgSetList = {
                            PkgSetListActivity.start(context)
                        },
                        goToGV = {
                            GlobalVarListActivity.start(context)
                        },
                        goToSystemEnv = {
                            ContextVarListActivity.start(context)
                        },
                        goToFunctionList = {
                            FunctionListActivity.start(context)
                        },
                        goToShellCommandHelp = {
                            ShellCommandHelpActivity.start(context)
                        },
                        goToDanmuUISettings = {
                            DanmuUISettingsActivity.start(context)
                        },
                        goToStatusBarClipUISettings = {
                            StatusBarClipUISettingsActivity.start(context)
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
                            TextDisplayActivity.start(
                                context = context,
                                title = statementTitle,
                                text = AssetUtils.readFileToString(
                                    context,
                                    "privacy/privacy_agreement.html"
                                )
                            )
                        },
                        manageShortcutApps = {
                            ShortcutStubManageActivity.start(context)
                        },
                        goToFeatToggle = {
                            FeatureToggleActivity.start(context)
                        },
                        goToWebdavProfile = {
                            DavSettingsActivity.start(context)
                        }
                    )

                    BackHandler(enabled = drawerState.isOpen) {
                        scope.launch { drawerState.close() }
                    }
                }
            },
            content = {
                CompositionLocalProvider(LocalDrawerState provides drawerState) {
                    val context = LocalContext.current
                    val pref = remember { Pref(context) }
                    val miscToggles by pref.miscUiToggles.collectAsState(initial = null)
                    val lastViewedChangeLogVersionCode by pref.lastViewedChangeLogVersionCode.collectAsState(
                        initial = BuildPropInternal.APP_VERSION_CODE
                    )
                    val changeLogState = rememberShortXBottomSheetState()
                    ChangeLogBottomSheet(state = changeLogState)

                    LaunchedEffect(lastViewedChangeLogVersionCode) {
                        if (lastViewedChangeLogVersionCode < BuildPropInternal.APP_VERSION_CODE) {
                            delay(1000)
                            changeLogState.show()
                            pref.setLastViewedChangeLogVersionCode(BuildPropInternal.APP_VERSION_CODE)
                        }
                    }

                    val items = miscToggles?.let {
                        listOfNotNull(
                            if (!it.contains(FeatureToggleActivity.FEAT_KEY_DISABLE_FEAT_DA)) BottomNavItem.DirectActions else null,
                            if (!it.contains(FeatureToggleActivity.FEAT_KEY_DISABLE_FEAT_RULE)) BottomNavItem.Rules else null,
                            if (!it.contains(FeatureToggleActivity.FEAT_KEY_DISABLE_FEAT_TOGGLE)) BottomNavItem.Toggles else null,
                            if (!it.contains(FeatureToggleActivity.FEAT_KEY_DISABLE_FEAT_DISCOVER)) BottomNavItem.Discover else null,
                        )
                    } ?: emptyList()

                    if (items.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            BottomNavigationGraph(
                                modifier = Modifier
                                    .fillMaxSize(),
                                navController = navController,
                                startDestination = items.first()
                            )

                            BottomNavigationContent(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(horizontal = 0.dp)
                                    .padding(bottom = 0.dp),
                                navController = navController,
                                items = items,
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun BottomNavigationContent(
    modifier: Modifier,
    navController: NavHostController,
    items: List<BottomNavItem>,
) {
    ShortXNavigationBar(
        modifier = modifier
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            ShortXNavigationBarItem(
                icon = {
                    if (item.isExperimental) {
                        BadgedBox(badge = {
                            Badge(containerColor = ExperimentalYellow) {
                                Text(text = LocalI18N.current[I18N_R.UI_COMMON_EXPERIMENTAL])
                            }
                        }) {
                            RemixIcon(remixName = item.icon)
                        }
                    } else {
                        RemixIcon(remixName = item.icon)
                    }
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
    navController: NavHostController,
    startDestination: BottomNavItem
) {
    MainContentHook()

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination.screenRoute.name
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

        composable(BottomNavItem.Toggles.screenRoute.name) {
            ToggleListScreen()
        }
    }
}
