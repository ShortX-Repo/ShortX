package tornaco.apps.shortx.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.ui.addda.AddDirectActionScreen
import tornaco.apps.shortx.ui.addrule.AddRuleScreen
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.base.animateComposable
import tornaco.apps.shortx.ui.base.webview.WebViewScreen
import tornaco.apps.shortx.ui.base.webview.WebViewTarget
import tornaco.apps.shortx.ui.gvar.GlobalVarDetailScreen
import tornaco.apps.shortx.ui.gvar.GlobalVarListScreen
import tornaco.apps.shortx.ui.pkgset.PkgSetDetailScreen
import tornaco.apps.shortx.ui.pkgset.PkgSetListScreen
import tornaco.apps.shortx.ui.record.ActionEvaRecordScreen
import tornaco.apps.shortx.ui.record.ConditionEvaRecordScreen
import tornaco.apps.shortx.ui.settings.SettingsScreen
import tornaco.apps.shortx.ui.settings.danmu.DanmuUISettingsScreen
import tornaco.apps.shortx.ui.subscribe.LVLCheckScreen
import tornaco.apps.shortx.ui.subscribe.SubscribeScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigationGraph(
) {
    val appNavController = LocalNavHostController.current
    AnimatedNavHost(appNavController, startDestination = startDestination) {
        animateComposable(AppRoute.Home.name) {
            HomeScreen()
        }

        animateComposable(AppRoute.Settings.name) {
            SettingsScreen()
        }

        animateComposable(
            "${AppRoute.AddRule.name}?ruleId={ruleId}",
            arguments = listOf(navArgument("ruleId") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getString("ruleId")
            AddRuleScreen(
                ruleId = ruleId,
                screenTitle = if (ruleId != null) {
                    LocalI18N.current["ui.title.edit.rule"]
                } else {
                    LocalI18N.current["ui.title.add.rule"]
                }
            ) {
                appNavController.popBackStack()
            }
        }

        animateComposable(
            "${AppRoute.AddDA.name}?daId={daId}",
            arguments = listOf(navArgument("daId") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) { backStackEntry ->
            val daId = backStackEntry.arguments?.getString("daId")
            val screenTitle = if (daId == null) {
                LocalI18N.current["ui.title.add.da"]
            } else {
                LocalI18N.current["ui.title.edit.da"]
            }
            AddDirectActionScreen(daId = daId, screenTitle = screenTitle) {
                appNavController.popBackStack()
            }
        }

        animateComposable(AppRoute.PkgSetList.name) {
            PkgSetListScreen(back = {
                appNavController.popBackStack()
            }, goToDetail = {
                appNavController.navigate("${AppRoute.PkgSetDetails.name}?id=$it")
            })
        }

        animateComposable(
            "${AppRoute.PkgSetDetails.name}?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            id?.let {
                PkgSetDetailScreen(id = id) {
                    appNavController.popBackStack()
                }
            }
        }

        animateComposable(AppRoute.GlobalVarList.name) {
            GlobalVarListScreen(back = {
                appNavController.popBackStack()
            }, goToDetail = {
                appNavController.navigate("${AppRoute.GlobalDetails.name}?id=$it")
            })
        }

        animateComposable(
            "${AppRoute.GlobalDetails.name}?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            id?.let {
                GlobalVarDetailScreen(name = id) {
                    appNavController.popBackStack()
                }
            }
        }

        animateComposable(AppRoute.DanmuUISettings.name) {
            DanmuUISettingsScreen {
                appNavController.popBackStack()
            }
        }

        animateComposable(AppRoute.ActionEvaRecord.name) {
            ActionEvaRecordScreen {
                appNavController.popBackStack()
            }
        }

        animateComposable(AppRoute.ConditionEvaRecord.name) {
            ConditionEvaRecordScreen {
                appNavController.popBackStack()
            }
        }

        animateComposable(AppRoute.ViewOpenSourceLicense.name) {
            WebViewScreen(
                target = WebViewTarget(
                    "Open source license",
                    BuildProp.OPEN_SOURCE_LICENSE_URL
                )
            ) {
                appNavController.popBackStack()
            }
        }

        animateComposable(AppRoute.SubscriptionIntro.name) {
            SubscribeScreen {
                appNavController.popBackStack()
            }
        }

        animateComposable(AppRoute.LVLCheck.name) {
            val navHost = LocalNavHostController.current
            LVLCheckScreen(showSkipButton = false) {
                navHost.popBackStack()
            }
        }

        appNavGraphHook()
    }
}