package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.pm.ApplicationInfo
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.platform.Methods.removeLruProcessLocked
import tornaco.apps.shortx.core.proto.common.ProcessRecord
import tornaco.apps.shortx.core.toAppPkg
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.amsClass
import tornaco.apps.shortx.services.xposed.hooks.hook.Classes.processListClass
import util.Reflections
import util.ReflectionsExt

fun XposedEntry.hookProcessList(lpparam: XC_LoadPackage.LoadPackageParam) {
    if (OsUtils.isQOrAbove()) {
        afterMethod(lpparam.classLoader.processListClass(), removeLruProcessLocked) {
            handleRemoveLruProcessLocked(it)
        }
    } else {
        afterMethod(lpparam.classLoader.amsClass(), removeLruProcessLocked) {
            handleRemoveLruProcessLocked(it)
        }
    }
}

private fun XposedEntry.handleRemoveLruProcessLocked(param: XC_MethodHook.MethodHookParam) {
    // void removeLruProcessLocked(ProcessRecord app)
    val record = param.args[0].toProcessRecord()
    ShortXService.shortXContext.appState.onProcessRemoved(record)
}

private fun Any.toProcessRecord(): ProcessRecord {
    val applicationInfo = Reflections.getObjectField(this, "info") as ApplicationInfo
    val processName = Reflections.getObjectField(this, "processName") as String
    val pid = ReflectionsExt.getIntFieldWithPotentialNames(this, "pid", "mPid")
    val uid = ReflectionsExt.getIntFieldWithPotentialNames(this, "uid", "mUid")
    return ProcessRecord.newBuilder()
        .setOwner(applicationInfo.toAppPkg())
        .setPid(pid.toLong())
        .setUid(uid)
        .setProcessName(processName)
        .build()
}