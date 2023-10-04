package tornaco.apps.shortx.ui.addrule.fact.model

import tornaco.apps.shortx.core.alarm.toProto
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.AnyFact
import tornaco.apps.shortx.core.proto.fact.AppAdded
import tornaco.apps.shortx.core.proto.fact.AppBecomeBg
import tornaco.apps.shortx.core.proto.fact.AppBecomeFg
import tornaco.apps.shortx.core.proto.fact.AppProcessStarted
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
import tornaco.apps.shortx.core.proto.fact.HasFoundNodeOnScreen
import tornaco.apps.shortx.core.proto.fact.HeadsetPlug
import tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged
import tornaco.apps.shortx.core.proto.fact.KeyEvent
import tornaco.apps.shortx.core.proto.fact.LocationStatusChanged
import tornaco.apps.shortx.core.proto.fact.MediaStoreInsert
import tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged
import tornaco.apps.shortx.core.proto.fact.NFCStatusChanged
import tornaco.apps.shortx.core.proto.fact.NodeMatcherText
import tornaco.apps.shortx.core.proto.fact.NodeMatcherViewId
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
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.pack_


fun Fact.toProtoFact(overrideId: String? = null): ProtoMessage {
    return when (this) {
        is Fact.AnyFact -> {
            AnyFact.newBuilder().build()
        }

        is Fact.KeyEvent -> {
            KeyEvent.newBuilder().setKeyCode(keyCode).setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.AdvancedKeyEvent -> {
            AdvancedKeyEvent.newBuilder().setKeyCode(keyCode).setGesture(gesture)
                .setIsInterceptMode(isInterceptMode).setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.ActivityStarted -> {
            ActivityStarted.newBuilder()
                .addAllComponents(components)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.AnyActivityStarted -> {
            ActivityStarted.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.EdgeGesture -> {
            EdgeGesture.newBuilder().setEdge(edge).setGesture(gesture).setId(overrideId ?: id)
                .setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.ScreenOff -> {
            ScreenOff.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.ScreenOn -> {
            ScreenOn.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.UserPresent -> {
            UserPresent.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.SystemReady -> {
            SystemReady.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.Alarm -> {
            Alarm.newBuilder()
                .setRepeat(repeatDays)
                .setTriggerAt(timeOfADay)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.RandomInPeriod -> {
            RandomInPeriod.newBuilder()
                .setRepeat(repeatDays)
                .setStart(start)
                .setEnd(end)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.FixedInPeriod -> {
            FixedInPeriod.newBuilder()
                .setRepeat(repeatDays)
                .setStart(start)
                .setEnd(end)
                .setFixedBy(fixedBy.toProto())
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.AppBecomeBackground -> {
            AppBecomeBg.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.AppBecomeForeground -> {
            AppBecomeFg.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.WifiStatusChanged -> {
            WifiStatusChanged.newBuilder()
                .setOoa(state)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.WifiConnectedTo -> {
            WifiConnectedTo.newBuilder()
                .addAllSsidList(ssidList)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.WifiDisconnectedFrom -> {
            WifiDisconnectedFrom.newBuilder()
                .addAllSsidList(ssidList)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.BTStatusChanged -> {
            BTStatusChanged.newBuilder()
                .setOoa(state)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.BTConnectedTo -> {
            BTConnectedTo.newBuilder()
                .addAllDevice(devices)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.BTDisconnectedFrom -> {
            BTDisconnectedFrom.newBuilder()
                .addAllDevice(devices)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.DNDStatusChanged -> {
            DNDStatusChanged.newBuilder()
                .setOoa(state)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.FlashLightStatusChanged -> {
            FlashLightStatusChanged.newBuilder()
                .setOoa(state)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.HotSpotStatusChanged -> {
            HotSpotStatusChanged.newBuilder()
                .setOoa(state)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.LocationStatusChanged -> {
            LocationStatusChanged.newBuilder()
                .setOoa(state)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.MobileDataStatusChanged -> {
            MobileDataStatusChanged.newBuilder()
                .setOoa(state)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.NFCStatusChanged -> {
            NFCStatusChanged.newBuilder()
                .setOoa(state)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.DarkModeStatusChanged -> {
            DarkModeStatusChanged.newBuilder()
                .setOoa(state)
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.NotificationPosted -> {
            NotificationPosted.newBuilder()
                .setRecord(
                    Notification.newBuilder()
                        .addAllApps(n.apps.map { it.pkg })
                        .addAllPkgSets(n.pkgSets.map { it.label })
                        .setTitle(n.titleRegex)
                        .setContentText(n.contentRegex)
                        .setTag(n.tagRegex)
                        .build()
                )
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.NotificationRemoved -> {
            NotificationRemoved.newBuilder()
                .setRecord(
                    Notification.newBuilder()
                        .addAllApps(n.apps.map { it.pkg })
                        .addAllPkgSets(n.pkgSets.map { it.label })
                        .setTitle(n.titleRegex)
                        .setContentText(n.contentRegex)
                        .setTag(n.tagRegex)
                        .build()
                )
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.NotificationUpdated -> {
            NotificationUpdated.newBuilder()
                .setNew(
                    Notification.newBuilder()
                        .addAllApps(n.apps.map { it.pkg })
                        .addAllPkgSets(n.pkgSets.map { it.label })
                        .setTitle(n.titleRegex)
                        .setContentText(n.contentRegex)
                        .setTag(n.tagRegex)
                        .build()
                )
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.TaskRemoved -> {
            TaskRemoved.newBuilder()
                .addAllApps(apps.map { it.pkg })
                .addAllPkgSets(pkgSets.map { it.label })
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.TaskRemovedAny -> {
            TaskRemoved.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.PkgStopRunning -> {
            PkgStopRunning.newBuilder()
                .addAllApps(apps.map { it.pkg })
                .addAllPkgSets(pkgSets.map { it.label })
                .setId(overrideId ?: id).setNote(note).setCustomContextDataKey(customContextDataKey)
                .build()
        }

        is Fact.PkgStopRunningAny -> {
            PkgStopRunning.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.AppAdded -> {
            AppAdded.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.AppRemoved -> {
            AppRemoved.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.AppUpdated -> {
            AppUpdated.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.AudioFocusGain -> {
            AudioFocusGain.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.AudioFocusLost -> {
            AudioFocusLost.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.AudioFocusChanged -> {
            AudioFocusChanged.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.BatteryLevelChanged -> {
            BatteryLevelChanged.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).setLevel(level)
                .build()
        }

        is Fact.BatteryTemperatureChanged -> {
            BatteryTemperatureChanged.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .setTemp(temp).build()
        }

        is Fact.ChargerPlug -> {
            ChargerPlug.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.ChargerUnplug -> {
            ChargerUnplug.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.Broadcast -> {
            Broadcast.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).addAllActions(actions)
                .build()
        }

        is Fact.VPNConnected -> {
            VPNConnected.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.VPNDisconnected -> {
            VPNDisconnected.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey).build()
        }

        is Fact.MediaStoreInsert -> {
            MediaStoreInsert.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .setFilterPath(filterPathRegex)
                .build()
        }

        is Fact.OnStartOp -> {
            OnStartOp.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .addAllOpCodes(codes)
                .addAllApps(apps.map {
                    AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
                })
                .addAllPkgSets(pkgSets.map { it.label })
                .build()
        }

        is Fact.OnFinishOp -> {
            OnFinishOp.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .addAllOpCodes(codes)
                .addAllApps(apps.map {
                    AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
                })
                .addAllPkgSets(pkgSets.map { it.label })
                .build()
        }

        is Fact.OnQSTileClick -> {
            OnQSTileClick.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .setTileNumber(tile.number)
                .build()
        }

        is Fact.SystemSettingsChanged -> {
            SystemSettingsChanged.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .setUrlAndExpectedValueRegex(urlAndValueRegex)
                .build()
        }

        is Fact.CallStateChanged -> {
            CallStateChanged.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .setCallState(callState)
                .build()
        }

        is Fact.ClipboardContentChanged -> {
            ClipboardContentChanged.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .setContent(content)
                .build()
        }

        is Fact.GlobalVarChanged -> {
            GlobalVarChanged.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .setGvId(gvId)
                .build()
        }

        is Fact.AppProcessStarted -> {
            AppProcessStarted.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .addAllProcessName(processNames)
                .build()
        }

        is Fact.HeadsetPlug -> {
            HeadsetPlug.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .setIsPlug(isPlug)
                .build()
        }

        is Fact.HasFoundNodeOnScreen -> {
            HasFoundNodeOnScreen.newBuilder().setId(overrideId ?: id).setNote(note)
                .setCustomContextDataKey(customContextDataKey)
                .setPackageName(packageName)
                .setComponentName(componentName)
                .setDetectTimeout(detectTimeout)
                .addAllMatchers(matchers.mapNotNull {
                    when (it) {
                        is NodeMatcher.Text -> {
                            NodeMatcherText.newBuilder().setText(it.text).setIsRegex(it.isRegex)
                                .build()
                                .pack_()
                        }

                        is NodeMatcher.ViewId -> {
                            NodeMatcherViewId.newBuilder().setId(it.viewId).build().pack_()
                        }

                        else -> null
                    }
                })
                .build()
        }

    }
}