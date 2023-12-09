package tornaco.apps.shortx.ui.clipbord

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.BaseComposeView

@SuppressLint("ViewConstructor")
class ClipboardView(
    context: Context,
    i18N: I18N,
    onDetach: (BaseComposeView) -> Unit,
) : BaseComposeView(context, i18N, onDetach) {
    @Composable
    override fun ComposeContent() {

    }
}