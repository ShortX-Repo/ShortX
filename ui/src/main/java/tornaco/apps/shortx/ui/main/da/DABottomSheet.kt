package tornaco.apps.shortx.ui.main.da

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import tornaco.apps.shortx.ui.base.ShortXBottomSheet
import tornaco.apps.shortx.ui.base.ShortXBottomSheetState
import tornaco.apps.shortx.ui.composeLogger

@Composable
fun DADetailsBottomSheet(dialogState: ShortXBottomSheetState) {
    ShortXBottomSheet(state = dialogState) {
        val context = LocalContext.current
        val vm = viewModel<DASetDetailsViewModel>(
            factory = DADetailsViewModelFactory(context, it),
        )
        val daHandler = getLocalDAItemHandler(vm = vm)
        val state by vm.state.collectAsState()

        LaunchedEffect(vm) {
            vm.loadDAList()
        }
        CompositionLocalProvider(LocalDAItemHandler provides daHandler,
            LocalDASetItemHandler provides object : DASetItemHandler {}) {
            val activeJobIds by vm.activeJobIds.collectAsState()
            DirectActionListContent(
                isLoading = state.isLoading,
                isShowDetailedDAItem = true,
                das = state.das,
                showOverflowMenu = true,
                colorfulCard = state.colorfulCard,
                activeJobs = activeJobIds
            )
        }
    }
}

class DADetailsViewModelFactory(private val context: Context, private val setId: String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        composeLogger.d("DADetailsViewModelFactory create")
        @Suppress("UNCHECKED_CAST")
        return DASetDetailsViewModel(context, setId) as T
    }
}