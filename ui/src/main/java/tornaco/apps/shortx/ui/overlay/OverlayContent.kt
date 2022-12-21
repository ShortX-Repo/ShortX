package tornaco.apps.shortx.ui.overlay

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.base.MDIcons

const val BUTTON_COUNT = 6

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
    settings: List<String>,
    onButtonClick: (Int) -> Unit,
    motionEventSpy: (MotionEvent) -> Unit
) {

    val scope = rememberCoroutineScope()

    val expandState by remember(settings) {
        mutableStateOf(ExpandState(scope))
    }

    if (autoCollapse) {
        LaunchedEffect(settings) {
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
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
                        .width(48.dp)
                        .height(24.dp)
                        .clickable {
                            expandState.expand()
                        }) {
                        Icon(
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.Center),
                            imageVector = Icons.Filled.ExpandMore,
                            contentDescription = "Expand"
                        )
                    }
                }

                // Expand content.
                AnimatedVisibility(visible = expandState.isExpand) {
                    Column {
                        settings.forEachIndexed { index, icon ->
                            IconButton(onClick = {
                                onButtonClick(index)
                            }) {
                                IconForButton(icon)
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
    Icon(imageVector = MDIcons.filled(icon), contentDescription = icon)
}