package tornaco.apps.shortx.ui.dav

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.base.BaseActivity

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

    }
}