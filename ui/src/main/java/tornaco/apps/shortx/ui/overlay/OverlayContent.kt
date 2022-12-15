package tornaco.apps.shortx.ui.overlay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tornaco.apps.shortx.ui.base.LargeSpacer
import tornaco.apps.shortx.ui.base.MDIcons

const val BUTTON_COUNT = 6

@Composable
fun OverlayContent() {
    val vm = viewModel<OverlayViewModel>()
    val state = vm.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(vm) {
        vm.start(context)
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
            repeat(BUTTON_COUNT) {
                IconButton(onClick = {

                }) {
                    IconForButton(index = it, state.value.buttonIcons[it])
                }
            }
            LargeSpacer()
        }
    }
}

@Composable
private fun IconForButton(index: Int, icon: String) {
    Icon(imageVector = MDIcons.filled(icon), contentDescription = icon)
}