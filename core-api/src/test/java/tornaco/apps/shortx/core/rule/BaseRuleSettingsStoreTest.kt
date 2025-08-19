package tornaco.apps.shortx.core.rule

import io.kotest.assertions.throwables.shouldThrow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.store.BaseRuleSettingsStore
import java.io.File

class BaseRuleSettingsStoreTest {
    @Test
    fun `should throw exception when add rule with extras`() {
        runTest {
            val store = object : BaseRuleSettingsStore(this, File("out/tests/rule.store")) {

            }
            shouldThrow<IllegalArgumentException> {
                store.addRule(
                    Rule.newBuilder().addAllExtras(listOf(StringPair.getDefaultInstance())).build()
                )
            }
        }
    }
}