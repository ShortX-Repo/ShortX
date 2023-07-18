package tornaco.apps.shortx.services.rule.actor

import com.google.gson.Gson
import org.junit.Test
import tornaco.apps.shortx.core.res.Remix

data class Index(
    val directActions: List<Item>,
    val rules: List<Item>,
)

data class Item(
    val fileUrl: String,
    val title: String,
    val description: String,
    val tag: String? = null,
    val icon: String? = null
)

class OnlineRepoTest {
    @Test
    fun outputJson() {
        val das = listOf(
            Item(
                fileUrl = "www.xxx.xxx",
                title = "Title",
                description = "Description",
                tag = "Life"
            ),
            Item(
                fileUrl = "www.xxx.xxx",
                title = "Title",
                description = "Description",
                tag = "System",
                icon = Remix.System.forbid_2_fill
            )
        )

        Index(directActions = das, rules = emptyList()).apply {
            println(
                Gson().toJson(this)
            )
        }
    }
}