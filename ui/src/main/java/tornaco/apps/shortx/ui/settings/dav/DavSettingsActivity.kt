package tornaco.apps.shortx.ui.settings.dav

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.ui.base.BaseActivity

@AndroidEntryPoint
class DavSettingsActivity : BaseActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, DavSettingsActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Composable
    override fun Content() {
        DavScreen { finish() }
    }
}