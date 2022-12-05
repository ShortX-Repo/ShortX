package tornaco.apps.shortx.ui.addrule.models

import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_

sealed class Fact(val label: () -> String) {
    data class KeyEvent(val keyCode: Int) : Fact({ "KeyEvent $keyCode" })
    data class ActivityStarted(val name: String) :
        Fact({ "Activity Started $name" })
}

fun Fact.toProtoFact(): ProtoMessage {
    return when (this) {
        is Fact.KeyEvent -> {
            tornaco.apps.shortx.core.proto.fact.KeyEvent.newBuilder().setKeyCode(keyCode).build()
        }
        is Fact.ActivityStarted -> TODO()
    }
}

fun ProtoAny.toFact(): Fact? {
    return if (this is_ KeyEvent::class.java) {
        val data = this unpack_ KeyEvent::class.java
        Fact.KeyEvent(keyCode = data.keyCode)
    } else {
        null
    }
}