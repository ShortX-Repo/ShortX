package tornaco.apps.shortx.ui.gesture

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.theme.ShortXTheme

@AndroidEntryPoint
class GestureRecordeViewerActivity : BaseActivity() {

    companion object {
        const val ID = "id"

        @JvmStatic
        fun start(context: Context, id: String) {
            val starter = Intent(context, GestureRecordeViewerActivity::class.java).putExtra(ID, id)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @Composable
    override fun Content() {
        ShortXTheme {
            Box(modifier = Modifier.fillMaxSize())
            LaunchedEffect(Unit) {
                delay(1500)
                intent.getStringExtra(ID)?.let { shortXManager.injectGestureRecord(it) }
            }
        }
    }
}