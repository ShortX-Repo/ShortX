package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.common.WindowInsetType
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class ShowHideInsetsSelectorState(
    val id: String = defaultNewActionId(),
    val isHide: Boolean = true,
    val types: List<WindowInsetType> = listOf(WindowInsetType.WindowInsetType_StatusBar)
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ShowHideInsetsViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ShowHideInsetsSelectorState>(
        context, {
            ShowHideInsetsSelectorState()
        }
    ) {


    fun initFrom(settings: Action.ShowHideInsets?) {
        logger.d("initFrom: $settings")
        settings?.let {
            updateState {
                copy(
                    id = it.id,
                    isHide = it.isHide,
                    types = it.types
                )
            }
        }
    }

    fun addOrRemoveType(type: WindowInsetType, add: Boolean) {
        updateState {
            copy(types = types.toMutableList().apply {
                if (add) add(type) else remove(type)
            })
        }
    }

    fun setIsHide(hide: Boolean) {
        updateState {
            copy(isHide = hide)
        }
    }
}