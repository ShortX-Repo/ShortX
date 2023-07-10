package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.pm.PackageManager
import android.os.Binder
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
            if (param.args[0] == ManifestCompat.permission.MANAGE_MEDIA_PROJECTION) {
                val result = param.result
                if (result != PackageManager.PERMISSION_GRANTED && Binder.getCallingUid() == 1000) {
                    logger.i("MANAGE_MEDIA_PROJECTION denied, let's fix it.")
                    param.result = PackageManager.PERMISSION_GRANTED
                }
            }
        }
    }
}