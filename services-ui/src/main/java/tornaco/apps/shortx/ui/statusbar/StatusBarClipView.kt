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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.isVisible
import androidx.lifecycle.AtomicReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.common.StatusBarClipState
import tornaco.apps.shortx.core.proto.common.StatusBarClipUiSettings
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.replaceFuncParameterInputs
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.ViewAnimationUtil
import tornaco.apps.shortx.ui.base.AnimatedTextContainer
import tornaco.apps.shortx.ui.base.AppImageIcon
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeXL
import tornaco.apps.shortx.ui.base.thenIf
import tornaco.apps.shortx.ui.overlay.addToLifecycle
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

data class StatusBarClipUiState(
    val text: String = "",
    val icon: String = "",
    val settings: StatusBarClipUiSettings = StatusBarClipUiSettings.getDefaultInstance()
)

fun StatusBarClipState.toUiState() = StatusBarClipUiState(
    text = text.replaceFuncParameterInputs(funcParameterInputsList)
        .replaceGlobalVarValues { shortXManager.getAllGlobalVars() }
        .replaceLocalVarValues(LocalVars.obtain().apply { localVarList.forEach { add(it) } }),
    icon = icon,
    settings = shortXManager.getStatusbarClipSettings()
)

@SuppressLint("ViewConstructor")
class StatusBarClipView(
    context: Context,
) : FrameLayout(context) {
    private val logger = Logger("StatusBarClipView")
    private val uiState = MutableStateFlow(StatusBarClipUiState())

    private val clipProto = AtomicReference<StatusBarClipState?>(null)

    private val clipSettingsListener = object : ICallback.Stub() {
        override fun receive(data: ByteArrayWrapper) {
            val settings = StatusBarClipUiSettings.parseFrom(data.byteData)
            uiState.update {
                it.copy(settings = settings)
            }
        }
    }

    private val clipListener = object : ICallback.Stub() {
        override fun receive(data: ByteArrayWrapper) {
            post {
                runCatching {
                    val clipData = StatusBarClipState.parseFrom(data.byteData)
                    clipProto.set(clipData)
                    uiState.update { clipData.toUiState() }

                    val shouldBeVisible = clipData.text.isNotEmpty() || clipData.icon.isNotEmpty()
                    val isVisible = isVisible
                    if (shouldBeVisible != isVisible) {
                        if (shouldBeVisible) {
                            ViewAnimationUtil.showWithScaleAnimation(this@StatusBarClipView)
                        } else {
                            ViewAnimationUtil.hideWithScaleAnimation(this@StatusBarClipView)
                        }
                    }
                }.onFailure {
                    ShortXManagerNative.shortX?.log("StatusBarClipView", it.stackTraceToString())
                }
            }
        }
    }

    private val globalVarObs = object : IGlobalVarObserver.Stub() {
        override fun onAddOrUpdate(id: String?) {
            logger.d("globalVarObs onAddOrUpdate: $id")
            formatValues()
        }

        override fun onDelete(id: String?) {
            logger.d("globalVarObs onDelete: $id")
            formatValues()
        }
    }

    private fun formatValues() {
        clipProto.get()?.let { proto ->
            uiState.update {
                proto.toUiState()
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
        isVisible = false

        ShortXManagerNative.shortX?.registerStatusbarClipStateListener(clipListener)
        ShortXManagerNative.shortX?.registerStatusbarClipSettingsListener(clipSettingsListener)
        ShortXManagerNative.shortX?.registerGlobalVarObs(globalVarObs)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ShortXManagerNative.shortX?.unregisterStatusbarClipStateListener(clipListener)
        ShortXManagerNative.shortX?.unregisterStatusbarClipSettingsListener(clipListener)
        ShortXManagerNative.shortX?.unregisterGlobalVarObs(globalVarObs)
    }

    @Composable
    private fun ComposeContent() {
        ShortXServiceUITheme {
            val clipState by uiState.collectAsState()
            Box(
                modifier = Modifier
                    .padding(
                        start = clipState.settings.paddingStart.dp,
                        end = clipState.settings.paddingEnd.dp,
                        top = clipState.settings.paddingTop.dp,
                        bottom = clipState.settings.paddingBottom.dp
                    )
                    .widthIn(max = 100.dp)
                    .clip(ShortXCardRoundedCornerShapeXL)
                    .clickable {

                    }
                    .thenIf(
                        Modifier.background(MaterialTheme.colorScheme.primaryContainer),
                        true
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (clipState.icon.isNotEmpty()) {
                        val iconModifier = Modifier.size(
                            clipState.settings.iconSize.dp
                        )
                        val isAppIcon = clipState.icon.startsWith("app://")
                        if (isAppIcon) {
                            AppImageIcon(app = clipState.icon, modifier = iconModifier)
                        } else {
                            RemixIcon(remixName = clipState.icon, modifier = iconModifier)
                        }
                    }
                    if (clipState.text.isNotEmpty()) {
                        AnimatedTextContainer(text = clipState.text) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = clipState.settings.fontSize.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}