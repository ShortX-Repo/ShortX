package tornaco.apps.shortx.ui

import android.content.Context
import tornaco.apps.shortx.app.binder.retriever.Retriever
import tornaco.apps.shortx.core.ShortXManagerNative
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.I18NCache

object EntryPoint {
    private val logger = Logger("EntryPoint")

    fun onCreate(context: Context) {
        I18NCache.init(context)
        Retriever().retrieveServiceBinder().apply {
            ShortXManagerNative.binder = this
            logger.d("onCreate, binder: $this")
        }

    }
}