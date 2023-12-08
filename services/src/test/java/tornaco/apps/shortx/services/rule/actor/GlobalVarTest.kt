package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.StringListVar
import tornaco.apps.shortx.core.proto.gv.StringVar
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.util.logAdapter

class GlobalVarTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testReplaceGlobalVar() {
        val res = "hello world".replaceGlobalVarValues {
            emptyList()
        }
        Assert.assertEquals("hello world", res)
    }

    @Test
    fun testReplaceGlobalVarWithoutValue() {
        val res = "hello world globalVarOf$".replaceGlobalVarValues {
            emptyList()
        }
        Assert.assertEquals("hello world globalVarOf$", res)
    }

    @Test
    fun testReplaceGlobalVarWithValue() {
        val res = "hello world globalVarOf\$name".replaceGlobalVarValues {
            listOf(
                GlobalVar.newBuilder().setName("name")
                    .setType(StringVar.getDefaultInstance().pack_())
                    .setVar(
                        StringVar.newBuilder().setValue("Nick").build().pack_()
                    ).build()
            )
        }
        Assert.assertEquals("hello world Nick", res)
    }

    @Test
    fun testReplaceGlobalVarWithListValue() {
        val res = "hello world globalVarOf\$name".replaceGlobalVarValues {
            listOf(
                GlobalVar.newBuilder().setName("name")
                    .setType(StringListVar.getDefaultInstance().pack_())
                    .setVar(
                        StringListVar.newBuilder().addValues("Nick").addValues("Guo").build()
                            .pack_()
                    ).build()
            )
        }
        Assert.assertEquals("hello world Nick, Guo", res)
    }

    @Test
    fun testReplaceGlobalVarWithListValue_0() {
        val res = "hello world globalVarOf\$name[0]".replaceGlobalVarValues {
            listOf(
                GlobalVar.newBuilder().setName("name")
                    .setType(StringListVar.getDefaultInstance().pack_())
                    .setVar(
                        StringListVar.newBuilder().addValues("Nick").addValues("Guo").build()
                            .pack_()
                    ).build()
            )
        }
        Assert.assertEquals("hello world Nick", res)
    }

    @Test
    fun testReplaceGlobalVarWithListValue_OutofBounds() {
        val res = "hello world globalVarOf\$name[2]".replaceGlobalVarValues {
            listOf(
                GlobalVar.newBuilder().setName("name")
                    .setType(StringListVar.getDefaultInstance().pack_())
                    .setVar(
                        StringListVar.newBuilder().addValues("Nick").addValues("Guo").build()
                            .pack_()
                    ).build()
            )
        }
        Assert.assertEquals("hello world Nick, Guo[2]", res)
    }

    @Test
    fun testReplaceGlobalVarWithListValue_0_NoVaue() {
        val res = "hello world globalVarOf\$name[0]".replaceGlobalVarValues {
            listOf(
                GlobalVar.newBuilder().setName("name")
                    .setType(StringListVar.getDefaultInstance().pack_())
                    .setVar(
                        StringListVar.newBuilder().build()
                            .pack_()
                    ).build()
            )
        }
        Assert.assertEquals("hello world [0]", res)
    }
}