package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.Context
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.AppGlobals
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.SystemUIClient
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.notification.handleButtonClick
import tornaco.apps.shortx.core.notification.handleNotificationClick
import tornaco.apps.shortx.core.platform.ClassNames.SYSTEM_UI_APP
import tornaco.apps.shortx.core.platform.Methods.onCreate
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.PicassoInitializer
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.su.LibShKtShellImpl
import tornaco.apps.shortx.services.xposed.hooks.hook.CustomHookApp
import tornaco.apps.shortx.services.xposed.hooks.hook.afterMethod
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.data.GlobalSettings
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls.installInputMonitor
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.ocr.OCRImpl
import util.Reflections

fun SystemUIHook.hookApp(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
    fun createNotificationPoster(shortx: IShortX, context: Context): INotificationPoster {
        logger.d("installNotificationPoster")
        val appRes = AppResources(
            context,
            BuildPropInternal.APP_PACKAGE_NAME
        )
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
                        appRes.getIcon(it)
                    }
                },
                getBitmap = {
                    appRes.getBitmap(it)
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

    if (PackageNames.systemUI == lpparam.packageName) {
        afterMethod(
            Reflections.findClass(SYSTEM_UI_APP, lpparam.classLoader),
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

            AppGlobals.setContext(app)
            initShortXAppContext(app)
            GlobalSettings.init(app)

            PicassoInitializer.install(
                app,
                app.cacheDir,
                BuildPropInternal.APP_PACKAGE_NAME
            )

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

            installInputMonitor(app)

            CustomHookApp.hookApp(
                lpparam.classLoader,
                PackageNames.systemUI,
                MethodHookLifecycle.LoadedPackage
            )
        }
    }
}.onFailure {
    logger.e(it, "hookApp")
}