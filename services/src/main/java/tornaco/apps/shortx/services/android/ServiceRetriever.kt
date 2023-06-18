package tornaco.apps.shortx.services.android

import android.app.ActivityManagerInternal
import tornaco.apps.shortx.core.platform.ClassNames
import tornaco.apps.shortx.core.platform.lifecycleClassName
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

@Suppress("UNCHECKED_CAST")
class ServiceRetriever(private val classLoader: ClassLoader) {
    private val logger = Logger("AndroidServices")

    fun getInputMethodManagerService(): Any? = kotlin.runCatching {
        val className = ClassNames.INPUT_MANAGER_SERVICE.lifecycleClassName()
        val lifecycle =
            getServiceFromSystemServiceManager(Reflections.findClass(className, classLoader))
        return@runCatching Reflections.getObjectField(lifecycle, "mService")
    }.getOrElse {
        logger.e(it, "getInputMethodManagerService error")
        null
    }

    fun getAMS(): Any? = kotlin.runCatching {
        val className = ClassNames.AMS.lifecycleClassName()
        val lifecycle =
            getServiceFromSystemServiceManager(Reflections.findClass(className, classLoader))
        return@runCatching Reflections.getObjectField(lifecycle, "mService")
    }.getOrElse {
        logger.e(it, "getAMS error")
        null
    }

    fun getATMS(): Any? = kotlin.runCatching {
        val className = ClassNames.ATMS.lifecycleClassName()
        val lifecycle =
            getServiceFromSystemServiceManager(Reflections.findClass(className, classLoader))
        return@runCatching Reflections.getObjectField(lifecycle, "mService")
    }.getOrElse {
        logger.e(it, "getATMS error")
        null
    }

    fun getATMSInternal(): Any? = kotlin.runCatching {
        getATMS()?.let {
            Reflections.getObjectField(it, "mInternal")
        }
    }.getOrElse {
        logger.e(it, "getATMSInternal error")
        null
    }

    fun getAMSInternal(): Any? = kotlin.runCatching {
        getService(ActivityManagerInternal::class.java, classLoader)
    }.getOrElse {
        logger.e(it, "getAMSInternal error")
        null
    }

    private fun getServiceFromSystemServiceManager(serviceClass: Class<*>): Any? {
        return getServicesFromSystemServiceManager().firstOrNull {
            it::class.java == serviceClass
        }
    }

    private fun getServicesFromSystemServiceManager(): List<Any> {
        val systemServiceManager = getSystemServiceManager()
        if (systemServiceManager == null) {
            logger.w("LocalServices.getService, systemServiceManager is null.")
            return emptyList()
        }
        // private final ArrayList<SystemService> mServices = new ArrayList();
        return Reflections.getObjectField(systemServiceManager, "mServices") as List<Any>
    }

    // LocalServices.addService(SystemServiceManager.class, mSystemServiceManager);
    private fun getSystemServiceManager(): Any? {
        return getService(
            systemServiceManagerClass(
                classLoader
            ), classLoader
        )
    }

    private fun localServiceClass(classLoader: ClassLoader): Class<*> {
        return Reflections.findClass("com.android.server.LocalServices", classLoader)
    }

    private fun getService(serviceClass: Class<*>, classLoader: ClassLoader): Any? {
        val localServiceClass = localServiceClass(classLoader)
        return Reflections.callStaticMethod(localServiceClass, "getService", serviceClass)
    }

    private fun systemServiceManagerClass(classLoader: ClassLoader): Class<*> {
        return Reflections.findClass("com.android.server.SystemServiceManager", classLoader)
    }
}