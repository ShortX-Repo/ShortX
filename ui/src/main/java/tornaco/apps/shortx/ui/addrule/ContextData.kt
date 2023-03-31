package tornaco.apps.shortx.ui.addrule

import tornaco.apps.shortx.core.ContextDataMapping
import tornaco.apps.shortx.core.ContextDataMapping.accessExample
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import kotlin.reflect.KClass

val logger = Logger("ContextData")

data class ContextData(
    val key: String,
    val description: String,
    val accessExample: String
)

@Suppress("UNCHECKED_CAST")
private inline fun <reified T : Enum<T>> dats(): List<ContextData> = runCatching {
    val values: Array<T> = enumValues()
    val descriptionField =
        values.first().javaClass.getDeclaredField("description").apply { isAccessible = true }
    values.map {
        ContextData(it.name, descriptionField.get(it) as String, it.accessExample())
    }
}.getOrElse {
    logger.e(it, "get dat error")
    emptyList()
}

fun KClass<out Fact>.factContextData(): List<ContextData> {
    return when (this) {
        Fact.NotificationPosted::class -> {
            dats<ContextDataMapping.NotificationPosted>()
        }
        Fact.NotificationUpdated::class -> {
            dats<ContextDataMapping.NotificationUpdated>()
        }
        Fact.NotificationRemoved::class -> {
            dats<ContextDataMapping.NotificationRemoved>()
        }
        else -> {
            emptyList()
        }
    }
}

fun KClass<out Action>.actionContextData(): List<ContextData> {
    return when (this) {
        Action.MVEL::class, Action.RemoteMVEL::class -> {
            dats<ContextDataMapping.ExecuteMVEL>()
        }
        else -> {
            emptyList()
        }
    }
}