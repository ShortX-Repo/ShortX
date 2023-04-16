package tornaco.apps.shortx.ui.addrule

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextDataMapping.accessExample
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.ColoredIcon
import tornaco.apps.shortx.ui.base.RemixIcon
import kotlin.reflect.KClass

val logger = Logger("ContextData")

data class ContextData(
    val key: String, val description: String, val accessExample: String
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
        Fact.AppBecomeBackground::class -> {
            dats<ContextDataMapping.AppBecomeBg>()
        }
        Fact.AppBecomeForeground::class -> {
            dats<ContextDataMapping.AppBecomeFg>()
        }
        Fact.ActivityStarted::class -> {
            dats<ContextDataMapping.ActivityStarted>()
        }
        Fact.AnyActivityStarted::class -> {
            dats<ContextDataMapping.ActivityStarted>()
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

@Composable
fun ContextDataIcon() {
    ColoredIcon(size = 24.dp, color = MaterialTheme.colorScheme.surface) {
        RemixIcon(modifier = Modifier.size(16.dp), remixName = Remix.Document.file_list_line)
    }
}