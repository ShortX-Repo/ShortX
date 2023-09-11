package tornaco.apps.shortx.ui.subscribe

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.compose.collectSideEffect
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.LocalShortXProps
import tornaco.apps.shortx.ui.base.requireActivity
import tornaco.apps.shortx.ui.subscribe.util.getLVLTokenForServer

@Composable
inline fun ShortXProps(crossinline content: @Composable () -> Unit) {
    val propLogger = remember { Logger("ShortXProps") }
    val props = mutableListOf<String>()
    CompositionLocalProvider(LocalShortXProps provides props) {
        val context = LocalContext.current
        val i18N = LocalI18N.current

        LVLStateHolder.collectSideEffect { effect ->
            propLogger.p("subscribeState side effect: $effect")
            if (effect is LVLStateHolder.Effect.StateChanged) {
                getLVLTokenForServer { t ->
                    propLogger.p("collectSideEffect getLVLTokenForServer: $t")
                    t?.let { props.add(it) }
                }
            } else if (effect is LVLStateHolder.Effect.FAB) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        i18N["ui.subscription.intro.message"],
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        LaunchedEffect(Unit) {
            propLogger.p("subscribeState collect: ${context.requireActivity()}")
            getLVLTokenForServer { t ->
                propLogger.p("LaunchedEffect getLVLTokenForServer: $t")
                t?.let { props.add(it) }
            }
        }
        content()
    }

}
