package tornaco.apps.shortx.services.util

import android.annotation.SuppressLint
import android.app.ActivityThread
import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.os.UserHandle
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.util.Logger
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method


@Suppress("INACCESSIBLE_TYPE")
class AppServiceLoader {
    private val logger = Logger("AppServiceLoader")

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    fun load(cls: String): Any? {
        try {
            val activityThread = ActivityThread.systemMain()
            val systemContext: Context = activityThread.systemContext

            val userHandle: UserHandle = UserHandle.of(myUserId)
            val context: Context =
                systemContext.createPackageContextAsUser(
                    BuildPropInternal.APP_PACKAGE_NAME,
                    Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY,
                    userHandle
                )
            val mPackageInfo: Field = context.javaClass.getDeclaredField("mPackageInfo")
            mPackageInfo.isAccessible = true
            val loadedApk: Any = mPackageInfo.get(context)
            val makeApplication: Method = loadedApk.javaClass.getDeclaredMethod(
                "makeApplication",
                Boolean::class.javaPrimitiveType,
                Instrumentation::class.java
            )
            val application: Application =
                makeApplication.invoke(loadedApk, true, null) as Application
            val mInitialApplication: Field =
                activityThread.javaClass.getDeclaredField("mInitialApplication")
            mInitialApplication.isAccessible = true
            mInitialApplication.set(activityThread, application)

            val classLoader: ClassLoader = application.classLoader
            val serviceClass = classLoader.loadClass(cls)
            var constructorWithContext: Constructor<*>? = null
            try {
                constructorWithContext = serviceClass.getConstructor(Context::class.java)
            } catch (ignored: NoSuchMethodException) {
            } catch (ignored: SecurityException) {
            }
            val service = if (constructorWithContext != null) {
                constructorWithContext.newInstance(application)
            } else {
                serviceClass.newInstance()
            }
            return service
        } catch (tr: Throwable) {
            logger.e(tr, "Unable to load app service.")
            return null
        }
    }
}