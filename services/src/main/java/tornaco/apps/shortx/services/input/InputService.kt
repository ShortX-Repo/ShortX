package tornaco.apps.shortx.services.input

import com.android.internal.view.IInputContext
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.android.ServiceRetriever
import tornaco.apps.shortx.services.watchdog.postSafety
import util.Reflections

class InputService(shortXContext: ShortXContext) : SystemService(shortXContext, "InputService") {
    private val imeService by lazy {
        ServiceRetriever(context.classLoader).getInputMethodManagerService()
    }

    private val _onClipboardListener = mutableListOf<(String) -> Unit>()

    fun addOnClipboardListener(listener: (String) -> Unit) {
        _onClipboardListener.add(listener)
    }

    fun onClipboardChange(text: String) {
        postSafety {
            _onClipboardListener.forEach {
                it(text)
            }
        }
    }

    fun submitText(text: String) {
        if (OsUtils.isUOrAbove()) {
            submitTextAndroid14(text)
        } else {
            submitTextAndroid14Below(text)
        }
    }

    private fun submitTextAndroid14Below(text: String) {
        logger.d("submitText, imeService: $imeService")
        @Suppress("UNCHECKED_CAST") val mCurInputContext =
            Reflections.getObjectField(imeService, "mCurInputContext")
        logger.d("submitText, mCurInputContext: $mCurInputContext")
        val inputContext: IInputContext =
            requireNotNull(mCurInputContext) { "No Input context. (no focused TextField?)" } as IInputContext
        logger.d("submitText, inputContext: $inputContext")
        InputContextCompat.commitText(inputContext, text)
    }

    // IRemoteInputConnection mCurInputConnection;
    // https://github.com/ProjectMatrixx/android_frameworks_base/commit/7165fae4a959efa5023f4a66d23e53de65b3ed02
    private fun submitTextAndroid14(text: String) {
        logger.d("submitText, imeService: $imeService")
        @Suppress("UNCHECKED_CAST") val mCurInputConnection =
            Reflections.getObjectField(imeService, "mCurInputConnection")
        logger.d("submitText, mCurInputConnection: $mCurInputConnection")
        val inputConnection =
            requireNotNull(mCurInputConnection) { "No Input context. (no focused TextField?)" }
        logger.d("submitText, inputConnection: $inputConnection")
        InputConnectionCompat.commitText(inputConnection, text)
    }
}