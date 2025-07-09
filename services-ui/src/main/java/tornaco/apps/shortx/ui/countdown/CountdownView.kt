package tornaco.apps.shortx.ui.countdown

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShapeXL
import tornaco.apps.shortx.ui.base.StandardSpacer
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
    private val note: String = "",
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
        Box(
            modifier = Modifier
                .clip(ShortXCardRoundedCornerShapeXL)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .combinedClickable(onClick = {
                    onClick()
                }, onLongClick = {
                    onLongClick()
                })
                .padding(vertical = 24.dp, horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            var currentNum by remember { mutableIntStateOf(totalNumber) }
            LaunchedEffect(Unit) {
                while (currentNum > 0) {
                    delay(1000)
                    currentNum--
                }
                onDismissRequest()
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedTextContainer(currentNum.toString()) {
                    Text(
                        text = it, style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 28.sp
                        )
                    )
                }
                if (note.isNotEmpty()) {
                    StandardSpacer()
                    Text(
                        modifier = Modifier.widthIn(max = 120.dp),
                        text = note,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }
    }
}