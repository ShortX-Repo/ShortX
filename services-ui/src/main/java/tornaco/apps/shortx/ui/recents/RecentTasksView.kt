package tornaco.apps.shortx.ui.recents

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.WindowManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.action.RecentsLayoutStyle
import tornaco.apps.shortx.core.proto.common.UiThemeMode
import tornaco.apps.shortx.core.util.OsUtils
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
    val layoutStyle: RecentsLayoutStyle = RecentsLayoutStyle.RecentsLayoutStyle_TiltStack,
    val gridColumns: Int = 3,
    val backgroundColor: Int = 0x99000000.toInt(),
    val backgroundBlurRadius: Int = 0,
    val backgroundImagePath: String = "",
    val miniMode: Boolean = false,
    val showAppName: Boolean = true,
    val arcArrangement: Boolean = false,
    val centerActiveApp: Boolean = false,
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
            if (backgroundBlurRadius > 0 && OsUtils.isSOrAbove()) {
                flags = flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
                try {
                    WindowManager.LayoutParams::class.java
                        .getMethod("setBlurBehindRadius", Int::class.javaPrimitiveType)
                        .invoke(this, backgroundBlurRadius)
                } catch (_: Throwable) {}
            }
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
                contentAlignment = if (miniMode) Alignment.BottomCenter else Alignment.Center,
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
                    val contentModifier = if (miniMode) {
                        Modifier.fillMaxWidth().fillMaxHeight(0.45f)
                    } else {
                        Modifier.fillMaxSize()
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = contentModifier,
                    ) {
                        if (!miniMode) Spacer(modifier = Modifier.weight(0.08f))

                        Box(modifier = Modifier.weight(0.84f)) {
                            when (layoutStyle) {
                                RecentsLayoutStyle.RecentsLayoutStyle_TiltStack ->
                                    TiltStackLayout(taskItems)
                                RecentsLayoutStyle.RecentsLayoutStyle_Grid ->
                                    GridLayout(taskItems, gridColumns)
                                RecentsLayoutStyle.RecentsLayoutStyle_VerticalList ->
                                    VerticalListLayout(taskItems)
                                RecentsLayoutStyle.RecentsLayoutStyle_SlimList ->
                                    SlimListLayout(taskItems)
                                RecentsLayoutStyle.UNRECOGNIZED ->
                                    TiltStackLayout(taskItems)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        ClearAllButton()
                        if (!miniMode) Spacer(modifier = Modifier.weight(0.08f))
                        else Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    @Composable
    private fun TiltStackLayout(taskItems: MutableList<RecentTaskItem>) {
        var currentIndex by remember { mutableIntStateOf(0) }
        // Finger-follow offset in "pages" (1 = one card). Updated synchronously while
        // dragging; only goes through Animatable when settling after release/tap.
        var dragOffset by remember { mutableFloatStateOf(0f) }
        var dismissOffset by remember { mutableFloatStateOf(0f) }
        val scope = rememberCoroutineScope()
        val settleAnim = remember { Animatable(0f) }
        val cardWidth = if (miniMode) 160 else 240
        val cardHeight = if (miniMode) 260 else 400

        fun settleTo(targetOffset: Float, commitIndex: Int?) {
            scope.launch {
                settleAnim.snapTo(dragOffset)
                settleAnim.animateTo(targetOffset, animationSpec = tween(250)) {
                    dragOffset = value
                }
                if (commitIndex != null) {
                    currentIndex = commitIndex
                }
                // offsetFromCenter = (index - currentIndex) + dragOffset stays continuous:
                // e.g. target=-1 then currentIndex++ + dragOffset=0 yields the same visual.
                dragOffset = 0f
                settleAnim.snapTo(0f)
            }
        }

        fun animateToIndex(targetIndex: Int) {
            if (targetIndex == currentIndex || targetIndex !in taskItems.indices) return
            // Animate dragOffset so the tapped card reaches center, then commit index.
            settleTo(
                targetOffset = (currentIndex - targetIndex).toFloat(),
                commitIndex = targetIndex,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(currentIndex, taskItems.size) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            // Take over from any in-flight settle animation.
                            scope.launch { settleAnim.stop() }
                        },
                        onDragEnd = {
                            val value = dragOffset
                            val threshold = 0.3f
                            when {
                                value < -threshold && currentIndex < taskItems.lastIndex -> {
                                    settleTo(targetOffset = -1f, commitIndex = currentIndex + 1)
                                }
                                value > threshold && currentIndex > 0 -> {
                                    settleTo(targetOffset = 1f, commitIndex = currentIndex - 1)
                                }
                                else -> {
                                    settleTo(targetOffset = 0f, commitIndex = null)
                                }
                            }
                        },
                        onDragCancel = {
                            settleTo(targetOffset = 0f, commitIndex = null)
                        },
                        onHorizontalDrag = { change, amount ->
                            change.consume()
                            val pageWidth = size.width.toFloat()
                            val raw = dragOffset + amount / (pageWidth * 0.55f)  // 0.55 makes it fly faster
                            val minOffset =
                                if (currentIndex >= taskItems.lastIndex) -0.45f else -1.25f
                            val maxOffset = if (currentIndex == 0) 0.45f else 1.25f
                            dragOffset = raw.coerceIn(minOffset, maxOffset)
                        },
                    )
                }
                .pointerInput(currentIndex, taskItems.size) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (dismissOffset < -300f && currentIndex in taskItems.indices) {
                                val task = taskItems[currentIndex]
                                onTaskDismissed(task.taskId)
                                taskItems.remove(task)
                                if (taskItems.isEmpty()) {
                                    detach(reason = "all tasks dismissed")
                                } else if (currentIndex >= taskItems.size) {
                                    currentIndex = taskItems.lastIndex
                                }
                            }
                            dismissOffset = 0f
                        },
                        onDragCancel = { dismissOffset = 0f },
                        onVerticalDrag = { change, amount ->
                            change.consume()
                            dismissOffset = (dismissOffset + amount).coerceAtMost(0f)
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            taskItems.forEachIndexed { index, task ->
                val relativeIndex = index - currentIndex
                val offsetFromCenter = relativeIndex + dragOffset
                val absOffset = offsetFromCenter.absoluteValue

                if (absOffset > 3f) return@forEachIndexed

                val rotationY = offsetFromCenter * 45f
                val scale = (1f - absOffset * 0.12f).coerceAtLeast(0.6f)
                val translationXDp = offsetFromCenter * cardWidth * 0.65f
                val alphaVal = (1f - absOffset * 0.3f).coerceIn(0f, 1f)
                val zIdx = 10f - absOffset

                val isCurrent = index == currentIndex
                val dismissProgress = if (isCurrent) {
                    (dismissOffset.absoluteValue / 300f).coerceIn(0f, 1f)
                } else 0f

                Box(
                    modifier = Modifier
                        .zIndex(zIdx)
                        .graphicsLayer {
                            this.rotationY = rotationY.coerceIn(-60f, 60f)
                            this.scaleX = scale
                            this.scaleY = scale
                            this.translationX = translationXDp * density
                            this.alpha = alphaVal * (1f - dismissProgress)
                            this.cameraDistance = 12f * density
                        }
                        .then(
                            if (isCurrent) {
                                Modifier.offset {
                                    IntOffset(0, dismissOffset.toInt().coerceAtMost(0))
                                }
                            } else Modifier
                        ),
                ) {
                    TaskCard(
                        task = task,
                        cardWidth = cardWidth,
                        cardHeight = cardHeight,
                        showLabel = showAppName,
                        onTap = {
                            if (isCurrent) {
                                onTaskSelected(task.taskId)
                                detach(reason = "task selected")
                            } else {
                                animateToIndex(index)
                            }
                        },
                    )
                }
            }
        }
    }

    @Composable
    private fun VerticalListLayout(taskItems: MutableList<RecentTaskItem>) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(if (arcArrangement) 12.dp else 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(
                items = taskItems,
                key = { _, item -> item.taskId },
            ) { index, task ->
                val arcOffset = if (arcArrangement) {
                    val center = taskItems.size / 2f
                    val dist = (index - center) / center.coerceAtLeast(1f)
                    (dist * dist * 40f).dp
                } else 0.dp

                VerticalListCard(
                    task = task,
                    horizontalPadding = arcOffset,
                    showLabel = showAppName,
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
    private fun GridLayout(taskItems: MutableList<RecentTaskItem>, columns: Int) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            gridItemsIndexed(
                items = taskItems,
                key = { _, item -> item.taskId },
            ) { _, task ->
                GridCard(
                    task = task,
                    showLabel = showAppName,
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
    private fun SlimListLayout(taskItems: MutableList<RecentTaskItem>) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(
                items = taskItems,
                key = { _, item -> item.taskId },
            ) { _, task ->
                SlimListItem(
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
                text = "Close all",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            )
        }
    }
}

// ─── Card composables ──────────────────────────────────────────────────

@Composable
private fun TaskCard(
    task: RecentTaskItem,
    cardWidth: Int = 240,
    cardHeight: Int = 400,
    showLabel: Boolean = true,
    onTap: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showLabel) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp),
            ) {
                AppImageIcon(
                    app = task.packageName,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = task.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(100.dp),
                )
            }
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
            SnapshotContent(task, 48)
        }
    }
}

@Composable
private fun VerticalListCard(
    task: RecentTaskItem,
    horizontalPadding: androidx.compose.ui.unit.Dp = 0.dp,
    showLabel: Boolean = true,
    onTap: () -> Unit,
    onDismiss: () -> Unit,
) {
    var dragX by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 300f
    val progress = (dragX.absoluteValue / dismissThreshold).coerceIn(0f, 1f)
    val alphaAnim by animateFloatAsState(targetValue = 1f - progress, label = "alpha")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .alpha(alphaAnim)
            .offset { IntOffset(dragX.toInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragX.absoluteValue > dismissThreshold) onDismiss()
                        dragX = 0f
                    },
                    onDragCancel = { dragX = 0f },
                    onHorizontalDrag = { _, amount -> dragX += amount },
                )
            }
            .clickable(indication = null, interactionSource = null) { onTap() },
    ) {
        if (showLabel) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(48.dp)
                    .padding(end = 8.dp),
            ) {
                AppImageIcon(
                    app = task.packageName,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp,
                )
            }
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .height(160.dp),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            SnapshotContent(task, 40)
        }
    }
}

@Composable
private fun GridCard(
    task: RecentTaskItem,
    showLabel: Boolean = true,
    onTap: () -> Unit,
    onDismiss: () -> Unit,
) {
    var offsetY by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 200f
    val progress = (offsetY.absoluteValue / dismissThreshold).coerceIn(0f, 1f)
    val alphaAnim by animateFloatAsState(targetValue = 1f - progress, label = "alpha")

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
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            SnapshotContent(task, 32)
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                AppImageIcon(
                    app = task.packageName,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = task.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

@Composable
private fun SlimListItem(
    task: RecentTaskItem,
    onTap: () -> Unit,
    onDismiss: () -> Unit,
) {
    var dragX by remember { mutableFloatStateOf(0f) }
    val dismissThreshold = 300f
    val progress = (dragX.absoluteValue / dismissThreshold).coerceIn(0f, 1f)
    val alphaAnim by animateFloatAsState(targetValue = 1f - progress, label = "alpha")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alphaAnim)
            .offset { IntOffset(dragX.toInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragX.absoluteValue > dismissThreshold) onDismiss()
                        dragX = 0f
                    },
                    onDragCancel = { dragX = 0f },
                    onHorizontalDrag = { _, amount -> dragX += amount },
                )
            }
            .clickable(indication = null, interactionSource = null) { onTap() },
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.08f),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            AppImageIcon(
                app = task.packageName,
                modifier = Modifier.size(36.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = task.label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (task.snapshot != null) {
                Surface(
                    modifier = Modifier
                        .width(56.dp)
                        .height(36.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Image(
                        bitmap = task.snapshot.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}

@Composable
private fun SnapshotContent(task: RecentTaskItem, iconSize: Int) {
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
                modifier = Modifier.size(iconSize.dp),
            )
        }
    }
}
