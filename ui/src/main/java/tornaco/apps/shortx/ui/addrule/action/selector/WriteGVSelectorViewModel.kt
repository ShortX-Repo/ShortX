package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject

fun Action.WriteGV.isValid() = varName.isNotEmpty()

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class WriteGVSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<Action.WriteGV>(
        context,
        Action.WriteGV(valueAsString = "", varName = "")
    ) {

    fun selectGlobalVar(name: String) {
        updateState {
            copy(varName = name)
        }
    }

    fun inputValue(value: String) {
        updateState {
            copy(valueAsString = value)
        }
    }

}