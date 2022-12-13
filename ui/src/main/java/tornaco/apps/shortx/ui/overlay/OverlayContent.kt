package tornaco.apps.shortx.ui.overlay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private const val BUTTON_COUNT = 6

@Composable
fun OverlayContent() {
    val vm = viewModel<OverlayViewModel>()
    val state = vm.state.collectAsState()
    LaunchedEffect(vm) {
        vm.start()
    }

    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.wrapContentSize()) {
            repeat(BUTTON_COUNT) {
                IconButton(onClick = {

                }) {
                    IconForButton(index = it)
                }
            }
        }
    }
}

@Composable
private fun IconForButton(index: Int) {
    Icon(imageVector = Icons.Filled.Stadium, contentDescription = "Stadium")
}