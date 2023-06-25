package tornaco.apps.shortx.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.base.animateComposable
import tornaco.apps.shortx.ui.base.webview.WebViewScreen
import tornaco.apps.shortx.ui.base.webview.WebViewTarget
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