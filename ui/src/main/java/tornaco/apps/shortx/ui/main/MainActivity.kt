package tornaco.apps.shortx.ui.main

import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import dev.enro.core.NavigationKey
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.ui.base.BaseActivity

@Parcelize
class MainKey : NavigationKey

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Composable
    override fun Content() {
        MainScreen()
    }

}

