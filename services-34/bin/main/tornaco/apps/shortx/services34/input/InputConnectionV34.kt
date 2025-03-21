package tornaco.apps.shortx.services34.input

import com.android.internal.inputmethod.IRemoteInputConnection
import com.android.internal.inputmethod.InputConnectionCommandHeader

object InputConnectionV34 {
    fun commitText(input: Any, text: String) {
        (input as IRemoteInputConnection).commitText(
            InputConnectionCommandHeader(0),
            text,
            1
        )
    }

    fun performContextMenuAction(input: Any, action: Int) {
        (input as IRemoteInputConnection).performContextMenuAction(
            InputConnectionCommandHeader(0),
            action
        )
    }
}