package tornaco.apps.shortx.services.android

import tornaco.apps.shortx.core.util.Logger
import util.Reflections

@Suppress("UNCHECKED_CAST")
class ServiceRetriever(private val classLoader: ClassLoader) {
    private val logger = Logger("AndroidServices")

    fun getInputMethodManagerService(): Any? = kotlin.runCatching {
        val className = "com.android.server.inputmethod.InputMethodManagerService\$Lifecycle"
        val lifecycle = getService(Reflections.findClass(className, classLoader))
        return@runCatching Reflections.getObjectField(lifecycle, "mService")
    }.getOrElse {
        logger.e(it, "getInputMethodManagerService error")
        null
    }

    fun getService(serviceClass: Class<*>): Any? {
        return getServices().firstOrNull {
            it::class.java == serviceClass
        }
    }

    fun getServices(): List<Any> {
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