package tornaco.apps.shortx.ui.recents

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.WindowManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed as gridItemsIndexed
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.action.RecentsLayoutStyle
import tornaco.apps.shortx.core.proto.action.RecentsScrollOrientation
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.ui.AttachableComposeView
import tornaco.apps.shortx.ui.base.AppImageIcon
import tornaco.apps.shortx.ui.theme.ShortXServiceUITheme
import tornaco.apps.shortx.ui.theme.normalized
import kotlin.math.absoluteValue

enum class DismissDirection { SwipeUp, SwipeHorizontal }

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
    val layoutStyle: RecentsLayoutStyle = RecentsLayoutStyle.RecentsLayoutStyle_Carousel,
    val scrollOrientation: RecentsScrollOrientation = RecentsScrollOrientation.RecentsScrollOrientation_Horizontal,
    val gridColumns: Int = 3,
    val backgroundColor: Int = 0x99000000.toInt(),
    val backgroundBlurRadius: Int = 0,
    val backgroundImagePath: String = "",
    val onTaskSelected: (taskId: Int) -> Unit,
    val onTaskDismissed: (taskId: Int) -> Unit,
    val onClearAll: () -> Unit,
    onDetach: (AttachableComposeView) -> Unit,
    private val themeMode: UiThemeMode = UiThemeMode.UiThemeMode_System,
) : AttachableComposeView(context, i18N, onDetach) {

    private val bgBitmap: Bitmap? by lazy {
        if (backgroundImagePath.isNotEmpty()) {
            runCatching { BitmapFactory.decodeFile(backgroundImagePath) }.getOrNull()
        } else null
    }

    override fun createLayoutParams(): WindowManager.LayoutParams {
        return super.createLayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
    }

    @Composable
    override fun ComposeContent() {
        ShortXServiceUITheme(themeMode.normalized()) {
            val taskItems = remember { mutableStateListOf(*tasks.toTypedArray()) }
            val bgColor = remember { Color(backgroundColor) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (bgBitmap != null) Modifier else Modifier.background(bgColor)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = null
                    ) { detach(reason = "background tap") },
                contentAlignment = Alignment.Center,
            ) {
                bgBitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().alpha(bgColor.alpha),
                        contentScale = ContentScale.Crop,
                    )
                }

                if (taskItems.isEmpty()) {
                    Text(
                        text = "No recent tasks",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Spacer(modifier = Modifier.weight(0.1f))

                        Box(modifier = Modifier.weight(0.8f)) {
                            when (layoutStyle) {
                                RecentsLayoutStyle.RecentsLayoutStyle_Carousel ->
                                    CarouselLayout(taskItems, scrollOrientation)
                                RecentsLayoutStyle.RecentsLayoutStyle_Stack ->
                                    StackLayout(taskItems)
                                RecentsLayoutStyle.RecentsLayoutStyle_Grid ->
                                    GridLayout(taskItems, gridColumns)
                                RecentsLayoutStyle.UNRECOGNIZED ->
                                    CarouselLayout(taskItems, scrollOrientation)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        ClearAllButton()
                        Spacer(modifier = Modifier.weight(0.1f))
                    }
                }
            }
        }
    }

    @Composable
    private fun CarouselLayout(
        taskItems: MutableList<RecentTaskItem>,
        orientation: RecentsScrollOrientation,
    ) {
        val onDismiss: (RecentTaskItem) -> Unit = { task ->
            onTaskDismissed(task.taskId)
            taskItems.remove(task)
            if (taskItems.isEmpty()) detach(reason = "all tasks dismissed")
        }
        val onTap: (RecentTaskItem) -> Unit = { task ->
            onTaskSelected(task.taskId)
            detach(reason = "task selected")
        }

        if (orientation == RecentsScrollOrientation.RecentsScrollOrientation_Vertical) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(
                    items = taskItems,
                    key = { _, item -> item.taskId },
                ) { _, task ->
                    // Vertical scroll → horizontal swipe to dismiss
                    TaskCard(
                        task = task,
                        dismissDirection = DismissDirection.SwipeHorizontal,
                        onTap = { onTap(task) },
                        onDismiss = { onDismiss(task) },
                    )
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                itemsIndexed(
                    items = taskItems,
                    key = { _, item -> item.taskId },
                ) { _, task ->
                    // Horizontal scroll → vertical swipe-up to dismiss
                    TaskCard(
                        task = task,
                        dismissDirection = DismissDirection.SwipeUp,
                        onTap = { onTap(task) },
                        onDismiss = { onDismiss(task) },
                    )
                }
            }
        }
    }

    @Composable
    private fun StackLayout(taskItems: MutableList<RecentTaskItem>) {
        // Horizontal LazyRow with overlapping cards (negative spacing),
        // swipe-up individual cards to dismiss
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy((-100).dp),
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            itemsIndexed(
                items = taskItems,
                key = { _, item -> item.taskId },
            ) { index, task ->
                val depthScale = 1f - index.coerceAtMost(6) * 0.02f
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = depthScale
                            scaleY = depthScale
                        },
                ) {
                    TaskCard(
                        task = task,
                        cardWidth = 160,
                        cardHeight = 280,
                        dismissDirection = DismissDirection.SwipeUp,
                        onTap = {
                            onTaskSelected(task.taskId)
                            detach(reason = "task selected")
                        },
                        onDismiss = {
                            onTaskDismissed(task.taskId)
                            taskItems.remove(task)
                            if (taskItems.isEmpty()) detach(reason = "all tasks dismissed")
                        },
                    )
                }
            }
        }
    }

    @Composable
    private fun GridLayout(taskItems: MutableList<RecentTaskItem>, columns: Int) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            gridItemsIndexed(
                items = taskItems,
                key = { _, item -> item.taskId },
            ) { _, task ->
                SmallTaskCard(
                    task = task,
                    onTap = {
                        onTaskSelected(task.taskId)
                        detach(reason = "task selected")
                    },
                    onDismiss = {
                        onTaskDismissed(task.taskId)
                        taskItems.remove(task)
                        if (taskItems.isEmpty()) detach(reason = "all tasks dismissed")
                    },
                )
            }
        }
    }

    @Composable
    private fun ClearAllButton() {
        Surface(
            modifier = Modifier.clickable {
                onClearAll()
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

@Composable
private fun TaskCard(
    task: RecentTaskItem,
    cardWidth: Int = 200,
    cardHeight: Int = 340,
    dismissDirection: DismissDirection = DismissDirection.SwipeUp,
    onTap: () -> Unit,
    onDismiss: () -> Unit,
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 300f
    val progress = (dragOffset.absoluteValue / dismissThreshold).coerceIn(0f, 1f)
    val alphaAnim by animateFloatAsState(
        targetValue = 1f - progress,
        label = "alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = 1f - progress * 0.15f,
        label = "scale"
    )

    val offsetModifier = when (dismissDirection) {
        DismissDirection.SwipeUp ->
            Modifier.offset { IntOffset(0, dragOffset.toInt().coerceAtMost(0)) }
        DismissDirection.SwipeHorizontal ->
            Modifier.offset { IntOffset(dragOffset.toInt(), 0) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .alpha(alphaAnim)
            .scale(scaleAnim)
            .then(offsetModifier)
            .pointerInput(dismissDirection) {
                when (dismissDirection) {
                    DismissDirection.SwipeUp -> detectVerticalDragGestures(
                        onDragEnd = {
                            if (dragOffset < -dismissThreshold) onDismiss()
                            dragOffset = 0f
                        },
                        onDragCancel = { dragOffset = 0f },
                        onVerticalDrag = { _, amount ->
                            dragOffset = (dragOffset + amount).coerceAtMost(0f)
                        },
                    )
                    DismissDirection.SwipeHorizontal -> detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragOffset.absoluteValue > dismissThreshold) onDismiss()
                            dragOffset = 0f
                        },
                        onDragCancel = { dragOffset = 0f },
                        onHorizontalDrag = { _, amount ->
                            dragOffset += amount
                        },
                    )
                }
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
                .width(cardWidth.dp)
                .height(cardHeight.dp)
                .clickable(indication = null, interactionSource = null) { onTap() },
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

@Composable
private fun SmallTaskCard(
    task: RecentTaskItem,
    onTap: () -> Unit,
    onDismiss: () -> Unit,
) {
    var offsetY by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 200f
    val progress = (offsetY.absoluteValue / dismissThreshold).coerceIn(0f, 1f)
    val alphaAnim by animateFloatAsState(
        targetValue = 1f - progress,
        label = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .alpha(alphaAnim)
            .offset { IntOffset(0, offsetY.toInt().coerceAtMost(0)) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (offsetY < -dismissThreshold) onDismiss()
                        offsetY = 0f
                    },
                    onDragCancel = { offsetY = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        offsetY = (offsetY + dragAmount).coerceAtMost(0f)
                    },
                )
            },
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.56f)
                .clickable(indication = null, interactionSource = null) { onTap() },
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            if (task.snapshot != null) {
                Image(
                    bitmap = task.snapshot.asImageBitmap(),
                    contentDescription = task.label,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    AppImageIcon(
                        app = task.packageName,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            AppImageIcon(
                app = task.packageName,
                modifier = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = task.label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
