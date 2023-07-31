package tornaco.apps.shortx.ui.base

import android.content.Context
import android.icu.text.MessageFormat
import android.preference.PreferenceManager
import kotlinx.serialization.json.Json
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.common.AppLanguage
import tornaco.apps.shortx.core.res.appLanguageToAssetsFileName
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.R

val i18NIns by lazy { I18NImpl() }

class I18NImpl : I18N {
    override fun get(key: String, args: Map<String, String>): String {
        return I18NCache.get(key, args)
    }
}

object I18NCache {
    private const val DEFAULT_LANGUAGE = "i18n-en-US.json"

    private val logger = Logger("I18NCache")
    private val res: MutableMap<String, String> = mutableMapOf()

    fun get(key: String, args: Map<String, String>): String {
        return res[key]?.let {
            MessageFormat(it).format(args)
        } ?: key
    }

    fun init(context: Context) {
        val preferredAppLanguage = AppLanguage.valueOf(
            PreferenceManager.getDefaultSharedPreferences(context)
                .getString("i18n", AppLanguage.UNRECOGNIZED.name) ?: AppLanguage.UNRECOGNIZED.name
        )
        logger.d("preferredAppLanguage: $preferredAppLanguage")

        val i18NFileName = preferredAppLanguage.appLanguageToAssetsFileName {
            context.getString(R.string.ui_i18n_name)
        }

        logger.d("i18NFileName: $i18NFileName")
        kotlin.runCatching { context.assets.open(i18NFileName) }.getOrElse {
            logger.e(it, "Using fallback file: $DEFAULT_LANGUAGE")
            context.assets.open(DEFAULT_LANGUAGE)
        }.use {
            val content = it.bufferedReader(Charsets.UTF_8).readText()
            res.putAll(Json.decodeFromString<Map<String, String>>(content))
        }
    }
}