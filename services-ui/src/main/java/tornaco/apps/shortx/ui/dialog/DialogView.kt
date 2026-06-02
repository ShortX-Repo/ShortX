package tornaco.apps.shortx.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.TypedValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme
import tornaco.apps.shortx.ui.theme.normalized

@SuppressLint("ViewConstructor")
abstract class DialogView(
    context: Context,
    i18N: I18N,
    val style: DialogUiStyleSettings,
) : BaseComposeView(context, i18N) {
    @Composable
    override fun ComposeContent() {
        val platformRadius = kotlin.runCatching {
            context.getDialogCornerRadius()
        }.getOrNull()?.takeIf { it > 10f }
        Logger.nameless.w("platformRadius: $platformRadius")
        ShortXServiceUITheme(style.themeMode.normalized()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = if (platformRadius == null) {
                    RoundedCornerShape(24.dp)
                } else {
                    RoundedCornerShape(
                        platformRadius
                    )
                },
                tonalElevation = 4.dp,
            ) {
                DialogContent()
            }
        }
    }

    @Composable
    abstract fun DialogContent()
}

fun Context.getDialogCornerRadius(): Float {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.dialogCornerRadius, typedValue, true)
        return typedValue.getDimension(resources.displayMetrics)
    } else {
        return 10f
    }
}
