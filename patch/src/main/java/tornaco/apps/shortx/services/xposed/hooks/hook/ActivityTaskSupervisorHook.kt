package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.RecentTask
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.activityStackSupervisorClass
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.amsClass
import util.Reflections
import util.ReflectionsExt

fun XposedEntry.hookActivityTaskSupervisor(lpparam: XC_LoadPackage.LoadPackageParam) {
    if (OsUtils.isOOrAbove()) {
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
}

private fun XposedEntry.handleTaskRemoved(param: XC_MethodHook.MethodHookParam) {
    // void cleanUpRemovedTaskLocked(TaskRecord tr, boolean killProcess, boolean removeFromRecents)
    logger.d("cleanUpRemovedTaskLocked: ${param.args}")
    val recentTask = param.args[0].RecentTask()
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