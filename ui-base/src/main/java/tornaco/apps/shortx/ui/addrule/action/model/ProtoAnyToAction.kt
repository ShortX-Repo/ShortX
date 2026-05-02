package tornaco.apps.shortx.ui.addrule.action.model

import android.content.Intent
import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.action.HttpRequestHeaderBodyJsonMapAdapter
import tornaco.apps.shortx.core.proto.action.HttpRequestJsonMapAdapter
import tornaco.apps.shortx.core.proto.action.IfThenElse
import tornaco.apps.shortx.core.proto.action.SwitchCase
import tornaco.apps.shortx.core.proto.action.TextProcessingToPinyin
import tornaco.apps.shortx.core.proto.action.TextProcessingTrimLength
import tornaco.apps.shortx.core.proto.action.TextProcessingTrimSpace
import tornaco.apps.shortx.core.proto.action.WaitUtilConditionMatch
import tornaco.apps.shortx.core.proto.common.GestureRecord
import tornaco.apps.shortx.core.proto.common.RectSourceFullScreen
import tornaco.apps.shortx.core.proto.common.RectSourcePath
import tornaco.apps.shortx.core.proto.common.RectSourceRect
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.IntentConverter
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.condition.op.toOp
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.toFact
import tornaco.apps.shortx.ui.addrule.protoPkgsAndPkgSetsToModels
import tornaco.apps.shortx.ui.remote.toModel


fun ProtoAny.toAction(shortXManager: ShortXManager): Action? {
    return when {
        this is_ tornaco.apps.shortx.core.proto.action.NoAction::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.NoAction::class.java
            Action.NoAction(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                icon = data.icon
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.GetTextFromScreenNode::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.GetTextFromScreenNode::class.java
            Action.GetTextFromScreenNode(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                nodeId = data.nodeId
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.GetScreenOnTime::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.GetScreenOnTime::class.java
            Action.GetScreenOnTime(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                from = data.from
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.GetCurrentLocationInfo::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.GetCurrentLocationInfo::class.java
            Action.GetCurrentLocationInfo(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                providerPreference = data.providerPreference,
                timeoutMillis = data.timeoutMillis
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.GetCurrentLocationAddress::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.GetCurrentLocationAddress::class.java
            Action.GetCurrentLocationAddress(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                providerPreference = data.providerPreference,
                timeoutMillis = data.timeoutMillis
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ReadClipboard::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ReadClipboard::class.java
            Action.ReadClipboard(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WriteClipboard::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.WriteClipboard::class.java
            Action.WriteClipboard(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                text = data.text
            )
        }


        this is_ tornaco.apps.shortx.core.proto.action.TakeScreenshot::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.TakeScreenshot::class.java
            Action.TakeScreenshot(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShellCommand::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ShellCommand::class.java
            Action.ShellCommand(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                command = data.command,
                singleShot = data.singleShot
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.InputText::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.InputText::class.java
            Action.InputText(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note, text = data.text
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.PerformContextMenuAction::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.PerformContextMenuAction::class.java
            Action.PerformContextMenuAction(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                action = data.action
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowToast::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ShowToast::class.java
            Action.ShowToast(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note, message = data.message
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowDanmu::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ShowDanmu::class.java
            Action.ShowDanmu(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note, text = data.text, icon = data.icon
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowStatusBarChip::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowStatusBarChip::class.java
            Action.ShowStatusBarChip(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                text = data.text,
                icon = data.icon,
                clickActions = data.clickActionList.mapNotNull { it.toAction(shortXManager) },
                longClickActions = data.longClickActionList.mapNotNull { it.toAction(shortXManager) }
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.HideStatusBarClip::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.HideStatusBarClip::class.java
            Action.HideStatusBarClip(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetStatusBarIcon::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetStatusBarIcon::class.java
            Action.SetStatusBarIcon(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                slot = data.slot,
                icon = data.icon
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.RemoveStatusBarIcon::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.RemoveStatusBarIcon::class.java
            Action.RemoveStatusBarIcon(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                slot = data.slot
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.Delay::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.Delay::class.java
            Action.Delay(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                time = data.timeString.fallbackOnEmpty(data.time.toString()),
                useAlarm = data.useAlarm,
                showCD = data.showCD,
                timeUnit = data.timeUnit
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ExecuteJS::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ExecuteJS::class.java
            Action.ExecuteJS(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                expression = data.expression,
                context = data.context
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ExecuteMVEL::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ExecuteMVEL::class.java
            Action.MVEL(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                expression = data.expression,
                context = data.context
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.RemoteExecuteMVEL::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.RemoteExecuteMVEL::class.java
            Action.RemoteMVEL(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note, action = data.toModel()
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowOverlayButton::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowOverlayButton::class.java
            Action.ShowOverlay(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                autoCollapse = data.autoCollapse,
                settings = data.buttonSettingsList,
                tag = data.tag,
                maxWidth = data.maxWidthInDp,
                maxHeight = data.maxHeightInDp,
                buttonMinWidth = data.buttonMinWidth,
                backgroundAlpha = data.backgroundAlpha,
                backgroundColor = data.backgroundColor,
                enableGlobalDrag = data.enableGlobalDrag,
                overlayPaddingH = data.overlayPaddingH,
                overlayPaddingV = data.overlayPaddingV,
                disableAutoEdge = data.disableAutoEdge,
                closeOnTouchOutside = data.closeOnTouchOutside,
                closeOnAction = data.closeOnAction,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.HideOverlayButton::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.HideOverlayButton::class.java
            if (data.overlayTagsList.isEmpty()) {
                Action.HideAllOverlay(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            } else {
                Action.HideOverlay(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id,
                    note = data.note,
                    tags = data.overlayTagsList
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowFloatWindow::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowFloatWindow::class.java
            Action.ShowFloatWindow(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                settings = data.buttonSettingsList,
                tag = data.tag,
                panelRadiusScale = data.panelRadiusScale,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.HideFloatWindow::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.HideFloatWindow::class.java
            Action.HideFloatWindow(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                tags = data.overlayTagsList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowSideBar::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowSideBar::class.java
            Action.ShowSideBar(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                settings = data.buttonSettingsList,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.HideSideBar::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.HideSideBar::class.java
            Action.HideSideBar(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                tags = data.overlayTagsList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.LaunchApp::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.LaunchApp::class.java
            val pkg = Pkg(data.appPkg.pkgName, userId = data.appPkg.userId)
            Action.LaunchApp(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                app = App(
                    pkg = pkg,
                    label = shortXManager.getAppLabel(pkg) ?: "Unknown",
                    tornaco.apps.shortx.core.proto.common.AppCategory.User
                )
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.LaunchAppByPkg::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.LaunchAppByPkg::class.java
            Action.LaunchAppByPkg(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                stringPairs = data.pkgAndUsersList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetWifiEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetWifiEnabled::class.java
            if (data.enable) {
                Action.EnableWifi(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            } else {
                Action.DisableWifi(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetSensorsOffEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetSensorsOffEnabled::class.java
            if (data.enable) {
                Action.EnableSensorsOff(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            } else {
                Action.DisableSensorsOff(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetBTEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetBTEnabled::class.java
            if (data.enable) {
                Action.EnableBT(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            } else {
                Action.DisableBT(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetNFCEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetNFCEnabled::class.java
            if (data.enable) {
                Action.EnableNFC(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            } else {
                Action.DisableNFC(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetDNDEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetDNDEnabled::class.java
            if (data.enable) {
                Action.EnableDND(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            } else {
                Action.DisableDND(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetLocationEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetLocationEnabled::class.java
            if (data.enable) {
                Action.EnableLocation(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            } else {
                Action.DisableLocation(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetDataEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetDataEnabled::class.java
            if (data.enable) {
                Action.EnableData(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            } else {
                Action.DisableData(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetDarkModeEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetDarkModeEnabled::class.java
            if (data.enable) {
                Action.EnableDarkMode(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            } else {
                Action.DisableDarkMode(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetFlashLightEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetFlashLightEnabled::class.java
            if (data.enable) {
                Action.EnableFlashLight(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            } else {
                Action.DisableFlashLight(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetHotSpotEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetHotSpotEnabled::class.java
            if (data.enable) {
                Action.EnableHotSpot(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            } else {
                Action.DisableHotSpot(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.StopApp::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.StopApp::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.StopApp(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StopAppByPkg::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.StopAppByPkg::class.java
            Action.StopAppByPkg(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                stringPairs = data.pkgAndUsersList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartAppProcess::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StartAppProcess::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.StartAppProcess(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartAppProcessByPkg::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StartAppProcessByPkg::class.java
            Action.StartAppProcessByPkg(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                stringPairs = data.pkgAndUsersList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAppInactive::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetAppInactive::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.SetAppInactive(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAppInactiveByPkg::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetAppInactiveByPkg::class.java
            Action.SetAppInactiveByPkg(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                stringPairs = data.pkgAndUsersList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAppEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetAppEnabled::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            data.enable.select(
                Action.EnableApp(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id,
                    note = data.note,
                    apps = models.first,
                    pkgSets = models.second
                ),
                Action.DisableApp(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id,
                    note = data.note,
                    apps = models.first,
                    pkgSets = models.second
                )
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAppEnabledByPkg::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetAppEnabledByPkg::class.java
            data.enable.select(
                Action.EnableAppByPkg(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id,
                    note = data.note,
                    stringPairs = data.pkgAndUsersList
                ),
                Action.DisableAppByPkg(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id,
                    note = data.note,
                    stringPairs = data.pkgAndUsersList
                )
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAppSuspend::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetAppSuspend::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            data.suspend.select(
                Action.SuspendApp(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id,
                    note = data.note,
                    apps = models.first,
                    pkgSets = models.second
                ),
                Action.UnSuspendApp(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id,
                    note = data.note,
                    apps = models.first,
                    pkgSets = models.second
                )
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAppSuspendByPkg::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetAppSuspendByPkg::class.java
            data.suspend.select(
                Action.SuspendAppByPkg(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id,
                    note = data.note,
                    stringPairs = data.pkgAndUsersList
                ),
                Action.UnSuspendAppByPkg(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id,
                    note = data.note,
                    stringPairs = data.pkgAndUsersList
                )
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.BreakActionExecute::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.BreakActionExecute::class.java
            Action.Brk(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                scope = data.scope
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowAlertDialog::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowAlertDialog::class.java
            Action.ShowAlertDialog(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                title = data.title,
                message = data.message,
                cancelable = data.cancelable,
                positiveText = data.positive,
                negativeText = data.negative,
                neutralText = data.neutral,
                positiveActions = data.onPositiveList.mapNotNull { it.toAction(shortXManager) },
                negativeActions = data.onNegativeList.mapNotNull { it.toAction(shortXManager) },
                neutralActions = data.onNeutralList.mapNotNull { it.toAction(shortXManager) },
                style = data.style
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowTextFieldDialog::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowTextFieldDialog::class.java
            Action.ShowTextFieldDialog(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                title = data.title,
                message = data.message,
                cancelable = data.cancelable,
                textFields = data.textFieldsList,
                style = data.style
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowChoiceDialog::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowChoiceDialog::class.java
            Action.ShowChoiceDialog(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                title = data.title,
                message = data.message,
                cancelable = data.cancelable,
                choices = data.choicesList,
                isMultipleChoice = data.isMultipleChoice,
                style = data.style
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowListDialog::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowListDialog::class.java
            Action.ShowListDialog(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                title = data.title,
                message = data.message,
                cancelable = data.cancelable,
                data = data.data,
                isMultipleChoice = data.isMultipleChoice,
                delimiters = data.delimiterList,
                needConfirmAction = data.needConfirmAction,
                dataType = data.dataType,
                style = data.style
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowMenuDialog::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowMenuDialog::class.java
            Action.ShowMenuDialog(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                title = data.title,
                message = data.message,
                cancelable = data.cancelable,
                items = data.itemsList,
                style = data.style
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartActivity::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.StartActivity::class.java
            Action.StartActivity(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                componentNameAsString = data.componentNameAsString
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartActivityIntent::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StartActivityIntent::class.java
            Action.StartActivityIntent(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                userId = data.userId,
                action = data.intent.action,
                pkgName = data.intent.pkgName,
                className = data.intent.className,
                data = data.intent.data,
                flags = data.intent.flags,
                extras = data.intent.extrasList,
                componentType = data.componentType
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartActivityUrlSchema::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StartActivityUrlSchema::class.java
            Action.StartActivityUrlSchema(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                userId = data.userId,
                urlSchema = data.urlSchema
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartActivityIntentUri::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StartActivityIntentUri::class.java
            Action.StartActivityIntentUri(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                intentUri = data.intentUri
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ExpandNotification::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ExpandNotification::class.java
            Action.ExpandNotification(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WhileLoop::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.WhileLoop::class.java
            Action.WhileLoop(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                delay = data.delay.toLong(),
                repeat = data.repeatTimes,
                conditions = data.conditionsList.mapNotNull { it.toCondition(shortXManager) },
                actions = data.actionsList.mapNotNull { it.toAction(shortXManager) },
                op = data.condOp.toOp(data.condOpPayload),
                actionAsyncMode = data.actionAsyncMode
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ForEach::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ForEach::class.java
            Action.ForEach(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                actions = data.actionsList.mapNotNull { it.toAction(shortXManager) },
                data = data.data,
                delimiters = data.delimiterList,
                actionAsyncMode = data.actionAsyncMode
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ForEachPkgSet::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ForEachPkgSet::class.java
            Action.ForEachPkgSet(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                actions = data.actionList.mapNotNull { it.toAction(shortXManager) },
                pkgSet = shortXManager.getPkgSetByLabel(data.pkgSet) ?: PkgSet.getDefaultInstance()
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WriteGlobalVar::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.WriteGlobalVar::class.java
            Action.WriteGV(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                varName = data.varName,
                valueAsString = data.valueAsString,
                op = data.op,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.CreateGlobalVar::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.CreateGlobalVar::class.java
            Action.CreateGV(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                globalVar = data.globalVar
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.DeleteGlobalVar::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.DeleteGlobalVar::class.java
            Action.DeleteGV(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                varName = data.varName,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.MapNav::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.MapNav::class.java
            Action.MapNav(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                longitude = data.loc.longitude,
                latitude = data.loc.latitude,
                poi = data.poi,
                mapApp = data.app,
                navType = data.navType
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.MapQueryBus::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.MapQueryBus::class.java
            Action.MapQueryBus(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                mapApp = data.app,
                bus = data.bus,
                city = data.city
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.PostNotification::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.PostNotification::class.java
            Action.PostNotification(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                tag = data.tag,
                message = data.message,
                title = data.title,
                overrideAppName = data.overrideAppName,
                largeIcon = data.largeIcon,
                smallIcon = data.smallIcon,
                vibrate = data.vibrate,
                sound = data.sound,
                ongoing = data.onGoing,
                isImportant = data.isImportant,
                buttons = data.buttonList,
                onClickActions = data.clickActionList.mapNotNull { it.toAction(shortXManager) }
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.RemoveNotification::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.RemoveNotification::class.java
            val models =
                (data.notification.appsList to data.notification.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.RemoveNotification(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                n = Fact.Notification(
                    apps = models.first,
                    pkgSets = models.second,
                    titleRegex = data.notification.title,
                    contentRegex = data.notification.contentText,
                    titleMatchOptions = data.notification.titleRegexOptions,
                    contentMatchOptions = data.notification.contentRegexOptions,
                    tag = data.notification.tag
                ),
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ClickNotification::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ClickNotification::class.java
            val models =
                (data.notification.appsList to data.notification.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.ClickNotification(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                n = Fact.Notification(
                    apps = models.first,
                    pkgSets = models.second,
                    titleRegex = data.notification.title,
                    contentRegex = data.notification.contentText,
                    titleMatchOptions = data.notification.titleRegexOptions,
                    contentMatchOptions = data.notification.contentRegexOptions,
                    tag = data.notification.tag
                ),
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ClickNotificationActionButton::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ClickNotificationActionButton::class.java
            val models =
                (data.notification.appsList to data.notification.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.ClickNotificationActionButton(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                n = Fact.Notification(
                    apps = models.first,
                    pkgSets = models.second,
                    titleRegex = data.notification.title,
                    contentRegex = data.notification.contentText,
                    titleMatchOptions = data.notification.titleRegexOptions,
                    contentMatchOptions = data.notification.contentRegexOptions,
                    tag = data.notification.tag
                ),
                button = data.button
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowPPN::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowPPN::class.java
            Action.ShowPPN(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                tag = data.tag,
                message = data.message,
                title = data.title,
                icon = data.icon,
                vibrate = data.vibrate,
                sound = data.sound,
                displayTimeSeconds = data.displayTimeSeconds,
                buttons = data.buttonList,
                onClickActions = data.clickActionList.mapNotNull { it.toAction(shortXManager) }
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.RemovePPN::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.RemovePPN::class.java
            Action.RemovePPN(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                tag = data.tag,
            )
        }


        this is_ tornaco.apps.shortx.core.proto.action.FromDA::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.FromDA::class.java
            val da = shortXManager.getDirectActionById(data.daId)
            Action.FromDA(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note, daId = data.daId,
                title = da?.title,
                description = da?.description,
                parameterInputs = data.funcParameterInputsList ?: emptyList(),
                parameters = da?.parametersList ?: emptyList()
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ExecuteFunction::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ExecuteFunction::class.java
            val function = shortXManager.getFunctionById(data.functionId)
            Action.ExecuteFunction(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                functionId = data.functionId,
                name = function?.name,
                parameterInputs = data.funcParameterInputsList ?: emptyList(),
                parameters = function?.parametersList ?: emptyList()
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetFunctionReturnValue::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetFunctionReturnValue::class.java
            Action.SetFunctionReturnValue(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                value = data.value,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.FindAndClickViewByText::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.FindAndClickViewByText::class.java
            Action.FindAndClickViewByText(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                text = data.text,
                isRegex = data.isRegex,
                timeout = data.timeout,
                rect = data.rect,
                method = data.method
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.FindAndClickViewById::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.FindAndClickViewById::class.java
            Action.FindAndClickViewById(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                viewId = data.viewId,
                isRegex = data.isRegex,
                timeout = data.timeout
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.FindAndClickMatchedView::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.FindAndClickMatchedView::class.java
            Action.FindAndClickMatchedView(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.InputTap::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.InputTap::class.java
            Action.InputTap(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                x = data.xs.ifBlank { data.x.toString() },
                y = data.ys.ifBlank { data.y.toString() }
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.InputSwipe::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.InputSwipe::class.java
            Action.InputSwipe(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                startX = data.startXS.ifBlank { data.startX.toString() },
                startY = data.startYS.ifBlank { data.startY.toString() },
                endX = data.endXS.ifBlank { data.endX.toString() },
                endY = data.endYS.ifBlank { data.endY.toString() },
                swipeTime = data.swipeTimeS.ifBlank { data.swipeTime.toString() }
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WaitForIdle::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.WaitForIdle::class.java
            Action.WaitForIdle(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ IfThenElse::class.java -> {
            val data = this unpack_ IfThenElse::class.java
            Action.IfThenElse(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                ifConditions = data.ifList.mapNotNull {
                    it.toCondition(
                        shortXManager
                    )
                },
                ifConditionOp = data.ifCondOp.toOp(data.ifCondOpPayload),
                ifActions = data.ifActionsList.mapNotNull { it.toAction(shortXManager) },
                elseActions = data.elseActionsList.mapNotNull { it.toAction(shortXManager) },
                ifActionAsyncMode = data.ifActionAsyncMode,
                elseActionAsyncMode = data.elseActionAsyncMode
            )
        }

        this is_ SwitchCase::class.java -> {
            val data = this unpack_ SwitchCase::class.java
            Action.SwitchCase(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                cases = data.caseList.map { case ->
                    Action.SwitchCase.Case(
                        conditions = case.caseList.mapNotNull {
                            it.toCondition(
                                shortXManager
                            )
                        },
                        conditionOp = case.caseCondOp.toOp(case.caseCondOpPayload),
                        actions = case.actionList.mapNotNull { it.toAction(shortXManager) },
                        description = case.description.orEmpty(),
                        isBreak = case.isBreak,
                        isDisabled = case.isDisabled,
                        actionAsyncMode = case.actionAsyncMode,
                        id = case.id.fallbackOnEmpty(Action.SwitchCase.defaultNewCaseId()),
                    )
                },
                deft = Action.SwitchCase.Default(data.deft.defaultActionList.mapNotNull {
                    it.toAction(
                        shortXManager
                    )
                })
            )
        }

        this is_ WaitUtilConditionMatch::class.java -> {
            val data = this unpack_ WaitUtilConditionMatch::class.java
            Action.WaitUtilConditionMatch(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                conditions = data.conditionList.mapNotNull {
                    it.toCondition(
                        shortXManager
                    )
                },
                op = data.conditionOp.toOp(data.condOpPayload),
                quitFacts = data.quitFactsList.mapNotNull {
                    it.toFact(shortXManager)
                },
                quitConditions = data.quitConditionList.mapNotNull {
                    it.toCondition(
                        shortXManager
                    )
                },
                quitOp = data.quitConditionOp.toOp(data.quitCondOpPayload),
                isQuitEnabled = data.quitEnabled,
                timeout = data.timeout,
                timeoutStr = data.timeoutStr,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.CreatePkgSet::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.CreatePkgSet::class.java
            Action.CreatePkgSet(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                label = data.label,
                description = data.description
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetRingerMode::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetRingerMode::class.java
            Action.SetRingerMode(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                mode = data.mode
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetBrightness::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetBrightness::class.java
            Action.SetBrightness(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                value = data.value
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ContinuousAdjustBrightness::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ContinuousAdjustBrightness::class.java
            Action.ContinuousAdjustBrightness(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                sensitivityScale = data.sensitivityScale,
                minValue = data.minValue,
                maxValue = data.maxValue
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAutoBrightness::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetAutoBrightness::class.java
            data.enable.select(
                Action.EnableAutoBrightness(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id,
                    note = data.note,
                ),
                Action.DisableAutoBrightness(
                    isEnabled = !data.isDisabled,
                    actionOnError = data.actionOnError,
                    customContextDataKey = data.customContextDataKey,
                    id = data.id,
                    note = data.note,
                )
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StopService::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StopService::class.java
            Action.StopService(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                services = data.servicesList.distinct()
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartService::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StartService::class.java
            Action.StartService(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                userId = data.userId,
                action = data.intent.action,
                pkgName = data.intent.pkgName,
                className = data.intent.className,
                data = data.intent.data,
                flags = data.intent.flags,
                extras = data.intent.extrasList,
                isFG = data.isForegroundService
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.RemoveTasks::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.RemoveTasks::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.RemoveTasks(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.RemoveTasksByPkg::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.RemoveTasksByPkg::class.java
            Action.RemoveTasksByPkg(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                stringPairs = data.pkgAndUsersList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.RemoveNotificationForPackage::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.RemoveNotificationForPackage::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.RemoveNotificationForPackage(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.RemoveNotificationForPackageByPkg::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.RemoveNotificationForPackageByPkg::class.java
            Action.RemoveNotificationForPackageByPkg(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                stringPairs = data.pkgAndUsersList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.KillProcessByName::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.KillProcessByName::class.java
            Action.KillProcessByName(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                processList = data.processesList.distinct()
            )
        }


        this is_ tornaco.apps.shortx.core.proto.action.HttpRequest::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.HttpRequest::class.java
            Action.HttpRequest(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                url = data.url,
                method = data.method,
                headers = data.headersList,
                requestBody = data.requestBody,
                adapter = data.adapter.let {
                    if (it is_ HttpRequestJsonMapAdapter::class.java) {
                        val ja = it unpack_ HttpRequestJsonMapAdapter::class.java
                        HttpRequestAdapter.BodyJsonMap(ja.expressionsList)
                    } else if (it is_ HttpRequestHeaderBodyJsonMapAdapter::class.java) {
                        val ja = it unpack_ HttpRequestHeaderBodyJsonMapAdapter::class.java
                        HttpRequestAdapter.HeaderBodyJsonMap(ja.expressionsList)
                    } else {
                        HttpRequestAdapter.Raw
                    }
                },
                withCookieJar = data.withCookieJar,
                trustAllCerts = data.trustAllCerts,
                executeInAppProcess = data.executeInAppProcess
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.TextProcessing::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.TextProcessing::class.java
            Action.TextProcessing(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                text = data.text,
                processors = data.processorsList.mapNotNull {
                    if (it is_ TextProcessingToPinyin::class.java) {
                        TextProcessor.ToPinyin
                    } else if (it is_ TextProcessingTrimSpace::class.java) {
                        TextProcessor.TrimSpace
                    } else if (it is_ TextProcessingTrimLength::class.java) {
                        val ttl = it unpack_ TextProcessingTrimLength::class.java
                        TextProcessor.TrimLength(ttl.length)
                    } else {
                        null
                    }
                },
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.InjectKeyCode::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.InjectKeyCode::class.java
            Action.InjectKeyCode(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                code = data.keyCode,
                longpress = data.longpress,
                doublepress = data.doublepress,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.InjectCombineKeyCode::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.InjectCombineKeyCode::class.java
            Action.InjectCombineKeyCode(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                codes = data.keyCodeList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WakeupScreen::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.WakeupScreen::class.java
            Action.WakeupScreen(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SleepScreen::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SleepScreen::class.java
            Action.SleepScreen(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartLastApp::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StartLastApp::class.java
            Action.StartLastApp(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StopCurrentApp::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StopCurrentApp::class.java
            Action.StopCurrentApp(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.AdjustVolume::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.AdjustVolume::class.java
            Action.AdjustVolume(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                direction = data.direction
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetVolume::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetVolume::class.java
            Action.SetVolume(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                type = data.type,
                index = data.index
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ContinuousAdjustVolume::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ContinuousAdjustVolume::class.java
            Action.ContinuousAdjustVolume(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                type = data.streamType,
                sensitivityScale = data.sensitivityScale
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.Vibrate::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.Vibrate::class.java
            Action.Vibrate(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                vib1 = data.vib1,
                vib2 = data.vib2,
                vib3 = data.vib3,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.AudioRecording::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.AudioRecording::class.java
            Action.AudioRecording(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                src = data.src,
                fileNamePrefix = data.fileNamePrefix
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StopAudioRecording::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StopAudioRecording::class.java
            Action.StopAudioRecording(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SwitchMobileDataSlot::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SwitchMobileDataSlot::class.java
            Action.SwitchMobileDataSlot(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                slotId = data.slotId
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ToggleSlot::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ToggleSlot::class.java
            Action.ToggleSlot(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                slotId = data.slotId,
                state = data.state
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.CreateLocalVar::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.CreateLocalVar::class.java
            Action.CreateLocalVar(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                localVar = data.localVar,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WriteLocalVar::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.WriteLocalVar::class.java
            Action.WriteLocalVar(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                varName = data.varName,
                valueAsString = data.valueAsString
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartGestureRecording::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StartGestureRecording::class.java
            Action.StartGestureRecording(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StopGestureRecording::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StopGestureRecording::class.java
            Action.StopGestureRecording(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ToggleGestureRecording::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ToggleGestureRecording::class.java
            Action.ToggleGestureRecording(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.InjectGestureRecording::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.InjectGestureRecording::class.java
            Action.InjectGestureRecording(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                speed = data.speed,
                gestureRecord = shortXManager.getGestureRecordById(data.gestureId)
                    ?: GestureRecord.newBuilder().setLabel("N/A").setId("NotExists").build(),
                showGesturePathView = data.showGesturePathView
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.EnableUniversalCopy::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.EnableUniversalCopy::class.java
            Action.EnableUniversalCopy(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowDrawBoard::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowDrawBoard::class.java
            Action.ShowDrawBoard(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowClipboardView::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowClipboardView::class.java
            Action.ShowClipboardView(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.EnableViewIdViewer::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.EnableViewIdViewer::class.java
            Action.EnableViewIdViewer(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.TTS::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.TTS::class.java
            Action.TTS(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                text = data.text
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetWallpaper::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetWallpaper::class.java
            Action.SetWallpaper(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                url = data.fileUrl,
                crop = data.crop
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetRuleEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetRuleEnabled::class.java
            Action.SetRuleEnabled(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                enableRule = data.isEnable,
                ruleId = data.ruleId,
                ruleLabel = shortXManager.getRuleById(data.ruleId)?.title ?: "N/A"
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ClickTile::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ClickTile::class.java
            Action.ClickTile(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                tile = data.tile,
                isLongClick = data.isLongClick
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.MediaPlayback::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.MediaPlayback::class.java
            Action.MediaPlayback(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                action = data.action
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.BiometricVerify::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.BiometricVerify::class.java
            Action.BiometricVerify(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                title = data.title,
                subtitle = data.subTitle,
                allowActions = data.allowActionsList.mapNotNull { it.toAction(shortXManager) },
                denyActions = data.denyActionsList.mapNotNull { it.toAction(shortXManager) },
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.AppShortcut::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.AppShortcut::class.java
            Action.AppShortcut(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                intent = Intent.parseUri(data.intentUri, Intent.URI_INTENT_SCHEME),
                label = data.label
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAPMModeEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetAPMModeEnabled::class.java
            Action.SetAPMModeEnabled(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                isEnableAPM = data.isEnable
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetScreenTimeout::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetScreenTimeout::class.java
            Action.SetScreenTimeout(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                timeout = data.timeoutMillis
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetScreenRotate::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetScreenRotate::class.java
            Action.SetScreenRotate(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                degree = data.degree
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ScrollViewTo::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ScrollViewTo::class.java
            Action.ScrollViewTo(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                location = data.location
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ConnectWifi::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ConnectWifi::class.java
            Action.ConnectWifi(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                ssid = data.ssid
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.DisconnectCurrentWifi::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.DisconnectCurrentWifi::class.java
            Action.DisconnectCurrentWifi(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StayAwake::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StayAwake::class.java
            Action.StayAwake(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                stay = data.isStay
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.Toggle5G::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.Toggle5G::class.java
            Action.Toggle5G(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                onOff = data.onOff,
                slotId = data.slotId,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowRecentApps::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowRecentApps::class.java
            Action.ShowRecentApps(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                state = data.state
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowHideInsets::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowHideInsets::class.java
            Action.ShowHideInsets(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                isHide = data.isHide,
                types = data.typeList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowGlobalActionsMenu::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowGlobalActionsMenu::class.java
            Action.ShowGlobalActionsMenu(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.LockDeviceNow::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.LockDeviceNow::class.java
            Action.LockDeviceNow(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetMasterSync::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetMasterSync::class.java
            Action.SetMasterSync(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                sync = data.sync
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.PlayRingtone::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.PlayRingtone::class.java
            Action.PlayRingtone(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                ringtone = data.ringtone
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.DownloadFile::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.DownloadFile::class.java
            Action.DownloadFile(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                subject = data.subject
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WebDavList::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.WebDavList::class.java
            Action.WebDavList(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                profile = shortXManager.getWebDavProfileById(data.profileId),
                url = data.url,
                depth = data.depth,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WebDavGet::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.WebDavGet::class.java
            Action.WebDavGet(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                profile = shortXManager.getWebDavProfileById(data.profileId),
                url = data.url,
                destPath = data.destPath,
                headers = data.headersList,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WebDavPut::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.WebDavPut::class.java
            Action.WebDavPut(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                profile = shortXManager.getWebDavProfileById(data.profileId),
                url = data.url,
                localFilePath = data.localFilePath,
                contentType = data.contentType,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SendSMS::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SendSMS::class.java
            Action.SendSMS(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                slotId = data.slotId,
                message = data.message,
                to = data.to
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StopAllActions::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StopAllActions::class.java
            Action.StopAllActions(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.MatchRegex::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.MatchRegex::class.java
            Action.MatchRegex(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                string = data.string,
                regex = data.regex,
                matchOptions = data.matchOptions
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ReplaceRegex::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ReplaceRegex::class.java
            Action.ReplaceRegex(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                string = data.string,
                regex = data.regex,
                replacement = data.replacement
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ExportBackup::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ExportBackup::class.java
            Action.ExportBackup(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                destDir = data.destDir
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.PluginAction::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.PluginAction::class.java
            Action.PluginAction(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                action = data.action,
                params = data.params
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WebSocketConnect::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.WebSocketConnect::class.java
            Action.WebSocketConnect(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                url = data.url,
                openActions = data.onOpenList.mapNotNull { it.toAction(shortXManager) },
                closeActions = data.onClosedList.mapNotNull { it.toAction(shortXManager) },
                messageActions = data.onMessageList.mapNotNull { it.toAction(shortXManager) },
                failureActions = data.onFailureList.mapNotNull { it.toAction(shortXManager) },
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WebSocketSend::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.WebSocketSend::class.java
            Action.WebSocketSend(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                url = data.url,
                message = data.message,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.FindImagePosition::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.FindImagePosition::class.java
            Action.FindImagePosition(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                template = data.template.toImageSource(),
                image = data.image.toImageSource(),
                outputType = data.outputType,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.FindPointsByColor::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.FindPointsByColor::class.java
            Action.FindPointsByColor(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                config = data.config.toFindPointsByColorConfig()
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.FindPointsByColorAndClick::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.FindPointsByColorAndClick::class.java
            Action.FindPointsByColorAndClick(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                config = data.config.toFindPointsByColorConfig()
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.LaunchPinedItem::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.LaunchPinedItem::class.java
            Action.LaunchPinedItem(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                item = Action.LaunchPinedItem.PinedItem(
                    label = data.label,
                    intent = IntentConverter.bytesToIntent(data.intent.toByteArray()),
                    userId = data.userId
                )
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.AreaScreenshot::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.AreaScreenshot::class.java
            Action.AreaScreenshot(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SelectScreenArea::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SelectScreenArea::class.java
            Action.SelectScreenArea(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ScreenFlash::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ScreenFlash::class.java
            Action.ScreenFlash(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                colorInt = data.colorInt,
                times = data.times
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.DisplayImage::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.DisplayImage::class.java
            Action.DisplayImage(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                imageUrl = data.imageUrl,
                scaleType = data.scaleType,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.RequestAudioFocus::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.RequestAudioFocus::class.java
            Action.RequestAudioFocus(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                isRequest = data.isRequest
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.OcrDetect::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.OcrDetect::class.java
            Action.OcrDetect(
                isEnabled = !data.isDisabled,
                actionOnError = data.actionOnError,
                customContextDataKey = data.customContextDataKey,
                id = data.id,
                note = data.note,
                src = if (data.rectSrc is_ RectSourceRect::class.java) {
                    val rectSrc = data.rectSrc unpack_ RectSourceRect::class.java
                    Action.OcrDetect.RectSrc.FromRect(rectSrc.rect)
                } else if (data.rectSrc is_ RectSourcePath::class.java) {
                    val rectSrc = data.rectSrc unpack_ RectSourcePath::class.java
                    Action.OcrDetect.RectSrc.FromPath(rectSrc.path)
                } else if (data.rectSrc is_ RectSourceFullScreen::class.java) {
                    Action.OcrDetect.RectSrc.FullScreen
                } else {
                    Action.OcrDetect.RectSrc.SelectScreenArea
                },
                threads = data.threads,
                useSlim = data.useSlim,
                separator = data.separator,
                outputType = data.outputType,
            )
        }

        else -> null
    }
}