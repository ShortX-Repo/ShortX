package tornaco.apps.shortx.ui.remote

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.util.Logger
import java.util.concurrent.TimeUnit

@DoNotStrip
data class Index(
    val directActions: List<Item>,
    val rules: List<Item>,
)

@DoNotStrip
data class Item(
    val fileUrl: String,
    val title: String,
    val description: String,
    val author: String,
    val tag: String? = null,
    val icon: String? = null,
    val iconColor: String? = null,
)


class OnlineRepo(private val context: Context) {
    private val logger = Logger("OnlineRepo")
    private val logging = HttpLoggingInterceptor {
        logger.d(it)
    }.apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    private val client by lazy {
        OkHttpClient.Builder()
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(logging)
            .addInterceptor(ChuckerInterceptor(context))
            .build()
    }

    companion object {
        private val gson = Gson()
    }

    suspend fun index(indexUrl: String): Index {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(indexUrl)
                .method("GET", null)
                .build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: "{}"
            gson.fromJson(responseBody, Index::class.java)
        }
    }
}