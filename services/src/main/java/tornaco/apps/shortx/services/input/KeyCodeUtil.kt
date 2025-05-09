package tornaco.apps.shortx.services.input

import android.view.KeyEvent

object KeyCodeUtil {

    private val keyCodeMap: Map<Int, String> by lazy {
        KeyEvent::class.java.fields
            .filter { it.name.startsWith("KEYCODE_") && it.type == Int::class.javaPrimitiveType }
            .associate { field ->
                field.getInt(null) to field.name.removePrefix("KEYCODE_")
            }
    }

    fun keyCodeToString(keyCode: Int): String {
        return keyCodeMap[keyCode] ?: "UNKNOWN($keyCode)"
    }

    fun keyEventToCodeAndActionString(event: KeyEvent): String {
        return "KeyEvent-${keyCodeToString(event.keyCode)}-${event.action}"
    }
}
