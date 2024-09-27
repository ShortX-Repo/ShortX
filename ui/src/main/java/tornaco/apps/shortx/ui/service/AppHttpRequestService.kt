package tornaco.apps.shortx.ui.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Keep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import tornaco.apps.shortx.core.http.HttpCallerImpl
import tornaco.apps.shortx.core.http.IHttpCaller
import tornaco.apps.shortx.core.http.persistentcookiejar.persistence.SettingsStoreCookiePersistor
import tornaco.apps.shortx.core.os.SynchronousResultReceiver
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.store.BaseSettingsStore
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import java.io.File

@Keep
class AppHttpRequestService : Service() {
    private val logger = Logger("AppHttpRequestService")

    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private val shortX by lazy { shortXManager }

    private val callerImpl by lazy {
        HttpCallerImpl(
            cookiePersistor = SettingsStoreCookiePersistor(
                BaseSettingsStore(
                    applicationContext,
                    "AppHttpRequestService- PersistentCookieJar",
                    File(applicationContext.cacheDir, "PersistentCookieJar-cache"),
                    serviceScope
                )
            )
        ) {
            shortX.log(it, "http")
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return object : IHttpCaller.Stub() {
            override fun call(
                requestId: String,
                receiver: SynchronousResultReceiver,
                url: String,
                method: String,
                headers: MutableMap<String, String>,
                requestBodyData: ByteArrayWrapper?,
                withCookieJar: Boolean,
                trustAllCerts: Boolean
            ) {
                runCatching {
                    val data = callerImpl.call(
                        url,
                        method,
                        headers,
                        requestBodyData,
                        withCookieJar,
                        trustAllCerts
                    )
                    receiver.send(ByteArrayWrapper(data.toByteArray()))
                }.onFailure {
                    logger.e("Http call onFailure: $it")
                    receiver.propagateException(RuntimeException(it))
                }
            }

            override fun close(requestId: String?) {
                logger.w("close: $requestId")
                // TODO.
            }
        }
    }
}