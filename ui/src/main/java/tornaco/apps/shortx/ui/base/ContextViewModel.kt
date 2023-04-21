package tornaco.apps.shortx.ui.base

import android.annotation.SuppressLint
import android.content.Context
import tornaco.apps.shortx.core.util.Logger

@SuppressLint("StaticFieldLeak")
abstract class ContextViewModel<T> constructor(val context: Context, initState: T) :
    StateViewModel<T>(initState) {
    val shortXManager = tornaco.apps.shortx.core.shortXManager
    protected val logger = Logger(javaClass.simpleName)
    val i18N = i18NIns
}