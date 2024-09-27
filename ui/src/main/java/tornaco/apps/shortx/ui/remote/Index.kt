package tornaco.apps.shortx.ui.remote

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import tornaco.apps.shortx.core.rule.repo.Index
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import java.util.concurrent.TimeUnit


class OnlineRepo(private val context: Context) {
    private val logger = Logger("OnlineRepo")
    private val logging = HttpLoggingInterceptor {
        logger.d(it)
    }.apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    companion object {
        private val gson = Gson()
    }

    suspend fun index(indexUrl: String, enableChucker: Boolean): Index {
        val client = OkHttpClient.Builder()
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(logging)
            .apply {
                if (enableChucker) {
                    addInterceptor(ChuckerInterceptor(context))
                }
            }
            .build()
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