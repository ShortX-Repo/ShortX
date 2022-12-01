package tornaco.apps.shortx.core

import android.os.Parcel
import android.os.Parcelable

data class App(val pkg: Pkg, val label: String, val category: AppCategory) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Pkg::class.java.classLoader),
        parcel.readString(),
        AppCategory.valueOf(parcel.readString())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(pkg, flags)
        parcel.writeString(label)
        parcel.writeString(category.name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<App> {
        override fun createFromParcel(parcel: Parcel): App {
            return App(parcel)
        }

        override fun newArray(size: Int): Array<App?> {
            return arrayOfNulls(size)
        }
    }

}

enum class AppCategory {
    User, System
}