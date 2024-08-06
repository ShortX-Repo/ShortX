package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class FindImageSelectorState(
    val id: String = defaultNewActionId(),
    val image: Action.FindImagePosition.ImageSource = Action.FindImagePosition.ImageSource.Screenshot,
    val template: Action.FindImagePosition.ImageSource? = null
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class FindImageSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<FindImageSelectorState>(
        context, {
            FindImageSelectorState()
        }
    ) {


    fun initFrom(settings: Action.FindImagePosition?) {
        logger.d("initFrom: $settings")
        settings?.let {
            updateState {
                copy(
                    id = it.id,
                    image = it.image,
                    template = it.template,
                )
            }
        }
    }

    fun setImage(src: Action.FindImagePosition.ImageSource) {
        updateState {
            copy(image = src)
        }
    }

    fun setTemplate(src: Action.FindImagePosition.ImageSource) {
        updateState {
            copy(template = src)
        }
    }

    fun toActionOrNull(): Action.FindImagePosition? {
        return state.value.template?.let {
            Action.FindImagePosition(
                image = state.value.image,
                template = it,
                id = state.value.id
            )
        }
    }
}