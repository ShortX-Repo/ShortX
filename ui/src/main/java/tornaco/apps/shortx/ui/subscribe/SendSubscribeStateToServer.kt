package tornaco.apps.shortx.ui.subscribe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest
import tornaco.apps.shortx.core.onDebugBuild
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.requireActivity
import tornaco.apps.shortx.ui.subscribe.util.getLVLTokenForServer

@Composable
inline fun ShortXProps(crossinline content: @Composable () -> Unit) {
    val props = mutableListOf<String>()
    CompositionLocalProvider(LocalShortXProps provides props) {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            onDebugBuild {
                Logger("ShortXProps").d("subscribeState collect: ${context.requireActivity()}")
            }
            LVLStateHolder.subscribeState.collectLatest {
                onDebugBuild {
                    Logger("ShortXProps").d("subscribeState changed: ${it.isSubscribed}")
                }
                getLVLTokenForServer { t ->
                    t?.let { props.add(it) }
                }
            }
        }
        content()
    }

}
