package tornaco.apps.shortx.ui.addrule.fact.model

import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.alarm.FixedSetting
import tornaco.apps.shortx.core.alarm.toFixedBy
import tornaco.apps.shortx.core.proto.common.CustomContextMenu
import tornaco.apps.shortx.core.proto.common.DynamicShortcut
import tornaco.apps.shortx.core.proto.common.StatusBarTile
import tornaco.apps.shortx.core.proto.fact.NodeMatcherText
import tornaco.apps.shortx.core.proto.fact.NodeMatcherViewId
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.ui.addrule.fact.model.Fact.MethodHook.Companion.toConstraint
import tornaco.apps.shortx.ui.addrule.protoPkgsAndPkgSetsToModels


fun ProtoAny.toFact(shortXManager: ShortXManager): Fact? {
    return when {
        this is_ tornaco.apps.shortx.core.proto.fact.AnyFact::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AnyFact::class.java
            Fact.AnyFact(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.KeyEvent::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.KeyEvent::class.java
            Fact.KeyEvent(
                keyCode = data.keyCode,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.CombineKeyEvent::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.CombineKeyEvent::class.java
            Fact.CombineKeyEvent(
                keyCode1 = data.keyCode1,
                keyCode2 = data.keyCode2,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AdvancedKeyEvent::class.java
            Fact.AdvancedKeyEvent(
                keyCode = data.keyCode,
                gesture = data.gesture,
                isInterceptMode = data.isInterceptMode,
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.EdgeGesture::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.EdgeGesture::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.EdgeGesture(
                edge = data.edge,
                gesture = data.gesture,
                childSteps = data.childStepsList.map { it.type.toModel() },
                isIntercept = data.isIntercept,
                apps = models.first,
                pkgSets = models.second,
                customRect = if (data.hasCustomRect()) data.customRect else null,
                enableGestureHint = data.hasEnableGestureHint() && data.enableGestureHint,
                enableGestureHaptic = data.hasEnableGestureHaptic() && data.enableGestureHaptic,
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.CornerSwipeToCenter::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.CornerSwipeToCenter::class.java
            Fact.CornerSwipeToCenter(
                corner = data.corner,
                enableGestureHint = data.enableGestureHint,
                enableGestureHaptic = data.enableGestureHaptic,
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ScreenGesture::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.ScreenGesture::class.java
            Fact.ScreenGesture(
                gesture = data.gesture,
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ScreenOff::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.ScreenOff::class.java
            Fact.ScreenOff(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ScreenOn::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.ScreenOn::class.java
            Fact.ScreenOn(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.UserPresent::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.UserPresent::class.java
            Fact.UserPresent(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.UserPresentAtTheFirstTime::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.UserPresentAtTheFirstTime::class.java
            Fact.UserPresentAtTheFirstTime(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.SystemReady::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.SystemReady::class.java
            Fact.SystemReady(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.Alarm::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.Alarm::class.java
            Fact.Alarm(
                timeOfADay = data.triggerAt,
                repeatDays = data.repeat,
                workdayFilter = if (data.hasWorkdayFilter()) data.workdayFilter else tornaco.apps.shortx.core.proto.fact.WorkdayFilter.getDefaultInstance(),
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.RandomInPeriod::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.RandomInPeriod::class.java
            Fact.RandomInPeriod(
                start = data.start,
                end = data.end,
                repeatDays = data.repeat,
                workdayFilter = if (data.hasWorkdayFilter()) data.workdayFilter else tornaco.apps.shortx.core.proto.fact.WorkdayFilter.getDefaultInstance(),
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.FixedInPeriod::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.FixedInPeriod::class.java
            Fact.FixedInPeriod(
                start = data.start,
                end = data.end,
                repeatDays = data.repeat,
                fixedBy = data.fixedBy.toFixedBy() ?: FixedSetting.Times(0),
                workdayFilter = if (data.hasWorkdayFilter()) data.workdayFilter else tornaco.apps.shortx.core.proto.fact.WorkdayFilter.getDefaultInstance(),
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppBecomeFg::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppBecomeFg::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppBecomeForeground(
                apps = models.first,
                pkgSets = models.second,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppBecomeBg::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppBecomeBg::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppBecomeBackground(
                apps = models.first,
                pkgSets = models.second,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppGainWindowFocus::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.AppGainWindowFocus::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppGainWindowFocus(
                apps = models.first,
                pkgSets = models.second,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppLostWindowFocus::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.AppLostWindowFocus::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppLostWindowFocus(
                apps = models.first,
                pkgSets = models.second,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppAdded::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppAdded::class.java
            Fact.AppAdded(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppRemoved::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppRemoved::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppRemoved(
                apps = models.first,
                pkgSets = models.second,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppUpdated::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.AppUpdated::class.java
            val models = (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.AppUpdated(
                apps = models.first,
                pkgSets = models.second,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }


        this is_ tornaco.apps.shortx.core.proto.fact.ActivityStarted::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.ActivityStarted::class.java
            if (data.componentsList.isEmpty()) {
                Fact.AnyActivityStarted(
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
                )
            } else {
                Fact.ActivityStarted(
                    data.componentsList,
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ActivityStopped::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.ActivityStopped::class.java

            if (data.componentsList.isEmpty()) {
                Fact.AnyActivityStopped(
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
                )
            } else {
                Fact.ActivityStopped(
                    data.componentsList,
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ActivityDestroyed::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ActivityDestroyed::class.java
            if (data.componentsList.isEmpty()) {
                Fact.AnyActivityDestroyed(
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
                )
            } else {
                Fact.ActivityDestroyed(
                    data.componentsList,
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.fact.WifiStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.WifiStatusChanged::class.java
            Fact.WifiStatusChanged(
                data.ooa,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.WifiConnectedTo::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.WifiConnectedTo::class.java
            Fact.WifiConnectedTo(
                ssidList = data.ssidListList,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.WifiDisconnectedFrom::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.WifiDisconnectedFrom::class.java
            Fact.WifiDisconnectedFrom(
                ssidList = data.ssidListList,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BTStatusChanged::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.BTStatusChanged::class.java
            Fact.BTStatusChanged(
                data.ooa,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BTConnectedTo::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.BTConnectedTo::class.java
            Fact.BTConnectedTo(
                devices = data.deviceList,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom::class.java
            Fact.BTDisconnectedFrom(
                devices = data.deviceList,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.HotSpotStatusChanged::class.java
            Fact.HotSpotStatusChanged(
                data.ooa,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.NFCStatusChanged::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.NFCStatusChanged::class.java
            Fact.NFCStatusChanged(
                data.ooa,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.APMStatusChanged::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.APMStatusChanged::class.java
            Fact.APMStatusChanged(
                data.ooa,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.LocationStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.LocationStatusChanged::class.java
            Fact.LocationStatusChanged(
                data.ooa,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.MobileDataStatusChanged::class.java
            Fact.MobileDataStatusChanged(
                data.ooa,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.FlashLightStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.FlashLightStatusChanged::class.java
            Fact.FlashLightStatusChanged(
                data.ooa,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.DNDStatusChanged::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.DNDStatusChanged::class.java
            Fact.DNDStatusChanged(
                data.ooa,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.DarkModeStatusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.DarkModeStatusChanged::class.java
            Fact.DarkModeStatusChanged(
                data.ooa,
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
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
                    titleMatchOptions = data.record.titleRegexOptions,
                    contentMatchOptions = data.record.contentRegexOptions,
                    tag = data.record.tag
                ),
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
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
                    titleMatchOptions = data.record.titleRegexOptions,
                    contentMatchOptions = data.record.contentRegexOptions,
                    tag = data.record.tag
                ),
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
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
                    titleMatchOptions = data.new.titleRegexOptions,
                    contentMatchOptions = data.new.contentRegexOptions,
                    tag = data.new.tag
                ),
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.TaskRemoved::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.TaskRemoved::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()

            if (models.first.isEmpty() && models.second.isEmpty()) {
                Fact.TaskRemovedAny(
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
                )
            } else {
                Fact.TaskRemoved(
                    apps = models.first,
                    pkgSets = models.second,
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.fact.PkgStopRunning::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.PkgStopRunning::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()

            if (models.first.isEmpty() && models.second.isEmpty()) {
                Fact.PkgStopRunningAny(
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
                )
            } else {
                Fact.PkgStopRunning(
                    apps = models.first,
                    pkgSets = models.second,
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.fact.PkgStartRunning::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.PkgStartRunning::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()

            if (models.first.isEmpty() && models.second.isEmpty()) {
                Fact.PkgStartRunningAny(
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
                )
            } else {
                Fact.PkgStartRunning(
                    apps = models.first,
                    pkgSets = models.second,
                    id = data.id,
                    customContextDataKey = data.customContextDataKey,
                    note = data.note,
                    isDisabled = data.isDisabled,
                    tag = data.tag
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
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
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
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AudioFocusChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.AudioFocusChanged::class.java
            Fact.AudioFocusChanged(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BatteryLevelChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.BatteryLevelChanged::class.java
            Fact.BatteryLevelChanged(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                level = data.level
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BatteryTemperatureChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.BatteryTemperatureChanged::class.java
            Fact.BatteryTemperatureChanged(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                temp = data.temp
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ChargerPlug::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ChargerPlug::class.java
            Fact.ChargerPlug(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ChargerUnplug::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ChargerUnplug::class.java
            Fact.ChargerUnplug(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.Broadcast::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.Broadcast::class.java
            Fact.Broadcast(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                actions = data.actionsList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.VPNConnected::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.VPNConnected::class.java
            Fact.VPNConnected(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.VPNDisconnected::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.VPNDisconnected::class.java
            Fact.VPNDisconnected(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BackNavDone::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.BackNavDone::class.java
            Fact.BackNavDone(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ScreenOnTime::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ScreenOnTime::class.java
            Fact.ScreenOnTime(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                timeInSeconds = data.timeInSeconds,
                from = data.from
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.SunRiseSet::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.SunRiseSet::class.java
            Fact.SunRiseSet(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                rise = data.rise
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.CpuAvgAvailability::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.CpuAvgAvailability::class.java
            Fact.CpuAvgAvailability(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.BackNavStart::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.BackNavStart::class.java
            Fact.BackNavStart(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.MediaStoreInsert::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.MediaStoreInsert::class.java
            Fact.MediaStoreInsert(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                filterPathRegex = data.filterPath,
                matchOptions = data.options
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.MediaStoreDelete::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.fact.MediaStoreDelete::class.java
            Fact.MediaStoreDelete(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                filterPathRegex = data.filterPath,
                matchOptions = data.options
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.OnStartOp::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.OnStartOp::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()

            Fact.OnStartOp(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
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
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                codes = data.opCodesList,
                apps = models.first,
                pkgSets = models.second,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.OnQSTileClick::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.OnQSTileClick::class.java

            Fact.OnQSTileClick(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                tile = StatusBarTile.forNumber(data.tileNumber)
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.OnStartDynamicShortcut::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.OnStartDynamicShortcut::class.java
            val models =
                (data.appsList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Fact.OnStartDynamicShortcut(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                shortcut = DynamicShortcut.forNumber(data.shortcutNumber),
                apps = models.first,
                pkgSets = models.second,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.OnMenuActionTrigger::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.OnMenuActionTrigger::class.java

            Fact.OnMenuActionTrigger(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                menu = CustomContextMenu.forNumber(data.menuNumber),
                text = data.text
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.SystemSettingsChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.SystemSettingsChanged::class.java

            Fact.SystemSettingsChanged(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                urlAndValueRegex = data.urlAndExpectedValueRegex,
                options = data.matchOptions
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.CallStateChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.CallStateChanged::class.java

            Fact.CallStateChanged(
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                callState = data.callState
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ClipboardContentChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ClipboardContentChanged::class.java

            Fact.ClipboardContentChanged(
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                content = data.content,
                options = data.matchOptions
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.GlobalVarChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.GlobalVarChanged::class.java

            Fact.GlobalVarChanged(
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                gvId = data.gvId
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.HeadsetPlug::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.HeadsetPlug::class.java

            Fact.HeadsetPlug(
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                isPlug = data.isPlug
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ScreenRotate::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ScreenRotate::class.java

            Fact.ScreenRotate(
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                degree = data.degree
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.WindowRotationChange::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.WindowRotationChange::class.java

            Fact.WindowRotationChange(
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                degree = data.degree
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppProcessStarted::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.AppProcessStarted::class.java

            Fact.AppProcessStarted(
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                processNames = data.processNameList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AppProcessRemoved::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.AppProcessRemoved::class.java

            Fact.AppProcessRemoved(
                id = data.id, customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                processNames = data.processNameList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.IMEVisibilityChange::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.IMEVisibilityChange::class.java

            Fact.IMEVisibilityChange(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                isShown = data.isShown
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ConnectedWifiSignalLevelChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ConnectedWifiSignalLevelChanged::class.java

            Fact.ConnectedWifiSignalLevelChanged(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                level = data.level,
                rssi = data.rssi
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.NFCTagDiscover::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.NFCTagDiscover::class.java

            Fact.NFCTagDiscover(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                uid = data.uid
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.DeepLinkCall::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.DeepLinkCall::class.java

            Fact.DeepLinkCall(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                deepLinkTag = data.deepLinkTag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.Logcat::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.Logcat::class.java

            Fact.Logcat(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                regex = data.regex,
                regexMatchOptions = data.regexMatchOptions,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.UsbDeviceAttached::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.UsbDeviceAttached::class.java

            Fact.UsbDeviceAttached(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                deviceName = data.deviceName,
                deviceNameRegexMatchOptions = data.deviceNameRegexMatchOptions,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.UsbDeviceDetached::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.UsbDeviceDetached::class.java

            Fact.UsbDeviceDetached(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                deviceName = data.deviceName,
                deviceNameRegexMatchOptions = data.deviceNameRegexMatchOptions,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.LightSensor::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.LightSensor::class.java

            Fact.LightSensor(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                constraints = data.constraintsList,
                trend = data.trend
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.AccelerometerSensor::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.AccelerometerSensor::class.java

            Fact.AccelerometerSensor(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                constraintsX = data.constraintsXList,
                constraintsY = data.constraintsYList,
                constraintsZ = data.constraintsZList,
                trendX = data.trendX,
                trendY = data.trendY,
                trendZ = data.trendZ,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ProximitySensor::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ProximitySensor::class.java

            Fact.ProximitySensor(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                constraints = data.constraintsList,
                trend = data.trend
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.Sound::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.Sound::class.java

            Fact.Sound(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                constraints = data.constraintsList,
                trend = data.trend
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ProximitySensorStateChanged::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ProximitySensorStateChanged::class.java

            Fact.ProximitySensorStateChanged(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                state = data.state
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.ShakeDevice::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.ShakeDevice::class.java

            Fact.ShakeDevice(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.MethodHook::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.MethodHook::class.java

            Fact.MethodHook(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                packageName = data.packageName,
                className = data.className,
                methodName = data.methodName,
                beforeMethod = data.beforeMethod,
                expressions = data.expressionsList,
                lifecycle = data.lifecycle,
                constraints = data.constraintList.mapNotNull {
                    it.toConstraint()
                }
            )
        }

        this is_ tornaco.apps.shortx.core.proto.fact.HasFoundNodeOnScreen::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.fact.HasFoundNodeOnScreen::class.java

            Fact.HasFoundNodeOnScreen(
                id = data.id,
                customContextDataKey = data.customContextDataKey,
                note = data.note,
                isDisabled = data.isDisabled,
                tag = data.tag,
                packageName = data.packageName,
                componentName = data.componentName,
                detectTimeout = data.detectTimeout,
                matchers = data.matchersList.mapNotNull {
                    if (it is_ NodeMatcherText::class.java) {
                        val m = it unpack_ NodeMatcherText::class.java
                        NodeMatcher.Text(m.text, m.isRegex, m.regexOptions)
                    } else if (it is_ NodeMatcherViewId::class.java) {
                        val m = it unpack_ NodeMatcherViewId::class.java
                        NodeMatcher.ViewId(m.id)
                    } else null
                }
            )
        }

        else -> {
            null
        }
    }
}
