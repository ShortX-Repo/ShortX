package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Classes.activityStarterClass
import tornaco.apps.shortx.core.util.Logger
import util.Reflections
import util.ReflectionsExt

// Currently Not Used.
object ActivityStarterHook {
    private val logger = Logger("ShortXHook.ActivityStarterHook")

    fun hookEnqueueRequest(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        val executeRequest = ReflectionsExt.findMethodWithMostArgs(
            lpparam.classLoader.activityStarterClass(),
            "executeRequest"
        )
        logger.i("executeRequest: $executeRequest")
        afterMethod(executeRequest) {
            val request: Any = it.args[0]
            val intent = Reflections.getObjectField(request, "intent") as Intent?
            val userId = Reflections.getObjectField(request, "userId") as Int
            // final ActivityRecord caller = ActivityRecord.forTokenLocked(mRequest.resultTo);
            val resultTo = Reflections.getObjectField(request, "resultTo") as IBinder?
            logger.d(
                "ActivityStarter executeRequest ---> ${
                    intent?.let { it1 -> formatIntentAsString(it1) }
                }"
            )
            logger.d(intent?.let { it1 -> convertIntentToAmStartCommand(it1) })
        }
    }
}

fun formatIntentAsString(intent: Intent): String {
    val extras: Bundle? = intent.extras
    val extrasString = extras?.let {
        val stringBuilder = StringBuilder()
        for (key in it.keySet()) {
            val value = it[key]
            stringBuilder.append("$key=$value, ")
        }
        stringBuilder.toString()
    } ?: ""

    return "Action: ${intent.action}, " +
            "Component: ${intent.component}, " +
            "Extras: {$extrasString}"
}

fun convertIntentToAmStartCommand(intent: Intent): String {
    val packageName = intent.component?.packageName
    val activityName = intent.component?.className
    val action = intent.action
    val dataUri = intent.data?.toString()
    val extras = intent.extras

    val commandBuilder = StringBuilder("am start")

    packageName?.let { commandBuilder.append(" -n $it/$activityName") }
    action?.let { commandBuilder.append(" -a $it") }
    dataUri?.let { commandBuilder.append(" -d $it") }

    extras?.let {
        for (key in it.keySet()) {
            val value = it.get(key)
            if (value is String) {
                commandBuilder.append(" --es $key $value")
            } else if (value is Int) {
                commandBuilder.append(" --ei $key $value")
            }
            // Handle other data types as needed
        }
    }

    return commandBuilder.toString()
}




