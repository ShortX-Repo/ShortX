package tornaco.apps.shortx.ui.main

import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.LocalNavHostController

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            // handle callback
            if (result.resultCode != RESULT_OK) {
                Logger.nameless.e("Update flow failed! Result code: " + result.resultCode)
                finishAffinity()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen will cause app stuck on resume
        // Don't know why.
        // installSplashScreen()
        super.onCreate(savedInstanceState)

        val checkForUpdate = CheckForUpdate(this@MainActivity, launcher)
        checkForUpdate()
    }

    @Composable
    override fun Content() {
        val navController = rememberNavController()
        CompositionLocalProvider(LocalNavHostController provides navController) {
            AppNavigationGraph()
        }
    }
}

