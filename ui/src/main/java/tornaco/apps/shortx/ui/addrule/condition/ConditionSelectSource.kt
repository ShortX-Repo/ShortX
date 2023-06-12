package tornaco.apps.shortx.ui.addrule.condition

import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import kotlin.reflect.KClass


sealed interface ConditionSelectSource {
    val clazz: KClass<out Condition>

    data class FromClass(override val clazz: KClass<out Condition>) : ConditionSelectSource
    data class FromCondition(val condition: Condition) : ConditionSelectSource {
        override val clazz: KClass<out Condition> get() = condition::class
    }
}

fun <T : Condition> ConditionSelectSource.getConditionAs(): T? {
    @Suppress("UNCHECKED_CAST")
    return (this as? ConditionSelectSource.FromCondition)?.condition as T?
}