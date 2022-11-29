package tornaco.apps.shortx

import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import tornaco.apps.shortx.core.proto.action.StartActivity
import tornaco.apps.shortx.core.proto.condition.CurrentPkg
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.engine.EngineListener
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleEngineFactory


class RuleProtoTest {
    @Test
    fun should_convert_byte_array_correctly() {
        val fact = KeyEvent.newBuilder().setKeyCode(20).build().pack_()
        val conditions = listOf(
            CurrentPkg.newBuilder().setPkgName("com.example.pkg").build().pack_(),
            True.newBuilder().build().pack_(),
        )
        val actions = listOf(
            StartActivity.newBuilder().setComponentNameAsString("name").build().pack_()
        )
        val rule = Rule.newBuilder()
            .setFact(fact)
            .addAllConditions(conditions)
            .addAllActions(actions)
            .build()

        val data: ByteArray = rule.toByteArray()

        Rule.parseFrom(data).apply {
            Assert.assertTrue(this.fact is_ KeyEvent::class.java)
            Assert.assertEquals((this.fact unpack_ KeyEvent::class.java).keyCode, 20)
            Assert.assertTrue(this.conditionsList[0].is_(CurrentPkg::class.java))
            Assert.assertEquals(this.conditionsList[0].unpack_(CurrentPkg::class.java).pkgName,
                "com.example.pkg")
            Assert.assertTrue(this.actionsList[0].is_(StartActivity::class.java))
            Assert.assertEquals(this.actionsList[0].unpack_(StartActivity::class.java).componentNameAsString,
                "name")
        }
    }

    @Test
    fun should_call_engine() = runBlocking {
        val fact = KeyEvent.newBuilder().setKeyCode(20).build().pack_()
        val conditions = listOf(
            True.newBuilder().build().pack_(),
        )
        val actions = listOf(
            StartActivity.newBuilder().setComponentNameAsString("name").build().pack_()
        )
        val rule = Rule.newBuilder()
            .setFact(fact)
            .addAllConditions(conditions)
            .addAllActions(actions)
            .build()

        val listener = mockk<EngineListener>(relaxed = true)

        RuleEngineFactory.newRuleEngine(
            context = mockk(),
            handler = mockk(),
            listener = listener
        ).fire(
            KeyEvent.newBuilder().setKeyCode(20).build(),
            listOf(rule)
        )

        verify {
            listener.onFactMatch(rule, any())
            listener.beforeEvaluateAction(rule, any())
        }
    }
}