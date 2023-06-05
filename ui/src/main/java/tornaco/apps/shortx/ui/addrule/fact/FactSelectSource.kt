package tornaco.apps.shortx.ui.addrule.fact

import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import kotlin.reflect.KClass


sealed interface FactSelectSource {
    val clazz: KClass<out Fact>

    data class FromClass(override val clazz: KClass<out Fact>) : FactSelectSource
    data class FromFact(val fact: Fact) : FactSelectSource {
        override val clazz: KClass<out Fact> get() = fact::class
    }
}

fun <T : Fact> FactSelectSource.getFactAs(): T? {
    @Suppress("UNCHECKED_CAST")
    return (this as? FactSelectSource.FromFact)?.fact as T?
}