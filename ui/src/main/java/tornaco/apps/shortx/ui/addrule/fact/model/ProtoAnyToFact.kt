package tornaco.apps.shortx.ui.addrule.fact.model

import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.alarm.FixedSetting
import tornaco.apps.shortx.core.alarm.toFixedBy
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.addrule.protoPkgsAndPkgSetsToModels


fun ProtoAny.toFact(shortXManager: ShortXManager): Fact? {
    return when {
        this is_ tornaco.apps.shortx.core.proto.fact.KeyEvent::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.KeyEvent::class.java
            Fact.KeyEvent(keyCode = data.keyCode, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent::class.java
            Fact.AdvancedKeyEvent(
                keyCode = data.keyCode,
                gesture = data.gesture,
                isInterceptMode = data.isInterceptMode,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.EdgeGesture::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.EdgeGesture::class.java
            Fact.EdgeGesture(
                edge = data.edge,
                gesture = data.gesture,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ScreenOff::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.ScreenOff::class.java
            Fact.ScreenOff(id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ScreenOn::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.ScreenOn::class.java
            Fact.ScreenOn(id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.UserPresent::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.UserPresent::class.java
            Fact.UserPresent(id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.SystemReady::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.SystemReady::class.java
            Fact.SystemReady(id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.Alarm::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.Alarm::class.java
            Fact.Alarm(
                timeOfADay = data.triggerAt,
                repeatDays = data.repeat,
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.RandomInPeriod::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.RandomInPeriod::class.java
            Fact.RandomInPeriod(
                start = data.start,
                end = data.end,
                repeatDays = data.repeat,
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.FixedInPeriod::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.FixedInPeriod::class.java
            Fact.FixedInPeriod(
                start = data.start,
                end = data.end,
                repeatDays = data.repeat,
                fixedBy = data.fixedBy.toFixedBy() ?: FixedSetting.Times(0),
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppBecomeFg::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppBecomeFg::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppBecomeForeground(
                apps = models.first,
                pkgSets = models.second,
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppBecomeBg::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppBecomeBg::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppBecomeBackground(
                apps = models.first,
                pkgSets = models.second,
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppAdded::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppAdded::class.java
            Fact.AppAdded(id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppRemoved::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppRemoved::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppRemoved(
                apps = models.first,
                pkgSets = models.second,
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppUpdated::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppUpdated::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppUpdated(
                apps = models.first,
                pkgSets = models.second,
                id = data.id, note = data.note
            )
        }


        this is_ tornaco.apps.shortx.core.proto.fact.ActivityStarted::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.ActivityStarted::class.java
            if (data.componentsList.isEmpty()) {
                Fact.AnyActivityStarted(id = data.id, note = data.note)
            } else {
                Fact.ActivityStarted(data.componentsList, id = data.id, note = data.note)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.fact.WifiStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.WifiStatusChanged::class.java
            Fact.WifiStatusChanged(data.ooa, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.WifiConnectedTo::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.WifiConnectedTo::class.java
            Fact.WifiConnectedTo(ssidList = data.ssidListList, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.WifiDisconnectedFrom::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.WifiDisconnectedFrom::class.java
            Fact.WifiDisconnectedFrom(ssidList = data.ssidListList, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BTStatusChanged::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.BTStatusChanged::class.java
            Fact.BTStatusChanged(data.ooa, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BTConnectedTo::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.BTConnectedTo::class.java
            Fact.BTConnectedTo(devices = data.deviceList, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom::class.java
            Fact.BTDisconnectedFrom(devices = data.deviceList, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged::class.java
            Fact.HotSpotStatusChanged(data.ooa, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.NFCStatusChanged::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.NFCStatusChanged::class.java
            Fact.NFCStatusChanged(data.ooa, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.LocationStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.LocationStatusChanged::class.java
            Fact.LocationStatusChanged(data.ooa, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged::class.java
            Fact.MobileDataStatusChanged(data.ooa, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.FlashLightStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.FlashLightStatusChanged::class.java
            Fact.FlashLightStatusChanged(data.ooa, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.DNDStatusChanged::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.DNDStatusChanged::class.java
            Fact.DNDStatusChanged(data.ooa, id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.fact.DarkModeStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.DarkModeStatusChanged::class.java
            Fact.DarkModeStatusChanged(data.ooa, id = data.id, note = data.note)
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
                    contentRegex = data.record.contentText,
                    tagRegex = data.record.tag
                ),
                id = data.id, note = data.note
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
                    contentRegex = data.record.contentText,
                    tagRegex = data.record.tag
                ),
                id = data.id, note = data.note
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
                    contentRegex = data.new.contentText,
                    tagRegex = data.new.tag
                ),
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.TaskRemoved::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.TaskRemoved::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()

            if (models.first.isEmpty() && models.second.isEmpty()) {
                Fact.TaskRemovedAny(id = data.id, note = data.note)
            } else {
                Fact.TaskRemoved(
                    apps = models.first,
                    pkgSets = models.second,
                    id = data.id, note = data.note
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.fact.PkgStopRunning::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.PkgStopRunning::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()

            if (models.first.isEmpty() && models.second.isEmpty()) {
                Fact.PkgStopRunningAny(id = data.id, note = data.note)
            } else {
                Fact.PkgStopRunning(
                    apps = models.first,
                    pkgSets = models.second,
                    id = data.id, note = data.note
                )
            }
        }


        this is_ tornaco.apps.shortx.core.proto.fact.AudioFocusGain::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.AudioFocusGain::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()

            Fact.AudioFocusGain(
                apps = models.first,
                pkgSets = models.second,
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AudioFocusLost::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.AudioFocusLost::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()

            Fact.AudioFocusLost(
                apps = models.first,
                pkgSets = models.second,
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AudioFocusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.AudioFocusChanged::class.java
            Fact.AudioFocusChanged(
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BatteryLevelChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.BatteryLevelChanged::class.java
            Fact.BatteryLevelChanged(
                id = data.id, note = data.note,
                level = data.level
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BatteryTemperatureChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.BatteryTemperatureChanged::class.java
            Fact.BatteryTemperatureChanged(
                id = data.id, note = data.note,
                temp = data.temp
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ChargerPlug::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ChargerPlug::class.java
            Fact.ChargerPlug(
                id = data.id, note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ChargerUnplug::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ChargerUnplug::class.java
            Fact.ChargerUnplug(
                id = data.id, note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.Broadcast::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.Broadcast::class.java
            Fact.Broadcast(
                id = data.id, note = data.note, actions = data.actionsList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.VPNConnected::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.VPNConnected::class.java
            Fact.VPNConnected(
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.VPNDisconnected::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.VPNDisconnected::class.java
            Fact.VPNDisconnected(
                id = data.id, note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.MediaStoreInsert::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.MediaStoreInsert::class.java
            Fact.MediaStoreInsert(
                id = data.id, note = data.note,
                filterPathRegex = data.filterPath
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.OnStartOp::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.OnStartOp::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()

            Fact.OnStartOp(
                id = data.id, note = data.note,
                codes = data.opCodesList,
                apps = models.first,
                pkgSets = models.second,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.OnFinishOp::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.OnFinishOp::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()

            Fact.OnFinishOp(
                id = data.id, note = data.note,
                codes = data.opCodesList,
                apps = models.first,
                pkgSets = models.second,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.OnQSTileClick::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.OnQSTileClick::class.java

            Fact.OnQSTileClick(
                id = data.id, note = data.note,
                tile = StatusBarTile.forNumber(data.tileNumber)
            )
        }


        else -> {
            null
        }
    }
}
