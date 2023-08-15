package tornaco.apps.shortx.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.base.animateComposable
import tornaco.apps.shortx.ui.base.webview.WebViewScreen
import tornaco.apps.shortx.ui.base.webview.WebViewTarget
import tornaco.apps.shortx.ui.subscribe.LVLCheckScreen

@Composable
fun AppNavigationGraph(
) {
    val appNavController = LocalNavHostController.current
    NavHost(appNavController, startDestination = startDestination) {
        animateComposable(AppRoute.Home.name) {
            HomeScreen()
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

        animateComposable(AppRoute.LVLCheck.name) {
            val navHost = LocalNavHostController.current
            LVLCheckScreen(showSkipButton = false) {
                navHost.popBackStack()
            }
        }

        appNavGraphHook()
    }
}