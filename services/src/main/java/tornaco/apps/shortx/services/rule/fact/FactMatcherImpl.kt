package tornaco.apps.shortx.services.rule.fact

import android.annotation.SuppressLint
import com.google.protobuf.Descriptors
import tornaco.apps.shortx.core.PrebuiltStablePkgSetLabel
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.RegexMatchData
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.common.Rect
import tornaco.apps.shortx.core.proto.common.RegexMatchOptions
import tornaco.apps.shortx.core.proto.common.ScreenOnTimeStartFrom
import tornaco.apps.shortx.core.proto.common.ScreenRotateDegree
import tornaco.apps.shortx.core.proto.fact.APMStatusChanged
import tornaco.apps.shortx.core.proto.fact.AccelerometerSensor
import tornaco.apps.shortx.core.proto.fact.ActivityDestroyed
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.fact.ActivityStopped
import tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.AnyFact
import tornaco.apps.shortx.core.proto.fact.AppAdded
import tornaco.apps.shortx.core.proto.fact.AppBecomeBg
import tornaco.apps.shortx.core.proto.fact.AppBecomeFg
import tornaco.apps.shortx.core.proto.fact.AppGainWindowFocus
import tornaco.apps.shortx.core.proto.fact.AppLostWindowFocus
import tornaco.apps.shortx.core.proto.fact.AppProcessRemoved
import tornaco.apps.shortx.core.proto.fact.AppProcessStarted
import tornaco.apps.shortx.core.proto.fact.AppRemoved
import tornaco.apps.shortx.core.proto.fact.AppUpdated
import tornaco.apps.shortx.core.proto.fact.AudioFocusChanged
import tornaco.apps.shortx.core.proto.fact.AudioFocusGain
import tornaco.apps.shortx.core.proto.fact.AudioFocusLost
import tornaco.apps.shortx.core.proto.fact.BTConnectedTo
import tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom
import tornaco.apps.shortx.core.proto.fact.BTStatusChanged
import tornaco.apps.shortx.core.proto.fact.BackNavDone
import tornaco.apps.shortx.core.proto.fact.BackNavStart
import tornaco.apps.shortx.core.proto.fact.BatteryLevelChanged
import tornaco.apps.shortx.core.proto.fact.BatteryTemperatureChanged
import tornaco.apps.shortx.core.proto.fact.Broadcast
import tornaco.apps.shortx.core.proto.fact.CallStateChanged
import tornaco.apps.shortx.core.proto.fact.ChargerPlug
import tornaco.apps.shortx.core.proto.fact.ChargerUnplug
import tornaco.apps.shortx.core.proto.fact.ClipboardContentChanged
import tornaco.apps.shortx.core.proto.fact.CombineKeyEvent
import tornaco.apps.shortx.core.proto.fact.ConnectedWifiSignalLevelChanged
import tornaco.apps.shortx.core.proto.fact.CornerSwipeToCenter
import tornaco.apps.shortx.core.proto.fact.CpuAvgAvailability
import tornaco.apps.shortx.core.proto.fact.DNDStatusChanged
import tornaco.apps.shortx.core.proto.fact.DarkModeStatusChanged
import tornaco.apps.shortx.core.proto.fact.DeepLinkCall
import tornaco.apps.shortx.core.proto.fact.Edge
import tornaco.apps.shortx.core.proto.fact.EdgeGesture
import tornaco.apps.shortx.core.proto.fact.FixedInPeriod
import tornaco.apps.shortx.core.proto.fact.FlashLightStatusChanged
import tornaco.apps.shortx.core.proto.fact.GlobalVarChanged
import tornaco.apps.shortx.core.proto.fact.HasFoundNodeOnScreen
import tornaco.apps.shortx.core.proto.fact.HeadsetPlug
import tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged
import tornaco.apps.shortx.core.proto.fact.IMEVisibilityChange
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.fact.LightSensor
import tornaco.apps.shortx.core.proto.fact.LocationStatusChanged
import tornaco.apps.shortx.core.proto.fact.Logcat
import tornaco.apps.shortx.core.proto.fact.MediaStoreDelete
import tornaco.apps.shortx.core.proto.fact.MediaStoreInsert
import tornaco.apps.shortx.core.proto.fact.MethodHook
import tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged
import tornaco.apps.shortx.core.proto.fact.NFCStatusChanged
import tornaco.apps.shortx.core.proto.fact.NFCTagDiscover
import tornaco.apps.shortx.core.proto.fact.Notification
import tornaco.apps.shortx.core.proto.fact.NotificationPosted
import tornaco.apps.shortx.core.proto.fact.NotificationRemoved
import tornaco.apps.shortx.core.proto.fact.NotificationUpdated
import tornaco.apps.shortx.core.proto.fact.OnFinishOp
import tornaco.apps.shortx.core.proto.fact.OnMenuActionTrigger
import tornaco.apps.shortx.core.proto.fact.OnQSTileClick
import tornaco.apps.shortx.core.proto.fact.OnStartDynamicShortcut
import tornaco.apps.shortx.core.proto.fact.OnStartOp
import tornaco.apps.shortx.core.proto.fact.PkgStartRunning
import tornaco.apps.shortx.core.proto.fact.PkgStopRunning
import tornaco.apps.shortx.core.proto.fact.ProximitySensor
import tornaco.apps.shortx.core.proto.fact.ProximitySensorState
import tornaco.apps.shortx.core.proto.fact.ProximitySensorStateChanged
import tornaco.apps.shortx.core.proto.fact.RandomInPeriod
import tornaco.apps.shortx.core.proto.fact.ScreenGesture
import tornaco.apps.shortx.core.proto.fact.ScreenOff
import tornaco.apps.shortx.core.proto.fact.ScreenOn
import tornaco.apps.shortx.core.proto.fact.ScreenOnTime
import tornaco.apps.shortx.core.proto.fact.ScreenRotate
import tornaco.apps.shortx.core.proto.fact.SensorValueTrend
import tornaco.apps.shortx.core.proto.fact.ShakeDevice
import tornaco.apps.shortx.core.proto.fact.Sound
import tornaco.apps.shortx.core.proto.fact.SunRiseSet
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.proto.fact.SystemSettingsChanged
import tornaco.apps.shortx.core.proto.fact.TaskRemoved
import tornaco.apps.shortx.core.proto.fact.UsbDeviceAttached
import tornaco.apps.shortx.core.proto.fact.UsbDeviceDetached
import tornaco.apps.shortx.core.proto.fact.UserPresent
import tornaco.apps.shortx.core.proto.fact.UserPresentAtTheFirstTime
import tornaco.apps.shortx.core.proto.fact.VPNConnected
import tornaco.apps.shortx.core.proto.fact.VPNDisconnected
import tornaco.apps.shortx.core.proto.fact.ValueConstraints
import tornaco.apps.shortx.core.proto.fact.WifiConnectedTo
import tornaco.apps.shortx.core.proto.fact.WifiDisconnectedFrom
import tornaco.apps.shortx.core.proto.fact.WifiStatusChanged
import tornaco.apps.shortx.core.proto.fact.WindowRotationChange
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.cond.impl.EvaluateContextVar

data class FactAttrs(
    val id: String,
    val isDisabled: Boolean,
)

internal class Matcher(
    val targetClass: String,
    val impl: (l: ProtoMessage, r: ProtoMessage) -> Boolean
)

private val logger = Logger("FactMatcher")

@SuppressLint("SdCardPath")
class FactMatcherImpl(private val shortXContextProvider: () -> ShortXContext) : FactMatcher {
    private val factValueMatchers: MutableMap<Class<*>, Matcher> =
        mutableMapOf()

    init {
        register(CombineKeyEvent::class.java) { actual, rule ->
            val lk = actual as CombineKeyEvent
            val rk = rule as CombineKeyEvent
            lk.keyCode1 == rk.keyCode1 && lk.keyCode2 == rk.keyCode2
        }
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
                shortXContextProvider,
                AppBecomeFgFactWrapper(fgActual),
                AppBecomeFgFactWrapper(fgRule)
            )
        }

        register(AppBecomeBg::class.java) { actual, rule ->
            val bgActual = actual as AppBecomeBg
            val bgRule = rule as AppBecomeBg
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                AppBecomeBgFactWrapper(bgActual),
                AppBecomeBgFactWrapper(bgRule)
            )
        }

        register(TaskRemoved::class.java) { actual, rule ->
            val trActual = actual as TaskRemoved
            val trRule = rule as TaskRemoved
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                TaskRemovedWrapper(trActual),
                TaskRemovedWrapper(trRule)
            )
        }

        register(PkgStopRunning::class.java) { actual, rule ->
            val trActual = actual as PkgStopRunning
            val trRule = rule as PkgStopRunning
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                PkgStopRunningWrapper(trActual),
                PkgStopRunningWrapper(trRule)
            )
        }

        register(PkgStartRunning::class.java) { actual, rule ->
            val trActual = actual as PkgStartRunning
            val trRule = rule as PkgStartRunning
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                PkgStartRunningWrapper(trActual),
                PkgStartRunningWrapper(trRule)
            )
        }

        register(ActivityStarted::class.java) { actual, rule ->
            val aa = actual as ActivityStarted
            val ra = rule as ActivityStarted
            (ra.componentsList.isEmpty() || ra.componentsList.containsAll(aa.componentsList))
        }

        register(ActivityStopped::class.java) { actual, rule ->
            val aa = actual as ActivityStopped
            val ra = rule as ActivityStopped
            (ra.componentsList.isEmpty() || ra.componentsList.containsAll(aa.componentsList))
        }

        register(ActivityDestroyed::class.java) { actual, rule ->
            val aa = actual as ActivityDestroyed
            val ra = rule as ActivityDestroyed
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

        register(APMStatusChanged::class.java) { actual, rule ->
            val actualStatus = actual as APMStatusChanged
            val ruleStatus = rule as APMStatusChanged
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
                shortXContextProvider,
                actual = NotificationWrapper(actualN),
                rule = NotificationWrapper(ruleN)
            ) && matchNotificationTagTitleContent(actualN, ruleN)
        }

        register(NotificationUpdated::class.java) { actual, rule ->
            val actualN = (actual as NotificationUpdated).new
            val ruleN = (rule as NotificationUpdated).new
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                actual = NotificationWrapper(actualN),
                rule = NotificationWrapper(ruleN)
            ) && matchNotificationTagTitleContent(actualN, ruleN)
        }

        register(NotificationRemoved::class.java) { actual, rule ->
            val actualN = (actual as NotificationRemoved).record
            val ruleN = (rule as NotificationRemoved).record
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                actual = NotificationWrapper(actualN),
                rule = NotificationWrapper(ruleN)
            ) && matchNotificationTagTitleContent(actualN, ruleN)
        }

        register(AppRemoved::class.java) { actual, rule ->
            val actualN = (actual as AppRemoved)
            val ruleN = (rule as AppRemoved)
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                AppRemovedWrapper(actualN),
                AppRemovedWrapper(ruleN)
            )
        }

        register(AppUpdated::class.java) { actual, rule ->
            val actualN = (actual as AppUpdated)
            val ruleN = (rule as AppUpdated)
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                AppUpdatedWrapper(actualN),
                AppUpdatedWrapper(ruleN)
            )
        }

        register(AudioFocusGain::class.java) { actual, rule ->
            val actualN = (actual as AudioFocusGain)
            val ruleN = (rule as AudioFocusGain)
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                AudioFocusGainWrapper(actualN),
                AudioFocusGainWrapper(ruleN)
            )
        }

        register(AudioFocusLost::class.java) { actual, rule ->
            val actualN = (actual as AudioFocusLost)
            val ruleN = (rule as AudioFocusLost)
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
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
        register(UserPresentAtTheFirstTime::class.java) { _, _ ->
            true
        }
        register(AppAdded::class.java) { _, _ ->
            true
        }
        register(HeadsetPlug::class.java) { actual, rule ->
            val actualN = (actual as HeadsetPlug)
            val ruleN = (rule as HeadsetPlug)
            actualN.isPlug == ruleN.isPlug
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

            val gestureMatches = actualN.gesture == ruleN.gesture
            val isDefaultRect = { rect: Rect -> rect == Rect.getDefaultInstance() }
            val isValidEdge = { edge: Edge -> edge != Edge.UNRECOGNIZED }

            val edgeMatches = isDefaultRect(actualN.customRect) &&
                    isDefaultRect(ruleN.customRect) &&
                    isValidEdge(actualN.edge) &&
                    actualN.edge == ruleN.edge

            val customRectMatches = !isDefaultRect(actualN.customRect) &&
                    actualN.customRect == ruleN.customRect

            gestureMatches && (edgeMatches || customRectMatches)
        }

        register(CornerSwipeToCenter::class.java) { actual, rule ->
            val actualN = actual as CornerSwipeToCenter
            val ruleN = rule as CornerSwipeToCenter
            actualN.corner == ruleN.corner
        }

        register(ScreenGesture::class.java) { actual, rule ->
            val actualN = (actual as ScreenGesture)
            val ruleN = (rule as ScreenGesture)
            actualN.gesture == ruleN.gesture
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
            emptyOrRegexMatch(
                ruleField = ruleN.filterPath,
                actualField = actualN.path,
                options = ruleN.options
            ) || emptyOrRegexMatch(
                ruleField = ruleN.filterPath.replace("/sdcard", "/storage/emulated/0"),
                actualField = actualN.path,
                options = ruleN.options
            )
        }

        register(MediaStoreDelete::class.java) { actual, rule ->
            val actualN = (actual as MediaStoreDelete)
            val ruleN = (rule as MediaStoreDelete)
            emptyOrRegexMatch(
                ruleField = ruleN.filterPath,
                actualField = actualN.path,
                options = ruleN.options
            ) || emptyOrRegexMatch(
                ruleField = ruleN.filterPath.replace("/sdcard", "/storage/emulated/0"),
                actualField = actualN.path,
                options = ruleN.options
            )
        }

        register(OnStartOp::class.java) { actual, rule ->
            val actualN = (actual as OnStartOp)
            val ruleN = (rule as OnStartOp)

            ruleN.opCodesList.containsAll(actual.opCodesList) && matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                actual = OnStartOpWrapper(actualN),
                rule = OnStartOpWrapper(ruleN)
            )
        }

        register(OnFinishOp::class.java) { actual, rule ->
            val actualN = (actual as OnFinishOp)
            val ruleN = (rule as OnFinishOp)

            ruleN.opCodesList.containsAll(actual.opCodesList) && matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                actual = OnFinishOpWrapper(actualN),
                rule = OnFinishOpWrapper(ruleN)
            )
        }

        register(OnQSTileClick::class.java) { actual, rule ->
            val actualN = (actual as OnQSTileClick)
            val ruleN = (rule as OnQSTileClick)

            actualN.tileNumber == ruleN.tileNumber
        }

        register(OnMenuActionTrigger::class.java) { actual, rule ->
            val actualN = (actual as OnMenuActionTrigger)
            val ruleN = (rule as OnMenuActionTrigger)

            actualN.menuNumber == ruleN.menuNumber
        }

        register(SystemSettingsChanged::class.java) { actual, rule ->
            val actualN = (actual as SystemSettingsChanged)
            val ruleN = (rule as SystemSettingsChanged)

            ruleN.urlAndExpectedValueRegex.first == actualN.urlAndExpectedValueRegex.first
                    && emptyOrRegexMatch(
                ruleN.urlAndExpectedValueRegex.second,
                actualN.urlAndExpectedValueRegex.second,
                ruleN.matchOptions
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
            emptyOrRegexMatch(ruleN.content, actualN.content, ruleN.matchOptions)
        }

        register(UsbDeviceAttached::class.java) { actual, rule ->
            val actualN = (actual as UsbDeviceAttached)
            val ruleN = (rule as UsbDeviceAttached)
            emptyOrRegexMatch(
                ruleN.deviceName,
                actualN.deviceName,
                ruleN.deviceNameRegexMatchOptions
            )
        }

        register(UsbDeviceDetached::class.java) { actual, rule ->
            val actualN = (actual as UsbDeviceDetached)
            val ruleN = (rule as UsbDeviceDetached)
            emptyOrRegexMatch(
                ruleN.deviceName,
                actualN.deviceName,
                ruleN.deviceNameRegexMatchOptions
            )
        }

        register(GlobalVarChanged::class.java) { actual, rule ->
            val actualN = (actual as GlobalVarChanged)
            val ruleN = (rule as GlobalVarChanged)

            actualN.gvId == ruleN.gvId
        }
        register(HasFoundNodeOnScreen::class.java) { actual, rule ->
            val actualN = (actual as HasFoundNodeOnScreen)
            val ruleN = (rule as HasFoundNodeOnScreen)
            actualN.id == ruleN.id
        }

        register(AppProcessStarted::class.java) { actual, rule ->
            val actualN = (actual as AppProcessStarted)
            val ruleN = (rule as AppProcessStarted)
            val actualProcessName = actualN.processNameList[0]
            ruleN.processNameList.any {
                it.userId == actualProcessName.userId && emptyOrRegexMatch(
                    it.processName,
                    actualProcessName.processName
                )
            }
        }

        register(AppProcessRemoved::class.java) { actual, rule ->
            val actualN = (actual as AppProcessRemoved)
            val ruleN = (rule as AppProcessRemoved)
            val actualProcessName = actualN.processNameList[0]
            ruleN.processNameList.any {
                it.userId == actualProcessName.userId && emptyOrRegexMatch(
                    it.processName,
                    actualProcessName.processName
                )
            }
        }

        register(ScreenRotate::class.java) { actual, rule ->
            val actualN = (actual as ScreenRotate)
            val ruleN = (rule as ScreenRotate)
            ruleN.degree == ScreenRotateDegree.ScreenRotateDegree_Any || ruleN.degree == actualN.degree
        }

        register(WindowRotationChange::class.java) { actual, rule ->
            val actualN = (actual as WindowRotationChange)
            val ruleN = (rule as WindowRotationChange)
            ruleN.degree == ScreenRotateDegree.ScreenRotateDegree_Any || ruleN.degree == actualN.degree
        }

        register(IMEVisibilityChange::class.java) { actual, rule ->
            val actualN = (actual as IMEVisibilityChange)
            val ruleN = (rule as IMEVisibilityChange)
            ruleN.isShown == actualN.isShown
        }

        register(AppGainWindowFocus::class.java) { actual, rule ->
            val actualN = (actual as AppGainWindowFocus)
            val ruleN = (rule as AppGainWindowFocus)
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                AppGainWindowFocusWrapper(actualN),
                AppGainWindowFocusWrapper(ruleN)
            )
        }

        register(AppLostWindowFocus::class.java) { actual, rule ->
            val actualN = (actual as AppLostWindowFocus)
            val ruleN = (rule as AppLostWindowFocus)
            matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                AppLostWindowFocusWrapper(actualN),
                AppLostWindowFocusWrapper(ruleN)
            )
        }

        register(DeepLinkCall::class.java) { actual, rule ->
            val actualN = (actual as DeepLinkCall)
            val ruleN = (rule as DeepLinkCall)
            actualN.deepLinkTag == ruleN.deepLinkTag
        }

        register(ConnectedWifiSignalLevelChanged::class.java) { actual, rule ->
            val actualN = (actual as ConnectedWifiSignalLevelChanged)
            val ruleN = (rule as ConnectedWifiSignalLevelChanged)
            ruleN.level < 0 || actualN.level == ruleN.level
        }

        register(NFCTagDiscover::class.java) { actual, rule ->
            val actualN = (actual as NFCTagDiscover)
            val ruleN = (rule as NFCTagDiscover)
            ruleN.uid.isEmpty || ruleN.uid == actualN.uid
        }

        register(Logcat::class.java) { actual, rule ->
            val actualN = (actual as Logcat)
            val ruleN = (rule as Logcat)
            ruleN.id == actualN.id
        }

        register(MethodHook::class.java) { actual, rule ->
            val actualN = (actual as MethodHook)
            val ruleN = (rule as MethodHook)
            ruleN.id == actualN.id
        }

        register(BackNavStart::class.java) { actual, rule ->
            true
        }
        register(BackNavDone::class.java) { actual, rule ->
            true
        }

        register(ScreenOnTime::class.java) { actual, rule ->
            val actualN = (actual as ScreenOnTime)
            val ruleN = (rule as ScreenOnTime)
            if (ruleN.from == ScreenOnTimeStartFrom.ScreenOnTimeStartFrom_LastScreenOff) {
                actualN.publishDataFromScreenOffTime.toString() == ruleN.timeInSeconds
            } else {
                actualN.publishDataFromSystemReadyTime.toString() == ruleN.timeInSeconds
            }
        }

        register(LightSensor::class.java) { actual, rule ->
            val actualN = (actual as LightSensor)
            val ruleN = (rule as LightSensor)
            matchSensorTrendAndValue(
                ruleTrend = ruleN.trend,
                actualTrend = actualN.trend,
                constraints = ruleN.constraintsList,
                actualValue = actualN.value
            )
        }

        register(Sound::class.java) { actual, rule ->
            val actualN = (actual as Sound)
            val ruleN = (rule as Sound)
            matchSensorTrendAndValue(
                ruleTrend = ruleN.trend,
                actualTrend = actualN.trend,
                constraints = ruleN.constraintsList,
                actualValue = actualN.value
            )
        }

        register(ProximitySensor::class.java) { actual, rule ->
            val actualN = (actual as ProximitySensor)
            val ruleN = (rule as ProximitySensor)
            matchSensorTrendAndValue(
                ruleTrend = ruleN.trend,
                actualTrend = actualN.trend,
                constraints = ruleN.constraintsList,
                actualValue = actualN.value
            )
        }
        register(AccelerometerSensor::class.java) { actual, rule ->
            val actualN = (actual as AccelerometerSensor)
            val ruleN = (rule as AccelerometerSensor)
            matchSensorTrendAndValue(
                ruleTrend = ruleN.trendX,
                actualTrend = actualN.trendX,
                constraints = ruleN.constraintsXList,
                actualValue = actualN.valueX
            ) && matchSensorTrendAndValue(
                ruleTrend = ruleN.trendY,
                actualTrend = actualN.trendY,
                constraints = ruleN.constraintsYList,
                actualValue = actualN.valueY
            ) && matchSensorTrendAndValue(
                ruleTrend = ruleN.trendZ,
                actualTrend = actualN.trendZ,
                constraints = ruleN.constraintsZList,
                actualValue = actualN.valueZ
            )
        }
        register(ProximitySensorStateChanged::class.java) { actual, rule ->
            val actualN = (actual as ProximitySensorStateChanged)
            val ruleN = (rule as ProximitySensorStateChanged)
            ruleN.state == ProximitySensorState.ProximitySensorState_Any || actualN.state == ruleN.state
        }
        register(ShakeDevice::class.java) { actual, rule ->
            true
        }
        register(CpuAvgAvailability::class.java) { actual, rule ->
            true
        }
        register(SunRiseSet::class.java) { actual, rule ->
            (actual as SunRiseSet)
            (rule as SunRiseSet)
            actual.rise == rule.rise
        }
        register(OnStartDynamicShortcut::class.java) { actual, rule ->
            (actual as OnStartDynamicShortcut)
            (rule as OnStartDynamicShortcut)
            actual.shortcutNumber == rule.shortcutNumber && matchWithAppOrPkgSetListFact(
                shortXContextProvider,
                OnStartDynamicShortcutWrapper(actual),
                OnStartDynamicShortcutWrapper(rule)
            )
        }

        logger.i("Init complete.")
    }

    private fun register(clazz: Class<*>, matcher: (l: ProtoMessage, f: ProtoMessage) -> Boolean) {
        logger.d("register: $clazz --> $matcher")
        if (factValueMatchers[clazz] != null) throw IllegalStateException("Dup matcher for class: $clazz")
        factValueMatchers[clazz] = Matcher(clazz.name, matcher)
    }

    override fun match(
        actualFact: ProtoMessage,
        ruleFacts: List<ProtoAny>
    ): FactMatcher.MatcherResult {
        for (ruleFact in ruleFacts) {
            if (ruleFact is_ AnyFact::class.java) {
                val tag = ruleFact unpack_ AnyFact::class.java
                return FactMatcher.MatcherResult.Match(tag.tag)
            } else {
                val isMatch = match(
                    actualFact,
                    ruleFact
                )
                if (isMatch) {
                    val tag = runCatching {
                        val unpackedRuleFact = ruleFact unpack_ actualFact::class.java
                        val tagDescriptor: Descriptors.FieldDescriptor =
                            unpackedRuleFact.descriptorForType.findFieldByName("tag")
                        unpackedRuleFact.getField(tagDescriptor) as String
                    }.getOrElse {
                        logger.e(it, "get tag from ruleFact")
                        ""
                    }
                    return FactMatcher.MatcherResult.Match(tag)
                }
            }
        }
        return FactMatcher.MatcherResult.NotMatch
    }

    private fun match(actualFact: ProtoMessage, ruleFact: ProtoAny): Boolean {
        return if (ruleFact is_ actualFact.javaClass) {
            val targetFactMessage = ruleFact unpack_ actualFact.javaClass

            val factAttrs = extractFactAttrs(targetFactMessage)
            logger.d("ruleFact attrs: $factAttrs")
            val isDisabled = factAttrs?.isDisabled ?: false
            !isDisabled && match0(actualFact, targetFactMessage)
        } else {
            false
        }
    }

    private fun match0(actual: ProtoMessage, rule: ProtoMessage): Boolean {
        val matcher = requireNotNull(factValueMatchers[actual.javaClass]) {
            "No matcher found for class: ${actual.javaClass}, pls report to the developer."
        }
        val res = runCatching {
            matcher.impl.invoke(actual, rule) ?: (actual == rule)
        }.getOrElse {
            logger.e(it, "Matcher error")
            false
        }
        logger.logOnDebug("matching actual: $actual, rule: $rule, res: $res")
        logger.d("matching class: ${matcher?.targetClass} res: $res")
        return res
    }


    private fun extractFactAttrs(fact: ProtoMessage): FactAttrs? {
        return runCatching {
            val idDescriptor: Descriptors.FieldDescriptor =
                fact.descriptorForType.findFieldByName("id")
            val id: String = fact.getField(idDescriptor) as String

            val isDisabledDescriptor: Descriptors.FieldDescriptor =
                fact.descriptorForType.findFieldByName("isDisabled")
            val isDisabled: Boolean = fact.getField(isDisabledDescriptor) as Boolean

            FactAttrs(id, isDisabled)
        }.getOrElse {
            logger.e(it, "extractFactAttrs: $fact")
            null
        }
    }
}

fun matchWithAppOrPkgSetListFact(
    // For Test.
    shortXContextProvider: () -> ShortXContext,
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
        shortXContextProvider().pkgSet.getPkgSetByLabel(label)?.appPkgsList ?: emptyList()
    } + rule.appList
    logger.d("matchWithAppOrPkgSetListFact, pkgListActual: ${pkgListActual.map { it.appPkgToString() }}, pkgListRule: ${pkgListRule.map { it.appPkgToString() }}")
    return pkgListRule.isNotEmpty() && pkgListRule.containsAll(pkgListActual)
}


fun emptyOrRegexMatch(
    vararg ruleAndActuals: Pair<RegexMatchData, String>,
) =
    ruleAndActuals.all {
        emptyOrRegexMatch(it.first.regex, it.second, it.first.options)
    }

fun matchNotificationTagTitleContent(
    actual: Notification,
    rule: Notification
): Boolean {
    // FAST: No matcher settings
    if (rule == Notification.getDefaultInstance()) {
        return true
    }
    // User set a tag, check tag first.
    if (rule.tag.isNotEmpty() && rule.tag != actual.tag) {
        logger.d("matchNotificationMatcher, false at tag.")
        return false
    }

    return emptyOrRegexMatch(
        RegexMatchData(rule.title, rule.titleRegexOptions) to actual.title,
        RegexMatchData(rule.contentText, rule.contentRegexOptions) to actual.contentText,
    )
}

fun matchNotificationTag(
    actual: Notification,
    rule: Notification
): Boolean {
    // FAST: No matcher settings
    if (rule == Notification.getDefaultInstance()) {
        return true
    }
    // User set a tag, check tag first.
    if (rule.tag.isNotEmpty() && rule.tag != actual.tag) {
        logger.d("matchNotificationMatcher, false at tag.")
        return false
    }

    return true
}

fun emptyOrRegexMatch(
    ruleField: String,
    actualField: String,
    options: RegexMatchOptions = RegexMatchOptions.RegexMatchOptions_Match
) = emptyOrRegexMatchAndResults(ruleField, actualField, options).first

fun emptyOrRegexMatchAndResults(
    ruleField: String,
    actualField: String,
    options: RegexMatchOptions = RegexMatchOptions.RegexMatchOptions_Match
): Pair<Boolean, List<String>> {
    if (ruleField.isEmpty()) return true to emptyList()
    return ruleField.toRegex().let { regex ->
        val isMatch = if (options == RegexMatchOptions.RegexMatchOptions_Match) {
            regex.matches(actualField)
        } else {
            regex.containsMatchIn(actualField)
        }
        isMatch to (regex.findAll(actualField).map { it.value }.toList())
    }.apply {
        logger.d("emptyOrRegexMatch, regex: $ruleField, options: $options, result: $this")
    }
}

private fun matchSensorTrendAndValue(
    ruleTrend: SensorValueTrend,
    actualTrend: SensorValueTrend,
    constraints: List<ValueConstraints>,
    actualValue: Float
): Boolean {
    return (ruleTrend == SensorValueTrend.SensorValueTrend_Any
            || ruleTrend == SensorValueTrend.UNRECOGNIZED
            || ruleTrend == actualTrend
            ) && (
            constraints.isEmpty() || constraints.all { con ->
                EvaluateContextVar.evaluate(
                    actualValue.toString(),
                    con.op,
                    con.payload.value.orEmpty()
                )
            })
}

interface WithAppOrPkgSetListFact {
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

class AppGainWindowFocusWrapper(private val appGainWindowFocus: AppGainWindowFocus) :
    WithAppOrPkgSetListFact {
    override val pkgSetList: List<String>
        get() = appGainWindowFocus.pkgSetsList ?: emptyList()
    override val appList: List<AppPkg>
        get() = appGainWindowFocus.appsList ?: emptyList()

}

class AppLostWindowFocusWrapper(private val appLostWindowFocus: AppLostWindowFocus) :
    WithAppOrPkgSetListFact {
    override val pkgSetList: List<String>
        get() = appLostWindowFocus.pkgSetsList ?: emptyList()
    override val appList: List<AppPkg>
        get() = appLostWindowFocus.appsList ?: emptyList()

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

class PkgStartRunningWrapper(private val startRunning: PkgStartRunning) : WithAppOrPkgSetListFact {
    override val pkgSetList: List<String>
        get() = startRunning.pkgSetsList ?: emptyList()
    override val appList: List<AppPkg>
        get() = startRunning.appsList ?: emptyList()

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

class OnStartDynamicShortcutWrapper(private val os: OnStartDynamicShortcut) :
    WithAppOrPkgSetListFact {
    override val pkgSetList: List<String>
        get() = os.pkgSetsList ?: emptyList()
    override val appList: List<AppPkg>
        get() = os.appsList ?: emptyList()

}