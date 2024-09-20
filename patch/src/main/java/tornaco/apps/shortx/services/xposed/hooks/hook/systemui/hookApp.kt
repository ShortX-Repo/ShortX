package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.Context
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.platform.ClassNames.SYSTEM_UI_APP
import tornaco.apps.shortx.core.platform.Methods.onCreate
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.PicassoInitializer
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.su.LibShKtShellImpl
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.xposed.hooks.hook.CustomHook
import tornaco.apps.shortx.services.xposed.hooks.hook.afterMethod
import util.Reflections

fun SystemUIHook.hookApp(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
    fun installShellImpl(shortx: IShortX) {
        logger.d("installShellImpl")
        val shell = LibShKtShellImpl
        shortx.attachSUImpl("SystemUI", shell)
        logger.i("installShellImpl success.")
    }

    fun installCV(shortx: IShortX, context: Context) {
        logger.d("installCV")
        val cv = CVImpl(context)
        shortx.attachCV(cv)
        logger.i("installCV success.")
    }

    fun installORC(shortx: IShortX, context: Context) {
        logger.d("installORC")
        val orc = OCRImpl(context)
        shortx.attachOCR(orc)
        logger.i("installORC success.")
    }

    fun installNotificationPoster(shortx: IShortX, context: Context) {
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
                onButtonClick = { button ->
                    button.actionList.forEach {
                        val evaluateContext = EvaluateContext.newBuilder()
                            .setSrcId("Notification button")
                            .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
                            .setSrcTitle(button.label)
                            .build()
                        shortx.executeAction(
                            ByteArrayWrapper(evaluateContext.toByteArray()),
                            ByteArrayWrapper(it.toByteArray())
                        )
                    }
                },
                onPostNotificationClick = { post ->
                    post.clickActionList.forEach {
                        val evaluateContext = EvaluateContext.newBuilder()
                            .setSrcId("Notification")
                            .setSrcEvent(ContextSrcEvent.ContextSrc_Rule)
                            .setSrcTitle(post.title)
                            .build()
                        shortx.executeAction(
                            ByteArrayWrapper(evaluateContext.toByteArray()),
                            ByteArrayWrapper(it.toByteArray())
                        )
                    }
                }
            )
        }

        val stub = object : INotificationPoster.Stub() {
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

        shortx.attachNotificationPoster("SystemUI", stub)
        logger.i("installNotificationPoster success.")
    }

    if (PackageNames.systemUI == lpparam.packageName) {
        afterMethod(
            Reflections.findClass(SYSTEM_UI_APP, lpparam.classLoader),
            onCreate
        ) { param ->
            val shortx = ShortXManagerNative.shortX
            if (shortx == null) {
                XposedBridge.log("systemUI hook. ERROR, shortX is null")
                return@afterMethod
            }
            logger.i("ShortX: $shortx")

            // Install log
            logAdapter = { _: Int, tag: String, msg: String ->
                shortx.log("$tag - $msg", "")
            }

            val app: Context = param.thisObject as Context
            logger.d("After onCreate: $app")

            PicassoInitializer.install(
                app,
                app.cacheDir,
                BuildPropInternal.APP_PACKAGE_NAME
            )

            installShellImpl(shortx)
            installNotificationPoster(shortx, app)
            installCV(shortx, app)
            installORC(shortx, app)

            CustomHook.hookApp(
                lpparam.classLoader,
                PackageNames.systemUI,
                MethodHookLifecycle.LoadedPackage
            )
        }
    }
}.onFailure {
    logger.e(it, "hookApp")
}