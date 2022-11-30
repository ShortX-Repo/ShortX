package tornaco.apps.shortx.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.enro.annotations.NavigationDestination
import tornaco.apps.shortx.ui.addrule.AddRule
import tornaco.apps.shortx.ui.addrule.AddRuleScreen

@NavigationDestination(AddRule::class)
@AndroidEntryPoint
class AddRuleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { AddRuleScreen() }
    }
}