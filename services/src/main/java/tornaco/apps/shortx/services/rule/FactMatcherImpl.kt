package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.PrebuiltPkgSetLabel
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.AppAdded
import tornaco.apps.shortx.core.proto.fact.AppBecomeBg
import tornaco.apps.shortx.core.proto.fact.AppBecomeFg
import tornaco.apps.shortx.core.proto.fact.AppRemoved
import tornaco.apps.shortx.core.proto.fact.AppUpdated
import tornaco.apps.shortx.core.proto.fact.AudioFocusChanged
import tornaco.apps.shortx.core.proto.fact.AudioFocusGain
import tornaco.apps.shortx.core.proto.fact.AudioFocusLost
import tornaco.apps.shortx.core.proto.fact.BTStatusChanged
import tornaco.apps.shortx.core.proto.fact.BatteryLevelChanged
import tornaco.apps.shortx.core.proto.fact.Broadcast
import tornaco.apps.shortx.core.proto.fact.ChargerPlug
import tornaco.apps.shortx.core.proto.fact.ChargerUnplug
import tornaco.apps.shortx.core.proto.fact.DNDStatusChanged
import tornaco.apps.shortx.core.proto.fact.DarkModeStatusChanged
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.FlashLightStatusChanged
import tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.fact.LocationStatusChanged
import tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged
import tornaco.apps.shortx.core.proto.fact.NFCStatusChanged
import tornaco.apps.shortx.core.proto.fact.NotificationPosted
import tornaco.apps.shortx.core.proto.fact.NotificationRemoved
import tornaco.apps.shortx.core.proto.fact.NotificationUpdated
import tornaco.apps.shortx.core.proto.fact.PkgStopRunning
import tornaco.apps.shortx.core.proto.fact.ScreenOff
import tornaco.apps.shortx.core.proto.fact.ScreenOn
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.proto.fact.TaskRemoved
import tornaco.apps.shortx.core.proto.fact.UserPresent
import tornaco.apps.shortx.core.proto.fact.VPNConnected
import tornaco.apps.shortx.core.proto.fact.VPNDisconnected
import tornaco.apps.shortx.core.proto.fact.WifiConnectedTo
import tornaco.apps.shortx.core.proto.fact.WifiDisconnectedFrom
import tornaco.apps.shortx.core.proto.fact.WifiStatusChanged
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.services.ShortXContext

internal class Matcher(
    val targetClass: String,
    val impl: (l: ProtoMessage, r: ProtoMessage) -> Boolean
)

class FactMatcherImpl(private val shortXContext: ShortXContext) : FactMatcher {
    private val logger = Logger("FactMatcher")
    private val factValueMatchers: MutableMap<Class<*>, Matcher> =
        mutableMapOf()

    init {
        register(KeyEvent::class.java) { actual, rule ->
            val lk = actual as KeyEvent
            val rk = rule as KeyEvent
            lk.keyCode == rk.keyCode
        }
        register(Alarm::class.java) { l, f ->
            // Alarm match label.
            val la = l as Alarm
            val ra = f as Alarm
            la.id == ra.id
        }
        register(AppBecomeFg::class.java) { actual, rule ->
            val fgActual = actual as AppBecomeFg
            val fgRule = rule as AppBecomeFg
            matchWithAppOrPkgSetListFact(
                AppBecomeFgFactWrapper(fgActual),
                AppBecomeFgFactWrapper(fgRule)
            )
        }

        register(AppBecomeBg::class.java) { actual, rule ->
            val bgActual = actual as AppBecomeBg
            val bgRule = rule as AppBecomeBg
            matchWithAppOrPkgSetListFact(
                AppBecomeBgFactWrapper(bgActual),
                AppBecomeBgFactWrapper(bgRule)
            )
        }

        register(TaskRemoved::class.java) { actual, rule ->
            val trActual = actual as TaskRemoved
            val trRule = rule as TaskRemoved
            matchWithAppOrPkgSetListFact(
                TaskRemovedWrapper(trActual),
                TaskRemovedWrapper(trRule)
            )
        }

        register(PkgStopRunning::class.java) { actual, rule ->
            val trActual = actual as PkgStopRunning
            val trRule = rule as PkgStopRunning
            matchWithAppOrPkgSetListFact(
                PkgStopRunningWrapper(trActual),
                PkgStopRunningWrapper(trRule)
            )
        }

        register(ActivityStarted::class.java) { actual, rule ->
            val aa = actual as ActivityStarted
            val ra = rule as ActivityStarted
            (ra.componentsList.isEmpty() || ra.componentsList.containsAll(aa.componentsList))
        }

        register(WifiStatusChanged::class.java) { actual, rule ->
            val actualStatus = actual as WifiStatusChanged
            val ruleStatus = rule as WifiStatusChanged
            (ruleStatus.ooa == OnOffAny.Any || ruleStatus.ooa == actualStatus.ooa)
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

        register(NotificationPosted::class.java) { actual, rule ->
            val actualN = (actual as NotificationPosted).record
            val ruleN = (rule as NotificationPosted).record

            val pkgListActual = actualN.appsList

            val pkgListRule = if (ruleN.pkgSetsList.isEmpty().not()) {
                ruleN.pkgSetsList.flatMap {
                    shortXContext.pkgSet.getPkgSetByLabel(it)?.appPkgsList ?: emptyList()
                }
            } else {
                ruleN.appsList
            }

            (pkgListRule.isEmpty() || pkgListRule.containsAll(pkgListActual))
        }

        register(NotificationUpdated::class.java) { actual, rule ->
            val actualN = (actual as NotificationUpdated).new
            val ruleN = (rule as NotificationUpdated).new

            val pkgListActual = actualN.appsList

            val pkgListRule = if (ruleN.pkgSetsList.isEmpty().not()) {
                ruleN.pkgSetsList.flatMap {
                    shortXContext.pkgSet.getPkgSetByLabel(it)?.appPkgsList ?: emptyList()
                }
            } else {
                ruleN.appsList
            }

            (pkgListRule.isEmpty() || pkgListRule.containsAll(pkgListActual))
        }

        register(NotificationRemoved::class.java) { actual, rule ->
            val actualN = (actual as NotificationRemoved).record
            val ruleN = (rule as NotificationRemoved).record

            val pkgListActual = actualN.appsList

            val pkgListRule = if (ruleN.pkgSetsList.isEmpty().not()) {
                ruleN.pkgSetsList.flatMap {
                    shortXContext.pkgSet.getPkgSetByLabel(it)?.appPkgsList ?: emptyList()
                }
            } else {
                ruleN.appsList
            }

            (pkgListRule.isEmpty() || pkgListRule.containsAll(pkgListActual))
        }

        register(AppRemoved::class.java) { actual, rule ->
            val actualN = (actual as AppRemoved)
            val ruleN = (rule as AppRemoved)
            matchWithAppOrPkgSetListFact(
                AppRemovedWrapper(actualN),
                AppRemovedWrapper(ruleN)
            )
        }

        register(AppUpdated::class.java) { actual, rule ->
            val actualN = (actual as AppUpdated)
            val ruleN = (rule as AppUpdated)
            matchWithAppOrPkgSetListFact(
                AppUpdatedWrapper(actualN),
                AppUpdatedWrapper(ruleN)
            )
        }

        register(AudioFocusGain::class.java) { actual, rule ->
            val actualN = (actual as AudioFocusGain)
            val ruleN = (rule as AudioFocusGain)
            matchWithAppOrPkgSetListFact(
                AudioFocusGainWrapper(actualN),
                AudioFocusGainWrapper(ruleN)
            )
        }

        register(AudioFocusLost::class.java) { actual, rule ->
            val actualN = (actual as AudioFocusLost)
            val ruleN = (rule as AudioFocusLost)
            matchWithAppOrPkgSetListFact(
                AudioFocusLostWrapper(actualN),
                AudioFocusLostWrapper(ruleN)
            )
        }

        register(AudioFocusChanged::class.java) { _, _ ->
            true
        }
        register(ChargerPlug::class.java) { _, _ ->
            true
        }
        register(ChargerUnplug::class.java) { _, _ ->
            true
        }
        register(SystemReady::class.java) { _, _ ->
            true
        }
        register(ScreenOn::class.java) { _, _ ->
            true
        }
        register(ScreenOff::class.java) { _, _ ->
            true
        }
        register(UserPresent::class.java) { _, _ ->
            true
        }
        register(AppAdded::class.java) { _, _ ->
            true
        }

        register(BatteryLevelChanged::class.java) { actual, rule ->
            val actualN = (actual as BatteryLevelChanged)
            val ruleN = (rule as BatteryLevelChanged)
            if (ruleN.level < 0) {
                true
            } else {
                ruleN.level == actualN.level
            }
        }

        register(Broadcast::class.java) { actual, rule ->
            val actualN = (actual as Broadcast)
            val ruleN = (rule as Broadcast)
            actualN.id == ruleN.id
        }

        register(EdgeGesture::class.java) { actual, rule ->
            val actualN = (actual as EdgeGesture)
            val ruleN = (rule as EdgeGesture)
            actualN.edge == ruleN.edge && actualN.gesture == ruleN.gesture
        }

        register(WifiConnectedTo::class.java) { actual, rule ->
            val actualN = (actual as WifiConnectedTo)
            val ruleN = (rule as WifiConnectedTo)
            ruleN.ssidListList.isEmpty() || ruleN.ssidListList.any { it == "*" } || ruleN.ssidListList.containsAll(
                actualN.ssidListList
            )
        }

        register(WifiDisconnectedFrom::class.java) { actual, rule ->
            val actualN = (actual as WifiDisconnectedFrom)
            val ruleN = (rule as WifiDisconnectedFrom)
            ruleN.ssidListList.isEmpty() || ruleN.ssidListList.any { it == "*" } || ruleN.ssidListList.containsAll(
                actualN.ssidListList
            )
        }


        register(VPNConnected::class.java) { actual, rule ->
            true
        }
        register(VPNDisconnected::class.java) { actual, rule ->
            true
        }

        logger.i("Init complete.")
    }

    private fun nullEmptyOrEq(vararg ruleAndActuals: Pair<String?, String>) = ruleAndActuals.all {
        nullEmptyOrEq(it.first, it.second)
    }

    private fun nullEmptyOrEq(ruleField: String?, actualField: String) =
        ruleField.isNullOrEmpty() || ruleField == actualField

    private fun register(clazz: Class<*>, matcher: (l: ProtoMessage, f: ProtoMessage) -> Boolean) {
        logger.d("register: $clazz --> $matcher")
        if (factValueMatchers[clazz] != null) throw IllegalStateException("Dup matcher for class: $clazz")
        factValueMatchers[clazz] = Matcher(clazz.name, matcher)
    }

    override fun match(actualFact: ProtoMessage, ruleFacts: List<ProtoAny>): Boolean {
        return ruleFacts.any { match(actualFact, it) }
    }

    private fun match(actualFact: ProtoMessage, ruleFact: ProtoAny): Boolean {
        return if (ruleFact is_ actualFact.javaClass) {
            val targetFactMessage = ruleFact unpack_ actualFact.javaClass
            match0(actualFact, targetFactMessage)
        } else {
            false
        }
    }

    private fun match0(actual: ProtoMessage, rule: ProtoMessage): Boolean {
        val matcher = factValueMatchers[actual.javaClass]
        val res = matcher?.impl?.invoke(actual, rule) ?: (actual == rule)
        logger.p("matching actual: $actual, rule: $rule, res: $res")
        logger.d("matching class: ${matcher?.targetClass} res: $res")
        return res
    }


    private fun matchWithAppOrPkgSetListFact(
        actual: WithAppOrPkgSetListFact,
        rule: WithAppOrPkgSetListFact
    ): Boolean {
        val pkgListActual = actual.appList

        val hasAllPkgSetInRule = rule.pkgSetList.firstOrNull {
            it == PrebuiltPkgSetLabel.All.name
        } != null
        if (hasAllPkgSetInRule) {
            logger.d("matchWithAppOrPkgSetListFact, hasAllPkgSetInRule, assume true")
            return true
        }

        val pkgListRule = rule.pkgSetList.flatMap { label ->
            shortXContext.pkgSet.getPkgSetByLabel(label)?.appPkgsList ?: emptyList()
        } + rule.appList

        if (pkgListRule.isEmpty()) {
            logger.d("matchWithAppOrPkgSetListFact, pkgListRule is empty, assume true")
            return true
        }

        logger.d("matchWithAppOrPkgSetListFact, pkgListActual: ${pkgListActual.map { it.appPkgToString() }}, pkgListRule: ${pkgListRule.map { it.appPkgToString() }}")
        return pkgListRule.containsAll(pkgListActual)
    }

    internal interface WithAppOrPkgSetListFact {
        val pkgSetList: List<String>
        val appList: List<AppPkg>
    }

    class AppBecomeBgFactWrapper(private val appBecomeBg: AppBecomeBg) : WithAppOrPkgSetListFact {
        override val pkgSetList: List<String>
            get() = appBecomeBg.pkgSetsList ?: emptyList()
        override val appList: List<AppPkg>
            get() = appBecomeBg.appsList ?: emptyList()

    }

    class AppBecomeFgFactWrapper(private val appBecomeFg: AppBecomeFg) : WithAppOrPkgSetListFact {
        override val pkgSetList: List<String>
            get() = appBecomeFg.pkgSetsList ?: emptyList()
        override val appList: List<AppPkg>
            get() = appBecomeFg.appsList ?: emptyList()

    }

    class TaskRemovedWrapper(private val taskRemoved: TaskRemoved) : WithAppOrPkgSetListFact {
        override val pkgSetList: List<String>
            get() = taskRemoved.pkgSetsList ?: emptyList()
        override val appList: List<AppPkg>
            get() = taskRemoved.appsList ?: emptyList()

    }

    class PkgStopRunningWrapper(private val stopRunning: PkgStopRunning) : WithAppOrPkgSetListFact {
        override val pkgSetList: List<String>
            get() = stopRunning.pkgSetsList ?: emptyList()
        override val appList: List<AppPkg>
            get() = stopRunning.appsList ?: emptyList()

    }

    class AppRemovedWrapper(private val appRemoved: AppRemoved) : WithAppOrPkgSetListFact {
        override val pkgSetList: List<String>
            get() = appRemoved.pkgSetsList ?: emptyList()
        override val appList: List<AppPkg>
            get() = appRemoved.appsList ?: emptyList()

    }

    class AppUpdatedWrapper(private val appUpdated: AppUpdated) : WithAppOrPkgSetListFact {
        override val pkgSetList: List<String>
            get() = appUpdated.pkgSetsList ?: emptyList()
        override val appList: List<AppPkg>
            get() = appUpdated.appsList ?: emptyList()

    }

    class AudioFocusGainWrapper(private val focusGain: AudioFocusGain) : WithAppOrPkgSetListFact {
        override val pkgSetList: List<String>
            get() = focusGain.pkgSetsList ?: emptyList()
        override val appList: List<AppPkg>
            get() = focusGain.appsList ?: emptyList()

    }

    class AudioFocusLostWrapper(private val focusLost: AudioFocusLost) : WithAppOrPkgSetListFact {
        override val pkgSetList: List<String>
            get() = focusLost.pkgSetsList ?: emptyList()
        override val appList: List<AppPkg>
            get() = focusLost.appsList ?: emptyList()

    }
}