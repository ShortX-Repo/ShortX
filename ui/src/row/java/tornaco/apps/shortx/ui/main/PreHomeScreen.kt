package tornaco.apps.shortx.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.subscribe.LVLCheckScreen
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder

@Composable
fun PreHomeScreen() {
    val state by LVLStateHolder.subscribeState.collectAsState()
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