package tornaco.apps.shortx.services.input

import com.android.internal.view.IInputContext
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.android.ServiceRetriever
import util.Reflections

class InputService(shortXContext: ShortXContext) : SystemService(shortXContext, "InputService") {
    private val imeService by lazy {
        ServiceRetriever(context.classLoader).getInputMethodManagerService()
    }

    fun submitText(text: String) {
        logger.d("submitText, imeService: $imeService")
        @Suppress("UNCHECKED_CAST") val mCurInputContext =
            Reflections.getObjectField(imeService, "mCurInputContext")
        logger.d("submitText, mCurInputContext: $mCurInputContext")
        val inputContext: IInputContext =
            requireNotNull(mCurInputContext) { "No Input context." } as IInputContext
        logger.d("submitText, inputContext: $inputContext")
        InputContextCompat.commitText(inputContext, text)
    }
}