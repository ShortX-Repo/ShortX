package tornaco.apps.shortx.core.rule.action

import android.os.Parcel
import android.os.Parcelable

data class ByteArrayWrapper(val byteData: ByteArray) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createByteArray()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByteArray(byteData)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteArrayWrapper

        return byteData.contentEquals(other.byteData)
    }

    override fun hashCode(): Int {
        return byteData.contentHashCode()
    }

    companion object CREATOR : Parcelable.Creator<ByteArrayWrapper> {
        override fun createFromParcel(parcel: Parcel): ByteArrayWrapper {
            return ByteArrayWrapper(parcel)
        }

        override fun newArray(size: Int): Array<ByteArrayWrapper?> {
            return arrayOfNulls(size)
        }
    }
}