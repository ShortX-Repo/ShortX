package tornaco.apps.shortx.ui.addrule.fact.model

import tornaco.apps.shortx.core.proto.common.AppPkg
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
import tornaco.apps.shortx.core.proto.fact.Notification
import tornaco.apps.shortx.core.proto.fact.NotificationPosted
import tornaco.apps.shortx.core.proto.fact.NotificationRemoved
import tornaco.apps.shortx.core.proto.fact.NotificationUpdated
import tornaco.apps.shortx.core.proto.fact.PkgStopRunning
import tornaco.apps.shortx.core.proto.fact.ScreenOff
import tornaco.apps.shortx.core.proto.fact.ScreenOn
import tornaco.apps.shortx.core.proto.fact.SystemReady
import tornaco.apps.shortx.core.proto.fact.TaskRemoved
import tornaco.apps.shortx.core.proto.fact.UserPresent
import tornaco.apps.shortx.core.proto.fact.WifiStatusChanged
import tornaco.apps.shortx.core.rule.ProtoMessage


fun Fact.toProtoFact(): ProtoMessage {
    return when (this) {
        is Fact.KeyEvent -> {
            KeyEvent.newBuilder().setKeyCode(keyCode).setId(id).setNote(note).build()
        }

        is Fact.ActivityStarted -> {
            ActivityStarted.newBuilder()
                .addAllComponents(components)
                .setId(id).setNote(note).build()
        }

        is Fact.AnyActivityStarted -> {
            ActivityStarted.newBuilder().setId(id).setNote(note).build()
        }

        is Fact.EdgeGesture -> {
            EdgeGesture.newBuilder().setEdge(edge).setGesture(gesture).setId(id).setNote(note)
                .build()
        }

        is Fact.ScreenOff -> {
            ScreenOff.newBuilder().setId(id).setNote(note).build()
        }

        is Fact.ScreenOn -> {
            ScreenOn.newBuilder().setId(id).setNote(note).build()
        }

        is Fact.UserPresent -> {
            UserPresent.newBuilder().setId(id).setNote(note).build()
        }

        is Fact.SystemReady -> {
            SystemReady.newBuilder().setId(id).setNote(note).build()
        }

        is Fact.Alarm -> {
            Alarm.newBuilder()
                .setRepeat(repeatDays)
                .setTriggerAt(timeOfADay)
                .setTag(tag)
                .setId(id).setNote(note).build()
        }

        is Fact.AppBecomeBackground -> {
            AppBecomeBg.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(id).setNote(note).build()
        }

        is Fact.AppBecomeForeground -> {
            AppBecomeFg.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(id).setNote(note).build()
        }

        is Fact.WifiStatusChanged -> {
            WifiStatusChanged.newBuilder()
                .setOoa(state)
                .setId(id).setNote(note).build()
        }

        is Fact.BTStatusChanged -> {
            BTStatusChanged.newBuilder()
                .setOoa(state)
                .setId(id).setNote(note).build()
        }

        is Fact.DNDStatusChanged -> {
            DNDStatusChanged.newBuilder()
                .setOoa(state)
                .setId(id).setNote(note).build()
        }

        is Fact.FlashLightStatusChanged -> {
            FlashLightStatusChanged.newBuilder()
                .setOoa(state)
                .setId(id).setNote(note).build()
        }

        is Fact.HotSpotStatusChanged -> {
            HotSpotStatusChanged.newBuilder()
                .setOoa(state)
                .setId(id).setNote(note).build()
        }

        is Fact.LocationStatusChanged -> {
            LocationStatusChanged.newBuilder()
                .setOoa(state)
                .setId(id).setNote(note).build()
        }

        is Fact.MobileDataStatusChanged -> {
            MobileDataStatusChanged.newBuilder()
                .setOoa(state)
                .setId(id).setNote(note).build()
        }

        is Fact.NFCStatusChanged -> {
            NFCStatusChanged.newBuilder()
                .setOoa(state)
                .setId(id).setNote(note).build()
        }

        is Fact.DarkModeStatusChanged -> {
            DarkModeStatusChanged.newBuilder()
                .setOoa(state)
                .setId(id).setNote(note).build()
        }

        is Fact.NotificationPosted -> {
            NotificationPosted.newBuilder()
                .setRecord(
                    Notification.newBuilder()
                        .addAllApps(n.apps.map { it.pkg })
                        .addAllPkgSets(n.pkgSets.map { it.label })
                        .apply {
                            n.titleRegex?.let { title = it }
                            n.contentRegex?.let { contentText = it }
                        }
                        .build()
                )
                .setId(id).setNote(note).build()
        }

        is Fact.NotificationRemoved -> {
            NotificationRemoved.newBuilder()
                .setRecord(
                    Notification.newBuilder()
                        .addAllApps(n.apps.map { it.pkg })
                        .addAllPkgSets(n.pkgSets.map { it.label })
                        .apply {
                            n.titleRegex?.let { title = it }
                            n.contentRegex?.let { contentText = it }
                        }
                        .build()
                )
                .setId(id).setNote(note).build()
        }

        is Fact.NotificationUpdated -> {
            NotificationUpdated.newBuilder()
                .setNew(
                    Notification.newBuilder()
                        .addAllApps(n.apps.map { it.pkg })
                        .addAllPkgSets(n.pkgSets.map { it.label })
                        .apply {
                            n.titleRegex?.let { title = it }
                            n.contentRegex?.let { contentText = it }
                        }
                        .build()
                )
                .setId(id).setNote(note).build()
        }

        is Fact.TaskRemoved -> {
            TaskRemoved.newBuilder()
                .addAllApps(apps.map { it.pkg })
                .addAllPkgSets(pkgSets.map { it.label })
                .setId(id).setNote(note).build()
        }

        is Fact.TaskRemovedAny -> {
            TaskRemoved.newBuilder().setId(id).setNote(note).build()
        }

        is Fact.PkgStopRunning -> {
            PkgStopRunning.newBuilder()
                .addAllApps(apps.map { it.pkg })
                .addAllPkgSets(pkgSets.map { it.label })
                .setId(id).setNote(note).build()
        }

        is Fact.PkgStopRunningAny -> {
            TaskRemoved.newBuilder().setId(id).setNote(note).build()
        }

        is Fact.AppAdded -> {
            AppAdded.newBuilder().setId(id).setNote(note).build()
        }

        is Fact.AppRemoved -> {
            AppRemoved.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(id).setNote(note).build()
        }

        is Fact.AppUpdated -> {
            AppUpdated.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(id).setNote(note).build()
        }

        is Fact.AudioFocusGain -> {
            AudioFocusGain.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(id).setNote(note).build()
        }

        is Fact.AudioFocusLost -> {
            AudioFocusLost.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).setId(id).setNote(note).build()
        }

        is Fact.AudioFocusChanged -> {
            AudioFocusChanged.newBuilder().setId(id).setNote(note).build()
        }

        is Fact.BatteryLevelChanged -> {
            BatteryLevelChanged.newBuilder().setId(id).setNote(note).setLevel(level).build()
        }

        is Fact.ChargerPlug -> {
            ChargerPlug.newBuilder().setId(id).setNote(note).build()
        }

        is Fact.ChargerUnplug -> {
            ChargerUnplug.newBuilder().setId(id).setNote(note).build()
        }

    }
}