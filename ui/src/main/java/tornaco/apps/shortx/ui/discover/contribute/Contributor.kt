package tornaco.apps.shortx.ui.discover.contribute

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import tornaco.apps.shortx.core.annotations.DoNotStrip

@DoNotStrip
data class Contributor(
    val login: String, // 用户名
    val avatar_url: String, // 头像 URL
    val contributions: Int // 贡献次数
)

interface GitHubApi {
    @GET("repos/ShortX-Repo/ShortX-Files/contributors")
    suspend fun getContributors(
    ): List<Contributor>
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val gitHubApi = retrofit.create(GitHubApi::class.java)

suspend fun fetchContributors(token: String? = null): List<Contributor> {
    return gitHubApi.getContributors()
}