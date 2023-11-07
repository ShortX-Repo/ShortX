package tornaco.apps.shortx.services33.input

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
}