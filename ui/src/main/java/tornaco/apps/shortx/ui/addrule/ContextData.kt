package tornaco.apps.shortx.ui.addrule

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextDataMapping.accessExample
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.RemixIcon
import kotlin.reflect.KClass

val logger = Logger("ContextData")

data class ContextData(
    val key: String, val description: String, val accessExample: String
)

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
            dats<ContextDataMapping.Notification>()
        }

        Fact.NotificationUpdated::class -> {
            dats<ContextDataMapping.Notification>()
        }

        Fact.NotificationRemoved::class -> {
            dats<ContextDataMapping.Notification>()
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

        Fact.TaskRemoved::class, Fact.TaskRemovedAny::class -> {
            dats<ContextDataMapping.TaskRemoved>()
        }

        Fact.PkgStopRunning::class, Fact.PkgStopRunningAny::class -> {
            dats<ContextDataMapping.PkgStopRunning>()
        }

        Fact.AppAdded::class -> {
            dats<ContextDataMapping.AppAdded>()
        }

        Fact.AppUpdated::class -> {
            dats<ContextDataMapping.AppUpdated>()
        }

        Fact.AppRemoved::class -> {
            dats<ContextDataMapping.AppRemoved>()
        }

        Fact.AudioFocusChanged::class -> {
            dats<ContextDataMapping.AudioFocusChanged>()
        }

        Fact.WifiConnectedTo::class -> {
            dats<ContextDataMapping.WifiConnectedTo>()
        }

        Fact.WifiDisconnectedFrom::class -> {
            dats<ContextDataMapping.WifiDisconnectedFrom>()
        }

        Fact.BTConnectedTo::class -> {
            dats<ContextDataMapping.BTDeviceConnected>()
        }

        Fact.BTDisconnectedFrom::class -> {
            dats<ContextDataMapping.BTDeviceDisConnected>()
        }

        Fact.BatteryTemperatureChanged::class -> {
            dats<ContextDataMapping.BatteryTemperatureChanged>()
        }

        Fact.BatteryLevelChanged::class -> {
            dats<ContextDataMapping.BatteryLevelChanged>()
        }

        Fact.MediaStoreInsert::class -> {
            dats<ContextDataMapping.MediaStoreInsert>()
        }

        Fact.AdvancedKeyEvent::class -> {
            dats<ContextDataMapping.AdvancedKeyEvent>()
        }

        Fact.OnStartOp::class -> {
            dats<ContextDataMapping.OnStartOp>()
        }

        Fact.OnFinishOp::class -> {
            dats<ContextDataMapping.OnFinishOp>()
        }

        Fact.SystemSettingsChanged::class -> {
            dats<ContextDataMapping.SystemSettingsChanged>()
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

        Action.ReadClipboard::class -> {
            dats<ContextDataMapping.ReadClipboard>()
        }

        Action.ShellCommand::class -> {
            dats<ContextDataMapping.ShellCommand>()
        }

        Action.HttpRequest::class -> {
            dats<ContextDataMapping.HttpRequest>()
        }

        else -> {
            emptyList()
        }
    }
}

fun KClass<out Condition>.conditionContextData(): List<ContextData> {
    return when (this) {
        Condition.AppHasNotification::class -> {
            dats<ContextDataMapping.AppHasNotification>()
        }

        else -> {
            emptyList()
        }
    }
}

@Composable
fun ContextDataIcon() {
    RemixIcon(remixName = Remix.Document.file_list_line, modifier = Modifier.size(18.dp))
}