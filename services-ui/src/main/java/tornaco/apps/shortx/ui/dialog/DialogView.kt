package tornaco.apps.shortx.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

@SuppressLint("ViewConstructor")
abstract class DialogView(
    context: Context,
    i18N: I18N,
) : BaseComposeView(context, i18N) {
    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme {
            Surface(
                modifier = Modifier,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 8.dp,
                tonalElevation = 4.dp,
            ) {
                DialogContent()
            }
        }
    }

    @Composable
    abstract fun DialogContent()
}