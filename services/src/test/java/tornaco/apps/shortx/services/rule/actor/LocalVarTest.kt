package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.gv.LocalVar
import tornaco.apps.shortx.core.proto.gv.StringListVar
import tornaco.apps.shortx.core.proto.gv.StringVar
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.util.logAdapter

class LocalVarTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testReplaceLocalVar() {
        val localVars = LocalVars.obtain()
        val res = "hello world".replaceLocalVarValues(localVars)
        Assert.assertEquals("hello world", res)
    }

    @Test
    fun testReplaceLocalVarWithoutValue() {
        val localVars = LocalVars.obtain()
        val res = "hello world localVarOf$".replaceLocalVarValues(localVars)
        Assert.assertEquals("hello world localVarOf$", res)
    }

    @Test
    fun testReplaceLocalVarWithValue() {
        val localVars = LocalVars.obtain()
        localVars.add(
            LocalVar.newBuilder().setName("name").setType(StringVar.getDefaultInstance().pack_())
                .setVar(
                    StringVar.newBuilder().setValue("Nick").build().pack_()
                ).build()
        )
        val res = "hello world localVarOf\$name".replaceLocalVarValues(localVars)
        Assert.assertEquals("hello world Nick", res)
    }

    @Test
    fun testReplaceLocalVarWithListValue() {
        val localVars = LocalVars.obtain()
        localVars.add(
            LocalVar.newBuilder().setName("name")
                .setType(StringListVar.getDefaultInstance().pack_())
                .setVar(
                    StringListVar.newBuilder().addValues("Nick").addValues("Guo").build().pack_()
                ).build()
        )
        val res = "hello world localVarOf\$name".replaceLocalVarValues(localVars)
        Assert.assertEquals("hello world Nick, Guo", res)
    }
}