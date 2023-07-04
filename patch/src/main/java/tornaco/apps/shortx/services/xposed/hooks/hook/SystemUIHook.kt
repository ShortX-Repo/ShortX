package tornaco.apps.shortx.services.xposed.hooks.hook

import android.app.Application
import com.topjohnwu.superuser.Shell
import de.robv.android.xposed.callbacks.XC_LoadPackage
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.platform.ClassNames.SYSTEM_UI_APP
import tornaco.apps.shortx.core.platform.Methods.onCreate
import tornaco.apps.shortx.core.platform.PackageNames
import tornaco.apps.shortx.core.proto.common.ShellRes
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
                logger.d("After onCreate: ${app.applicationContext}")
                val shortx = ShortXManagerNative.shortX
                logger.i("ShortX: $shortx")

                val shell = object : ISu.Stub() {
                    override fun exe(command: Array<String>): ByteArrayWrapper {
                        logger.i("execute: ${command.joinToString()}")
                        return Shell.cmd(*command)
                            .exec().apply {
                                logger.i("Shell res: out: ${this.out} err: ${this.err}")
                            }.let {
                                ShellRes.newBuilder().setCode(it.code).addAllOut(it.out)
                                    .addAllErr(it.err)
                                    .build()
                            }.let {
                                ByteArrayWrapper(it.toByteArray())
                            }
                    }
                }

                shortx?.attachSUImpl("SystemUI", shell)
                logger.i("Attached.")
            }
        }
    }.onFailure {
        logger.e(it, "hookApp")
    }
}

