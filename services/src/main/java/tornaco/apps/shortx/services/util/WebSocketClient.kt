package tornaco.apps.shortx.services.util

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import tornaco.apps.shortx.core.util.Logger

interface WSCallback {
    companion object {
        val logger: Logger get() = Logger("WSCallback")
    }

    fun onOpen() {
        logger.d("onOpen")
    }

    fun onMessage(message: String) {
        logger.d("onMessage: $message")
    }

    fun onClosed(code: Int, reason: String) {
        logger.d("onClosed: $code $reason")
    }

    fun onFailure(t: Throwable, response: Response?) {
        logger.d("onFailure: $t $response")
    }
}

class WebSocketClient(
    private val url: String,
    private val callback: WSCallback
) {
    private val client = OkHttpClient()

    fun start(): WebSocket {
        val request = Request.Builder()
            .url(url)
            .build()

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                callback.onOpen()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                callback.onMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                callback.onMessage(bytes.toString())
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                callback.onClosed(code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                callback.onFailure(t, response)
                webSocket.close(1000, null)
            }
        }

        return client.newWebSocket(request, webSocketListener)
    }
}
