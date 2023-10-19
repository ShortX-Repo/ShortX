package tornaco.apps.shortx

import com.google.protobuf.Descriptors
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.action.InjectKeyCode
import tornaco.apps.shortx.core.proto.action.StartActivity
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.condition.CurrentPkgList
import tornaco.apps.shortx.core.proto.condition.True
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.RuleEngineFactory


class RuleProtoTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun should_convert_byte_array_correctly() {
        val fact = KeyEvent.newBuilder().setKeyCode(20).build().pack_()
        val conditions = listOf(
            CurrentPkgList.newBuilder().addAllPkgs(
                listOf(
                    AppPkg.newBuilder().setPkgName("com.example.pkg").setUserId(2).build()
                )
            ).build().pack_(),
            True.newBuilder().build().pack_(),
        )
        val actions = listOf(
            StartActivity.newBuilder().setComponentNameAsString("name").build().pack_()
        )
        val rule = Rule.newBuilder()
            .addFacts(fact)
            .addAllConditions(conditions)
            .addAllActions(actions)
            .build()

        val data: ByteArray = rule.toByteArray()

        Rule.parseFrom(data).apply {
            Assert.assertTrue(this.factsList[0] is_ KeyEvent::class.java)
            Assert.assertEquals((this.factsList[0] unpack_ KeyEvent::class.java).keyCode, 20)
            Assert.assertTrue(this.conditionsList[0].is_(CurrentPkgList::class.java))
            Assert.assertEquals(
                this.conditionsList[0].unpack_(CurrentPkgList::class.java).pkgsList[0].pkgName,
                "com.example.pkg"
            )
            Assert.assertTrue(this.actionsList[0].is_(StartActivity::class.java))
            Assert.assertEquals(
                this.actionsList[0].unpack_(StartActivity::class.java).componentNameAsString,
                "name"
            )
        }
    }

    @Test
    fun should_call_engine() {
        return runBlocking {
            val fact = KeyEvent.newBuilder().setKeyCode(20).build().pack_()
            val conditions = listOf(
                True.newBuilder().build().pack_(),
            )
            val actions = listOf(
                StartActivity.newBuilder().setComponentNameAsString("name").build().pack_()
            )
            val rule = Rule.newBuilder()
                .addFacts(fact)
                .addAllConditions(conditions)
                .addAllActions(actions)
                .build()

            val mockShortXContext = mockk<ShortXContext>(relaxed = true)
            every { mockShortXContext.rule.isRuleFeatureEnabled() } returns true

            RuleEngineFactory.newRuleEngine(
                shortXContext = mockShortXContext,
                androidContext = mockk(),
                handler = mockk(),
                onJobCancelled = {},
                onJobCompleted = {},
                onJobStarted = {},
                actionEvaluateResultListener = { },
                actionEvaluateStartListener = {},
                conditionEvaluateResultListener = {}
            ).fire(
                KeyEvent.newBuilder().setKeyCode(20).build(),
                listOf(rule)
            ) { emptyMap() }
        }
    }

    @Test
    fun test_unpack_diff_type() {
        val action1 = InjectKeyCode.newBuilder().setId("123").setIsDisabled(true).build()

        // ERROR: val cast = action1.pack_().unpack_(NoAction::class.java)

        val idDescriptor: Descriptors.FieldDescriptor =
            action1.descriptorForType.findFieldByName("id")
        val id: String = action1.getField(idDescriptor) as String

        val isDisabledDescriptor: Descriptors.FieldDescriptor =
            action1.descriptorForType.findFieldByName("isDisabled")
        val isDisabled: Boolean = action1.getField(isDisabledDescriptor) as Boolean

        println(id)
        println(isDisabled)

        Assert.assertEquals("123", id)
        Assert.assertTrue(isDisabled)
    }
}