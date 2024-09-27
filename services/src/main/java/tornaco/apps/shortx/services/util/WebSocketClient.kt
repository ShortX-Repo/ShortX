package tornaco.apps.shortx.services.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug

interface WSCallback {
    companion object {
        val logger: Logger get() = Logger("WSCallback")
    }

    suspend fun onOpen() {
        logger.d("onOpen")
    }

    suspend fun onMessage(message: String) {
        logger.d("onMessage: $message")
    }

    suspend fun onClosed(code: Int, reason: String) {
        logger.d("onClosed: $code $reason")
    }

    suspend fun onFailure(t: Throwable, response: Response?) {
        logger.d("onFailure: $t $response")
    }
}

class WebSocketClient(
    private val url: String,
    private val callback: WSCallback,
    private val scope: CoroutineScope
) {
    private val client = OkHttpClient()

    fun start(): WebSocket {
        val request = Request.Builder()
            .url(url)
            .build()

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                scope.launch { callback.onOpen() }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch { callback.onMessage(text) }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                scope.launch { callback.onMessage(bytes.toString()) }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                scope.launch { callback.onClosed(code, reason) }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                webSocket.close(1000, null)
                scope.launch { callback.onFailure(t, response) }
            }
        }

        return client.newWebSocket(request, webSocketListener)
    }
}
