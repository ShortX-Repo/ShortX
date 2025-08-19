package tornaco.apps.shortx.ui.base

import android.annotation.SuppressLint
import android.content.Context
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.util.Logger

@SuppressLint("StaticFieldLeak")
abstract class ContextViewModel<STATE, EFFECT> constructor(
    val context: Context,
    initState: () -> STATE
) :
    StateViewModel<STATE, EFFECT>(initState) {
    val shortXManager by lazy {
        shortxProvider?.invoke(context) ?: tornaco.apps.shortx.core.shortXManager
    }
    protected val logger = Logger(javaClass.simpleName)
    abstract val i18N: I18N

    companion object {
        var shortxProvider: (Context.() -> ShortXManager)? = null
    }
}