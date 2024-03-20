package tornaco.apps.shortx.services.rule.cond

import io.kotest.matchers.shouldBe
import org.junit.Test
import tornaco.apps.shortx.core.proto.condition.GlobalVarOp
import tornaco.apps.shortx.services.rule.cond.impl.EvaluateContextVar

class EvaluateContextVarTest {

    @Test
    fun `test greater than`() {
        EvaluateContextVar.evaluate("1.2", GlobalVarOp.GreaterThan, "1.1") shouldBe true
        EvaluateContextVar.evaluate("1.2", GlobalVarOp.GreaterThan, "1.3") shouldBe false
        EvaluateContextVar.evaluate("22", GlobalVarOp.GreaterThan, "21") shouldBe true
        EvaluateContextVar.evaluate("22", GlobalVarOp.GreaterThan, "23") shouldBe false
        EvaluateContextVar.evaluate("", GlobalVarOp.GreaterThan, "23") shouldBe false
        EvaluateContextVar.evaluate("22", GlobalVarOp.GreaterThan, "") shouldBe false
        EvaluateContextVar.evaluate("22", GlobalVarOp.GreaterThan, "22") shouldBe false
        EvaluateContextVar.evaluate("HELLO", GlobalVarOp.GreaterThan, "22") shouldBe false
    }

    @Test
    fun `test greater than or equal`() {
        EvaluateContextVar.evaluate("1.2", GlobalVarOp.GreaterThanOrEQ, "1.2") shouldBe true
        EvaluateContextVar.evaluate(
            "888888888",
            GlobalVarOp.GreaterThanOrEQ,
            "888888888"
        ) shouldBe true
        EvaluateContextVar.evaluate("HELLO", GlobalVarOp.GreaterThanOrEQ, "22") shouldBe false
    }

    @Test
    fun `test equal`() {
        EvaluateContextVar.evaluate("HELLO", GlobalVarOp.EqualTo, "Hello") shouldBe false
        EvaluateContextVar.evaluate("HELLO", GlobalVarOp.EqualTo, "HELLO") shouldBe true
        EvaluateContextVar.evaluate("HELLO", GlobalVarOp.EqualTo, "HELLO ") shouldBe false
    }
}