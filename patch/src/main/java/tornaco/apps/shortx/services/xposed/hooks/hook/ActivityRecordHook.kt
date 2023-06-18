package tornaco.apps.shortx.services.xposed.hooks.hook

import android.os.IBinder
import android.os.UserHandle
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.ActivityRecordUtils
import tornaco.apps.shortx.core.platform.Classes.activityRecordClass
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.services.ShortXService

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
        val uid = ActivityRecordUtils.getUid(activityRecord)
        if (intent == null || uid < 0) {
            logger.e("activityResumedLocked, can not get intent or uid for activityRecord: $activityRecord")
            return@afterMethod
        }
        logger.i("activityResumedLocked, user: $uid, intent: $intent")

        val componentName = intent.component
        if (componentName == null) {
            logger.e("activityResumedLocked, can not get component from Intent: $intent")
            return@afterMethod
        }
        ShortXService.shortXContext.appState.onActivityResumed(
            componentName, UserHandle.getUserId(uid)
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
        val uid = ActivityRecordUtils.getUid(activityRecord)
        if (intent == null || uid < 0) {
            logger.e("activityStopped, can not get intent or user for activityRecord: $activityRecord")
            return@afterMethod
        }
        logger.i("activityStopped, uid: $uid, intent: $intent")

        val componentName = intent.component
        if (componentName == null) {
            logger.e("activityStopped, can not get component from Intent: $intent")
            return@afterMethod
        }
        ShortXService.shortXContext.appState.onActivityStopped(
            componentName, UserHandle.getUserId(uid)
        )
    }
}

