package tornaco.apps.shortx.ui.addrule.action.mapper

import androidx.compose.ui.graphics.Color
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.allActionClasses
import kotlin.reflect.KClass


fun colorForAction(action: KClass<out Action>): Color {
    val index =
        runCatching { allActionClasses.indexOf(action) % ActionColorPalette.size }.getOrElse { 0 }
    return ActionColorPalette[index]
}

fun randomActionColor() = ActionColorPalette[(ActionColorPalette.indices).random()]