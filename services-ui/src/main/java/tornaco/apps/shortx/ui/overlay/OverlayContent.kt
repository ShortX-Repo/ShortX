package tornaco.apps.shortx.ui.overlay

import android.view.MotionEvent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.clickableWithRipple
import tornaco.apps.shortx.ui.base.thenIf

private val logger = Logger("ShortXOverlay")

private class ExpandState(private val scope: CoroutineScope) {
    private var _isExpand by mutableStateOf(true)
    val isExpand get() = _isExpand

    private var pendingCollapseJob: Job? = null

    fun cancelPendingCollapseJob() {
        pendingCollapseJob?.cancel()
    }

    fun scheduleCollapse() {
        cancelPendingCollapseJob()
        pendingCollapseJob = scope.launch {
            delay(5000)
            collapse()
        }
    }

    fun expand() {
        _isExpand = true
    }

    fun collapse() {
        _isExpand = false
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OverlayContent(
    action: ShowOverlayButton,
    onButtonClick: (OverlayButtonSetting) -> Unit,
    onButtonLongClick: (OverlayButtonSetting) -> Unit,
    dragHandleMotionEvent: (MotionEvent) -> Unit
) {
    val vm = viewModel<OverlayViewModel>()
    LaunchedEffect(action) {
        vm.init(action)
    }
    val state by vm.state.collectAsState()

    val scope = rememberCoroutineScope()

    val expandState by remember(action) {
        mutableStateOf(ExpandState(scope))
    }

    if (action.autoCollapse) {
        LaunchedEffect(action) {
            expandState.scheduleCollapse()
        }
    }

    Box(modifier = Modifier.motionEventSpy {
        if (action.autoCollapse) {
            if (it.action == MotionEvent.ACTION_UP) {
                expandState.scheduleCollapse()
            } else {
                expandState.cancelPendingCollapseJob()
            }
        }

        if (action.enableGlobalDrag) {
            dragHandleMotionEvent(it)
        }

    }) {
        Card(
            modifier = Modifier,
            colors = CardDefaults.cardColors(
                containerColor = ColorDefaults.backgroundSurfaceColor().copy(alpha = 0.86f)
            )
        ) {
            ExpandContainer(isExpanded = expandState.isExpand,
                expandContent = {

                    var gridWidth by remember {
                        mutableStateOf(32.dp)
                    }
                    val density = LocalDensity.current
                    // Drag handle
                    if (!action.enableGlobalDrag) {
                        Box(modifier = Modifier
                            .width(gridWidth)
                            .height(16.dp)
                            .motionEventSpy {
                                dragHandleMotionEvent(it)
                            })
                    }

                    LazyVerticalGrid(
                        modifier = Modifier
                            .padding(horizontal = action.overlayPaddingH.dp)
                            .padding(vertical = action.overlayPaddingV.dp)
                            .onSizeChanged {
                                gridWidth = with(density) {
                                    it.width.toDp()
                                }
                            }
                            .widthIn(
                                max = (maxOf(
                                    72,
                                    action.maxWidthInDp
                                ).dp)
                            )
                            .heightIn(
                                max = (maxOf(
                                    72,
                                    action.maxHeightInDp
                                ).dp)
                            ),
                        columns = GridCells.Adaptive(maxOf(action.buttonMinWidth.dp, 64.dp)),
                        content = {
                            overlayButtons(state, onButtonClick, onButtonLongClick)
                        })
                },
                collapseContent = {
                    Box(modifier = Modifier
                        .motionEventSpy {
                            dragHandleMotionEvent(it)
                        }
                        .width(48.dp)
                        .height(24.dp)
                        .clickable {
                            expandState.expand()
                        }) {
                        RemixIcon(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.Center),
                            remixName = Remix.Arrows.arrow_drop_down_line,
                        )
                    }
                })
        }

    }
}

private fun LazyGridScope.overlayButtons(
    state: OverlayState,
    onButtonClick: (OverlayButtonSetting) -> Unit,
    onButtonLongClick: (OverlayButtonSetting) -> Unit,
) {
    itemsIndexed(state.buttons) { index, button ->
        val color = ColorPalette[index % ColorPalette.size]
        val updateButton by rememberUpdatedState(newValue = button)
        Column(
            modifier = Modifier
                .thenIf(Modifier.padding(bottom = 8.dp), index != state.buttons.size - 1)
                .clip(ShortXCardRoundedCornerShape)
                .clickableWithRipple(
                    onLongClick = {
                        onButtonLongClick(updateButton.setting)
                    },
                    onClick = {
                        onButtonClick(updateButton.setting)
                    }),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val icon = updateButton.setting.icon

            if (icon.isNotBlank()) {
                ColoredIcon(size = 26.dp, color = color) {
                    RemixIcon(
                        modifier = Modifier
                            .size(12.dp),
                        remixName = icon,
                        tint = Color.White
                    )
                }
            }

            val label = updateButton.formattedLabel
            if (label.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .padding(top = 3.dp),
                    text = label,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                )
            }
        }
    }
}

@Composable
private fun DragHandle(
    modifier: Modifier = Modifier,
    width: Dp = 32.dp,
    height: Dp = 4.dp,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f),
) {
    val dragHandleDescription = "DragHandle"
    Surface(
        modifier = modifier
            .padding(vertical = 22.dp)
            .semantics { contentDescription = dragHandleDescription },
        color = color,
        shape = shape
    ) {
        Box(
            Modifier
                .size(
                    width = width,
                    height = height
                )
        )
    }
}

@Composable
fun ExpandContainer(
    isExpanded: Boolean,
    expandContent: @Composable () -> Unit,
    collapseContent: @Composable () -> Unit
) {
    AnimatedContent(
        targetState = isExpanded,
        transitionSpec = {
            fadeIn(animationSpec = tween(150, 150)) togetherWith
                    fadeOut(animationSpec = tween(150)) using
                    SizeTransform { initialSize, targetSize ->
                        if (targetState) {
                            keyframes {
                                // Expand horizontally first.
                                IntSize(targetSize.width, initialSize.height) at 150
                                durationMillis = 300
                            }
                        } else {
                            keyframes {
                                // Shrink vertically first.
                                IntSize(initialSize.width, targetSize.height) at 150
                                durationMillis = 300
                            }
                        }
                    }
        }, label = "ExpandAnim"
    ) { targetExpanded ->
        if (targetExpanded) {
            expandContent()
        } else {
            collapseContent()
        }
    }
}

private val ColorPalette = arrayOf(
    // Google
    Color(0xff4586F3),
    Color(0xffEB4334),
    Color(0xffFBBD06),
    Color(0xff35AA53),
    // MTR
    Color(0xffAE213B),
    Color(0xff11254A),
    Color(0xffFF0000),
    Color(0xff1A9431),
    Color(0xff0860A8),
    Color(0xffB5BD00),
    Color(0xff6B208B),
    Color(0xffFE7F1D),
    Color(0xffF550A6),
    Color(0xff5EB6E4),
    Color(0xffA40084),

    // Nintendo Switch
    Color(0xffF8B62E),
    Color(0xffBAD600),
    Color(0xff19D719),
    Color(0xffF02D7D),
    Color(0xffFFDC00),
    Color(0xffFF5145),
    Color(0xff00BBDF),
    Color(0xff8B26A6),
    Color(0xffC88C32),
)