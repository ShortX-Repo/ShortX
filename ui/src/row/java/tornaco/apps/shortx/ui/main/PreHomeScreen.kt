package tornaco.apps.shortx.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.ui.base.LocalNavHostController
import tornaco.apps.shortx.ui.row.LVLCheckScreen
import tornaco.apps.shortx.ui.row.util.isLVLChecked

@Composable
fun PreHomeScreen() {
    val context = LocalContext.current
    val isLVLChecked = remember {
        isLVLChecked(context)
    }
    if (isLVLChecked) {
        HomeScreen()
    } else {
        val navHost = LocalNavHostController.current
        LVLCheckScreen {
            navHost.popBackStack()
            navHost.navigate(AppRoute.Home.name)
        }
    }
}