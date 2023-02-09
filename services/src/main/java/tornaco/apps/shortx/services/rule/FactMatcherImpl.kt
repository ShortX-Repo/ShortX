package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.AppBecomeFg
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.engine.FactMatcher
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext

class FactMatcherImpl(private val shortXContext: ShortXContext) : FactMatcher {
    private val logger = Logger("FactMatcher")
    private val factValueMatchers: MutableMap<Class<*>, (l: ProtoMessage, r: ProtoMessage) -> Boolean> =
        mutableMapOf()

    init {
        register(KeyEvent::class.java) { l, f ->
            l == f
        }
        register(Alarm::class.java) { l, f ->
            // Alarm match label.
            val la = l as Alarm
            val ra = f as Alarm
            la.tag == ra.tag
        }
        register(AppBecomeFg::class.java) { actual, rule ->
            val fgActual = actual as AppBecomeFg
            val fgRule = rule as AppBecomeFg

            val pkgListActual =
                if (fgActual.pkgSetsList.isEmpty().not()) {
                    fgActual.pkgSetsList.flatMap {
                        shortXContext.pkgSet.getPkgSetById(it)?.appPkgsList ?: emptyList()
                    }
                } else {
                    fgActual.appsList
                }
            val pkgListRule =
                if (fgRule.pkgSetsList.isEmpty().not()) {
                    fgRule.pkgSetsList.flatMap {
                        shortXContext.pkgSet.getPkgSetById(it)?.appPkgsList ?: emptyList()
                    }
                } else {
                    fgRule.appsList
                }

            logger.d("AppBecomeFg, pkgListActual: ${pkgListActual.map { it.pkgName }}, pkgListRule: ${pkgListRule.map { it.pkgName }}")
            pkgListRule.containsAll(pkgListActual)
        }
    }

    private fun register(clazz: Class<*>, matcher: (l: ProtoMessage, f: ProtoMessage) -> Boolean) {
        factValueMatchers[clazz] = matcher
    }

    override fun match(actualFact: ProtoMessage, ruleFact: ProtoAny): Boolean {
        return if (ruleFact is_ actualFact.javaClass) {
            val targetFactMessage = ruleFact unpack_ actualFact.javaClass
            match0(actualFact, targetFactMessage)
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