package tornaco.apps.shortx.services33.input

import com.android.internal.inputmethod.InputConnectionCommandHeader
import com.android.internal.view.IInputContext

object InputContextV33 {
    fun commitText(input: IInputContext, text: String) {
        input.commitText(
            InputConnectionCommandHeader(0),
            text,
            1
        )
    }

    fun performContextMenuAction(input: IInputContext, action: Int) {
        input.performContextMenuAction(
            InputConnectionCommandHeader(0),
            action
        )
    }
}