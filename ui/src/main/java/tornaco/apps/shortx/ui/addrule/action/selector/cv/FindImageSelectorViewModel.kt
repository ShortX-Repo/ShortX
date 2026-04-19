package tornaco.apps.shortx.ui.addrule.action.selector.cv

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.action.FindImagePositionOutputType
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.ImageSource
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class FindImageSelectorState(
    val id: String = defaultNewActionId(),
    val image: ImageSource = ImageSource.Screenshot,
    val template: ImageSource = ImageSource.NA,
    val outputType: FindImagePositionOutputType =
        FindImagePositionOutputType.FindImagePositionOutputType_Text,
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
                    outputType = it.outputType,
                )
            }
        }
    }

    fun setImage(src: ImageSource) {
        updateState {
            copy(image = src)
        }
    }

    fun setTemplate(src: ImageSource) {
        updateState {
            copy(template = src)
        }
    }

    fun setOutputType(outputType: FindImagePositionOutputType) {
        updateState {
            copy(outputType = outputType)
        }
    }

    fun toActionOrNull(): Action.FindImagePosition? {
        return state.value.template.takeIf { it != ImageSource.NA }
            ?.let {
                Action.FindImagePosition(
                    image = state.value.image,
                    template = it,
                    id = state.value.id,
                    outputType = state.value.outputType,
                )
            }
    }
}