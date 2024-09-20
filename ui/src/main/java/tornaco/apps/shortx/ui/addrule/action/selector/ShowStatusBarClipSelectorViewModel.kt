package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowStatusBarClipSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<Action.ShowStatusBarClip>(context, {
        Action.ShowStatusBarClip(text = "", icon = null)
    }) {

    fun initFrom(settings: Action.ShowStatusBarClip) {
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