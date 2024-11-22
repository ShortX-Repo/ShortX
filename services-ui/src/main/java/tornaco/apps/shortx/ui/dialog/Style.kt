package tornaco.apps.shortx.ui.dialog

import androidx.compose.ui.text.TextStyle
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings

fun TextStyle.applyStyle(style: DialogUiStyleSettings): TextStyle {
    return if (style.fontScale > 0f) {
        this.copy(fontSize = this.fontSize * style.fontScale)
    } else {
        this
    }
}