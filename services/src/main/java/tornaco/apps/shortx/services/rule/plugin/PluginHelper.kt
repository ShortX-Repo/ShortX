package tornaco.apps.shortx.services.rule.plugin

import android.content.Context
import dalvik.system.PathClassLoader
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.PkgUtils
import tornaco.apps.shortx.services.rule.actor.impl.PluginAction
import java.io.File

object PluginHelper {
    private val logger = Logger("PluginHelper")

    fun loadPluginObjectOrThrow(pkgName: String, context: Context, classMetaDataKey: String): Any? {
        logger.d("loadPluginObject: $pkgName $classMetaDataKey")
        val appInfo = requireNotNull(
            PkgUtils.getApplicationInfo(
                context,
                pkgName
            )
        ) { "Plugin not installed: $pkgName" }

        val entry: String? = appInfo.metaData.getString(classMetaDataKey)
        require(!entry.isNullOrBlank()) {
            "Can not find plugin entry, message to dev: did you add meta data to your AndroidManifest.xml?"
        }

        val apkPath: File = requireNotNull(PkgUtils.getApkPath(pkgName)) {
            "Apk path not found for: $pkgName"
        }
        logger.d("Apk path $apkPath")
        return loadPluginObjectOrThrow(apkPath, entry, context)
    }

    private fun loadPluginObjectOrThrow(
        apkPath: File,
        className: String,
        context: Context
    ): Any? {
        logger.i("loadPluginObject: $className from apk: $apkPath")
        val appCl: ClassLoader = PathClassLoader(
            apkPath.absolutePath, PluginAction::class.java.classLoader
        )
        val objectClass = appCl.loadClass(className)
        logger.d("objectClass: $objectClass")
        return runCatching {
            val constructor = objectClass.getConstructor()
            constructor.newInstance()
        }.getOrElse {
            val constructor = objectClass.getConstructor(Context::class.java)
            constructor.newInstance(context)
        }
    }
}