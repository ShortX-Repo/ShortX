package tornaco.apps.shortx.ui.main.rule

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
fun RuleDetailsBottomSheet(dialogState: ShortXBottomSheetState) {
    ShortXBottomSheet(state = dialogState) {
        val context = LocalContext.current
        val vm = viewModel<RuleSetDetailsViewModel>(
            factory = RuleDetailsViewModelFactory(context, it),
        )
        val daHandler = getLocalRuleItemHandler(vm = vm)
        val state by vm.state.collectAsState()

        LaunchedEffect(vm) {
            vm.loadRuleList()
        }
        CompositionLocalProvider(LocalRuleItemHandler provides daHandler,
            LocalRuleSetItemHandler provides object : RuleSetItemHandler {}) {
            RuleContent(
                isLoading = false,
                rules = state.rules
            )
        }
    }
}

class RuleDetailsViewModelFactory(private val context: Context, private val setId: String) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        composeLogger.d("RuleDetailsViewModelFactory create")
        @Suppress("UNCHECKED_CAST")
        return RuleSetDetailsViewModel(context, setId) as T
    }
}