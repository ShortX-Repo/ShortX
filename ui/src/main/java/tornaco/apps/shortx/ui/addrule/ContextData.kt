package tornaco.apps.shortx.ui.addrule

import android.os.Parcelable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.proto.common.CustomContextDataKey
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.RemixIcon
import kotlin.reflect.KClass

val logger = Logger("ContextData")

@Parcelize
data class ContextData(
    val key: String, val description: String, val customKey: String?
) : Parcelable {
    val selectedKey: String get() = customKey.fallbackOnEmpty(key)
}

fun List<ContextData>.toCustomContextDataKey(): CustomContextDataKey {
    return CustomContextDataKey.newBuilder()
        .addAllKeys(
            this@toCustomContextDataKey.mapNotNull { ctx ->
                ctx.customKey?.let { customKey ->
                    (ctx.key to customKey).toStringPair()
                }
            }
        )
        .build()
}

inline fun <reified T : Enum<T>> dats(customContextDataKey: CustomContextDataKey): List<ContextData> =
    runCatching {
        val values: Array<T> = enumValues()
        val descriptionField =
            values.first().javaClass.getDeclaredField("description").apply { isAccessible = true }
        values.map {
            ContextData(
                key = it.name,
                description = descriptionField.get(it) as String,
                customKey = customContextDataKey.forKey(it.name)
            )
        }
    }.getOrElse {
        logger.e(it, "get dat error")
        emptyList()
    }

fun KClass<out Fact>.factContextData(customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance()): List<ContextData> {
    return when (this) {
        Fact.NotificationPosted::class -> {
            dats<ContextDataMapping.Notification>(customContextDataKey)
        }

        Fact.NotificationUpdated::class -> {
            dats<ContextDataMapping.Notification>(customContextDataKey)
        }

        Fact.NotificationRemoved::class -> {
            dats<ContextDataMapping.Notification>(customContextDataKey)
        }

        Fact.AppBecomeBackground::class -> {
            dats<ContextDataMapping.AppBecomeBg>(customContextDataKey)
        }

        Fact.AppBecomeForeground::class -> {
            dats<ContextDataMapping.AppBecomeFg>(customContextDataKey)
        }

        Fact.ActivityStarted::class -> {
            dats<ContextDataMapping.ActivityStarted>(customContextDataKey)
        }

        Fact.ActivityStopped::class -> {
            dats<ContextDataMapping.ActivityStopped>(customContextDataKey)
        }

        Fact.AnyActivityStarted::class -> {
            dats<ContextDataMapping.ActivityStarted>(customContextDataKey)
        }

        Fact.TaskRemoved::class, Fact.TaskRemovedAny::class -> {
            dats<ContextDataMapping.TaskRemoved>(customContextDataKey)
        }

        Fact.AppProcessStarted::class -> {
            dats<ContextDataMapping.AppProcessStarted>(customContextDataKey)
        }

        Fact.PkgStopRunning::class, Fact.PkgStopRunningAny::class -> {
            dats<ContextDataMapping.PkgStopRunning>(customContextDataKey)
        }

        Fact.AppAdded::class -> {
            dats<ContextDataMapping.AppAdded>(customContextDataKey)
        }

        Fact.AppUpdated::class -> {
            dats<ContextDataMapping.AppUpdated>(customContextDataKey)
        }

        Fact.AppRemoved::class -> {
            dats<ContextDataMapping.AppRemoved>(customContextDataKey)
        }

        Fact.AudioFocusChanged::class -> {
            dats<ContextDataMapping.AudioFocusChanged>(customContextDataKey)
        }

        Fact.ConnectedWifiSignalLevelChanged::class -> {
            dats<ContextDataMapping.ConnectedWifiSignalLevelChanged>(customContextDataKey)
        }

        Fact.WifiConnectedTo::class -> {
            dats<ContextDataMapping.WifiConnectedTo>(customContextDataKey)
        }

        Fact.WifiDisconnectedFrom::class -> {
            dats<ContextDataMapping.WifiDisconnectedFrom>(customContextDataKey)
        }

        Fact.BTConnectedTo::class -> {
            dats<ContextDataMapping.BTDeviceConnected>(customContextDataKey)
        }

        Fact.BTDisconnectedFrom::class -> {
            dats<ContextDataMapping.BTDeviceDisConnected>(customContextDataKey)
        }

        Fact.BatteryTemperatureChanged::class -> {
            dats<ContextDataMapping.BatteryTemperatureChanged>(customContextDataKey)
        }

        Fact.BatteryLevelChanged::class -> {
            dats<ContextDataMapping.BatteryLevelChanged>(customContextDataKey)
        }

        Fact.MediaStoreInsert::class -> {
            dats<ContextDataMapping.MediaStoreInsert>(customContextDataKey)
        }

        Fact.AdvancedKeyEvent::class -> {
            dats<ContextDataMapping.AdvancedKeyEvent>(customContextDataKey)
        }

        Fact.OnStartOp::class -> {
            dats<ContextDataMapping.OnStartOp>(customContextDataKey)
        }

        Fact.OnFinishOp::class -> {
            dats<ContextDataMapping.OnFinishOp>(customContextDataKey)
        }

        Fact.SystemSettingsChanged::class -> {
            dats<ContextDataMapping.SystemSettingsChanged>(customContextDataKey)
        }

        Fact.CallStateChanged::class -> {
            dats<ContextDataMapping.CallStateChanged>(customContextDataKey)
        }

        Fact.ClipboardContentChanged::class -> {
            dats<ContextDataMapping.ClipboardContentChanged>(customContextDataKey)
        }

        else -> {
            emptyList()
        }
    }
}

fun KClass<out Condition>.conditionContextData(customContextDataKey: CustomContextDataKey = CustomContextDataKey.getDefaultInstance()): List<ContextData> {
    return when (this) {
        Condition.AppHasNotification::class -> {
            dats<ContextDataMapping.AppHasNotification>(customContextDataKey)
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