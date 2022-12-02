package tornaco.apps.shortx.ui.addrule

import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import dev.enro.annotations.NavigationDestination
import tornaco.apps.shortx.ui.base.BaseActivity

@NavigationDestination(AddRule::class)
@AndroidEntryPoint
class AddRuleActivity : BaseActivity() {
    @Composable
    override fun Content() {
        AddRuleScreen()
    }

}