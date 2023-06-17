package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.Intent
import android.os.IBinder
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.activityRecordClass
import util.Reflections

fun XposedEntry.hookActivityResumed(lpparam: XC_LoadPackage.LoadPackageParam) {
    val activityRecordClazz = lpparam.classLoader.activityRecordClass()
    afterMethod(
        activityRecordClazz,
        Methods.activityResumedLocked
    ) { param ->
        //  final ActivityRecord r = ActivityRecord.forTokenLocked(token);
        val activityRecord =
            ActivityRecordUtils.forTokenLocked(param.args[0] as IBinder?, lpparam.classLoader)
        val intent = ActivityRecordUtils.getIntent(activityRecord)
        val userId = ActivityRecordUtils.getUid(activityRecord)
        logger.i("activityStopped, user: $userId, intent: $intent")
        if (intent == null || userId < 0) {
            logger.e("Can not get intent or user for activityRecord: $activityRecord")
            return@afterMethod
        }

        val componentName = intent.component
        if (componentName == null) {
            logger.e("Can not get component from Intent: $intent")
            return@afterMethod
        }
        ShortXService.shortXContext.appState.onActivityResumed(
            componentName, userId
        )
    }
}

fun XposedEntry.hookActivityStopped(lpparam: XC_LoadPackage.LoadPackageParam) {
    val activityRecordClazz = lpparam.classLoader.activityRecordClass()
    afterMethod(
        activityRecordClazz,
        Methods.activityStopped
    ) { param ->
        val activityRecord = param.thisObject
        val intent = ActivityRecordUtils.getIntent(activityRecord)
        val userId = ActivityRecordUtils.getUid(activityRecord)
        logger.i("activityStopped, user: $userId, intent: $intent")
        if (intent == null || userId < 0) {
            logger.e("Can not get intent or user for activityRecord: $activityRecord")
            return@afterMethod
        }
        val componentName = intent.component
        if (componentName == null) {
            logger.e("Can not get component from Intent: $intent")
            return@afterMethod
        }
        ShortXService.shortXContext.appState.onActivityStopped(
            componentName, userId
        )
    }
}


internal object ActivityRecordUtils {
    private val logger = Logger("ActivityRecordUtils")

    // final ActivityRecord r = ActivityRecord.forTokenLocked(token);
    fun forTokenLocked(token: IBinder?, classLoader: ClassLoader): Any? {
        return if (token == null) null else try {
            val clazz = classLoader.activityRecordClass()
            Reflections.callStaticMethod(
                clazz,
                "forTokenLocked",
                token
            )
        } catch (e: Throwable) {
            logger.e(e, "ActivityRecordUtils#forTokenLocked error")
            null
        }
    }

    fun getIntent(activityRecord: Any?): Intent? {
        return if (activityRecord == null) {
            null
        } else try {
            Reflections.getObjectField(activityRecord, "intent") as Intent
        } catch (e: Throwable) {
            logger.e(e, "ActivityRecordUtils#getIntent error")
            null
        }
    }

    fun getUid(activityRecord: Any?): Int {
        return if (activityRecord == null) {
            -1
        } else try {
            Reflections.callMethod(activityRecord, "getUid") as Int
        } catch (e: Throwable) {
            logger.e(e, "ActivityRecordUtils#getUid error")
            -1
        }
    }
}