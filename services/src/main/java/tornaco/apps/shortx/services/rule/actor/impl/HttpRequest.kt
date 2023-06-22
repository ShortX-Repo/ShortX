package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import org.mvel2.MVEL
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.action.HttpRequestJsonMapAdapter
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.net.URL


object HttpRequest : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.HttpRequest::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.HttpRequest::class.java
        val url = data.url
        logger.d("url: $url")
        handleUrl(url, data.adapter).apply {
            params.contextData.add(ContextDataMapping.HttpRequest.httpRequestRet.name, this)
        }
    }

    fun handleUrl(url: String, adapter: ProtoAny): String {
        URL(url).readText().apply {
            logger.d("response: $this")
            return if (adapter is_ HttpRequestJsonMapAdapter::class.java) {
                val jsonAdapter = adapter unpack_ HttpRequestJsonMapAdapter::class.java
                val jsonElement = Json.parseToJsonElement(this)
                val map: Map<String, JsonElement> = jsonElement.jsonObject.toMap()
                logger.d("map: $map")
                val expression = jsonAdapter.expression
                logger.d("expression: $expression")
                val res = MVEL.executeExpression(MVEL.compileExpression(expression), map)
                val resAsString = if (res is JsonPrimitive) {
                    res.content
                } else {
                    res.toString()
                }
                logger.d("resAsString: $resAsString")
                resAsString
            } else {
                this.trim()
            }
        }
    }
}