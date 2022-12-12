package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.engine.FactMatcher
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger

class FactMatcherImpl : FactMatcher {
    private val logger = Logger("FactMatcher")
    private val factValueMatchers: MutableMap<Class<*>, (l: ProtoMessage, r: ProtoMessage) -> Boolean> =
        mutableMapOf()

    init {
        register(KeyEvent::class.java) { l, r ->
            l == r
        }
        register(Alarm::class.java) { l, r ->
            // Alarm match label.
            val la = l as Alarm
            val ra = r as Alarm
            la.tag == ra.tag
        }
    }

    private fun register(clazz: Class<*>, matcher: (l: ProtoMessage, r: ProtoMessage) -> Boolean) {
        factValueMatchers[clazz] = matcher
    }

    override fun match(fact: ProtoMessage, targetFact: ProtoAny): Boolean {
        return if (targetFact is_ fact.javaClass) {
            val targetFactMessage = targetFact unpack_ fact.javaClass
            match0(fact, targetFactMessage)
        } else {
            false
        }
    }

    private fun match0(l: ProtoMessage, r: ProtoMessage): Boolean {
        val matcher = factValueMatchers[l.javaClass]
        logger.d("matching $l $r")
        return matcher?.invoke(l, r) ?: (l == r)
    }

}