package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject

fun Action.WriteGV.isValid() = varId.isNotEmpty() && varName.isNotEmpty()

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class WriteGVSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<Action.WriteGV>(
        context,
        Action.WriteGV(valueAsString = "", varId = "", varName = "")
    ) {

    fun selectGlobalVar(globalVar: GlobalVar) {
        updateState {
            copy(varId = globalVar.id, varName = globalVar.name)
        }
    }

    fun inputValue(value: String) {
        updateState {
            copy(valueAsString = value)
        }
    }

}