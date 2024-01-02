package tornaco.apps.shortx.ui.base.code

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class ParcelableCode(val expression: String) : Parcelable


fun String?.toParcelableCodeOrNull(): ParcelableCode? {
    return this?.let {
        ParcelableCode(expression = it)
    }
}