package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.util.*
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowDanmuSelectorViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ContextViewModel<Action.ShowDanmu>(context) {

    override fun initState(): Action.ShowDanmu {
        return Action.ShowDanmu(text = "", icon = null)
    }

    fun initFrom(settings: Action.ShowDanmu) {
        updateState(settings)
    }

    fun updateText(text: String) {
        updateState {
            copy(text = text)
        }
    }

    fun updateIcon(icon: String) {
        updateState {
            copy(icon = icon)
        }
    }
}