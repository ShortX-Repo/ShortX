package tornaco.apps.shortx.ui.overlay

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import github.tornaco.shortx.android.icon.Remix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.base.ColorDefaults
import tornaco.apps.shortx.ui.base.RemixIcon
import tornaco.apps.shortx.ui.base.ShortXCardRoundedCornerShape
import tornaco.apps.shortx.ui.base.clickableWithRipple

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
    autoCollapse: Boolean,
    icons: List<String>,
    labels: List<String>,
    onButtonClick: (Int) -> Unit,
    motionEventSpy: (MotionEvent) -> Unit
) {
    val vm = viewModel<OverlayViewModel>()
    LaunchedEffect(labels) {
        vm.init(labels)
    }
    val state by vm.state.collectAsState()
    val formattedValues = state.formattedValues

    val scope = rememberCoroutineScope()

    val expandState by remember(icons) {
        mutableStateOf(ExpandState(scope))
    }

    if (autoCollapse) {
        LaunchedEffect(icons) {
            expandState.scheduleCollapse()
        }
    }

    Box(modifier = Modifier.motionEventSpy {
        motionEventSpy(it)

        if (autoCollapse) {
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
                    .heightIn(max = 240.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Expand tool.
                AnimatedVisibility(visible = !expandState.isExpand) {
                    Box(modifier = Modifier
                        .widthIn(min = minWidth)
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
                }

                // Expand content.
                AnimatedVisibility(visible = expandState.isExpand) {
                    Column(
                        modifier = Modifier
                            .widthIn(min = minWidth)
                            .padding(vertical = 8.dp),
                        horizontalAlignment = CenterHorizontally
                    ) {
                        formattedValues.forEachIndexed { index, label ->
                            Column(
                                modifier = Modifier.clickableWithRipple {
                                    onButtonClick(index)
                                },
                                horizontalAlignment = CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                val icon = icons[index]

                                if (icon.isNotBlank()) {
                                    IconButtonContainer(onClick = {
                                        onButtonClick(index)
                                    }) {
                                        IconForButton(icon)
                                    }
                                }

                                if (label.isNotEmpty()) {
                                    Text(
                                        modifier = Modifier
                                            .offset(y = (-4).dp)
                                            .padding(horizontal = 4.dp),
                                        text = label,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IconForButton(icon: String) {
    RemixIcon(remixName = icon)
}

@Composable
private fun IconButtonContainer(onClick: () -> Unit, icon: @Composable () -> Unit) {
    Box(modifier = Modifier
        .clip(ShortXCardRoundedCornerShape)
        .clickableWithRipple {
            onClick()
        }
        .padding(4.dp)
    ) {
        icon()
    }
}