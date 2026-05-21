package tornaco.apps.shortx.services.xposed.hooks.hook

import android.Manifest
import android.content.pm.PackageManager
import android.os.Binder
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.compat.ManifestCompat
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

object ContextImplHook {
    private val logger = Logger("ShortXHook.ContextImplHook")

    fun onPackageLoaded() {
        logger.d("hook")
        afterMethod(
            Reflections.findClass("android.app.ContextImpl", null),
            "checkCallingPermission"
        ) { param ->
            val result = param.result
            if (param.args[0] == ManifestCompat.permission.MANAGE_MEDIA_PROJECTION
                || param.args[0] == ManifestCompat.permission.CAPTURE_VOICE_COMMUNICATION_OUTPUT
                || param.args[0] == ManifestCompat.permission.WRITE_SECURE_SETTINGS
                || param.args[0] == Manifest.permission.READ_CLIPBOARD_IN_BACKGROUND
            ) {
                if (result != PackageManager.PERMISSION_GRANTED && Binder.getCallingUid() == 1000) {
                    logger.i("${param.args[0]} denied, let's fix it.")
                    param.returnAndSkip(PackageManager.PERMISSION_GRANTED)
                }
            } else if (result != PackageManager.PERMISSION_GRANTED && BuildPropInternal.APP_IS_DEBUG) {
                logger.v("checkCallingPermission, DENIED: ${param.args[0]}")
            }
        }
    }
}
