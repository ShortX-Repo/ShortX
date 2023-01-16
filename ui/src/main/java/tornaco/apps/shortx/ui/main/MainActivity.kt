@file:OptIn(ExperimentalAnimationApi::class)

package tornaco.apps.shortx.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.ui.base.BaseActivity

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun beforeOnCreate() {
        super.beforeOnCreate()
        installSplashScreen()
    }

    @Composable
    override fun Content() {
        val navController = rememberAnimatedNavController()
        AppNavigationGraph(appNavController = navController)
    }
}

