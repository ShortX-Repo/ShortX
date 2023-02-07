package tornaco.apps.shortx.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import tornaco.apps.shortx.ui.addda.AddDirectActionScreen
import tornaco.apps.shortx.ui.addrule.AddRuleScreen
import tornaco.apps.shortx.ui.base.animateComposable
import tornaco.apps.shortx.ui.pkgset.PkgSetListScreen
import tornaco.apps.shortx.ui.settings.SettingsScreen

enum class AppRoute {
    Home, AddRule, AddDA, Settings, PkgSetList, PkgSetDetails
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigationGraph(
    appNavController: NavHostController,
) {
    AnimatedNavHost(appNavController, startDestination = AppRoute.Home.name) {
        animateComposable(AppRoute.Home.name) {
            HomeScreen(
                goToSettings = {
                    appNavController.navigate(AppRoute.Settings.name)
                },
                goToAddRule = {
                    appNavController.navigate(AppRoute.AddRule.name)
                },
                goToEditRule = {
                    appNavController.navigate("${AppRoute.AddRule.name}?ruleId=$it")
                },
                goToAddDA = {
                    appNavController.navigate(AppRoute.AddDA.name)
                },
                goToEditDA = {
                    appNavController.navigate("${AppRoute.AddDA.name}?daId=$it")
                }
            )
        }

        animateComposable(AppRoute.Settings.name) {
            SettingsScreen(
                back = { appNavController.popBackStack() },
                goToPkgSetList = {
                    appNavController.navigate(AppRoute.PkgSetList.name)
                })
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
            AddRuleScreen(ruleId) {
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
            AddDirectActionScreen(daId) {
                appNavController.popBackStack()
            }
        }

        animateComposable(AppRoute.PkgSetList.name) {
            PkgSetListScreen(back = {
                appNavController.popBackStack()
            })
        }
    }
}