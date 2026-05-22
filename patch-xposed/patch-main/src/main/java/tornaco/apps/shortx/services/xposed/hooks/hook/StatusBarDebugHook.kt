package tornaco.apps.shortx.services.xposed.hooks.hook

import android.util.Log
import com.android.internal.statusbar.IStatusBar
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded
import util.Reflections
import java.lang.reflect.Proxy

object StatusBarDebugHook {
    private val loggingMethods = setOf(
        "setWindowState",
        "setSystemUiVisibility",
        "onSystemBarAttributesChanged",
    )

    private val logger = Logger("ShortXHook.StatusBarDebugHook")

    fun hook(lpparam: ISystemServerLoaded.Param) = kotlin.runCatching {
        if (BuildPropInternal.APP_IS_DEBUG) {
            hookRegister(lpparam)
        }
    }.onFailure {
        logger.e(it, "hookRegister")
    }

    private fun hookRegister(lpparam: ISystemServerLoaded.Param) = kotlin.runCatching {
        afterMethod(
            Reflections.findClass(
                "com.android.server.statusbar.StatusBarManagerService",
                lpparam.classLoader
            ),
            "registerStatusBar"
        ) { param ->
            logger.d(
                "registerStatusBar: ${param.args.map { it }}"
            )
            val bar = param.args[0] as IStatusBar
            val proxyBar = Proxy.newProxyInstance(
                bar.javaClass.classLoader,
                arrayOf(IStatusBar::class.java)
            ) { _, method, args: Array<Any>? ->
                if (method.name == "toString") return@newProxyInstance "ProxyBarIns"

                if (loggingMethods.contains(method.name)) {
                    logger.d("${method.name} ${Log.getStackTraceString(Throwable())}")
                }

                val res =
                    if (args != null) {
                        Reflections.callMethod(
                            bar,
                            method.name,
                            *args
                        )
                    } else {
                        Reflections.callMethod(
                            bar,
                            method.name
                        )
                    }
                logger.d("Method call: ${method.name} ${args?.map { it }} ---> $res")
                return@newProxyInstance res
            }
            Reflections.setObjectField(param.thisObject, "mBar", proxyBar)
        }
    }.onFailure {
        logger.e(it, "registerStatusBar")
    }
}
