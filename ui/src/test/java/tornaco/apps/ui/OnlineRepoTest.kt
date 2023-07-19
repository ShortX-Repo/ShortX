package tornaco.apps.ui

import com.google.gson.Gson
import org.junit.Test
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.remote.Index
import tornaco.apps.shortx.ui.remote.Item

class OnlineRepoTest {
    @Test
    fun outputJson() {
        val das = listOf(
            Item(
                fileUrl = "www.xxx.xxx",
                title = "Title",
                description = "Description",
                tag = "Life",
                author = "ShortX",
                icon = Remix.System.forbid_2_fill,
                iconColor = "#ff4081"
            ),
            Item(
                fileUrl = "www.xxx.xxx",
                title = "Title",
                description = "Description",
                tag = "System",
                icon = Remix.System.forbid_2_fill,
                author = "ShortX",
                iconColor = "#ff4081"
            )
        )

        Index(directActions = das, rules = emptyList()).apply {
            println(
                Gson().toJson(this)
            )
        }
    }
}