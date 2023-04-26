package tornaco.apps.shortx.ui.share

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val headerSplitter = "${System.lineSeparator()}###------###${System.lineSeparator()}"
private val gson by lazy { Gson() }

fun String.addShareContentHeader(header: Map<String, String>): String {
    val json = gson.toJson(header)
    return this + headerSplitter + json
}

fun String.getContentHeader(): Map<String, String> {
    val headerString = substringAfter(delimiter = headerSplitter, missingDelimiterValue = "")
    return kotlin.runCatching {
        val type = object : TypeToken<Map<String, String>>() {}.type
        gson.fromJson(headerString.trim(), type) as Map<String, String>
    }.getOrDefault(emptyMap())
}