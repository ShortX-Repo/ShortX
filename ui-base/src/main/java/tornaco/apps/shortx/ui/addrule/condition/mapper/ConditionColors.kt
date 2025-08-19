package tornaco.apps.shortx.ui.addrule.condition.mapper

import androidx.compose.ui.graphics.Color
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorPalette
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.allConditionClasses
import kotlin.reflect.KClass


fun colorForCondition(condition: KClass<out Condition>): Color {
    val index =
        runCatching { allConditionClasses.indexOf(condition) % ConditionColorPalettes.size }.getOrElse { 0 }
    return ConditionColorPalettes[index]
}

val ConditionColorPalettes = ActionColorPalette
