package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.engine.FactMatcher
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger

object FactMatcherFactory {
    private val logger = Logger("FactMatcher")
    private val factValueMatchers: MutableMap<Class<*>, (l: ProtoMessage, r: ProtoMessage) -> Boolean> =
        mutableMapOf()

    init {
        register(KeyEvent::class.java) { l, r ->
            (l as KeyEvent).keyCode == (r as KeyEvent).keyCode
        }
    }

    private fun register(clazz: Class<*>, matcher: (l: ProtoMessage, r: ProtoMessage) -> Boolean) {
        factValueMatchers[clazz] = matcher
    }

    fun factMatcher() = object : FactMatcher {
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
            return matcher?.invoke(l, r) ?: false
        }
    }
}