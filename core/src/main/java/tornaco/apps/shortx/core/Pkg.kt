package tornaco.apps.shortx.core

import android.os.Parcel
import android.os.Parcelable

val unKnownPkg = Pkg("unknown", 0)

data class Pkg(val pkgName: String, val userId: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt()) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(pkgName)
        parcel.writeInt(userId)
    }

    companion object CREATOR : Parcelable.Creator<Pkg> {
        override fun createFromParcel(parcel: Parcel): Pkg {
            return Pkg(parcel)
        }

        override fun newArray(size: Int): Array<Pkg?> {
            return arrayOfNulls(size)
        }
    }
}