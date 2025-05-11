package tornaco.apps.shortx.services.app

import android.app.ActivityThread
import android.app.IApplicationThread
import android.app.IInstrumentationWatcher
import android.app.IUiAutomationConnection
import android.app.ProfilerInfo
import android.content.AutofillOptions
import android.content.ComponentName
import android.content.ContentCaptureOptions
import android.content.pm.ApplicationInfo
import android.content.pm.ProviderInfoList
import android.content.res.CompatibilityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import android.os.SharedMemory
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.util.Logger
import util.Reflections
import util.ReflectionsExt
import java.lang.reflect.Proxy
import java.util.Locale

object AppThread {
    private val logger = Logger("AppThread")

    fun make(original: IApplicationThread): IApplicationThread? {
        val at = ActivityThread.currentActivityThread()
        logger.w("ActivityThread.currentActivityThread()= $at")
        if (at == null) {
            logger.e("ActivityThread.currentActivityThread() is null")
            return null
        }
        val application = at.application
        logger.w("ActivityThread.getApplication()= $application")
        val loadedApk = application.mLoadedApk
        logger.w("ActivityThread.loadedApk()= $loadedApk")

        val classLoader = loadedApk.classLoader
        logger.w("ActivityThread.classLoader= $classLoader")

        val instance =
            Proxy.newProxyInstance(
                classLoader,
                arrayOf(
                    Reflections.findClass(
                        IApplicationThread::class.java.name,
                        classLoader
                    )
                )
            ) { _, method, args ->
                method.isAccessible = true
                if (method.name == "bindApplication" && args[0] == BuildPropInternal.APP_PACKAGE_NAME) {
                    logger.d("invoke method: ${method.name} ${args?.map { it }}")
                    val configIndex = ReflectionsExt.getFirstArgIndexLikeTypeForMethod(
                        method,
                        Configuration::class.java.name
                    )
                    val config = args?.get(configIndex) as Configuration?
                    config?.let {
                        it.fontScale = 0.6f
                        it.locales = LocaleList(Locale.US)
                        it.compatSmallestScreenWidthDp = 500
                    }
                    logger.d("config: $config")
                    args[configIndex] = config
                }
                method.invoke(original, *(args ?: emptyArray()))
            } as IApplicationThread
        logger.logOnDebug("Created IApplicationThread instance: $instance")
        return instance
    }

    class ProxyAppThread(val original: IApplicationThread) : IApplicationThread by original {
        override fun bindApplication(
            packageName: String?,
            info: ApplicationInfo?,
            providerList: ProviderInfoList?,
            testName: ComponentName?,
            profilerInfo: ProfilerInfo?,
            testArguments: Bundle?,
            testWatcher: IInstrumentationWatcher?,
            uiAutomationConnection: IUiAutomationConnection?,
            debugMode: Int,
            enableBinderTracking: Boolean,
            trackAllocation: Boolean,
            restrictedBackupMode: Boolean,
            persistent: Boolean,
            config: Configuration?,
            compatInfo: CompatibilityInfo?,
            services: MutableMap<Any?, Any?>?,
            coreSettings: Bundle?,
            buildSerial: String?,
            autofillOptions: AutofillOptions?,
            contentCaptureOptions: ContentCaptureOptions?,
            disabledCompatChanges: LongArray?,
            serializedSystemFontMap: SharedMemory?
        ) {
            original.bindApplication(
                packageName,
                info,
                providerList,
                testName,
                profilerInfo,
                testArguments,
                testWatcher,
                uiAutomationConnection,
                debugMode,
                enableBinderTracking,
                trackAllocation,
                restrictedBackupMode,
                persistent,
                config,
                compatInfo,
                services,
                coreSettings,
                buildSerial,
                autofillOptions,
                contentCaptureOptions,
                disabledCompatChanges,
                serializedSystemFontMap
            )
        }
    }
}