package tornaco.apps.shortx.ui.base

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.settings.pref.Pref
import tornaco.apps.shortx.ui.subscribe.ShortXProps
import tornaco.apps.shortx.ui.theme.ShortXTheme

abstract class BaseActivity : AppCompatActivity() {
    private val logger = Logger("ActivityLifecycle")

    override fun onCreate(savedInstanceState: Bundle?) {
        beforeOnCreate()
        super.onCreate(savedInstanceState)
        logger.i("onCreate")
        enableEdgeToEdge()

        val appName = "ShortX"
        setContent {
            val pref = Pref(LocalContext.current)
            val uiColorfulCard by pref.uiColorfulCard.collectAsStateWithLifecycle(false)
            val uiExperimental by pref.uiExperimental.collectAsStateWithLifecycle(false)
            val uiSettings = UiSettings(
                uiColorfulCard = uiColorfulCard,
                uiExperimental = uiExperimental
            )

            CompositionLocalProvider(
                LocalI18N provides i18NIns,
                LocalActivity provides this,
                LocalAppName provides appName,
                LocalUiStore provides UiStore(),
                LocalUiSettings provides uiSettings
            ) {
                ShortXProps {
                    ShortXTheme {
                        ClickOutsideClearFocusBox {
                            Content()
                        }
                    }
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