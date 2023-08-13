package tornaco.apps.shortx.services.rule

import tornaco.apps.shortx.core.PrebuiltStablePkgSetLabel
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.AppAdded
import tornaco.apps.shortx.core.proto.fact.AppBecomeBg
import tornaco.apps.shortx.core.proto.fact.AppBecomeFg
import tornaco.apps.shortx.core.proto.fact.AppRemoved
import tornaco.apps.shortx.core.proto.fact.AppUpdated
import tornaco.apps.shortx.core.proto.fact.AudioFocusChanged
import tornaco.apps.shortx.core.proto.fact.AudioFocusGain
import tornaco.apps.shortx.core.proto.fact.AudioFocusLost
import tornaco.apps.shortx.core.proto.fact.BTConnectedTo
import tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom
import tornaco.apps.shortx.core.proto.fact.BTStatusChanged
import tornaco.apps.shortx.core.proto.fact.BatteryLevelChanged
import tornaco.apps.shortx.core.proto.fact.BatteryTemperatureChanged
import tornaco.apps.shortx.core.proto.fact.Broadcast
import tornaco.apps.shortx.core.proto.fact.CallStateChanged
import tornaco.apps.shortx.core.proto.fact.ChargerPlug
import tornaco.apps.shortx.core.proto.fact.ChargerUnplug
import tornaco.apps.shortx.core.proto.fact.ClipboardContentChanged
import tornaco.apps.shortx.core.proto.fact.DNDStatusChanged
import tornaco.apps.shortx.core.proto.fact.DarkModeStatusChanged
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.FixedInPeriod
import tornaco.apps.shortx.core.proto.fact.FlashLightStatusChanged
import tornaco.apps.shortx.core.proto.fact.GlobalVarChanged
import tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.fact.LocationStatusChanged
import tornaco.apps.shortx.core.proto.fact.MediaStoreInsert
import tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged
import tornaco.apps.shortx.core.proto.fact.NFCStatusChanged
import tornaco.apps.shortx.core.proto.fact.Notification
import tornaco.apps.shortx.core.proto.fact.NotificationPosted
import tornaco.apps.shortx.core.proto.fact.NotificationRemoved
import tornaco.apps.shortx.core.proto.fact.NotificationUpdated
import tornaco.apps.shortx.core.proto.fact.OnFinishOp
import tornaco.apps.shortx.core.proto.fact.OnQSTileClick
import tornaco.apps.shortx.core.proto.fact.OnStartOp
import tornaco.apps.shortx.core.proto.fact.PkgStopRunning
import tornaco.apps.shortx.core.proto.fact.RandomInPeriod
import tornaco.apps.shortx.core.proto.fact.ScreenOff
import tornaco.apps.shortx.core.proto.fact.ScreenOn
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.proto.fact.SystemSettingsChanged
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

private val logger = Logger("FactMatcher")

class FactMatcherImpl(private val shortXContext: ShortXContext) : FactMatcher {
    private val factValueMatchers: MutableMap<Class<*>, Matcher> =
        mutableMapOf()

    init {
        register(KeyEvent::class.java) { actual, rule ->
            val lk = actual as KeyEvent
            val rk = rule as KeyEvent
            lk.keyCode == rk.keyCode
        }
        register(AdvancedKeyEvent::class.java) { actual, rule ->
            val lk = actual as AdvancedKeyEvent
            val rk = rule as AdvancedKeyEvent
            lk.keyCode == rk.keyCode && lk.gesture == rk.gesture
        }
        register(Alarm::class.java) { l, f ->
            val la = l as Alarm
            val ra = f as Alarm
            la.id == ra.id
        }
        register(RandomInPeriod::class.java) { l, f ->
            val la = l as RandomInPeriod
            val ra = f as RandomInPeriod
            la.id == ra.id
        }
        register(FixedInPeriod::class.java) { l, f ->
            val la = l as FixedInPeriod
            val ra = f as FixedInPeriod
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
            matchWithAppOrPkgSetListFact(
                actual = NotificationWrapper(actualN),
                rule = NotificationWrapper(ruleN)
            ) && matchNotificationMatcher(actualN, ruleN)
        }

        register(NotificationUpdated::class.java) { actual, rule ->
            val actualN = (actual as NotificationUpdated).new
            val ruleN = (rule as NotificationUpdated).new
            matchWithAppOrPkgSetListFact(
                actual = NotificationWrapper(actualN),
                rule = NotificationWrapper(ruleN)
            ) && matchNotificationMatcher(actualN, ruleN)
        }

        register(NotificationRemoved::class.java) { actual, rule ->
            val actualN = (actual as NotificationRemoved).record
            val ruleN = (rule as NotificationRemoved).record
            matchWithAppOrPkgSetListFact(
                actual = NotificationWrapper(actualN),
                rule = NotificationWrapper(ruleN)
            ) && matchNotificationMatcher(actualN, ruleN)
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
            if (ruleN.level <= 0) {
                true
            } else {
                ruleN.level == actualN.level
            }
        }

        register(BatteryTemperatureChanged::class.java) { actual, rule ->
            val actualN = (actual as BatteryTemperatureChanged)
            val ruleN = (rule as BatteryTemperatureChanged)
            if (ruleN.temp > 0) {
                logger.d("BatteryTemperatureChanged Check temp: rule.temp: ${ruleN.temp}")
                ruleN.temp == actualN.temp
            } else {
                logger.w("BatteryTemperatureChanged no temp and range, assume true.")
                true
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


        register(BTConnectedTo::class.java) { actual, rule ->
            val actualN = (actual as BTConnectedTo)
            val ruleN = (rule as BTConnectedTo)
            ruleN.deviceList.isEmpty() || ruleN.deviceList.any { it == "*" } || ruleN.deviceList.containsAll(
                actualN.deviceList
            )
        }

        register(BTDisconnectedFrom::class.java) { actual, rule ->
            val actualN = (actual as BTDisconnectedFrom)
            val ruleN = (rule as BTDisconnectedFrom)
            ruleN.deviceList.isEmpty() || ruleN.deviceList.any { it == "*" } || ruleN.deviceList.containsAll(
                actualN.deviceList
            )
        }

        register(VPNConnected::class.java) { actual, rule ->
            true
        }
        register(VPNDisconnected::class.java) { actual, rule ->
            true
        }

        register(MediaStoreInsert::class.java) { actual, rule ->
            val actualN = (actual as MediaStoreInsert)
            val ruleN = (rule as MediaStoreInsert)

            val filter = ruleN.filterPath
            val path = actualN.path
            logger.p("filter: $filter")
            logger.p("path: $path")

            filter.toRegex().matches(path)
        }

        register(OnStartOp::class.java) { actual, rule ->
            val actualN = (actual as OnStartOp)
            val ruleN = (rule as OnStartOp)

            ruleN.opCodesList.containsAll(actual.opCodesList) && matchWithAppOrPkgSetListFact(
                actual = OnStartOpWrapper(actualN),
                rule = OnStartOpWrapper(ruleN)
            )
        }

        register(OnFinishOp::class.java) { actual, rule ->
            val actualN = (actual as OnFinishOp)
            val ruleN = (rule as OnFinishOp)

            ruleN.opCodesList.containsAll(actual.opCodesList) && matchWithAppOrPkgSetListFact(
                actual = OnFinishOpWrapper(actualN),
                rule = OnFinishOpWrapper(ruleN)
            )
        }

        register(OnQSTileClick::class.java) { actual, rule ->
            val actualN = (actual as OnQSTileClick)
            val ruleN = (rule as OnQSTileClick)

            actualN.tileNumber == ruleN.tileNumber
        }

        register(SystemSettingsChanged::class.java) { actual, rule ->
            val actualN = (actual as SystemSettingsChanged)
            val ruleN = (rule as SystemSettingsChanged)

            ruleN.urlAndExpectedValueRegex.first == actualN.urlAndExpectedValueRegex.first
                    && emptyOrRegexMatch(
                ruleN.urlAndExpectedValueRegex.second,
                actualN.urlAndExpectedValueRegex.second
            )
        }

        register(CallStateChanged::class.java) { actual, rule ->
            val actualN = (actual as CallStateChanged)
            val ruleN = (rule as CallStateChanged)
            ruleN.callState == actualN.callState
        }

        register(ClipboardContentChanged::class.java) { actual, rule ->
            val actualN = (actual as ClipboardContentChanged)
            val ruleN = (rule as ClipboardContentChanged)
            emptyOrRegexMatch(ruleN.content, actualN.content)
        }

        register(GlobalVarChanged::class.java) { actual, rule ->
            val actualN = (actual as GlobalVarChanged)
            val ruleN = (rule as GlobalVarChanged)

            actualN.gvId == ruleN.gvId
        }

        logger.i("Init complete.")
    }

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
            it == PrebuiltStablePkgSetLabel.All.name
        } != null
        if (hasAllPkgSetInRule) {
            logger.d("matchWithAppOrPkgSetListFact, hasAllPkgSetInRule, assume true")
            return true
        }

        if (rule.appList.isEmpty() && rule.pkgSetList.isEmpty()) {
            logger.d("matchWithAppOrPkgSetListFact, appList and pkgSetList is empty, assume true")
            return true
        }

        val pkgListRule = rule.pkgSetList.flatMap { label ->
            shortXContext.pkgSet.getPkgSetByLabel(label)?.appPkgsList ?: emptyList()
        } + rule.appList
        logger.d("matchWithAppOrPkgSetListFact, pkgListActual: ${pkgListActual.map { it.appPkgToString() }}, pkgListRule: ${pkgListRule.map { it.appPkgToString() }}")
        return pkgListRule.isNotEmpty() && pkgListRule.containsAll(pkgListActual)
    }

    private fun matchNotificationMatcher(
        actual: Notification,
        rule: Notification
    ): Boolean {
        // FAST: No matcher settings
        if (rule == Notification.getDefaultInstance()) {
            return true
        }
        return emptyOrRegexMatch(
            rule.title to actual.title,
            rule.contentText to actual.contentText,
            rule.tag to actual.tag,
        )
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

    class NotificationWrapper(private val n: Notification) : WithAppOrPkgSetListFact {
        override val pkgSetList: List<String>
            get() = n.pkgSetsList ?: emptyList()
        override val appList: List<AppPkg>
            get() = n.appsList ?: emptyList()

    }

    class OnStartOpWrapper(private val onStartOp: OnStartOp) : WithAppOrPkgSetListFact {
        override val pkgSetList: List<String>
            get() = onStartOp.pkgSetsList ?: emptyList()
        override val appList: List<AppPkg>
            get() = onStartOp.appsList ?: emptyList()

    }

    class OnFinishOpWrapper(private val onFinishOp: OnFinishOp) : WithAppOrPkgSetListFact {
        override val pkgSetList: List<String>
            get() = onFinishOp.pkgSetsList ?: emptyList()
        override val appList: List<AppPkg>
            get() = onFinishOp.appsList ?: emptyList()

    }
}

fun emptyOrRegexMatch(vararg ruleAndActuals: Pair<String, String>) = ruleAndActuals.all {
    emptyOrRegexMatch(it.first, it.second)
}

fun emptyOrRegexMatch(ruleField: String, actualField: String) =
    ruleField.isEmpty() || ruleField.toRegex().matches(actualField).apply {
        logger.d("emptyOrRegexMatch, regex: $ruleField, result: $this")
    }