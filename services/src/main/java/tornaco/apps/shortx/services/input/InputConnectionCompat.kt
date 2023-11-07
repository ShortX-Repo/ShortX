package tornaco.apps.shortx.services.input

import tornaco.apps.shortx.services33.input.InputConnectionV34

object InputConnectionCompat {
    fun commitText(input: Any, text: String) {
        InputConnectionV34.commitText(input, text)
    }
}