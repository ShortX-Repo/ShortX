package tornaco.apps.shortx.ui.overlay

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.proto.settings.OverlayOrientation
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.clickableWithRipple
import tornaco.apps.shortx.ui.base.thenIf

private val minWidth = 48.dp

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
            delay(3000)
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
    motionEventSpy: (MotionEvent) -> Unit
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
        motionEventSpy(it)

        if (action.autoCollapse) {
            if (it.action == MotionEvent.ACTION_UP) {
                expandState.scheduleCollapse()
            } else {
                expandState.cancelPendingCollapseJob()
            }
        }

    }) {
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = ColorDefaults.backgroundSurfaceColor().copy(
                    alpha = if (expandState.isExpand) {
                        0.8f
                    } else 0.4f
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Expand tool.
                val expandSizeDp: MutableState<Dp> = remember {
                    mutableStateOf(minWidth)
                }
                val density = LocalDensity.current
                AnimatedVisibility(visible = !expandState.isExpand) {
                    Box(modifier = Modifier
                        .width(expandSizeDp.value)
                        .height(24.dp)
                        .clickable {
                            expandState.expand()
                        }) {
                        IconForButton(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.Center),
                            icon = Remix.Arrows.arrow_drop_down_line,
                        )
                    }
                }

                // Expand content.
                AnimatedVisibility(visible = expandState.isExpand) {
                    if (action.orientation == OverlayOrientation.Land) {
                        Row(
                            modifier = Modifier
                                .widthIn(min = minWidth)
                                .padding(horizontal = 8.dp)
                                .onSizeChanged {
                                    expandSizeDp.value = with(density) { it.height.toDp() }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OverlayButtons(state, onButtonClick, onButtonLongClick)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .widthIn(min = minWidth)
                                .padding(vertical = 8.dp)
                                .onSizeChanged {
                                    expandSizeDp.value = with(density) { it.width.toDp() }
                                },
                            horizontalAlignment = CenterHorizontally
                        ) {
                            OverlayButtons(state, onButtonClick, onButtonLongClick)
                        }
                    }


                }
            }
        }
    }
}

@Composable
private fun OverlayButtons(
    state: OverlayState,
    onButtonClick: (OverlayButtonSetting) -> Unit,
    onButtonLongClick: (OverlayButtonSetting) -> Unit,
) {
    state.buttons.forEachIndexed { index, button ->
        val updateButton by rememberUpdatedState(newValue = button)
        Column(
            modifier = Modifier
                .thenIf(Modifier.padding(top = 6.dp), index != 0)
                .clip(ShortXCardRoundedCornerShape)
                .clickableWithRipple(
                    onLongClick = {
                        onButtonLongClick(updateButton.setting)
                    },
                    onClick = {
                        onButtonClick(updateButton.setting)
                    }),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            val icon = updateButton.setting.icon

            if (icon.isNotBlank()) {
                IconButtonContainer {
                    IconForButton(icon = icon)
                }
            }

            val label = updateButton.formattedLabel
            if (label.isNotEmpty()) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 4.dp),
                    text = label,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                )
            }
        }
    }
}

@Composable
private fun IconForButton(modifier: Modifier = Modifier, icon: String) {
    RemixIcon(modifier = modifier, remixName = icon)
}

@Composable
private fun IconButtonContainer(icon: @Composable () -> Unit) {
    Box(modifier = Modifier) {
        icon()
    }
}