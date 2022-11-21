package tornaco.apps.shortx.core.action

import android.os.Parcel
import android.os.Parcelable

data class ActionBytes(val byte: ByteArray) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createByteArray()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByteArray(byte)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ActionBytes

        if (!byte.contentEquals(other.byte)) return false

        return true
    }

    override fun hashCode(): Int {
        return byte.contentHashCode()
    }

    companion object CREATOR : Parcelable.Creator<ActionBytes> {
        override fun createFromParcel(parcel: Parcel): ActionBytes {
            return ActionBytes(parcel)
        }

        override fun newArray(size: Int): Array<ActionBytes?> {
            return arrayOfNulls(size)
        }
    }
}