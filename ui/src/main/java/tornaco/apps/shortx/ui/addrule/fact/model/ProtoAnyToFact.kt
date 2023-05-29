package tornaco.apps.shortx.ui.addrule.fact.model

import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.addrule.protoPkgsAndPkgSetsToModels


fun ProtoAny.toFact(shortXManager: ShortXManager): Fact? {
    return when {
        this is_ tornaco.apps.shortx.core.proto.fact.KeyEvent::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.KeyEvent::class.java
            Fact.KeyEvent(keyCode = data.keyCode)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.EdgeGesture::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.EdgeGesture::class.java
            Fact.EdgeGesture(edge = data.edge, gesture = data.gesture)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ScreenOff::class.java -> {
            Fact.ScreenOff
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ScreenOn::class.java -> {
            Fact.ScreenOn
        }

        this is_ tornaco.apps.shortx.core.proto.fact.UserPresent::class.java -> {
            Fact.UserPresent
        }

        this is_ tornaco.apps.shortx.core.proto.fact.Alarm::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.Alarm::class.java
            Fact.Alarm(
                timeOfADay = data.triggerAt,
                repeatDays = data.repeat,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppBecomeFg::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppBecomeFg::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppBecomeForeground(
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppBecomeBg::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppBecomeBg::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppBecomeBackground(
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ActivityStarted::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.ActivityStarted::class.java
            if (data.componentsList.isEmpty()) {
                Fact.AnyActivityStarted
            } else {
                Fact.ActivityStarted(data.componentsList)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.fact.WifiStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.WifiStatusChanged::class.java
            Fact.WifiStatusChanged(data.ooa)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BTStatusChanged::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.BTStatusChanged::class.java
            Fact.BTStatusChanged(data.ooa)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged::class.java
            Fact.HotSpotStatusChanged(data.ooa)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.NFCStatusChanged::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.NFCStatusChanged::class.java
            Fact.NFCStatusChanged(data.ooa)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.LocationStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.LocationStatusChanged::class.java
            Fact.LocationStatusChanged(data.ooa)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged::class.java
            Fact.MobileDataStatusChanged(data.ooa)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.FlashLightStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.FlashLightStatusChanged::class.java
            Fact.FlashLightStatusChanged(data.ooa)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.DNDStatusChanged::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.DNDStatusChanged::class.java
            Fact.DNDStatusChanged(data.ooa)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.DarkModeStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.DarkModeStatusChanged::class.java
            Fact.DarkModeStatusChanged(data.ooa)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.NotificationPosted::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.NotificationPosted::class.java
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

        this is_ tornaco.apps.shortx.core.proto.fact.NotificationRemoved::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.NotificationRemoved::class.java
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

        this is_ tornaco.apps.shortx.core.proto.fact.NotificationUpdated::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.NotificationUpdated::class.java
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

        this is_ tornaco.apps.shortx.core.proto.fact.TaskRemoved::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.TaskRemoved::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()

            if (models.first.isEmpty() && models.second.isEmpty()) {
                Fact.TaskRemovedAny
            } else {
                Fact.TaskRemoved(
                    apps = models.first,
                    pkgSets = models.second
                )
            }
        }

        else -> {
            null
        }
    }
}
