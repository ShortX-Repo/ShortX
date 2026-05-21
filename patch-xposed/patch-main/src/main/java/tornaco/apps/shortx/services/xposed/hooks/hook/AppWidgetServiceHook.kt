package tornaco.apps.shortx.services.xposed.hooks.hook

import android.content.pm.ParceledListSlice
import android.os.Bundle
import tornaco.apps.shortx.core.platform.Classes.appWidgetServiceImplClass
import tornaco.apps.shortx.core.platform.Methods
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.xposed.hooks.hook.api.ISystemServerLoaded

object AppWidgetServiceHook {
    private val logger = Logger("ShortXHook.AppWidgetServiceHook")

    fun hookAppWidgetService(lpparam: ISystemServerLoaded.Param) =
        kotlin.runCatching {
            beforeMethod(
                lpparam.classLoader.appWidgetServiceImplClass().apply {
                    logger.d("appWidgetServiceImplClass class: $this")
                },
                Methods.startListening
            ) { hookParam ->
                logger.d("appWidgetServiceImplClass.startListening: ${hookParam.args.map { it?.toString() }}")
                val printJobName = hookParam.args[1] as String
                if (printJobName == "shortx") {
                    logger.d("Inject binder for appWidgetServiceImplClass")
                    val service = ShortXService.serviceBinder
                    val bundle = Bundle().apply {
                        putBinder("shortX-Binder", service)
                    }
                    hookParam.returnAndSkip(ParceledListSlice(listOf(bundle)))
                }
            }
        }.onFailure {
            logger.e(it, "hookAppWidgetService")
        }
}
