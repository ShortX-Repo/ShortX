package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.ComponentName
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.platform.ClassNames.SYSTEM_UI_APP
import tornaco.apps.shortx.core.platform.ClassNames.SYSTEM_UI_CUSTOM_TILE
import tornaco.apps.shortx.core.platform.Methods.onCreate
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.fact.MethodHookLifecycle
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.su.LibShKtShellImpl
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.xposed.hooks.hook.CustomHook
import tornaco.apps.shortx.services.xposed.hooks.hook.afterMethod
import tornaco.apps.shortx.ui.statusbar.StatusBarClipView
import util.Reflections

object SystemUIHook {
    private val logger = Logger("ShortXHook.SystemUIHook")

    fun hookTile(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        if (PackageNames.systemUI == lpparam.packageName) {
            afterMethod(
                Reflections.findClass(
                    SYSTEM_UI_CUSTOM_TILE,
                    lpparam.classLoader
                ),
                "handleClick"
            ) { param ->
                logger.d("handleClick: ${param.thisObject}")
                ShortXManagerNative.shortX?.let {
                    if (it.isQSTileOptEnabled) {
                        val name: ComponentName =
                            Reflections.getObjectField(
                                param.thisObject,
                                "mComponent"
                            ) as ComponentName
                        if (name.packageName.contains(BuildPropInternal.APP_PACKAGE_NAME)) {
                            // tornaco.apps.shortx/tornaco.apps.shortx.ui.service.tile.Tile7
                            val tileName = name.className.substringAfterLast(".")
                            val tile = StatusBarTile.valueOf(tileName)
                            logger.d("handleClick: $name, tile: $tile")
                            it.onTileClick(tile.number)
                            param.result = null
                        }
                    }
                }
            }
        }
    }.onFailure {
        logger.e(it, "hookTile")
    }

    fun hookQS(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        if (PackageNames.systemUI == lpparam.packageName) {
            afterMethod(
                clazz = Reflections.findClass(
                    "com.android.systemui.qs.QSTileHost",
                    lpparam.classLoader
                ),
                methodName = "onTuningChanged"
            ) { param ->
                logger.d("onTuningChanged: ${param.args.map { it }}")
                val shortx = ShortXManagerNative.shortX
                logger.i("ShortX: $shortx")
                shortx?.attachQSTileHost(QSTileHostImpl(param.thisObject, shortx))
            }
        }
    }.onFailure {
        logger.e(it, "hookTile")
    }

    fun hookView(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        if (PackageNames.systemUI == lpparam.packageName) {
            afterMethod(
                clazz = Reflections.findClass(
                    "com.android.systemui.statusbar.phone.PhoneStatusBarView",
                    lpparam.classLoader
                ),
                methodName = "onFinishInflate"
            ) { param ->
                logger.d("onFinishInflate: ${param.args.map { it }}")
                val view: ViewGroup = param.thisObject as ViewGroup
                view.dumpChild()
                repeat(view.childCount) { index ->
                    val child = view.getChildAt(index)
                    val id = child.getIdString()
                    if (id == "app:id/status_bar_contents") {
                        logger.d("status_bar_contents: $child")
                        kotlin.runCatching {
                            val linearRoot = child as LinearLayout
                            linearRoot.addView(StatusBarClipView(child.context))
                            logger.d("Clip added.")
                        }.onFailure {
                            logger.e(it, "Fail to add clip")
                        }
                    }
                }
            }
        }
    }.onFailure {
        logger.e(it, "hookTile")
    }

    fun hookApp(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
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

    private fun installShellImpl(shortx: IShortX) {
        logger.d("installShellImpl")
        val shell = LibShKtShellImpl
        shortx.attachSUImpl("SystemUI", shell)
        logger.i("installShellImpl success.")
    }

    private fun installCV(shortx: IShortX, context: Context) {
        logger.d("installCV")
        val cv = CVImpl(context)
        shortx.attachCV(cv)
        logger.i("installCV success.")
    }

    private fun installORC(shortx: IShortX, context: Context) {
        logger.d("installORC")
        val orc = OCRImpl(context)
        shortx.attachOCR(orc)
        logger.i("installORC success.")
    }

    private fun installNotificationPoster(shortx: IShortX, context: Context) {
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

    private fun ViewGroup.dumpChild(prefix: String = "") {
        repeat(childCount) {
            val child = getChildAt(it)
            logger.d("$prefix child: $child")
            if (child is ViewGroup) {
                child.dumpChild(prefix + "\t")
            }
        }
    }
}

fun View.getIdString(): String {
    val out = StringBuilder()
    val id = id
    if (id != View.NO_ID) {
        val r: Resources = context.resources
        if (id > 0 && Resources.resourceHasPackage(id) && r != null) {
            try {
                val pkgname = when (id and -0x1000000) {
                    0x7f000000 -> "app"
                    0x01000000 -> "android"
                    else -> r.getResourcePackageName(id)
                }
                val typename = r.getResourceTypeName(id)
                val entryname = r.getResourceEntryName(id)
                out.append(pkgname)
                out.append(":")
                out.append(typename)
                out.append("/")
                out.append(entryname)
            } catch (e: Resources.NotFoundException) {
            }
        }
    }
    return out.toString()
}