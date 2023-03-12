package tornaco.apps.shortx.ui.remote

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RemoteApi {
    @GET("Tornaco/ShortX-Release/main/files/mvel-actions.json")
    suspend fun getActions(): List<RemoteExecuteMVELAction>

    companion object {
        private const val BASE_GITHUB_API = "https://raw.githubusercontent.com/"

        fun create(context: Context): RemoteApi {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_GITHUB_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(ChuckerInterceptor.Builder(context).build()).build()
                )
                .build()
            return retrofit.create(RemoteApi::class.java)
        }
    }
}