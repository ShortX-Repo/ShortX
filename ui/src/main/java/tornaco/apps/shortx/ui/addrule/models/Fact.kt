package tornaco.apps.shortx.ui.addrule.models

import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.Gesture
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_

sealed interface Fact {
    data class KeyEvent(val keyCode: Int) : Fact
    data class ActivityStarted(val name: String) : Fact

    data class EdgeGesture(val edge: Edge, val gesture: Gesture) : Fact
}

fun Fact.toProtoFact(): ProtoMessage {
    return when (this) {
        is Fact.KeyEvent -> {
            KeyEvent.newBuilder().setKeyCode(keyCode).build()
        }
        is Fact.ActivityStarted -> TODO()
        is Fact.EdgeGesture -> {
            EdgeGesture.newBuilder().setEdge(edge).setGesture(gesture).build()
        }
    }
}

fun ProtoAny.toFact(): Fact? {
    return when {
        this is_ KeyEvent::class.java -> {
            val data = this unpack_ KeyEvent::class.java
            Fact.KeyEvent(keyCode = data.keyCode)
        }
        this is_ EdgeGesture::class.java -> {
            val data = this unpack_ EdgeGesture::class.java
            Fact.EdgeGesture(edge = data.edge, gesture = data.gesture)
        }
        else -> {
            null
        }
    }
}