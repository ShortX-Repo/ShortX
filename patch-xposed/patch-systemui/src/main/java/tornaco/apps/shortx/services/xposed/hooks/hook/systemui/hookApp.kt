package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.Context
import de.robv.android.xposed.XposedBridge
import tornaco.apps.shortx.core.AppGlobals
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.ICallback
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.SystemUIClient
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.notification.handleButtonClick
import tornaco.apps.shortx.core.notification.handleNotificationClick
import tornaco.apps.shortx.core.platform.ClassNames.SYSTEM_UI_APP
import tornaco.apps.shortx.core.platform.ClassNames.SYSTEM_UI_APP_2
import tornaco.apps.shortx.core.platform.Methods.onCreate
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.res.PicassoInitializer
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.su.LibShKtShellImpl
import tornaco.apps.shortx.services.xposed.hooks.hook.CustomHookApp
import tornaco.apps.shortx.services.xposed.hooks.hook.afterMethod
import tornaco.apps.shortx.services.xposed.hooks.hook.api.IPackageLoaded
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.data.GlobalSettings
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.installInputMonitor
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.ocr.OCRImpl
import util.Reflections

fun SystemUIHook.hookApp(lpparam: IPackageLoaded.Param) {
    if (PackageNames.systemUI == lpparam.packageName) {
        runCatching { hookSystemUIApp(lpparam, SYSTEM_UI_APP) }.onFailure {
            logger.e(
                "hookSystemUIApp failed - $SYSTEM_UI_APP"
            )
        }
        runCatching {
            hookSystemUIApp(lpparam, SYSTEM_UI_APP_2)
        }.onFailure {
            logger.e(
                "hookSystemUIApp failed - $SYSTEM_UI_APP_2"
            )
        }
    }
}

private fun SystemUIHook.hookSystemUIApp(lpparam: IPackageLoaded.Param, appClass: String) {
    fun createNotificationPoster(shortx: IShortX, context: Context): INotificationPoster {
        logger.d("installNotificationPoster")
        val notificationPoster by lazy {
            NotificationPoster(
                context,
                "SystemUI",
                "ShortX silence",
                "ShortX alert",
                "ShortX",
                getIcon = { remixNam ->
                    val resName = Remix.allFlat[remixNam]
                    resName?.let {
                        AppGlobals.appResources.getIcon(it)
                    }
                },
                getBitmap = {
                    AppGlobals.appResources.getBitmap(it)
                },
                onButtonClick = { postN, buttonId ->
                    handleButtonClick(postN, buttonId, ShortXManager(shortx))
                },
                onPostNotificationClick = { post ->
                    handleNotificationClick(post, ShortXManager(shortx))
                }
            )
        }

        return object : INotificationPoster.Stub() {
            override fun post(data: ByteArrayWrapper) {
                runCatching {
                    val n = PostNotification.parseFrom(data.byteData)
                    notificationPoster.invoke(n)
                }
            }

            override fun cancel(tag: String) {
                runCatching {
                    notificationPoster.cancelNotification(tag)
                }
            }
        }
    }
    afterMethod(
        Reflections.findClass(appClass, lpparam.classLoader),
        onCreate
    ) { param ->
        val shortx = ShortXManagerNative.shortX
        if (shortx == null) {
            XposedBridge.log("SystemUIHook. ERROR, shortX is null")
            return@afterMethod
        }
        logger.i("ShortX: $shortx")
        val app: Context = param.thisObject as Context
        logger.d("After onCreate: $app")

        initShortXAppContext(app)
        GlobalSettings.init(app)

        PicassoInitializer.install(
            app,
            app.cacheDir,
            BuildPropInternal.APP_PACKAGE_NAME
        )

        shortx.registerSystemReadyListener(object : ICallback.Stub() {
            override fun receive(p0: ByteArrayWrapper?) {
                logger.w("System is ready, GO!!!")
                // Attach client, let Service known SystemUI is up.
                shortx.attachSystemUIClient(
                    SystemUIClient(
                        cv = CVImpl(app),
                        ocr = OCRImpl(app),
                        su = LibShKtShellImpl,
                        poster = createNotificationPoster(shortx, app),
                        tile = QSTileHostImpl(shortx)
                    )
                )

                // Solution deprecated. Rollback to inputfilter solution.
                // installInputMonitor(app)

                CustomHookApp.hookApp(
                    lpparam.classLoader,
                    PackageNames.systemUI,
                    MethodHookLifecycle.LoadedPackage
                )
            }
        })
    }
}