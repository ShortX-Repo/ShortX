package tornaco.apps.shortx.core.util

import io.kotest.matchers.shouldBe
import org.junit.Test

class ExpressionCalculatorTest {
    @Test
    fun testMath() {
        "2.5 + 3.1 * 4".calculateMathExpression() shouldBe "14.9"
        "10 + (2.5 * 3)".calculateMathExpression() shouldBe "17.5"
        "100 / (2 + 3)".calculateMathExpression() shouldBe "20"
        "4 * (2 + 3.5) - 6 / 2".calculateMathExpression() shouldBe "19"
        "3.2 + 4.8 / 2 - 1".calculateMathExpression() shouldBe "4.6"
    }

    @Test
    fun testCustomOp() {
        val goodEvaluator: (String) -> String =
            { expr -> (expr.toIntOrNull()?.times(2) ?: throw Exception("bad")).toString() }
        val evaluators = mapOf("math" to goodEvaluator)

        println(processExpressions("测试 @math{5}", evaluators))             // 测试 10
        println(processExpressions("测试错误 @math{abc}", evaluators))       // 测试错误 @math{abc}
        println(processExpressions("转义 \\@math{5}", evaluators))           // 转义 @math{5}
    }

    @Test
    fun testDefaultOp() {
        processExpressions("测试 @math{5}") shouldBe "测试 5"
        processExpressions("测试 @math{5 + 2}") shouldBe "测试 7"
        processExpressions("测试 @math{5 * 2}") shouldBe "测试 10"
        processExpressions("测试错误 @math{abc}") shouldBe "测试错误 abc"
        processExpressions("转义 \\@math{5}") shouldBe "转义 @math{5}"
    }
}