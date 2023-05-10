package tornaco.apps.shortx.ui.base

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import tornaco.apps.shortx.core.util.ApkUtils

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        beforeOnCreate()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val appName = ApkUtils.loadNameByPkgName(this, packageName) ?: ""
        setContent {
            CompositionLocalProvider(
                LocalI18N provides i18NIns,
                LocalAppName provides appName.toString()
            ) {
                Content()
            }
        }
    }

    open fun beforeOnCreate() {}

    @Composable
    abstract fun Content()
}