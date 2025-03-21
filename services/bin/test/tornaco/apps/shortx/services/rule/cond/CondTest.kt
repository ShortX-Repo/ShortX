package tornaco.apps.shortx.services.rule.cond

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import tornaco.apps.shortx.core.proto.condition.OuterConditions

class CondTest {

    @Test
    @Ignore
    fun `should handle all proto conditions`() {
        val allConditionNames =
            OuterConditions.getDescriptor().messageTypes.map { it.name }.filter {
                !listOf("ConditionOperatorPayload", "GlobalVarOpPayload").contains(it)
            }

        allConditionNames.forEach { protoName ->
            Assert.assertTrue(
                "Should contains handler for: $protoName",
                PrebuiltCond.values().map { it.cond.condClass().simpleName }
                    .contains(protoName)
            )
        }
    }
}