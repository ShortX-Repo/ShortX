package tornaco.apps.shortx.ui.main.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Locale

val DefaultInstructionIconColor: ULong = Color.Red.value
const val DynamicMonetInstructionIconColor = "monet"

fun String.instructionIconColorOrNull(): ULong? = takeIf { it.isNotBlank() }?.let { value ->
    if (value == DynamicMonetInstructionIconColor) return null

    val argb = value.removePrefix("#")
        .toLongOrNull(16)
        ?.toInt()
        ?: value.toLongOrNull()
            ?.toInt()
    argb?.let { Color(it).value }
}

fun String.isDynamicMonetInstructionIconColor(): Boolean = this == DynamicMonetInstructionIconColor

fun ULong.toInstructionIconColorProto(): String = "#%08X".format(Locale.US, Color(this).toArgb())
