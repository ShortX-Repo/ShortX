package tornaco.apps.shortx.ui.share

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.base.LocalNavHostController
import java.nio.charset.Charset

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class ViewerActivity : BaseActivity() {
    private val logger = Logger("ViewerActivity")

    @Composable
    override fun Content() {
        val navController = rememberAnimatedNavController()
        CompositionLocalProvider(LocalNavHostController provides navController) {
            if (Intent.ACTION_VIEW == intent.action) {
                val uri = intent.data
                logger.w("uri: $uri")
                logger.w("scheme: ${uri?.scheme}")
                if (uri != null) {
                    contentResolver.openInputStream(uri)?.use {
                        val text = it.reader(Charset.defaultCharset()).readText()
                        logger.w("text: $text")
                        val header = text.getContentHeader()
                        logger.w("header: $header")
                    }
                }
            }

        }
    }
}