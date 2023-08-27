package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
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

    @Test
    fun testWriteStringAuto() {
        val originalGV = GlobalVar.newBuilder()
            .setType(StringVar.newBuilder().build().pack_())
            .setVar(StringVar.newBuilder().setValue("Hello").build().pack_())
            .setName("Test")
            .build()

        val updatedGV =
            WriteGlobalVar.handleGVOp(originalGV, WriteGlobalVarOp.WriteGlobalVarOp_Auto, "World")
        Assert.assertEquals(listOf("World"), updatedGV.valueList())
    }

    @Test
    fun testWriteStringListAuto() {
        val originalGV = GlobalVar.newBuilder()
            .setType(StringListVar.newBuilder().build().pack_())
            .setVar(StringListVar.newBuilder().addAllValues(listOf("Hello")).build().pack_())
            .setName("Test")
            .build()

        val updatedGV =
            WriteGlobalVar.handleGVOp(originalGV, WriteGlobalVarOp.WriteGlobalVarOp_Auto, "World")
        Assert.assertEquals(listOf("Hello", "World"), updatedGV.valueList())
    }

    @Test
    fun testWriteStringAppendToLast() {
        val originalGV = GlobalVar.newBuilder()
            .setType(StringVar.newBuilder().build().pack_())
            .setVar(StringVar.newBuilder().setValue("Hello").build().pack_())
            .setName("Test")
            .build()

        val updatedGV =
            WriteGlobalVar.handleGVOp(
                originalGV,
                WriteGlobalVarOp.WriteGlobalVarOp_AppendToLast,
                "World"
            )
        Assert.assertEquals(listOf("World"), updatedGV.valueList())
    }
}