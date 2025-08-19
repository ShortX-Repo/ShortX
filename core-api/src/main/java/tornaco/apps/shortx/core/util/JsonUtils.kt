package tornaco.apps.shortx.core.util

import com.google.gson.GsonBuilder
import com.google.gson.stream.MalformedJsonException

private val gson by lazy {
    GsonBuilder().setPrettyPrinting().create()
}

@Throws(MalformedJsonException::class)
fun formatJsonString(jsonString: String): String {
    val jsonElement = gson.fromJson(jsonString, Any::class.java)
    return gson.toJson(jsonElement)
}