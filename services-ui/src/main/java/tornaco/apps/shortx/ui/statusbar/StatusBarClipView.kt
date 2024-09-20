package tornaco.apps.shortx.ui.statusbar

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.proto.common.StatusBarClipState
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.ui.ViewAnimationUtil
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeXL
import tornaco.apps.shortx.ui.overlay.addToLifecycle
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

@SuppressLint("ViewConstructor")
class StatusBarClipView(
    context: Context,
) : FrameLayout(context) {
    private val statusBarClipState = MutableStateFlow(StatusBarClipState.getDefaultInstance())

    private val clipListener = object : ICallback.Stub() {
        override fun receive(data: ByteArrayWrapper) {
            post {
                runCatching {
                    val clipData = StatusBarClipState.parseFrom(data.byteData)
                    statusBarClipState.update { clipData }
                    val isVisible = clipData != StatusBarClipState.getDefaultInstance()
                    if (isVisible) {
                        ViewAnimationUtil.showWithScaleAnimation(this@StatusBarClipView)
                    } else {
                        ViewAnimationUtil.hideWithScaleAnimation(this@StatusBarClipView)
                    }
                }.onFailure {
                    ShortXManagerNative.shortX?.log("StatusBarClipView", it.stackTraceToString())
                }
            }
        }
    }

    init {
        val appResources = AppResources(context, BuildPropInternal.APP_PACKAGE_NAME)
        val appContext = appResources.appContext

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
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                CompositionLocalProvider(
                    LocalContext provides localContextWrapper,
                    LocalI18N provides object : I18N {
                        override fun get(
                            key: String,
                            args: Map<String, String>,
                            fallback: String?
                        ): String {
                            return key
                        }
                    }
                ) {
                    ComposeContent()
                }
            }
            isFocusable = true
            isClickable = true
        }

        addView(composeView)

        ShortXManagerNative.shortX?.registerStatusbarClipStateListener(clipListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ShortXManagerNative.shortX?.unregisterStatusbarClipStateListener(clipListener)
    }

    @Composable
    private fun ComposeContent() {
        ShortXServiceUITheme {
            Box(
                modifier = Modifier
                    .widthIn(max = 100.dp)
                    .clip(ShortXCardRoundedCornerShapeXL)
                    .clickable {

                    }
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val clipState by statusBarClipState.collectAsState()
                    if (clipState.icon.isNotEmpty()) {
                        RemixIcon(remixName = clipState.icon)
                    }
                    if (clipState.text.isNotEmpty()) {
                        Text(
                            text = clipState.text,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}