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
            CompositionLocalProvider(
                LocalI18N provides i18NIns,
                LocalActivity provides this,
                LocalAppName provides appName,
                LocalUiStore provides UiStore(),
            ) {
                ShortXProps {
                    ShortXTheme {
                        ClickOutsideClearFocusBox {
                            val pref = Pref(LocalContext.current)
                            val uiColorfulCard by pref.uiColorfulCard.collectAsStateWithLifecycle(
                                null
                            )
                            val uiExperimental by pref.uiExperimental.collectAsStateWithLifecycle(
                                null
                            )
                            val uiSettings = uiColorfulCard?.let { colorful ->
                                uiExperimental?.let { experimental ->
                                    UiSettings(
                                        uiColorfulCard = colorful,
                                        uiExperimental = experimental
                                    )
                                }
                            }

                            uiSettings?.let {
                                CompositionLocalProvider(
                                    LocalUiSettings provides uiSettings
                                ) {
                                    Content()
                                }
                            }

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