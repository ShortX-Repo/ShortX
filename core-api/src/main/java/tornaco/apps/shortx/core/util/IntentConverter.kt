package tornaco.apps.shortx.core.util

import android.content.Intent
import android.os.Parcel

object IntentConverter {
    fun intentToBytes(intent: Intent): ByteArray {
        val parcel = Parcel.obtain()
        intent.writeToParcel(parcel, 0)
        val bytes = parcel.marshall()
        parcel.recycle()
        return bytes
    }

    fun bytesToIntent(bytes: ByteArray): Intent {
        val parcel = Parcel.obtain()
        parcel.unmarshall(bytes, 0, bytes.size)
        parcel.setDataPosition(0)
        val intent = Intent.CREATOR.createFromParcel(parcel)
        parcel.recycle()
        return intent
    }
}
