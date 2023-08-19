package tornaco.apps.shortx.services.xposed.hooks.hook

import android.app.Application
import android.content.ComponentName
import android.content.Context
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildProp
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
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.su.LibSuperuserShellImpl
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.logAdapter
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
                        if (name.packageName.contains(BuildProp.APP_PACKAGE_NAME)) {
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


    fun hookApp(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        if (PackageNames.systemUI == lpparam.packageName) {
            afterMethod(
                Reflections.findClass(SYSTEM_UI_APP, lpparam.classLoader),
                onCreate
            ) { param ->
                // Install log
                logAdapter = { _: Int, tag: String, msg: String ->
                    XposedBridge.log("$tag - $msg")
                }

                val app: Application = param.thisObject as Application
                logger.d("After onCreate: $app")
                val shortx = ShortXManagerNative.shortX
                logger.i("ShortX: $shortx")
                shortx?.let {
                    installShellImpl(it)
                    installNotificationPoster(it, app.applicationContext)
                }
            }
        }
    }.onFailure {
        logger.e(it, "hookApp")
    }

    private fun installShellImpl(shortx: IShortX) {
        logger.d("installShellImpl")
        val shell = LibSuperuserShellImpl
        shortx.attachSUImpl("SystemUI", shell)
        logger.i("installShellImpl success.")
    }

    private fun installNotificationPoster(shortx: IShortX, context: Context) {
        logger.d("installNotificationPoster")
        val appRes = AppResources(
            context,
            BuildProp.APP_PACKAGE_NAME
        )
        val notificationPoster by lazy {
            NotificationPoster(
                context,
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
        }

        shortx.attachNotificationPoster("SystemUI", stub)
        logger.i("installNotificationPoster success.")
    }
}


