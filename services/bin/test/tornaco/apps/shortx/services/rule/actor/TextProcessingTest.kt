package tornaco.apps.shortx.services.rule.actor

import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.action.TextProcessingToPinyin
import tornaco.apps.shortx.core.proto.action.TextProcessingTrimLength
import tornaco.apps.shortx.core.proto.action.TextProcessingTrimSpace
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.rule.actor.impl.TextProcessing

class TextProcessingTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testToPinyin() {
        val result = TextProcessing.process(
            listOf(TextProcessingToPinyin.newBuilder().build().pack_()),
            "你好我叫小明",
            RuleContextAndVars()
        )
        result shouldBe "NI HAO WO JIAO XIAO MING"
    }

    @Test
    fun testTrimLength() {
        val result = TextProcessing.process(
            listOf(TextProcessingTrimLength.newBuilder().setLength("3").build().pack_()),
            "Hello",
            RuleContextAndVars()
        )
        result shouldBe "Hel"
    }

    @Test
    fun testTrimSpace() {
        val result = TextProcessing.process(
            listOf(TextProcessingTrimSpace.newBuilder().build().pack_()),
            "    Hello  ",
            RuleContextAndVars()
        )
        result shouldBe "Hello"
    }
}