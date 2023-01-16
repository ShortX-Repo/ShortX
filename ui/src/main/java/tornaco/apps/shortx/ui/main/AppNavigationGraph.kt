package tornaco.apps.shortx.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import tornaco.apps.shortx.ui.addrule.AddRuleScreen
import tornaco.apps.shortx.ui.settings.SettingsScreen

enum class AppRoute {
    Home, AddRule, Settings
}

@Composable
fun AppNavigationGraph(
    appNavController: NavHostController,
) {
    NavHost(appNavController, startDestination = AppRoute.Home.name) {
        composable(AppRoute.Home.name) {
            HomeScreen(
                goToSettings = {
                    appNavController.navigate(AppRoute.Settings.name)
                },
                goToAddRule = {
                    appNavController.navigate(AppRoute.AddRule.name)
                },
                goToEditRule = {
                    appNavController.navigate("${AppRoute.AddRule.name}/$it")
                }
            )
        }

        composable(AppRoute.Settings.name) {
            SettingsScreen()
        }

        composable(
            "${AppRoute.AddRule.name}/{ruleId}",
            arguments = listOf(navArgument("ruleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getString("ruleId")
            AddRuleScreen(ruleId)
        }
    }
}