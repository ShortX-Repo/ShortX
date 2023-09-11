package tornaco.apps.shortx.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import org.orbitmvi.orbit.compose.collectAsState
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.subscribe.LVLCheckScreen
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder

@Composable
fun PreHomeScreen() {
    val state by LVLStateHolder.collectAsState()
    if (state.isSubscribed) {
        HomeScreen()
    } else {
        val navHost = LocalNavHostController.current
        LVLCheckScreen {
            navHost.popBackStack()
            navHost.navigate(AppRoute.Home.name)
        }
    }
}