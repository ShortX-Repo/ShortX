package tornaco.apps.shortx.ui.addrule.fact.model

import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.fact.ActivityStarted
import tornaco.apps.shortx.core.proto.fact.Alarm
import tornaco.apps.shortx.core.proto.fact.AppBecomeBg
import tornaco.apps.shortx.core.proto.fact.AppBecomeFg
import tornaco.apps.shortx.core.proto.fact.BTStatusChanged
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
import tornaco.apps.shortx.core.proto.fact.ScreenOff
import tornaco.apps.shortx.core.proto.fact.ScreenOn
import tornaco.apps.shortx.core.proto.fact.UserPresent
import tornaco.apps.shortx.core.proto.fact.WifiStatusChanged
import tornaco.apps.shortx.core.rule.ProtoMessage


fun Fact.toProtoFact(): ProtoMessage {
    return when (this) {
        is Fact.KeyEvent -> {
            KeyEvent.newBuilder().setKeyCode(keyCode).build()
        }

        is Fact.ActivityStarted -> {
            ActivityStarted.newBuilder()
                .addAllComponents(components)
                .build()
        }

        Fact.AnyActivityStarted -> {
            ActivityStarted.newBuilder().build()
        }

        is Fact.EdgeGesture -> {
            EdgeGesture.newBuilder().setEdge(edge).setGesture(gesture).build()
        }

        Fact.ScreenOff -> {
            ScreenOff.newBuilder().build()
        }

        Fact.ScreenOn -> {
            ScreenOn.newBuilder().build()
        }

        Fact.UserPresent -> {
            UserPresent.newBuilder().build()
        }

        is Fact.Alarm -> {
            Alarm.newBuilder()
                .setRepeat(repeatDays)
                .setTriggerAt(timeOfADay)
                .setTag(tag)
                .build()
        }

        is Fact.AppBecomeBackground -> {
            AppBecomeBg.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).build()
        }

        is Fact.AppBecomeForeground -> {
            AppBecomeFg.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.label }).build()
        }

        is Fact.WifiStatusChanged -> {
            WifiStatusChanged.newBuilder()
                .setOoa(state)
                .build()
        }

        is Fact.BTStatusChanged -> {
            BTStatusChanged.newBuilder()
                .setOoa(state)
                .build()
        }

        is Fact.DNDStatusChanged -> {
            DNDStatusChanged.newBuilder()
                .setOoa(state)
                .build()
        }

        is Fact.FlashLightStatusChanged -> {
            FlashLightStatusChanged.newBuilder()
                .setOoa(state)
                .build()
        }

        is Fact.HotSpotStatusChanged -> {
            HotSpotStatusChanged.newBuilder()
                .setOoa(state)
                .build()
        }

        is Fact.LocationStatusChanged -> {
            LocationStatusChanged.newBuilder()
                .setOoa(state)
                .build()
        }

        is Fact.MobileDataStatusChanged -> {
            MobileDataStatusChanged.newBuilder()
                .setOoa(state)
                .build()
        }

        is Fact.NFCStatusChanged -> {
            NFCStatusChanged.newBuilder()
                .setOoa(state)
                .build()
        }

        is Fact.DarkModeStatusChanged -> {
            DarkModeStatusChanged.newBuilder()
                .setOoa(state)
                .build()
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
                .build()
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
                .build()
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
                .build()
        }
    }
}