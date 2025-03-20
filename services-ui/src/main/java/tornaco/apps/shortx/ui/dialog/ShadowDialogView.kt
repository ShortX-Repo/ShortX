package tornaco.apps.shortx.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

@SuppressLint("ViewConstructor")
abstract class ShadowDialogView(
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
        ShortXServiceUITheme {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp,
                color = Color.Transparent
            ) {
                Box(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    ShadowBox3(
                        modifier = Modifier.fillMaxWidth(),
                        shape = if (platformRadius == null) {
                            RoundedCornerShape(24.dp)
                        } else {
                            RoundedCornerShape(
                                platformRadius
                            )
                        },
                    ) {
                        DialogContent()
                    }
                }
            }
        }
    }

    @Composable
    abstract fun DialogContent()
}

@Composable
fun ShadowBox3(
    modifier: Modifier = Modifier,
    elevation: Dp = 12.dp,
    shape: Shape = CardDefaults.shape,
    shadowColor: Color = Color.Black.copy(alpha = 0.5f),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                spotColor = shadowColor,
                ambientColor = shadowColor,
                clip = true
            )
            .background(MaterialTheme.colorScheme.surface)
    ) {
        content()
    }
}