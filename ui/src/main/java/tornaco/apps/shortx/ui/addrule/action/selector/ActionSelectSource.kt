package tornaco.apps.shortx.ui.addrule.action.selector

import tornaco.apps.shortx.ui.addrule.action.model.Action
import kotlin.reflect.KClass


sealed interface ActionSelectSource {
    val clazz: KClass<out Action>

    data class FromClass(override val clazz: KClass<out Action>) : ActionSelectSource
    data class FromAction(val action: Action) : ActionSelectSource {
        override val clazz: KClass<out Action> get() = action::class
    }
}

fun <T : Action> ActionSelectSource.getActionAs(): T? {
    @Suppress("UNCHECKED_CAST")
    return (this as? ActionSelectSource.FromAction)?.action as T?
}