package tornaco.apps.shortx.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.overlay.addToLifecycle

@SuppressLint("ViewConstructor")
abstract class BaseComposeView(
    context: Context,
    private val i18N: I18N,
) :
    LinearLayout(context) {

    fun initView() {
        val wrapContent = FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )

        val appResources = AppResources(context, BuildPropInternal.APP_PACKAGE_NAME)
        val appContext = appResources.appContext

        // WTF?
        // ERROR: Attempt to invoke virtual method 'void android.content.Context.registerComponentCallbacks(android.content.ComponentCallbacks)'
        // on a null object reference
        val localContextWrapper = object : ContextWrapper(appContext) {
            override fun getApplicationContext(): Context {
                return context
            }
        }

        val contextWrapper = object : ContextWrapper(context) {
            override fun getResources(): Resources {
                return appContext.resources
            }
        }

        val composeView = ComposeView(contextWrapper).apply {
            addToLifecycle()
            setContent {
                CompositionLocalProvider(
                    LocalContext provides localContextWrapper,
                    LocalI18N provides i18N
                ) {
                    ComposeContent()
                }
            }
            isFocusable = true
            isClickable = true
        }
        addView(composeView, wrapContent)

        isFocusable = true
        isClickable = true
    }

    @Composable
    abstract fun ComposeContent()
}