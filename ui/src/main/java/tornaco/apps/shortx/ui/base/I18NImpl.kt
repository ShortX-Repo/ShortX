package tornaco.apps.shortx.ui.base

import android.content.Context
import android.icu.text.MessageFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import tornaco.apps.shortx.core.util.Logger

class I18NImpl : I18N {
    override fun get(key: String, args: Map<String, String>): String {
        return I18NCache.get(key, args)
    }
}

object I18NCache {
    private const val DEFAULT_LANGUAGE = "en-US"

    private val logger = Logger("I18NCache")
    private val res: MutableMap<String, String> = mutableMapOf()

    fun get(key: String, args: Map<String, String>): String {
        return res[key]?.let {
            MessageFormat(it).format(args)
        } ?: key
    }

    fun init(context: Context) {
        val locale = kotlin.runCatching {
            context.resources.configuration.locales.get(0).let {
                it.language + "-" + it.country
            }
        }.getOrElse {
            logger.e(it, "Get locale error.")
            DEFAULT_LANGUAGE
        }
        logger.d("locale: $locale")
        val fileName = "i18n-$locale.json"
        val fallbackFileName = "i18n-$DEFAULT_LANGUAGE.json"
        kotlin.runCatching { context.assets.open(fileName) }.getOrElse {
            logger.w("Using fallback file: $fallbackFileName")
            context.assets.open(fallbackFileName)
        }.use {
            val content = it.bufferedReader(Charsets.UTF_8).readText()
            res.putAll(Json.decodeFromString<Map<String, String>>(content))
        }
    }
}