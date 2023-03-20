package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.*
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
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
            matchAppBecomeBgOrFg(AppBecomeFgFactWrapper(fgActual), AppBecomeFgFactWrapper(fgRule))
        }

        register(AppBecomeBg::class.java) { actual, rule ->
            val bgActual = actual as AppBecomeBg
            val bgRule = rule as AppBecomeBg
            matchAppBecomeBgOrFg(AppBecomeBgFactWrapper(bgActual), AppBecomeBgFactWrapper(bgRule))
        }

        register(ActivityStarted::class.java) { actual, rule ->
            val aa = actual as ActivityStarted
            val ra = rule as ActivityStarted
            ra.componentsList.containsAll(aa.componentsList)
        }

        register(WifiStatusChanged::class.java) { actual, rule ->
            val actualStatus = actual as WifiStatusChanged
            val ruleStatus = rule as WifiStatusChanged
            (ruleStatus.ooa == OnOffAny.Any || ruleStatus.ooa == actualStatus.ooa)
                    && ruleStatus.ssid.isNullOrEmpty() || ruleStatus.ssid == actualStatus.ssid
        }

        register(BTStatusChanged::class.java) { actual, rule ->
            val actualStatus = actual as BTStatusChanged
            val ruleStatus = rule as BTStatusChanged
            ruleStatus.ooa == OnOffAny.Any || ruleStatus.ooa == actualStatus.ooa
        }

        register(LocationStatusChanged::class.java) { actual, rule ->
            val actualStatus = actual as LocationStatusChanged
            val ruleStatus = rule as LocationStatusChanged
            ruleStatus.ooa == OnOffAny.Any || ruleStatus.ooa == actualStatus.ooa
        }

        register(NFCStatusChanged::class.java) { actual, rule ->
            val actualStatus = actual as NFCStatusChanged
            val ruleStatus = rule as NFCStatusChanged
            ruleStatus.ooa == OnOffAny.Any || ruleStatus.ooa == actualStatus.ooa
        }

        register(FlashLightStatusChanged::class.java) { actual, rule ->
            val actualStatus = actual as FlashLightStatusChanged
            val ruleStatus = rule as FlashLightStatusChanged
            ruleStatus.ooa == OnOffAny.Any || ruleStatus.ooa == actualStatus.ooa
        }

        register(MobileDataStatusChanged::class.java) { actual, rule ->
            val actualStatus = actual as MobileDataStatusChanged
            val ruleStatus = rule as MobileDataStatusChanged
            ruleStatus.ooa == OnOffAny.Any || ruleStatus.ooa == actualStatus.ooa
        }

        register(HotSpotStatusChanged::class.java) { actual, rule ->
            val actualStatus = actual as HotSpotStatusChanged
            val ruleStatus = rule as HotSpotStatusChanged
            ruleStatus.ooa == OnOffAny.Any || ruleStatus.ooa == actualStatus.ooa
        }

        register(DarkModeStatusChanged::class.java) { actual, rule ->
            val actualStatus = actual as DarkModeStatusChanged
            val ruleStatus = rule as DarkModeStatusChanged
            ruleStatus.ooa == OnOffAny.Any || ruleStatus.ooa == actualStatus.ooa
        }

        register(DNDStatusChanged::class.java) { actual, rule ->
            val actualStatus = actual as DNDStatusChanged
            val ruleStatus = rule as DNDStatusChanged
            ruleStatus.ooa == OnOffAny.Any || ruleStatus.ooa == actualStatus.ooa
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


    private fun matchAppBecomeBgOrFg(actual: AppBecomeBgFgFact, rule: AppBecomeBgFgFact): Boolean {
        val pkgListActual = if (actual.pkgSetList.isEmpty().not()) {
            actual.pkgSetList.flatMap {
                shortXContext.pkgSet.getPkgSetById(it)?.appPkgsList ?: emptyList()
            }
        } else {
            actual.appList
        }
        val pkgListRule = if (rule.pkgSetList.isEmpty().not()) {
            rule.pkgSetList.flatMap {
                shortXContext.pkgSet.getPkgSetById(it)?.appPkgsList ?: emptyList()
            }
        } else {
            rule.appList
        }

        logger.d("matchAppBecomeBgOrFg, pkgListActual: ${pkgListActual.map { it.pkgName }}, pkgListRule: ${pkgListRule.map { it.pkgName }}")
        return pkgListRule.containsAll(pkgListActual)
    }

    internal interface AppBecomeBgFgFact {
        val pkgSetList: List<String>
        val appList: List<AppPkg>
    }

    class AppBecomeBgFactWrapper(private val appBecomeBg: AppBecomeBg) : AppBecomeBgFgFact {
        override val pkgSetList: List<String>
            get() = appBecomeBg.pkgSetsList
        override val appList: List<AppPkg>
            get() = appBecomeBg.appsList

    }

    class AppBecomeFgFactWrapper(private val appBecomeFg: AppBecomeFg) : AppBecomeBgFgFact {
        override val pkgSetList: List<String>
            get() = appBecomeFg.pkgSetsList
        override val appList: List<AppPkg>
            get() = appBecomeFg.appsList

    }
}