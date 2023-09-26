package tornaco.apps.shortx.services.rule

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.fact.AnyFact
import tornaco.apps.shortx.core.proto.fact.AppProcessStarted
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.logAdapter


class FactMatcherImplTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun should_match_when_has_AnyFact() {
        val impl = FactMatcherImpl {
            throw Error()
        }
        impl.match(
            ActivityStarted.getDefaultInstance().pack_(), listOf(
                AppProcessStarted.getDefaultInstance().pack_(),
                AnyFact.getDefaultInstance().pack_()
            )
        ) shouldBe true
    }

    @Test
    fun should_not_match_when_type_different() {
        val impl = FactMatcherImpl {
            throw Error()
        }
        impl.match(
            ActivityStarted.getDefaultInstance().pack_(), listOf(
                AppProcessStarted.getDefaultInstance().pack_(),
            )
        ) shouldBe false
    }
}