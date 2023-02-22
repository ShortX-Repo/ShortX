package tornaco.apps.shortx.services.rule.actor

import android.content.Context
import tornaco.apps.shortx.services.ShortXContext

class InputText(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val text: String,
) {
    operator fun invoke() {
        shortXContext.input.submitText(text)
    }
}