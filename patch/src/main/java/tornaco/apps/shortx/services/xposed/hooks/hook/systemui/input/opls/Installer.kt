package tornaco.apps.shortx.services.xposed.hooks.hook.systemui.input.opls

import android.content.Context
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.services.xposed.hooks.hook.systemui.SystemUIHook

fun SystemUIHook.installInputMonitor(context: Context) {
    if (BuildPropInternal.APP_IS_DEBUG) {
        logger.w("installInputMonitor.")
        OplusInputMonitorHelper.getInstance().apply {
            init(context)
            enableInputReceiver(
                {
                    logger.d("onInputEvent: $it")
                    false
                }, false
            )
        }
    }
}