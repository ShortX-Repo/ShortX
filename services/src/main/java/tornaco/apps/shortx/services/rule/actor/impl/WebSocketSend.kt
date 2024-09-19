package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object WebSocketSend : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WebSocketSend::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        with(params) {
            val data =
                action unpack_ tornaco.apps.shortx.core.proto.action.WebSocketSend::class.java
            val cv = RuleContextAndVars(params)
            withContext(Dispatchers.IO) {
                val url = data.url.compileContextAndVars(cv)
                val ws = requireNotNull(WebSocketConnect.getLivingWSByUrl(url)) {
                    "WebSocket connection not found: $url, please connect first."
                }

                val result = ws.send(data.message.compileContextAndVars(cv))
                logger.d("WS send result: $result")
                require(result) {
                    "Send message fail."
                }
            }
        }
    }
}