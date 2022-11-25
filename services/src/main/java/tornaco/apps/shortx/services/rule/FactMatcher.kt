package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.KeyEvent
import tornaco.apps.shortx.core.rule.*
import tornaco.apps.shortx.core.rule.engine.FactMatcher

object FactMatcherFactory {
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
        override suspend fun match(fact: ProtoMessage, targetFact: ProtoAny): Boolean {
            return if (targetFact is_ fact.javaClass) {
                val targetFactMessage = targetFact unpack_ fact.javaClass
                match0(fact, targetFactMessage)
            } else {
                false
            }
        }

        private fun match0(l: ProtoMessage, r: ProtoMessage): Boolean {
            val matcher = factValueMatchers[l.javaClass]
            return matcher?.invoke(l, r) ?: false
        }
    }
}