package tornaco.apps.shortx.core.rule

import io.kotest.matchers.shouldBe
import org.junit.Test
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.StringVar


class StringsTest {
    @Test
    fun `should compile func param`() {
        val cv = RuleContextAndVars(
            contextData = ContextData.obtain(mapOf("appLabel" to "WeChat")),
            globalVarsLazy = {
                listOf(
                    GlobalVar.newBuilder().setName("name")
                        .setType(StringVar.getDefaultInstance().pack_())
                        .setVar(
                            StringVar.newBuilder().setValue("Nick").build().pack_()
                        ).build()
                )
            },
            funcParameters = listOf(
                FuncParameterInput.newBuilder().setName("input").setValue("Trdo").build()
            )
        )

        "Your name is argOf\$input".compileContextAndVars(cv) shouldBe "Your name is Trdo"
    }

    @Test
    fun `should compile func param first`() {
        val cv = RuleContextAndVars(
            contextData = ContextData.obtain(mapOf("appLabel" to "WeChat")),
            globalVarsLazy = {
                listOf(
                    GlobalVar.newBuilder().setName("name")
                        .setType(StringVar.getDefaultInstance().pack_())
                        .setVar(
                            StringVar.newBuilder().setValue("Nick").build().pack_()
                        ).build()
                )
            },
            funcParameters = listOf(
                FuncParameterInput.newBuilder().setName("input").setValue("globalVarOf\$name").build()
            )
        )

        "Your name is argOf\$input, and the app is {appLabel}".compileContextAndVars(cv) shouldBe "Your name is Nick, and the app is WeChat"
    }
}