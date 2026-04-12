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
                context = applicationContext,
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
                context = applicationContext,
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