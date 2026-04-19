package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.action.OcrDetectOutputType
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class OCRSelectorState(
    val id: String = defaultNewActionId(),
    val src: Action.OcrDetect.RectSrc = Action.OcrDetect.RectSrc.SelectScreenArea,
    val outputType: OcrDetectOutputType = OcrDetectOutputType.OcrDetectOutputType_Json,
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class OCRSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<OCRSelectorState>(
        context, {
            OCRSelectorState()
        }
    ) {


    fun initFrom(settings: Action.OcrDetect?) {
        logger.d("initFrom: $settings")
        settings?.let {
            updateState {
                copy(
                    id = it.id,
                    src = it.src,
                    outputType = it.outputType,
                )
            }
        }
    }

    fun setSrc(src: Action.OcrDetect.RectSrc) {
        updateState {
            copy(src = src)
        }
    }

    fun setOutputType(outputType: OcrDetectOutputType) {
        updateState {
            copy(outputType = outputType)
        }
    }

    fun getAction(): Action.OcrDetect {
        return Action.OcrDetect(
            src = state.value.src,
            id = state.value.id,
            outputType = state.value.outputType,
        )
    }
}