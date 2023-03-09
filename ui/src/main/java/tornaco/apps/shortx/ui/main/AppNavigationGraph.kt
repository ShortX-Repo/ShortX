package tornaco.apps.shortx.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import tornaco.apps.shortx.ui.addda.AddDirectActionScreen
import tornaco.apps.shortx.ui.addrule.AddRuleScreen
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.selector.ActionSelector
import tornaco.apps.shortx.ui.addrule.action.selector.ShowDialogSelector
import tornaco.apps.shortx.ui.addrule.action.selector.ShowOverlayButtonSelector
import tornaco.apps.shortx.ui.addrule.action.selector.ShowOverlayButtonSettings
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.base.animateComposable
import tornaco.apps.shortx.ui.pkgset.PkgSetDetailScreen
import tornaco.apps.shortx.ui.pkgset.PkgSetListScreen
import tornaco.apps.shortx.ui.settings.SettingsScreen

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

        animateComposable(AppRoute.SelectAction.name) {
            ActionSelector()
        }

        animateComposable(AppRoute.SelectShowOverlayButton.name) {
            // Directly extract the argument from previousBackStackEntry
            val settings =
                appNavController.previousBackStackEntry?.arguments?.getParcelable<ShowOverlayButtonSettings>(
                    "settings"
                )
            ShowOverlayButtonSelector(settings)
        }

        animateComposable(AppRoute.SelectShowAlertDialog.name) {
            // Directly extract the argument from previousBackStackEntry
            val settings =
                appNavController.previousBackStackEntry?.arguments?.getParcelable<Action.ShowAlertDialog>(
                    "settings"
                )
            ShowDialogSelector(settings)
        }

        appNavGraphHook()
    }
}