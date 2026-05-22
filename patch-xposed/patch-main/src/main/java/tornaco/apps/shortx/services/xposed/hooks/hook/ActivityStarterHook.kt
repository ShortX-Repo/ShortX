package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.os.IBinder
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.platform.Classes.activityStarterClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.Reflections
import util.ReflectionsExt

object ActivityStarterHook {
    private val logger = Logger("ShortXHook.ActivityStarterHook")

    fun hookEnqueueRequest(lpparam: ISystemServerLoaded.Param) {
        if (OsUtils.isROrAbove()) {
            if (BuildPropInternal.APP_IS_DEBUG) {
                hookEnqueueRequest_30OrAbove(lpparam)
            }
        }
    }

    // Min API level 30
    private fun hookEnqueueRequest_30OrAbove(lpparam: ISystemServerLoaded.Param) =
        kotlin.runCatching {
            val executeRequest = ReflectionsExt.findMethodWithMostArgs(
                lpparam.classLoader.activityStarterClass(),
                "executeRequest"
            )
            logger.i("executeRequest: $executeRequest")
            afterMethod(executeRequest) {
                val request = requireNotNull(it.args[0])
                val intent = Reflections.getObjectField(request, "intent") as Intent?
                val userId = Reflections.getObjectField(request, "userId") as Int
                // final ActivityRecord caller = ActivityRecord.forTokenLocked(mRequest.resultTo);
                val resultTo = Reflections.getObjectField(request, "resultTo") as IBinder?
                logger.d("ActivityStarter executeRequest ---> $intent")
                maybeEnablePackageOnLaunch(request, true)
            }
        }

    private fun maybeEnablePackageOnLaunch(
        request: Any,
        autoEnable: Boolean
    ) {
        // Fix suspend state.
        // ActivityInfo activityInfo;
        // ResolveInfo resolveInfo;
        val activityInfo = Reflections.getObjectField(request, "activityInfo") as ActivityInfo?
        logger.d("ActivityStarter executeRequest, activityInfo: $activityInfo")
        // This is ap is in suspend state.
        if (activityInfo?.applicationInfo != null) {
            logger.d("ActivityStarter executeRequest, flags: " + activityInfo.applicationInfo.flags)
            val suspended =
                activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SUSPENDED != 0
            logger.d("ActivityStarter executeRequest, suspended: $suspended")
            if (suspended && autoEnable) {
                // Remove suspend flags
                activityInfo.applicationInfo.flags =
                    activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SUSPENDED.inv()
                logger.i("ActivityStarter executeRequest, Remove suspend flags to: " + activityInfo.applicationInfo.flags)
            }
        }
    }
}
