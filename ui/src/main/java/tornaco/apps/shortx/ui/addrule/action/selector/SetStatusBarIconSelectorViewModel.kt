package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class SetStatusBarIconSelectorState(
    val slot: String = "",
    val icon: String = Remix.Animals.dog,
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SetStatusBarIconSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<SetStatusBarIconSelectorState>(
        context,
        { SetStatusBarIconSelectorState() }) {

    fun initFrom(settings: Action.SetStatusBarIcon) {
        updateState {
            copy(slot = settings.slot, icon = settings.icon)
        }
    }

    fun updateSlot(slot: String) {
        updateState {
            copy(slot = slot)
        }
    }

    fun updateIcon(icon: String) {
        updateState {
            copy(icon = icon)
        }
    }
}