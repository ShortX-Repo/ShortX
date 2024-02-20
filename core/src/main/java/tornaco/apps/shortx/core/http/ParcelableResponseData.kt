package tornaco.apps.shortx.core.http

import android.os.Parcel
import android.os.Parcelable
import okhttp3.Response

@Suppress("UNCHECKED_CAST")
data class ParcelableResponseData(
    val code: Int,
    val message: String?,
    val body: String?,
    val headers: Map<String, String>?
) : Parcelable {
    constructor(response: Response) : this(
        response.code,
        response.message,
        response.body?.string(),
        response.headers.toMultimap().mapValues { it.value.joinToString(", ") }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(code)
        parcel.writeString(message)
        parcel.writeString(body)
        parcel.writeMap(headers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableResponseData> {
        override fun createFromParcel(parcel: Parcel): ParcelableResponseData {
            return ParcelableResponseData(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableResponseData?> {
            return arrayOfNulls(size)
        }
    }

    private constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readHashMap(String::class.java.classLoader) as? Map<String, String>
    )
}
