package tornaco.apps.shortx.ui.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.ui.base.BaseActivity

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Composable
    override fun Content() {
        val navController = rememberNavController()
        AppNavigationGraph(appNavController = navController)
    }
}

