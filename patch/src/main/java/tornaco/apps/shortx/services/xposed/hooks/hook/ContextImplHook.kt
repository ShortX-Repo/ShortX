package tornaco.apps.shortx.services.xposed.hooks.hook

import android.app.ActivityThread
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Binder
import android.util.Log
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.audio.AudioService
import util.Reflections

object ContextImplHook {
    private val logger = Logger("ShortXHook.ContextImplHook")

    fun onPackageLoaded() {
        logger.d("hook")
        afterMethod(
            Reflections.findClass("android.app.ContextImpl", null),
            "checkCallingPermission"
        ) { param ->
            if (param.args[0] == ManifestCompat.permission.MANAGE_MEDIA_PROJECTION) {
                val result = param.result
                if (result != PackageManager.PERMISSION_GRANTED && Binder.getCallingUid() == 1000) {
                    logger.i("MANAGE_MEDIA_PROJECTION denied, let's fix it.")
                    param.result = PackageManager.PERMISSION_GRANTED
                }
            }
        }

        // MediaRecorder#init need currentPackageName to be no-null
        afterMethod(
            ActivityThread::class.java,
            "currentPackageName"
        ) { param ->
            if (ShortXService.isSystemReady && Binder.getCallingUid() == 1000) {
                val stack = Log.getStackTraceString(
                    Throwable()
                )
                if (stack.contains(MediaRecorder::class.java.name) || stack.contains(AudioService::class.java.name)) {
                    param.result = "android"
                    logger.w("ActivityThread currentPackageName return as SystemUI")
                }
            }
        }
    }
}