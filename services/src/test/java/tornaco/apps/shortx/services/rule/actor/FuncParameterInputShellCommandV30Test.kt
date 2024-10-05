package tornaco.apps.shortx.services.rule.actor

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.rule.replaceFuncParameterInputs
import tornaco.apps.shortx.core.util.logAdapter

class FuncParameterInputTest {
    @Before
    fun installLogger() {
        logAdapter = { _: Int, tag: String, msg: String ->
            println("[$tag] $msg")
        }
    }

    @Test
    fun testReplaceFuncParameterInputs_no_ref() {
        val localVars = LocalVars.obtain()
        val res = "hello world".replaceFuncParameterInputs(
            listOf(
                FuncParameterInput.newBuilder().setName("name").setValue("Nick").build()
            )
        )
        Assert.assertEquals("hello world", res)
    }

    @Test
    fun testReplaceFuncParameterInputs_one_ref() {
        val res = "hello worldargOf\$name".replaceFuncParameterInputs(
            listOf(
                FuncParameterInput.newBuilder().setName("name").setValue("Nick").build()
            )
        )
        Assert.assertEquals("hello worldNick", res)
    }

    @Test
    fun testReplaceFuncParameterInputs_two_ref() {
        val res = "hello worldargOf\$name, you are argOf\$age".replaceFuncParameterInputs(
            listOf(
                FuncParameterInput.newBuilder().setName("name").setValue("Nick").build(),
                FuncParameterInput.newBuilder().setName("age").setValue("18").build(),
            )
        )
        Assert.assertEquals("hello worldNick, you are 18", res)
    }
}