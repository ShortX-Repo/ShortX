package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import okhttp3.Response
import okhttp3.WebSocket
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.proto.common.WebSocketEvent
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.util.runSubActions
import tornaco.apps.shortx.services.util.WSCallback
import tornaco.apps.shortx.services.util.WebSocketClient
import tornaco.apps.shortx.services.util.loopUntilQuitWithError

object WebSocketConnect : Actor {
    private val scope by lazy { CoroutineScope(SupervisorJob()) }

    private val livingWS: MutableMap<String, WebSocket> = mutableMapOf()

    fun getLivingWSByUrl(url: String) = livingWS[url]

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WebSocketConnect::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res {
        return with(params) {
            val data =
                params.action unpack_ tornaco.apps.shortx.core.proto.action.WebSocketConnect::class.java
            val cv = RuleContextAndVars(params)

            withContext(Dispatchers.IO) {
                var quitError: Throwable? = null

                val callback = object : WSCallback {
                    override suspend fun onMessage(message: String) {
                        super.onMessage(message)
                        contextData.add(
                            data.customContextDataKey.forKey(ContextDataMapping.WebSocket.wsEvent.name)
                                .fallbackOnEmpty(ContextDataMapping.WebSocket.wsEvent.name),
                            WebSocketEvent.WS_MESSAGE.name
                        )
                        contextData.add(
                            data.customContextDataKey.forKey(ContextDataMapping.WebSocket.wsMessage.name)
                                .fallbackOnEmpty(ContextDataMapping.WebSocket.wsMessage.name),
                            message
                        )
                        invokeCallback(data.onMessageList)
                    }

                    override suspend fun onOpen() {
                        super.onOpen()
                        contextData.add(
                            data.customContextDataKey.forKey(ContextDataMapping.WebSocket.wsEvent.name)
                                .fallbackOnEmpty(ContextDataMapping.WebSocket.wsEvent.name),
                            WebSocketEvent.WS_OPEN.name
                        )
                        invokeCallback(data.onOpenList)
                    }

                    override suspend fun onClosed(code: Int, reason: String) {
                        super.onClosed(code, reason)
                        contextData.add(
                            data.customContextDataKey.forKey(ContextDataMapping.WebSocket.wsEvent.name)
                                .fallbackOnEmpty(ContextDataMapping.WebSocket.wsEvent.name),
                            WebSocketEvent.WS_CLOSED.name
                        )
                        contextData.add(
                            data.customContextDataKey.forKey(ContextDataMapping.WebSocket.wsReason.name)
                                .fallbackOnEmpty(ContextDataMapping.WebSocket.wsReason.name),
                            reason
                        )
                        invokeCallback(data.onClosedList)

                        quitError = Error(WebSocketEvent.WS_CLOSED.name)
                    }

                    override suspend fun onFailure(t: Throwable, response: Response?) {
                        super.onFailure(t, response)
                        contextData.add(
                            data.customContextDataKey.forKey(ContextDataMapping.WebSocket.wsEvent.name)
                                .fallbackOnEmpty(ContextDataMapping.WebSocket.wsEvent.name),
                            WebSocketEvent.WS_FAILURE.name
                        )
                        invokeCallback(data.onFailureList)

                        quitError = Error(WebSocketEvent.WS_FAILURE.name, t)
                    }
                }
                val client = WebSocketClient(
                    url = data.url.compileContextAndVars(cv),
                    callback = callback,
                    scope = scope
                )
                var socket: WebSocket? = null
                runCatching {
                    socket = client.start()
                    synchronized(livingWS) {
                        socket?.let { livingWS[data.url] = it }
                        dumpWSMap()
                    }
                    loopUntilQuitWithError(quit = { quitError })
                }.onFailure {
                    logger.e(it, "WebSocketConnect")
                    logger.w("Closing websocket: $socket")
                    // https://datatracker.ietf.org/doc/html/rfc6455#section-7.4
                    socket?.close(1000, "Client closing connection")
                    synchronized(livingWS) {
                        livingWS.remove(data.url, socket)
                        dumpWSMap()
                    }

                    throw it
                }
            }
            Actor.Res.EMPTY
        }
    }

    private suspend fun HandleParams.invokeCallback(actions: List<ProtoAny>?) {
        actions?.let { runSubActions(it) }
    }

    private fun dumpWSMap() {
        logger.d("====== DUMP WS Start ======")
        livingWS.forEach {
            logger.d("WS: ${it.key} ${it.value}")
        }
        logger.d("====== DUMP WS End ======")
    }
}