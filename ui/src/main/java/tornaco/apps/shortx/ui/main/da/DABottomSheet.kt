package tornaco.apps.shortx.ui.main.da

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import tornaco.apps.shortx.ui.base.LoadError
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.Loading
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

        CompositionLocalProvider(LocalDAItemHandler provides daHandler,
            LocalDASetItemHandler provides object : DASetItemHandler {}) {
            val activeJobIds by vm.activeJobIds.collectAsState()
            val daList by vm.daList.collectAsState()
            when (daList) {
                is LoadState.Error -> {
                    LoadError(modifier = Modifier.fillMaxSize()) {
                    }
                }

                LoadState.Loading -> {
                    Loading(modifier = Modifier.fillMaxSize())
                }

                is LoadState.Success -> {
                    DirectActionListContent(
                        isShowDetailedDAItem = true,
                        das = (daList as LoadState.Success<DAList>).data.das,
                        showOverflowMenu = true,
                        colorfulCard = state.colorfulCard,
                        activeJobs = activeJobIds
                    )
                }
            }
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