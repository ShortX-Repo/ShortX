package tornaco.apps.shortx.ui.addrule.fact.mapper

import androidx.compose.ui.graphics.Color
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorPalette
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.allFactClasses
import kotlin.reflect.KClass


fun colorForFact(fact: KClass<out Fact>): Color {
    val index =
        runCatching { allFactClasses.indexOf(fact) % FactColorPalettes.size }.getOrElse { 0 }
    return FactColorPalettes[index]
}

val FactColorPalettes = ActionColorPalette