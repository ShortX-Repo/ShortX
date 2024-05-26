package tornaco.apps.shortx.ui.base

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
abstract class ShortXContextViewModel<T> constructor(context: Context, initState: () -> T) :
    ContextViewModel<T, Unit>(context, initState) {
    override val i18N = i18NIns
}

@SuppressLint("StaticFieldLeak")
abstract class ShortXContextViewModelWithEffect<T, E> constructor(
    context: Context,
    initState: () -> T
) :
    ContextViewModel<T, E>(context, initState) {
    override val i18N = i18NIns
}