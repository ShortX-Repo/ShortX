package tornaco.apps.shortx.services.input

import com.android.internal.view.IInputContext
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.android.ServiceRetriever
import tornaco.apps.shortx.services33.InputMethodManagerV33
import util.Reflections

class InputService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val imeService by lazy {
        ServiceRetriever(context.classLoader).getInputMethodManagerService()
    }

    fun submitText(text: String) {
        logger.d("submitText, imeService: $imeService")
        @Suppress("UNCHECKED_CAST") val mCurFocusedWindowClient =
            Reflections.getObjectField(imeService, "mCurFocusedWindowClient")
        logger.d("submitText, mCurFocusedWindowClient: $mCurFocusedWindowClient")
        val inputContext: IInputContext =
            Reflections.getObjectField(mCurFocusedWindowClient, "inputContext") as IInputContext
        logger.d("submitText, inputContext: $inputContext")
        InputMethodManagerV33().commitText(inputContext, text)
    }
}