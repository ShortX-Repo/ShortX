package tornaco.apps.shortx.services.xposed.hooks.hook

import android.app.Application
import android.content.Context
import com.topjohnwu.superuser.Shell
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.IShortX
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.notification.NotificationPoster
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.platform.ClassNames.SYSTEM_UI_APP
import tornaco.apps.shortx.core.platform.Methods.onCreate
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.common.ShellRes
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.util.Logger
import util.Reflections

object SystemUIHook {
    private val logger = Logger("ShortXHook.SystemUIHook")

    fun hookApp(lpparam: XC_LoadPackage.LoadPackageParam) = kotlin.runCatching {
        if (PackageNames.systemUI == lpparam.packageName) {
            afterMethod(
                Reflections.findClass(SYSTEM_UI_APP, lpparam.classLoader),
                onCreate
            ) { param ->
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
        val shell = SystemUIShellImpl
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

object SystemUIShellImpl : ISu.Stub() {
    private val logger = Logger("SystemUIShellImpl")

    init {
        logger.d("Init shell")
        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR or Shell.FLAG_MOUNT_MASTER)
                .setTimeout(10)
        )
    }

    override fun exe(
        command: Array<out String>,
        receiver: SynchronousResultReceiver
    ) {
        logger.i("execute: ${command.joinToString()}")
        runCatching {
            val shellResult: ByteArrayWrapper = Shell.cmd(*command)
                .exec().apply {
                    logger.i("Shell res: out: ${this.out} err: ${this.err}")
                }.let {
                    ShellRes.newBuilder().setCode(it.code).addAllOut(it.out)
                        .addAllErr(it.err)
                        .build()
                }.let {
                    ByteArrayWrapper(it.toByteArray())
                }

            receiver.send(shellResult)
        }.onFailure {
            receiver.propagateException(RuntimeException(it))
        }
    }
}

