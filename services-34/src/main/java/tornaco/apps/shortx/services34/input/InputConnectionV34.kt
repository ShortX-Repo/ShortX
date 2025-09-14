package tornaco.apps.shortx.services34.input

import com.android.internal.inputmethod.IRemoteInputConnection
import com.android.internal.inputmethod.InputConnectionCommandHeader
import tornaco.apps.shortx.core.util.Logger

object InputConnectionV34 {
    private const val INPUT_SESSION_ID_HIT_TIME = 20
    private val logger = Logger.nameless

    //  if (header.mSessionId != mCurrentSessionId.get()) {
    //562                  return;  // cancelled
    //563              }
    fun commitText(input: Any, text: String) {
        logger.d("commitText start.")
        for (i in 0 until INPUT_SESSION_ID_HIT_TIME) {
            (input as IRemoteInputConnection).commitText(
                InputConnectionCommandHeader(i),
                text,
                1
            )
        }
        logger.d("commitText done.")
    }

    fun performContextMenuAction(input: Any, action: Int) {
        logger.w("performContextMenuAction $action start.")
        for (i in 0 until INPUT_SESSION_ID_HIT_TIME) {
            (input as IRemoteInputConnection).performContextMenuAction(
                InputConnectionCommandHeader(i),
                action
            )
        }
        logger.w("performContextMenuAction $action done.")
    }
}