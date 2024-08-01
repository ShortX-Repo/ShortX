package tornaco.apps.shortx.ui.main

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.LocalNavHostController

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen will cause app stuck on resume
        // Don't know why.
        // installSplashScreen()
        super.onCreate(savedInstanceState)
    }

    @Composable
    override fun Content() {
        val navController = rememberNavController()
        CompositionLocalProvider(LocalNavHostController provides navController) {
            AppNavigationGraph()
        }
    }
}

