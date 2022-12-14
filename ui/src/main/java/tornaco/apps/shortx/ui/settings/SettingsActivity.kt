package tornaco.apps.shortx.ui.settings

import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import dev.enro.annotations.NavigationDestination
import tornaco.apps.shortx.ui.base.BaseActivity

@NavigationDestination(AppSettings::class)
@AndroidEntryPoint
class SettingsActivity : BaseActivity() {
    @Composable
    override fun Content() {
        SettingsScreen()
    }
}