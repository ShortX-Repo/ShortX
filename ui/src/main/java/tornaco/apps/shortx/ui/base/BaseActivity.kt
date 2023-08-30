package tornaco.apps.shortx.ui.base

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import tornaco.apps.shortx.core.util.ApkUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.subscribe.ShortXProps

abstract class BaseActivity : AppCompatActivity() {
    private val logger = Logger("ActivityLifecycle")

    override fun onCreate(savedInstanceState: Bundle?) {
        beforeOnCreate()
        super.onCreate(savedInstanceState)
        logger.i("onCreate")
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val appName = ApkUtils.loadNameByPkgName(this, packageName) ?: ""
        setContent {
            CompositionLocalProvider(
                LocalI18N provides i18NIns,
                LocalActivity provides this,
                LocalAppName provides appName.toString()
            ) {
                ShortXProps {
                    Content()
                }
            }
        }
    }

    open fun beforeOnCreate() {}

    @Composable
    abstract fun Content()

    override fun onResume() {
        super.onResume()
        logger.i("onResume")
    }

    override fun onStop() {
        super.onStop()
        logger.i("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.i("onDestroy")
    }
}