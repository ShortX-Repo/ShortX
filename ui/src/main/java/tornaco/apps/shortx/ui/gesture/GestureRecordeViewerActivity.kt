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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.afterNavDelay
import tornaco.apps.shortx.ui.theme.ShortXTheme
import java.time.Duration
import tornaco.apps.shortx.core.i18n.I18N_R

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
        val doneI18N = "\uD83C\uDF89 " + LocalI18N.current[I18N_R.UI_COMMON_DONE]
        ShortXTheme {
            Box(modifier = Modifier.fillMaxSize())
            LaunchedEffect(Unit) {
                afterNavDelay()
                intent.getStringExtra(ID)?.let {
                    withContext(Dispatchers.IO) {
                        val receiver = SynchronousResultReceiver.get()
                        shortXManager.injectGestureRecord(
                            id = it,
                            speed = 1f,
                            onComplete = receiver
                        )
                        receiver.awaitResultNoInterrupt(Duration.ofHours(24))
                        shortXManager.showDanmu(doneI18N, null)
                    }
                    delay(500)
                    finish()
                }
            }
        }
    }
}