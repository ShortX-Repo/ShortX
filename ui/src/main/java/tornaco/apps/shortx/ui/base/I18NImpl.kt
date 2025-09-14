package tornaco.apps.shortx.ui.base

import android.content.Context
import android.icu.text.MessageFormat
import kotlinx.serialization.json.Json
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.R

val i18NIns by lazy { I18NImpl() }

class I18NImpl : I18N {
    override fun get(key: String, args: Map<String, String>, fallback: String?): String {
        return I18NCache.get(key, args, fallback)
    }
}

object I18NCache {
    private const val DEFAULT_LANGUAGE = "i18n/en-US/i18n-en-US.json"
    private const val DEFAULT_LOCALE_DIR = "i18n/en-US"

    private val logger = Logger("I18NCache")
    private val res: MutableMap<String, String> = mutableMapOf()

    fun get(key: String, args: Map<String, String>, fallback: String?): String {
        return res[key]?.let {
            MessageFormat(it).format(args)
        } ?: (fallback ?: key)
    }

    fun init(context: Context) {
        val localeName = context.getString(R.string.ui_i18n_name)
        logger.d("localeName: $localeName")
        // 加载指定语言目录下的所有JSON文件
        loadI18nFromDirectory(context, localeName)
    }

    private fun loadI18nFromDirectory(context: Context, localeName: String) {
        try {
            // 首先尝试从语言目录加载
            val localeDir = "i18n/$localeName"
            val dirFiles = context.assets.list(localeDir)

            if (dirFiles != null && dirFiles.isNotEmpty()) {
                // 筛选JSON文件并排序
                val jsonFiles = dirFiles.filter { it.endsWith(".json") }.sorted()
                logger.d("Found JSON files in directory $localeDir: $jsonFiles")

                jsonFiles.forEach { fileName ->
                    loadSingleFile(context, "$localeDir/$fileName")
                }
            } else {
                // 如果目录不存在或为空，尝试加载旧格式的单文件
                logger.e("Directory $localeDir not found, fallback")
                loadFallbackLanguage(context)
            }
        } catch (e: Exception) {
            logger.e(e, "Error loading i18n files from directory, using fallback")
            // 尝试加载默认语言目录
            loadFallbackLanguage(context)
        }
    }

    private fun loadFallbackLanguage(context: Context) {
        try {
            val dirFiles = context.assets.list(DEFAULT_LOCALE_DIR)
            if (dirFiles != null && dirFiles.isNotEmpty()) {
                val jsonFiles = dirFiles.filter { it.endsWith(".json") }.sorted()
                logger.d("Loading fallback files from $DEFAULT_LOCALE_DIR: $jsonFiles")

                jsonFiles.forEach { fileName ->
                    loadSingleFile(context, "$DEFAULT_LOCALE_DIR/$fileName")
                }
            } else {
                // 最后的降级方案：加载根目录的默认文件
                loadSingleFile(context, DEFAULT_LANGUAGE)
            }
        } catch (e: Exception) {
            logger.e(e, "Error loading fallback language")
            loadSingleFile(context, DEFAULT_LANGUAGE)
        }
    }

    private fun loadSingleFile(context: Context, filePath: String) {
        logger.d("loadSingleFile - $filePath")
        kotlin.runCatching {
            context.assets.open(filePath).use { inputStream ->
                val content = inputStream.bufferedReader(Charsets.UTF_8).readText()
                val translations = Json.decodeFromString<Map<String, String>>(content)
                res.putAll(translations.mapKeys { it.key.lowercase() })
                logger.d("Loaded $filePath with ${translations.size} translations")
            }
        }.getOrElse { exception ->
            logger.e(exception, "Failed to load $filePath")
        }
    }
}