package tornaco.apps.shortx.ui.base.code

import android.os.Parcelable
import tornaco.apps.shortx.core.proto.common.CoroutineContext
import tornaco.apps.shortx.core.proto.func.FuncParameterInput

@kotlinx.parcelize.Parcelize
data class ParcelableCode(
    val expression: String,
    val coroutineContext: CoroutineContext,
    val parameterInputs: List<FuncParameterInput> = emptyList(),
) : Parcelable


fun String?.toParcelableCodeOrNull(
    coroutineContext: CoroutineContext,
    parameterInputs: List<FuncParameterInput> = emptyList()
): ParcelableCode? {
    return this?.let {
        ParcelableCode(
            expression = it,
            coroutineContext = coroutineContext,
            parameterInputs = parameterInputs
        )
    }
}
