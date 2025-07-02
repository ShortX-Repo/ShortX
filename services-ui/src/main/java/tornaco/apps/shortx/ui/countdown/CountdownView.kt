package tornaco.apps.shortx.ui.countdown

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.ui.BaseComposeView
import tornaco.apps.shortx.ui.base.AnimatedTextContainer
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme

@SuppressLint("ViewConstructor")
class CountdownView(
    context: Context,
    i18N: I18N,
    private val totalNumber: Int,
    private val onDismissRequest: () -> Unit,
    private val onClick: () -> Unit,
    private val onLongClick: () -> Unit,
    private val intervalMillis: Long = 1000L,
) : BaseComposeView(context, i18N) {

    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                DialogContent()
            }
        }
    }

    @Composable
    private fun DialogContent() {
        Row(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .combinedClickable(onClick = {
                    onClick()
                }, onLongClick = {
                    onLongClick()
                })
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var currentNum by remember { mutableIntStateOf(totalNumber) }
            LaunchedEffect(Unit) {
                while (currentNum > 0) {
                    delay(1000)
                    currentNum--
                }
                onDismissRequest()
            }
            AnimatedTextContainer(currentNum.toString()) {
                Text(
                    it, style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 28.sp
                    )
                )
            }
        }
    }
}