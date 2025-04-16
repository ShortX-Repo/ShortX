package tornaco.apps.shortx.services.xposed.hooks.hook

import io.kotest.matchers.shouldBe
import org.junit.Test
import tornaco.apps.shortx.core.proto.fact.MethodHookConstraintParamsCount
import tornaco.apps.shortx.core.proto.fact.MethodHookConstraintParamsType
import tornaco.apps.shortx.core.proto.fact.MethodHookConstraintReturnType
import tornaco.apps.shortx.core.rule.pack_
import util.ReflectionUtils
import util.Reflections

class CustomHookSystemServerAppTest {
    @Test
    fun testMatchConstraintParamsType() {
        JavaClass::class.java.declaredMethods.forEach { println(it) }

        val method1 = ReflectionUtils.findMethod(JavaClass::class.java, "method1")
        println(method1)
        CustomHookApp.matchConstraint(
            method1,
            MethodHookConstraintParamsType.newBuilder().build().pack_()
        ) { println(it) } shouldBe true

        CustomHookApp.matchConstraint(
            method1,
            MethodHookConstraintParamsType.newBuilder().addAllType(listOf(String::class.java.name))
                .build().pack_()
        ) { println(it) } shouldBe false

        val method2 = Reflections.findMethodExact(
            JavaClass::class.java,
            "method2",
            String::class.java,
            Int::class.java
        )
        println(method2)
        CustomHookApp.matchConstraint(
            method2,
            MethodHookConstraintParamsType.newBuilder()
                .addAllType(listOf(String::class.java.name, Int::class.java.name))
                .build().pack_()
        ) { println(it) } shouldBe true
    }

    @Test
    fun testMatchConstraintReturnType() {
        val method2 = Reflections.findMethodExact(
            JavaClass::class.java,
            "method2",
            String::class.java,
            Int::class.java
        )
        println(method2)
        println(method2.returnType.name)
        CustomHookApp.matchConstraint(
            method2,
            MethodHookConstraintReturnType.newBuilder()
                .setType("void")
                .build().pack_()
        ) { println(it) } shouldBe true


        val method3 = Reflections.findMethodExact(
            JavaClass::class.java,
            "method3",
            String::class.java,
            Int::class.java,
            Long::class.java
        )
        CustomHookApp.matchConstraint(
            method3,
            MethodHookConstraintReturnType.newBuilder()
                .setType("java.util.Date")
                .build().pack_()
        ) { println(it) } shouldBe true
    }

    @Test
    fun testMatchConstraintParamCount() {
        val method2 = Reflections.findMethodExact(
            JavaClass::class.java,
            "method2",
            String::class.java,
            Int::class.java
        )
        println(method2)
        println(method2.parameters.size)
        CustomHookApp.matchConstraint(
            method2,
            MethodHookConstraintParamsCount.newBuilder()
                .setCount(2)
                .build().pack_()
        ) { println(it) } shouldBe true
    }
}