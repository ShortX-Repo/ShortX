package tornaco.apps.shortx.ui.addrule.fact.model

import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppComponent
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.fact.*
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.addrule.protoPkgsAndPkgSetsToModels

sealed interface Fact {
    data class KeyEvent(val keyCode: Int) : Fact
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
            }).addAllPkgSets(pkgSets.map { it.id }).build()
        }
        is Fact.AppBecomeForeground -> {
            AppBecomeFg.newBuilder().addAllApps(apps.map {
                AppPkg.newBuilder().setPkgName(it.pkg.pkgName).setUserId(it.pkg.userId).build()
            }).addAllPkgSets(pkgSets.map { it.id }).build()
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
            Fact.ActivityStarted(data.componentsList)
        }
        else -> {
            null
        }
    }
}