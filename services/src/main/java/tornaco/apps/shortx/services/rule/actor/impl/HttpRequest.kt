package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import tornaco.apps.shortx.core.proto.action.HttpRequestAdapter
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
        logger.p("url: $url")
        handleUrl(url, data.adapter)
    }


    fun handleUrl(url: String, adapter: HttpRequestAdapter) {
        URL(url).readText().apply {
            logger.d("response: $this")
            when (adapter) {
                HttpRequestAdapter.Json -> {
                    val jsonElement = Json.parseToJsonElement(this)
                    val map: Map<String, JsonElement> = jsonElement.jsonObject.toMap()
                    logger.d("map: $map")
                    logger.d(
                        "map: ${
                            map.map {
                                "${it.key} - ${if (it.value is JsonPrimitive) "P" else if (it.value is JsonArray) "ARRAY" else "??"}"
                            }
                        }"
                    )
                }

                else -> {
                }
            }
        }
    }
}