package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.Intent
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.platform.Classes.activityStackSupervisorClass
import tornaco.apps.shortx.core.platform.Classes.amsClass
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.RecentTask
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.Reflections
import util.ReflectionsExt

object ActivityTaskSupervisorHook {
    private val logger = Logger("ShortXHook.ActivityTaskSupervisorHook")

    fun hookActivityTaskSupervisor(lpparam: ISystemServerLoaded.Param) {
        hookCleanUpTask(lpparam)
        // Debug.
        if (BuildPropInternal.APP_IS_DEBUG) {
            hookRealStartActivity(lpparam)
        }
    }

    // Observed: happens on the first time starting an Activity.
    private fun hookRealStartActivity(lpparam: ISystemServerLoaded.Param) {
        kotlin.runCatching {
            val methodName = "realStartActivityLocked"
            afterMethod(
                lpparam.classLoader.activityStackSupervisorClass(methodName),
                methodName
            ) { param ->
                logger.d("realStartActivityLocked: ${param.args.map { it }}")
            }
        }.onFailure {
            logger.e(it, "hookRealStartActivity")
        }
    }

    private fun hookCleanUpTask(lpparam: ISystemServerLoaded.Param) {
        kotlin.runCatching {
            if (OsUtils.isUOrAbove()) {
                afterMethod(
                    lpparam.classLoader.activityStackSupervisorClass(Methods.cleanUpRemovedTask),
                    Methods.cleanUpRemovedTask
                ) {
                    handleTaskRemoved(it)
                }
            } else if (OsUtils.isOOrAbove()) {
                afterMethod(
                    lpparam.classLoader.activityStackSupervisorClass(Methods.cleanUpRemovedTaskLocked),
                    Methods.cleanUpRemovedTaskLocked
                ) {
                    handleTaskRemoved(it)
                }
            } else {
                afterMethod(lpparam.classLoader.amsClass(), Methods.cleanUpRemovedTaskLocked) {
                    handleTaskRemoved(it)
                }
            }
        }.onFailure {
            logger.e(it, "hookActivityTaskSupervisor")
        }
    }

    private fun handleTaskRemoved(param: ShortXHookParam) {
        // void cleanUpRemovedTaskLocked(TaskRecord tr, boolean killProcess, boolean removeFromRecents)
        logger.d("cleanUpRemovedTaskLocked: ${param.args}")
        val recentTask = requireNotNull(param.args[0]).RecentTask()
        ShortXService.shortXContext.appState.onTaskRemoved(recentTask)
    }

    private fun Any.RecentTask(): RecentTask {
        val intent = Reflections.getObjectField(this, "intent") as Intent
        // TaskRecord class has merge with Task class since R.
        val userId = ReflectionsExt.getIntFieldWithPotentialNames(this, "mUserId", "userId")
        val taskId = ReflectionsExt.getIntFieldWithPotentialNames(this, "mTaskId", "taskId")

        val pkgName = PkgUtils.packageNameOf(intent)
        val owner = AppPkg.newBuilder().setPkgName(pkgName).setUserId(userId)

        return RecentTask.newBuilder().setTaskId(taskId).setOwner(owner).build()
    }
}
