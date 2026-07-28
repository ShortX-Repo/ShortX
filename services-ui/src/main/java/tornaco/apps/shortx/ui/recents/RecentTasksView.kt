package tornaco.apps.shortx.ui.recents

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.WindowManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.ui.AttachableComposeView
import tornaco.apps.shortx.ui.base.AppImageIcon
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme
import tornaco.apps.shortx.ui.theme.normalized
import kotlin.math.absoluteValue

data class RecentTaskItem(
    val taskId: Int,
    val packageName: String,
    val label: String,
    val snapshot: Bitmap?,
)

@SuppressLint("ViewConstructor")
class RecentTasksView(
    context: Context,
    i18N: I18N,
    val tasks: List<RecentTaskItem>,
    val onTaskSelected: (taskId: Int) -> Unit,
    val onTaskDismissed: (taskId: Int) -> Unit,
    val onClearAll: () -> Unit,
    onDetach: (AttachableComposeView) -> Unit,
    private val themeMode: UiThemeMode = UiThemeMode.UiThemeMode_System,
) : AttachableComposeView(context, i18N, onDetach) {

    override fun createLayoutParams(): WindowManager.LayoutParams {
        return super.createLayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
    }

    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme(themeMode.normalized()) {
            val taskItems = remember { mutableStateListOf(*tasks.toTypedArray()) }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        indication = null,
                        interactionSource = null
                    ) { detach(reason = "background tap") },
                contentAlignment = Alignment.Center,
            ) {
                if (taskItems.isEmpty()) {
                    Text(
                        text = "No recent tasks",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            state = rememberLazyListState(),
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = null
                            ) {},
                        ) {
                            itemsIndexed(
                                items = taskItems,
                                key = { _, item -> item.taskId },
                            ) { _, task ->
                                TaskCard(
                                    task = task,
                                    onTap = {
                                        onTaskSelected(task.taskId)
                                        detach(reason = "task selected")
                                    },
                                    onSwipeUp = {
                                        onTaskDismissed(task.taskId)
                                        taskItems.remove(task)
                                        if (taskItems.isEmpty()) {
                                            detach(reason = "all tasks dismissed")
                                        }
                                    },
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Surface(
                            modifier = Modifier.clickable {
                                onClearAll()
                                taskItems.clear()
                                detach(reason = "clear all")
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.15f),
                        ) {
                            Text(
                                text = "Clear all",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: RecentTaskItem,
    onTap: () -> Unit,
    onSwipeUp: () -> Unit,
) {
    var offsetY by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 300f
    val progress = (offsetY / dismissThreshold).coerceIn(-1f, 0f)
    val alphaAnim by animateFloatAsState(
        targetValue = 1f - progress.absoluteValue,
        label = "alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = 1f - progress.absoluteValue * 0.15f,
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .alpha(alphaAnim)
            .scale(scaleAnim)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (offsetY < -dismissThreshold) {
                            onSwipeUp()
                        }
                        offsetY = 0f
                    },
                    onDragCancel = { offsetY = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        offsetY = (offsetY + dragAmount).coerceAtMost(0f)
                    },
                )
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            AppImageIcon(
                app = task.packageName,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = task.label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(120.dp),
            )
        }

        Surface(
            modifier = Modifier
                .width(200.dp)
                .height(340.dp)
                .clickable(
                    indication = null,
                    interactionSource = null,
                ) { onTap() },
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            if (task.snapshot != null) {
                Image(
                    bitmap = task.snapshot.asImageBitmap(),
                    contentDescription = task.label,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    AppImageIcon(
                        app = task.packageName,
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
        }
    }
}
