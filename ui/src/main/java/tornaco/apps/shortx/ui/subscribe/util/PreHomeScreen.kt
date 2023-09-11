package tornaco.apps.shortx.ui.subscribe.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.main.AppRoute
import tornaco.apps.shortx.ui.main.HomeScreen
import tornaco.apps.shortx.ui.subscribe.LVLCheckScreen
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder

@Composable
fun PreHomeScreen() {
    val context = LocalContext.current
    val subscribeState by LVLStateHolder.collectAsState()
    if (subscribeState.isSubscribed) {
        HomeScreen()
    } else {
        val navHost = LocalNavHostController.current
        LVLCheckScreen {
            navHost.popBackStack()
            navHost.navigate(AppRoute.Home.name)
        }
    }
}