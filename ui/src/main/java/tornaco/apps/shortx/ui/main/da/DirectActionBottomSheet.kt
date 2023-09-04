package tornaco.apps.shortx.ui.main.da

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import tornaco.apps.shortx.ui.base.CommonDialogState
import tornaco.apps.shortx.ui.base.ShortXBottomSheet
import tornaco.apps.shortx.ui.main.MainViewModel

@Composable
fun DABottomSheet(dialogState: CommonDialogState) {
    val vm = hiltViewModel<MainViewModel>()
    val state by vm.state.collectAsState()

    LaunchedEffect(vm) {
        vm.loadDA(delay = true)
    }

    val daHandler = getLocalDAItemHandler(vm = vm)

    CompositionLocalProvider(LocalDAItemHandler provides daHandler,
        LocalDASetItemHandler provides object : DASetItemHandler {}) {
        ShortXBottomSheet(state = dialogState) {
            DirectActionListContent(
                isLoading = false,
                isShowDetailedDAItem = true,
                das = state.das,
                showOverflowMenu = true,
            )
        }
    }
}