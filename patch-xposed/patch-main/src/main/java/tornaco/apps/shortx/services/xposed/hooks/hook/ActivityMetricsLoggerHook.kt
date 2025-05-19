package tornaco.apps.shortx.services.xposed.hooks.hook

import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.platform.Classes.activityMetricsLoggerClass
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.ReflectionsExt

object ActivityMetricsLoggerHook {
    private val logger = Logger("ShortXHook.ActivityMetricsLoggerHook")

    fun hook(lpparam: ISystemServerLoaded.Param) {
        if (BuildPropInternal.APP_IS_DEBUG) {
            hookNotifyActivityLaunched(lpparam)
        }
    }

    private fun hookNotifyActivityLaunched(lpparam: ISystemServerLoaded.Param) =
        kotlin.runCatching {
            val method = ReflectionsExt.findMethodWithMostArgs(
                lpparam.classLoader.activityMetricsLoggerClass(),
                "notifyActivityLaunched"
            )
            val arIndex = ReflectionsExt.getFirstArgIndexLikeTypeForMethod(
                method,
                "ActivityRecord",
            )
            if (arIndex < 0) {
                logger.e("Can not find ActivityRecord index: $method")
                return@runCatching
            }

            logger.i("notifyActivityLaunched: $method arIndex: $arIndex")
            afterMethod(method) {
                // Android 14
                // void notifyActivityLaunched(@NonNull LaunchingState launchingState, int resultCode,
                //            boolean newActivityCreated, @Nullable ActivityRecord launchedActivity,
                //            @Nullable ActivityOptions options)

                // Android 10
                // private void notifyActivityLaunched(int resultCode, ActivityRecord launchedActivity,
                //371              boolean processRunning, boolean processSwitch

                val ar = it.args[arIndex]
                logger.d("ActivityMetricsLogger notifyActivityLaunched ---> $ar")
            }
        }
}

