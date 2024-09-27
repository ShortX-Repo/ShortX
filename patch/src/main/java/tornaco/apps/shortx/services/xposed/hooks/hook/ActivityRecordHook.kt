package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.Intent
import android.os.IBinder
import android.os.UserHandle
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.platform.ActivityRecordUtils
import tornaco.apps.shortx.core.platform.Classes.activityRecordClass
import tornaco.apps.shortx.core.platform.Classes.activityStoppedMethod
import tornaco.apps.shortx.core.platform.Classes.amsClass
import tornaco.apps.shortx.core.platform.Classes.atmsClass
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService
import util.Reflections

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
        hookSetState(lpparam)
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
        val displayId = ActivityRecordUtils.getDisplayId(activityRecord)
        val intentUri = runCatching {
            intent.toUri(Intent.URI_INTENT_SCHEME)
        }.getOrElse {
            it.toString()
        }
        logger.i("activityResumedLocked, user: $uid, displayId: $displayId, intent: $intent, intentUri: $intentUri")

        val componentName = intent.component
        if (componentName == null) {
            logger.e("activityResumedLocked, can not get component from Intent: $intent")
            return
        }

        val taskId = ActivityRecordUtils.getTaskId(activityRecord)
        logger.d("activityResumedLocked, taskId: $taskId")

        ShortXService.shortXContext.appState.onActivityResumed(
            componentName, UserHandle.getUserId(uid), intentUri, taskId
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
            val displayId = ActivityRecordUtils.getDisplayId(activityRecord)
            logger.i("activityStopped, uid: $uid, displayId: $displayId, intent: $intent")

            val componentName = intent.component
            if (componentName == null) {
                logger.e("activityStopped, can not get component from Intent: $intent")
                return@afterMethod
            }

            val taskId = ActivityRecordUtils.getTaskId(activityRecord)
            logger.d("activityResumedLocked, taskId: $taskId")

            ShortXService.shortXContext.appState.onActivityStopped(
                componentName, UserHandle.getUserId(uid), taskId
            )
        }
    }.onFailure {
        logger.e(it, "hookActivityStopped")
    }

    private fun hookSetState(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        val activityRecordClazz = lpparam.classLoader.activityRecordClass()
        beforeMethod(
            activityRecordClazz,
            Methods.setState
        ) { param ->
            logger.d("SetState: ${param.args.map { it }}")
            if (param.args[0] == null) return@beforeMethod
            val state = State.valueOf(param.args[0].toString())
            logger.d("SetState, state: $state")
            val getState: Any =
                Reflections.callMethod(param.thisObject, "getState") ?: return@beforeMethod
            val currentState = State.valueOf(getState.toString())
            logger.d("SetState, currentState: $state")

            if (currentState != State.DESTROYED && state == State.DESTROYED) {
                val activityRecord = param.thisObject
                val intent = ActivityRecordUtils.getIntent(activityRecord)
                val uid = ActivityRecordUtils.getUid(activityRecord)
                if (intent == null || uid < 0) {
                    logger.e("destroyed, can not get intent or user for activityRecord: $activityRecord")
                    return@beforeMethod
                }
                val displayId = ActivityRecordUtils.getDisplayId(activityRecord)
                logger.i("destroyed, uid: $uid, displayId: $displayId, intent: $intent")

                val componentName = intent.component
                if (componentName == null) {
                    logger.e("destroyed, can not get component from Intent: $intent")
                    return@beforeMethod
                }

                val taskId = ActivityRecordUtils.getTaskId(activityRecord)
                logger.d("destroyed, taskId: $taskId")

                ShortXService.shortXContext.appState.onActivityDestroyed(
                    componentName, UserHandle.getUserId(uid), taskId
                )
            }
        }
    }.onFailure {
        logger.e(it, "hookActivityStopped")
    }

}

@DoNotStrip
internal enum class State {
    INITIALIZING,
    STARTED,
    RESUMED,
    PAUSING,
    PAUSED,
    STOPPING,
    STOPPED,
    FINISHING,
    DESTROYING,
    DESTROYED,
    RESTARTING_PROCESS

    // Android 9 http://aospxref.com/android-9.0.0_r61/xref/frameworks/base/services/core/java/com/android/server/am/ActivityStack.java?fi=ActivityState#ActivityState
    //     enum ActivityState {
    //244          INITIALIZING,
    //245          RESUMED,
    //246          PAUSING,
    //247          PAUSED,
    //248          STOPPING,
    //249          STOPPED,
    //250          FINISHING,
    //251          DESTROYING,
    //252          DESTROYED
    //253      }
}
