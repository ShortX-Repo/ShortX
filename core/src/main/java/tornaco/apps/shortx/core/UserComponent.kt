package tornaco.apps.shortx.core

import android.os.Parcel
import android.os.Parcelable

val unknownUserComponent = UserComponent(unKnownPkg, "n/a")

data class UserComponent(val pkg: Pkg, val className: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Pkg::class.java.classLoader),
        parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(pkg, flags)
        parcel.writeString(className)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserComponent> {
        override fun createFromParcel(parcel: Parcel): UserComponent {
            return UserComponent(parcel)
        }

        override fun newArray(size: Int): Array<UserComponent?> {
            return arrayOfNulls(size)
        }
    }
}
