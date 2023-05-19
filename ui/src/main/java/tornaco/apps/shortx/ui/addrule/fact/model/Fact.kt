package tornaco.apps.shortx.ui.addrule.fact.model

import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.*
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.addrule.ContextData
import tornaco.apps.shortx.ui.addrule.factContextData
import tornaco.apps.shortx.ui.addrule.protoPkgsAndPkgSetsToModels

sealed interface Fact {
    data class KeyEvent(val keyCode: Int) : Fact

    object AnyActivityStarted : Fact
    data class ActivityStarted(val components: List<AppComponent>) : Fact

    data class EdgeGesture(val edge: Edge, val gesture: Gesture) : Fact

    object ScreenOn : Fact
    object ScreenOff : Fact
    object UserPresent : Fact

    data class Alarm(val tag: String, val timeOfADay: TimeOfADay, val repeatDays: RepeatDays) : Fact

    data class AppBecomeBackground(
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact

    data class AppBecomeForeground(
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Fact

    data class WifiStatusChanged(val state: OnOffAny) : Fact
    data class BTStatusChanged(val state: OnOffAny) : Fact
    data class MobileDataStatusChanged(val state: OnOffAny) : Fact
    data class LocationStatusChanged(val state: OnOffAny) : Fact
    data class NFCStatusChanged(val state: OnOffAny) : Fact
    data class DNDStatusChanged(val state: OnOffAny) : Fact
    data class HotSpotStatusChanged(val state: OnOffAny) : Fact
    data class FlashLightStatusChanged(val state: OnOffAny) : Fact
    data class DarkModeStatusChanged(val state: OnOffAny) : Fact

    data class Notification(
        val apps: List<App>,
        val pkgSets: List<PkgSet> = emptyList(),
        val titleRegex: String? = null,
        val contentRegex: String? = null
    )

    data class NotificationPosted(
        val n: Notification
    ) : Fact

    data class NotificationRemoved(
        val n: Notification
    ) : Fact

    data class NotificationUpdated(
        val n: Notification
    ) : Fact
}

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

fun ProtoAny.toFact(shortXManager: ShortXManager): Fact? {
    return when {
        this is_ KeyEvent::class.java -> {
            val data = this unpack_ KeyEvent::class.java
            Fact.KeyEvent(keyCode = data.keyCode)
        }
        this is_ EdgeGesture::class.java -> {
            val data = this unpack_ EdgeGesture::class.java
            Fact.EdgeGesture(edge = data.edge, gesture = data.gesture)
        }
        this is_ ScreenOff::class.java -> {
            Fact.ScreenOff
        }
        this is_ ScreenOn::class.java -> {
            Fact.ScreenOn
        }
        this is_ UserPresent::class.java -> {
            Fact.UserPresent
        }
        this is_ Alarm::class.java -> {
            val data = this unpack_ Alarm::class.java
            Fact.Alarm(
                timeOfADay = data.triggerAt,
                repeatDays = data.repeat,
                tag = data.tag
            )
        }
        this is_ AppBecomeFg::class.java -> {
            val data = this unpack_ AppBecomeFg::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppBecomeForeground(
                apps = models.first,
                pkgSets = models.second
            )
        }
        this is_ AppBecomeBg::class.java -> {
            val data = this unpack_ AppBecomeBg::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppBecomeBackground(
                apps = models.first,
                pkgSets = models.second
            )
        }
        this is_ ActivityStarted::class.java -> {
            val data = this unpack_ ActivityStarted::class.java
            if (data.componentsList.isEmpty()) {
                Fact.AnyActivityStarted
            } else {
                Fact.ActivityStarted(data.componentsList)
            }
        }
        this is_ WifiStatusChanged::class.java -> {
            val data = this unpack_ WifiStatusChanged::class.java
            Fact.WifiStatusChanged(data.ooa)
        }
        this is_ BTStatusChanged::class.java -> {
            val data = this unpack_ BTStatusChanged::class.java
            Fact.BTStatusChanged(data.ooa)
        }
        this is_ HotSpotStatusChanged::class.java -> {
            val data = this unpack_ HotSpotStatusChanged::class.java
            Fact.HotSpotStatusChanged(data.ooa)
        }
        this is_ NFCStatusChanged::class.java -> {
            val data = this unpack_ NFCStatusChanged::class.java
            Fact.NFCStatusChanged(data.ooa)
        }
        this is_ LocationStatusChanged::class.java -> {
            val data = this unpack_ LocationStatusChanged::class.java
            Fact.LocationStatusChanged(data.ooa)
        }
        this is_ MobileDataStatusChanged::class.java -> {
            val data = this unpack_ MobileDataStatusChanged::class.java
            Fact.MobileDataStatusChanged(data.ooa)
        }
        this is_ FlashLightStatusChanged::class.java -> {
            val data = this unpack_ FlashLightStatusChanged::class.java
            Fact.FlashLightStatusChanged(data.ooa)
        }
        this is_ DNDStatusChanged::class.java -> {
            val data = this unpack_ DNDStatusChanged::class.java
            Fact.DNDStatusChanged(data.ooa)
        }
        this is_ DarkModeStatusChanged::class.java -> {
            val data = this unpack_ DarkModeStatusChanged::class.java
            Fact.DarkModeStatusChanged(data.ooa)
        }

        this is_ NotificationPosted::class.java -> {
            val data = this unpack_ NotificationPosted::class.java
            val models =
                (data.record.appsList to data.record.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.NotificationPosted(
                n = Fact.Notification(
                    apps = models.first,
                    pkgSets = models.second,
                    titleRegex = data.record.title,
                    contentRegex = data.record.contentText
                )
            )
        }

        this is_ NotificationRemoved::class.java -> {
            val data = this unpack_ NotificationRemoved::class.java
            val models =
                (data.record.appsList to data.record.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.NotificationRemoved(
                n = Fact.Notification(
                    apps = models.first,
                    pkgSets = models.second,
                    titleRegex = data.record.title,
                    contentRegex = data.record.contentText
                )
            )
        }

        this is_ NotificationUpdated::class.java -> {
            val data = this unpack_ NotificationUpdated::class.java
            val models =
                (data.new.appsList to data.new.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.NotificationUpdated(
                n = Fact.Notification(
                    apps = models.first,
                    pkgSets = models.second,
                    titleRegex = data.new.title,
                    contentRegex = data.new.contentText
                )
            )
        }

        else -> {
            null
        }
    }
}

val Fact.contextData: List<ContextData> get() = this::class.factContextData()