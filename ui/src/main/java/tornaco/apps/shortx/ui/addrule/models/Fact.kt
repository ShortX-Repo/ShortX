package tornaco.apps.shortx.ui.addrule.models

import tornaco.apps.shortx.core.proto.fact.*
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_

sealed interface Fact {
    data class KeyEvent(val keyCode: Int) : Fact
    data class ActivityStarted(val name: String) : Fact

    data class EdgeGesture(val edge: Edge, val gesture: Gesture) : Fact

    object ScreenOn : Fact
    object ScreenOff : Fact
    object UserPresent : Fact

    data class Alarm(val label: String, val timeOfADay: TimeOfADay, val repeatDays: RepeatDays) :
        Fact
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
        Fact.ScreenOff -> {
            ScreenOff.newBuilder().build()
        }
        Fact.ScreenOn -> {
            ScreenOn.newBuilder().build()
        }
        Fact.UserPresent -> {
            UserPresent.newBuilder().build()
        }
        is Fact.Alarm -> {
            Alarm.newBuilder()
                .setLabel(label)
                .setRepeat(repeatDays)
                .setTriggerAt(timeOfADay)
                .build()
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
        this is_ ScreenOff::class.java -> {
            Fact.ScreenOff
        }
        this is_ ScreenOn::class.java -> {
            Fact.ScreenOn
        }
        this is_ UserPresent::class.java -> {
            Fact.UserPresent
        }
        this is_ Alarm::class.java -> {
            val data = this unpack_ Alarm::class.java
            Fact.Alarm(
                label = data.label,
                timeOfADay = data.triggerAt,
                repeatDays = data.repeat
            )
        }
        else -> {
            null
        }
    }
}