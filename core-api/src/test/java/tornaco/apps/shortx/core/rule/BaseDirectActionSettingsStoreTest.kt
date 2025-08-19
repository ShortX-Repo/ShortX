package tornaco.apps.shortx.core.rule

import io.kotest.assertions.throwables.shouldThrow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.store.BaseDirectActionSettingsStore
import java.io.File

class BaseDirectActionSettingsStoreTest {
    @Test
    fun `should throw exception when add rule with extras`() {
        runTest {
            val store = object : BaseDirectActionSettingsStore(this, File("out/tests/da.store")) {

            }
            shouldThrow<IllegalArgumentException> {
                store.addDirectAction(
                    DirectAction.newBuilder().addAllExtras(listOf(StringPair.getDefaultInstance()))
                        .build()
                )
            }
        }
    }
}