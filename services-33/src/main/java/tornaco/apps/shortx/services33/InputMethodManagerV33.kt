package tornaco.apps.shortx.services33

import com.android.internal.inputmethod.InputConnectionCommandHeader
import com.android.internal.view.IInputContext

class InputMethodManagerV33 {
    fun commitText(input: IInputContext, text: String) {
        input.commitText(
            InputConnectionCommandHeader(0),
            text,
            1
        )
    }
}