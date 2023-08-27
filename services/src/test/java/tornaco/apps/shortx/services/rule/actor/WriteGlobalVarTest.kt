package tornaco.apps.shortx.services.rule.actor

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.action.WriteGlobalVarOp
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.StringListVar
import tornaco.apps.shortx.core.proto.gv.StringVar
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.core.util.logAdapter
import tornaco.apps.shortx.services.rule.actor.impl.WriteGlobalVar

class WriteGlobalVarTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    private val originalStringGV = GlobalVar.newBuilder()
        .setType(StringVar.newBuilder().build().pack_())
        .setVar(StringVar.newBuilder().setValue("Hello").build().pack_())
        .setName("Test")
        .build()

    private val originalStringListGV = GlobalVar.newBuilder()
        .setType(StringListVar.newBuilder().build().pack_())
        .setVar(StringListVar.newBuilder().addAllValues(listOf("Hello", "Nice")).build().pack_())
        .setName("Test")
        .build()

    @Test
    fun testWriteStringAuto() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringGV,
                WriteGlobalVarOp.WriteGlobalVarOp_Auto,
                "World"
            )
        updatedGV.valueList() shouldBe listOf("World")
    }

    @Test
    fun testWriteStringListAuto() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringListGV,
                WriteGlobalVarOp.WriteGlobalVarOp_Auto,
                "World"
            )
        updatedGV.valueList() shouldBe listOf("Hello", "Nice", "World")
    }

    @Test
    fun testWriteStringAppendToLast() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringGV,
                WriteGlobalVarOp.WriteGlobalVarOp_AppendToLast,
                "World"
            )
        updatedGV.valueList() shouldBe listOf("HelloWorld")
    }

    @Test
    fun testWriteStringListAppendToLast() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringListGV,
                WriteGlobalVarOp.WriteGlobalVarOp_AppendToLast,
                "World"
            )
        updatedGV.valueList() shouldBe listOf("Hello", "Nice", "World")
    }

    @Test
    fun testWriteStringAppendToFirst() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringGV,
                WriteGlobalVarOp.WriteGlobalVarOp_AppendToFirst,
                "World"
            )
        updatedGV.valueList() shouldBe listOf("WorldHello")
    }

    @Test
    fun testWriteStringListAppendToFirst() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringListGV,
                WriteGlobalVarOp.WriteGlobalVarOp_AppendToFirst,
                "World"
            )
        updatedGV.valueList() shouldBe listOf("World", "Hello", "Nice")
    }

    @Test
    fun testWriteStringOverride() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringGV,
                WriteGlobalVarOp.WriteGlobalVarOp_Override,
                "World"
            )
        updatedGV.valueList() shouldBe listOf("World")
    }

    @Test
    fun testWriteStringListOverride() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringListGV,
                WriteGlobalVarOp.WriteGlobalVarOp_Override,
                "World"
            )
        updatedGV.valueList() shouldBe listOf("World")
    }

    @Test
    fun testWriteStringDeleteValue() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringGV,
                WriteGlobalVarOp.WriteGlobalVarOp_DeleteValue,
                "Hell"
            )
        updatedGV.valueList() shouldBe listOf("o")
    }

    @Test
    fun testWriteStringListDeleteValue() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringListGV,
                WriteGlobalVarOp.WriteGlobalVarOp_DeleteValue,
                "Hello"
            )
        updatedGV.valueList() shouldBe listOf("Nice")
    }

    @Test
    fun testWriteStringDeleteLast() {
        shouldThrow<IllegalArgumentException> {
            WriteGlobalVar.handleGVOp(
                originalStringGV,
                WriteGlobalVarOp.WriteGlobalVarOp_DeleteLast,
                "Hell"
            )
        }
    }

    @Test
    fun testWriteStringListDeleteLast() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringListGV,
                WriteGlobalVarOp.WriteGlobalVarOp_DeleteLast,
                "Value will not be used"
            )
        updatedGV.valueList() shouldBe listOf("Hello")
    }

    @Test
    fun testWriteStringDeleteFirst() {
        shouldThrow<IllegalArgumentException> {
            WriteGlobalVar.handleGVOp(
                originalStringGV,
                WriteGlobalVarOp.WriteGlobalVarOp_DeleteFirst,
                "Hell"
            )
        }
    }

    @Test
    fun testWriteStringListDeleteFirst() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringListGV,
                WriteGlobalVarOp.WriteGlobalVarOp_DeleteFirst,
                "Value will not be used"
            )
        updatedGV.valueList() shouldBe listOf("Nice")
    }

    @Test
    fun testWriteStringClear() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringGV,
                WriteGlobalVarOp.WriteGlobalVarOp_Clear,
                "Should not be used."
            )
        updatedGV.valueList(noValueReturn = { emptyList() }) shouldBe emptyList()
    }

    @Test
    fun testWriteStringListClear() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringListGV,
                WriteGlobalVarOp.WriteGlobalVarOp_Clear,
                "Value will not be used"
            )
        updatedGV.valueList() shouldBe emptyList()
    }

    @Test
    fun testWriteStringReverse() {
        shouldThrow<IllegalArgumentException> {
            WriteGlobalVar.handleGVOp(
                originalStringGV,
                WriteGlobalVarOp.WriteGlobalVarOp_Reverse,
                "Should not be used."
            )
        }
    }

    @Test
    fun testWriteStringListReverse() {
        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalStringListGV,
                WriteGlobalVarOp.WriteGlobalVarOp_Reverse,
                "Value will not be used"
            )
        updatedGV.valueList() shouldBe listOf("Nice", "Hello")
    }
}