package tornaco.apps.shortx.ui.overlay

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.base.MDIcons

const val BUTTON_COUNT = 6


private class ExpandState {
    private var _isExpand by mutableStateOf(true)
    val isExpand get() = _isExpand

    suspend fun expand() {
        _isExpand = true
        delay(3000)
        collapse()
    }

    fun collapse() {
        _isExpand = false
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OverlayContent(
    settings: List<String>,
    onButtonClick: (Int) -> Unit
) {
    val vm = viewModel<OverlayViewModel>()
    val state = vm.state.collectAsState()
    LaunchedEffect(vm) {
        vm.start()
    }
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
            val expandState by remember {
                mutableStateOf(ExpandState())
            }
            // Expand tool.
            val scope = rememberCoroutineScope()
            AnimatedVisibility(visible = !expandState.isExpand) {
                Box(modifier = Modifier
                    .width(48.dp)
                    .height(24.dp)
                    .clickable {
                        scope.launch { expandState.expand() }
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

            LaunchedEffect(settings) {
                expandState.expand()
            }
        }
    }
}

@Composable
private fun IconForButton(icon: String) {
    Icon(imageVector = MDIcons.filled(icon), contentDescription = icon)
}