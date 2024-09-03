package tornaco.apps.shortx.ui.base

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorPalette

sealed interface LoadState<out T> {
    data class Success<T>(val data: T) : LoadState<T>
    data object Loading : LoadState<Nothing>
    data class Error(val err: Throwable? = null) : LoadState<Nothing>

    fun isLoading() = this is Loading
    fun isSuccess() = this is Success
    fun isError() = this is Error

    val <T> LoadState<T>.data: T?
        get() = (this as? Success)?.data
}

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 定义颜色列表
        val colors = ActionColorPalette

        // 用于追踪当前的颜色索引
        var currentColorIndex by remember { mutableStateOf(0) }

        // 动画颜色状态，用于平滑过渡
        val animatedColor by animateColorAsState(
            targetValue = colors[currentColorIndex],
            animationSpec = tween(durationMillis = 200),
            label = "animatedColor"
        )

        // 颜色切换逻辑，每200ms切换一次
        LaunchedEffect(Unit) {
            while (true) {
                delay(2000)
                currentColorIndex = (currentColorIndex + 1) % colors.size
            }
        }
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            strokeWidth = 6.dp,
            color = animatedColor
        )
    }
}

@Composable
fun LoadError(
    modifier: Modifier = Modifier,
    message: String? = null,
    refresh: (() -> Unit)? = null
) {
    val context = LocalContext.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .offset(y = (-80).dp)
                .fillMaxWidth(),
            painter = painterResource(
                id = github.tornaco.shortx.ui.base.R.drawable.ui_load_error
            ),
            contentDescription = null,
        )

        message?.let {
            TextButton(onClick = {
                TextDisplayActivity.start(context, "ERROR", it)
            }) {
                Text(
                    text = LocalI18N.current["ui.common.error.view.message"],
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }

        refresh?.let {
            TextButton(onClick = { refresh() }) {
                Text(
                    text = LocalI18N.current["ui.common.error.click.retry"],
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}


@Composable
fun EmptyList(modifier: Modifier = Modifier, refresh: (() -> Unit)? = null) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .offset(y = (-80).dp)
                .fillMaxWidth(),
            painter = painterResource(
                id = github.tornaco.shortx.ui.base.R.drawable.ui_empty_list
            ),
            contentDescription = null,
        )

        refresh?.let {
            TextButton(onClick = { refresh() }) {
                Text(
                    text = LocalI18N.current["ui.common.empty.click.retry"],
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline
                    )
                )
            }
        }
    }
}