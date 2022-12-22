package tornaco.apps.shortx.ui

import android.content.Context
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tornaco.apps.shortx.app.binder.retriever.Retriever
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.printManager
import tornaco.apps.shortx.ui.base.I18NCache
import util.Reflections

object EntryPoint {
    private val logger = Logger("EntryPoint")

    fun onCreate(context: Context) {
        I18NCache.init(context)
        HiddenApiBypass.addHiddenApiExemptions("")
        Retriever().retrieveServiceBinder().apply {
            ShortXManagerNative.androidService = Reflections.getObjectField(
                context.printManager,
                "mService"
            )
            logger.d("onCreate, binder: $this")
        }

    }
}