package tornaco.apps.shortx.services.input

import android.os.Build
import com.android.internal.view.IInputContext
import tornaco.apps.shortx.services33.input.InputContextV33

object InputContextCompat {
    fun commitText(input: IInputContext, text: String) {
        if (Build.VERSION.SDK_INT >= 33) {
            InputContextV33.commitText(input, text)
        } else {
            input.commitText(text, 1)
        }
    }
}