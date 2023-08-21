package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.Intent
import android.os.IBinder
import android.os.UserHandle
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.ActivityRecordUtils
import tornaco.apps.shortx.core.platform.Classes.activityRecordClass
import tornaco.apps.shortx.core.platform.Classes.activityStoppedMethod
import tornaco.apps.shortx.core.platform.Classes.amsClass
import tornaco.apps.shortx.core.platform.Classes.atmsClass
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService

object ActivityRecordHook {
    private val logger = Logger("ShortXHook.ActivityRecordHook")

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        logger.d("hook")
        if (OsUtils.isSOrAbove()) {
            // Android 12+
            hookActivityResumedAndroid12_plus(lpparam)
        } else if (OsUtils.isQ() || OsUtils.isR()) {
            // Android10. Android11.
            hookActivityResumedAndroid10_11(lpparam)
        } else if (OsUtils.isPOrBelow()) {
            // Android8. Android 9
            hookActivityResumedAndroid9_(lpparam)
        }
        hookActivityStopped(lpparam)
    }

    private fun handleActivityResumed(
        param: XC_MethodHook.MethodHookParam,
        lpparam: XC_LoadPackage.LoadPackageParam
    ) {
        //  final ActivityRecord r = ActivityRecord.forTokenLocked(token);
        val activityRecord =
            ActivityRecordUtils.forTokenLocked(param.args[0] as IBinder?, lpparam.classLoader)
        val intent = ActivityRecordUtils.getIntent(activityRecord)
        val uid = ActivityRecordUtils.getUid(activityRecord)
        if (intent == null || uid < 0) {
            logger.e("activityResumedLocked, can not get intent or uid for activityRecord: $activityRecord")
            return
        }
        val intentUri = runCatching {
            intent.toUri(Intent.URI_INTENT_SCHEME)
        }.getOrElse {
            it.toString()
        }
        logger.i("activityResumedLocked, user: $uid, intent: $intent, intentUri: $intentUri")

        val componentName = intent.component
        if (componentName == null) {
            logger.e("activityResumedLocked, can not get component from Intent: $intent")
            return
        }
        ShortXService.shortXContext.appState.onActivityResumed(
            componentName, UserHandle.getUserId(uid), intentUri
        )
    }

    private fun hookActivityResumedAndroid12_plus(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            logger.d("hookActivityResumedAndroid12_plus")
            val activityRecordClazz = lpparam.classLoader.activityRecordClass()
            afterMethod(
                activityRecordClazz,
                Methods.activityResumedLocked
            ) { param ->
                handleActivityResumed(param, lpparam)
            }
        }.onFailure { logger.e(it, "hookActivityResumed") }

    private fun hookActivityResumedAndroid10_11(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            logger.d("hookActivityResumedAndroid10_11")
            val activityRecordClazz = lpparam.classLoader.atmsClass()
            afterMethod(
                activityRecordClazz,
                Methods.activityResumed
            ) { param ->
                handleActivityResumed(param, lpparam)
            }
        }.onFailure { logger.e(it, "hookActivityResumed") }


    private fun hookActivityResumedAndroid9_(lpparam: XC_LoadPackage.LoadPackageParam) =
        kotlin.runCatching {
            logger.d("hookActivityResumedAndroid9_")
            val activityRecordClazz = lpparam.classLoader.amsClass()
            afterMethod(
                activityRecordClazz,
                Methods.activityResumed
            ) { param ->
                handleActivityResumed(param, lpparam)
            }
        }.onFailure { logger.e(it, "hookActivityResumed") }

    private fun hookActivityStopped(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        val activityRecordClazz = lpparam.classLoader.activityRecordClass()
        val method = activityRecordClazz.activityStoppedMethod()
        logger.d("activityStoppedMethod: $method")
        afterMethod(
            requireNotNull(method) { "activityStoppedMethod not found." }
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
    }.onFailure {
        logger.e(it, "hookActivityStopped")
    }

}
