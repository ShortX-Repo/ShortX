package tornaco.apps.shortx.core.media

import android.os.Parcel
import android.os.Parcelable

data class FileRow(
    val id: Long,
    val volumeName: String,
    val mediaType: Int,
    val ownerPackageName: String,
    val path: String,
    val mediaUri: String?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(volumeName)
        parcel.writeInt(mediaType)
        parcel.writeString(ownerPackageName)
        parcel.writeString(path)
        parcel.writeString(mediaUri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FileRow> {
        override fun createFromParcel(parcel: Parcel): FileRow {
            return FileRow(parcel)
        }

        override fun newArray(size: Int): Array<FileRow?> {
            return arrayOfNulls(size)
        }
    }
}