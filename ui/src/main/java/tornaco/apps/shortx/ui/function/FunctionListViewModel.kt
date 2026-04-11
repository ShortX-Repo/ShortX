package tornaco.apps.shortx.ui.function

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject
import tornaco.apps.shortx.core.proto.func.`Function` as ShortXFunction

data class FunctionItemUiModel(
    val id: String,
    val name: String,
    val returnType: String,
    val parametersCount: Int,
    val actionsCount: Int,
    val raw: ShortXFunction,
)

data class FunctionListState(
    val functions: List<FunctionItemUiModel> = emptyList(),
)

@HiltViewModel
class FunctionListViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<FunctionListState>(context, { FunctionListState() }) {

    fun loadState() {
        viewModelScope.launch {
            val functions = shortXManager.getAllFunctions()
                .sortedByDescending { it.lastUpdateTime }
                .map {
                    FunctionItemUiModel(
                        id = it.id,
                        name = it.name,
                        returnType = it.returnType,
                        parametersCount = it.parametersCount,
                        actionsCount = it.actionsCount,
                        raw = it,
                    )
                }
            updateState { copy(functions = functions) }
        }
    }

    fun deleteFunction(id: String) {
        viewModelScope.launch {
            shortXManager.deleteFunction(id)
            loadState()
        }
    }
}