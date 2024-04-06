package tornaco.apps.shortx.ui.addrule.fact.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject


data class ShowDialogSelectorState(
    val keys: List<Int> = emptyList()
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class CombineKeyEventSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ShowDialogSelectorState>(
        context, { ShowDialogSelectorState() }
    ) {

    fun initFrom(currentAction: Action.InjectCombineKeyCode?) {
        currentAction?.let {
            updateState { copy(keys = currentAction.codes) }
        }
    }


    fun addKey(key: Int) {
        updateState {
            copy(
                keys = keys.toMutableList().apply {
                    add(key)
                }
            )
        }
    }

    fun removeKeyAt(index: Int) {
        updateState {
            copy(
                keys = keys.toMutableList().apply {
                    removeAt(index)
                }
            )
        }
    }
}