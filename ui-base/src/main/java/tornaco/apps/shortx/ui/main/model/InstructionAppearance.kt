package tornaco.apps.shortx.ui.main.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Locale

val DefaultInstructionIconColor: ULong = Color.Red.value

fun String.instructionIconColorOrNull(): ULong? = takeIf { it.isNotBlank() }?.let { value ->
    val argb = value.removePrefix("#")
        .toLongOrNull(16)
        ?.toInt()
        ?: value.toLongOrNull()
            ?.toInt()
    argb?.let { Color(it).value }
}

fun ULong.toInstructionIconColorProto(): String = "#%08X".format(Locale.US, Color(this).toArgb())
