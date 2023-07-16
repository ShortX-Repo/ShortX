package tornaco.apps.shortx.services.ui

import android.content.Context
import android.icu.text.MessageFormat
import kotlinx.serialization.json.Json
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.util.Logger

val i18NIns by lazy { I18NImpl() }

class I18NImpl : I18N {
    override fun get(key: String, args: Map<String, String>): String {
        return I18NCache.get(key, args)
    }
}

object I18NCache {
    private const val DEFAULT_LANGUAGE = "i18n-en-US.json"

    private val logger = Logger("services.I18NCache")
    private val res: MutableMap<String, String> = mutableMapOf()

    fun get(key: String, args: Map<String, String>): String {
        return res[key]?.let {
            MessageFormat(it).format(args)
        } ?: key
    }

    fun init(context: Context, localeName: String) {
        logger.d("init, locale: $localeName")
        kotlin.runCatching { context.assets.open(localeName) }.getOrElse {
            logger.e(it, "Using fallback file: $DEFAULT_LANGUAGE")
            context.assets.open(DEFAULT_LANGUAGE)
        }.use {
            val content = it.bufferedReader(Charsets.UTF_8).readText()
            res.putAll(Json.decodeFromString<Map<String, String>>(content))
        }
    }
}