package tornaco.apps.shortx.core

import android.os.Parcel
import android.os.Parcelable
import tornaco.apps.shortx.core.cv.ICV
import tornaco.apps.shortx.core.notification.INotificationPoster
import tornaco.apps.shortx.core.ocr.IOCR
import tornaco.apps.shortx.core.rule.action.su.ISu
import tornaco.apps.shortx.core.systemui.IQSTileHost
import java.util.Objects.requireNonNull

class SystemUIClient(
    val cv: ICV,
    val ocr: IOCR,
    val su: ISu,
    val poster: INotificationPoster,
    val tile: IQSTileHost,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        requireNonNull(ICV.Stub.asInterface(parcel.readStrongBinder()), { "cv is null" }),
        requireNonNull(IOCR.Stub.asInterface(parcel.readStrongBinder()), { "ocr is null" }),
        requireNonNull(ISu.Stub.asInterface(parcel.readStrongBinder()), { "su is null" }),
        requireNonNull(
            INotificationPoster.Stub.asInterface(parcel.readStrongBinder()),
            { "poster is null" }),
        requireNonNull(IQSTileHost.Stub.asInterface(parcel.readStrongBinder()), { "tile is null" }),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStrongBinder(cv.asBinder())
        parcel.writeStrongBinder(ocr.asBinder())
        parcel.writeStrongBinder(su.asBinder())
        parcel.writeStrongBinder(poster.asBinder())
        parcel.writeStrongBinder(tile.asBinder())
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SystemUIClient> {
        override fun createFromParcel(parcel: Parcel): SystemUIClient {
            return SystemUIClient(parcel)
        }

        override fun newArray(size: Int): Array<SystemUIClient?> {
            return arrayOfNulls(size)
        }
    }
}
