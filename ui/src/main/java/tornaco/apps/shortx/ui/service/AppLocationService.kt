package tornaco.apps.shortx.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Keep
import tornaco.apps.shortx.core.location.ILocationProvider
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper

@Keep
class AppLocationService : Service() {
    private val stub = object : ILocationProvider.Stub() {
        override fun getCurrentLocationInfo(
            requestId: String,
            receiver: SynchronousResultReceiver,
            requestData: ByteArrayWrapper,
        ) {
            AppLocationServiceSupport.getCurrentLocationInfo(
                service = this@AppLocationService,
                requestId = requestId,
                receiver = receiver,
                requestData = requestData,
            )
        }

        override fun getCurrentLocationAddress(
            requestId: String,
            receiver: SynchronousResultReceiver,
            requestData: ByteArrayWrapper,
        ) {
            AppLocationServiceSupport.getCurrentLocationAddress(
                service = this@AppLocationService,
                requestId = requestId,
                receiver = receiver,
                requestData = requestData,
            )
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return stub
    }
}