package tornaco.apps.shortx.ui.base.code

import android.os.Parcelable
import tornaco.apps.shortx.core.proto.common.CoroutineContext

@kotlinx.parcelize.Parcelize
data class ParcelableCode(
    val expression: String,
    val coroutineContext: CoroutineContext,
) : Parcelable


fun String?.toParcelableCodeOrNull(coroutineContext: CoroutineContext): ParcelableCode? {
    return this?.let {
        ParcelableCode(expression = it, coroutineContext = coroutineContext)
    }
}