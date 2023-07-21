package tornaco.apps.ui

import android.webkit.URLUtil
import android.webkit.URLUtil.isNetworkUrl
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.Test
import tornaco.apps.shortx.core.rule.repo.inflateOnlineFileUrl

class OnlineRepoTest {
    @Test
    fun testUrlInflate() {
        mockkStatic(URLUtil::class) {
            every {
                isNetworkUrl(any())
            } returns true

            val fileUrl = inflateOnlineFileUrl(
                "https://repo/main/index.json",
                "https://repo/main/da/Rule1.txt",
                "da"
            )
            Assert.assertEquals("https://repo/main/da/Rule1.txt", fileUrl)
        }
    }

    @Test
    fun testUrlInflateWithoutFullName() {
        mockkStatic(URLUtil::class) {
            every {
                isNetworkUrl(any())
            } returns false

            val fileUrl = inflateOnlineFileUrl(
                "https://repo/main/index.json",
                "Rule1.txt",
                "da"
            )
            Assert.assertEquals("https://repo/main/da/Rule1.txt", fileUrl)
        }
    }
}