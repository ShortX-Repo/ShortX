package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.WebSocket
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.util.WSCallback
import tornaco.apps.shortx.services.util.WebSocketClient
import tornaco.apps.shortx.services.util.loop

object WebSocketConnect : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WebSocketConnect::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        val data =
            params.action unpack_ tornaco.apps.shortx.core.proto.action.WebSocketConnect::class.java

        withContext(Dispatchers.IO) {
            val callback = object : WSCallback {}
            val client = WebSocketClient(
                data.url, callback
            )
            var socket: WebSocket? = null
            runCatching {
                socket = client.start()
                loop()
            }.onFailure {
                logger.e(it, "WebSocketConnect")
                logger.w("Closing websocket: $socket")
                // https://datatracker.ietf.org/doc/html/rfc6455#section-7.4
                socket?.close(1000, "Client closing connection")

                throw it
            }
        }
    }
}