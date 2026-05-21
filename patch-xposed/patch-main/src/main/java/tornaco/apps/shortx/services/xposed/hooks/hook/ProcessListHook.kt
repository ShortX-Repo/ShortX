package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.pm.ApplicationInfo
import tornaco.apps.shortx.core.platform.Classes.amsClass
import tornaco.apps.shortx.core.platform.Classes.processListClass
import tornaco.apps.shortx.core.platform.Methods.handleProcessStartedLocked
import tornaco.apps.shortx.core.platform.Methods.removeLruProcessLocked
import tornaco.apps.shortx.core.proto.common.ProcessRecord
import tornaco.apps.shortx.core.toAppPkg
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.Reflections
import util.ReflectionsExt

object ProcessListHook {
    private val logger = Logger("ShortXHook.ProcessListHook")

    fun hookProcessListRemoveProcess(lpparam: ISystemServerLoaded.Param) =
        kotlin.runCatching {
            if (OsUtils.isQOrAbove()) {
                afterMethod(lpparam.classLoader.processListClass(), removeLruProcessLocked) {
                    handleRemoveLruProcessLocked(it)
                }
            } else {
                afterMethod(lpparam.classLoader.amsClass(), removeLruProcessLocked) {
                    handleRemoveLruProcessLocked(it)
                }
            }
        }.getOrElse {
            logger.e(it, "hookProcessList")
        }

    fun hookProcessListOnProcessStarted(lpparam: ISystemServerLoaded.Param) =
        kotlin.runCatching {
            if (OsUtils.isQOrAbove()) {
                val method = ReflectionsExt.findMethodWithMostArgs(
                    lpparam.classLoader.processListClass(),
                    handleProcessStartedLocked
                )
                logger.i("hookProcessListOnProcessStarted, method: $method")
                afterMethod(method) {
                    handleProcessStartedLocked(it)
                }
            } else {
                // TODO
            }
        }.getOrElse {
            logger.e(it, "hookProcessListOnProcessStarted")
        }

    private fun handleProcessStartedLocked(param: ShortXHookParam) {
        // boolean handleProcessStartedLocked(ProcessRecord app, int pid, boolean usingWrapper, long expectedStartSeq, boolean procAttached)
        val record = requireNotNull(param.args[0]).toProcessRecord()
        ShortXService.shortXContext.appState.onProcessStarted(record)
    }

    private fun handleRemoveLruProcessLocked(param: ShortXHookParam) {
        // void removeLruProcessLocked(ProcessRecord app)
        val record = requireNotNull(param.args[0]).toProcessRecord()
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
}
