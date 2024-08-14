package tornaco.apps.shortx.ui.subscribe.code

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.CommonApiResWrapper

interface CodeApi {
    @POST("bindActivationCode")
    suspend fun verify(@Body body: RequestBody): CommonApiResWrapper

    @GET("getSubscriptionConfig2")
    suspend fun getSubscriptionConfig2(): CommonApiResWrapper

    @GET("verifyActivationCode")
    suspend fun verifyActivationCode(@Query("activationCode") code: String): CommonApiResWrapper

    companion object {
        private const val BASE_URL = BuildPropInternal.API_BASE_URL

        fun create(context: Context): CodeApi {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient.Builder().build())
                .build()
            return retrofit.create(CodeApi::class.java)
        }
    }
}