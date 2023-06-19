package tornaco.apps.shortx.ui.addrule.action.model

import tornaco.apps.shortx.core.App
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.action.IfThenElse
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.condition.op.toOp
import tornaco.apps.shortx.ui.addrule.protoPkgsAndPkgSetsToModels
import tornaco.apps.shortx.ui.remote.toModel


fun ProtoAny.toAction(shortXManager: ShortXManager): Action? {
    return when {
        this is_ tornaco.apps.shortx.core.proto.action.NoAction::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.NoAction::class.java
            Action.NoAction(id = data.id, note = data.note, icon = data.icon)
        }

        this is_ tornaco.apps.shortx.core.proto.action.ReadClipboard::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ReadClipboard::class.java
            Action.ReadClipboard(id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.action.WriteClipboard::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.WriteClipboard::class.java
            Action.WriteClipboard(id = data.id, note = data.note, text = data.text)
        }

        this is_ tornaco.apps.shortx.core.proto.action.TakeScreenshot::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.TakeScreenshot::class.java
            Action.TakeScreenshot(id = data.id, note = data.note)
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShellCommand::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ShellCommand::class.java
            Action.ShellCommand(
                id = data.id,
                note = data.note, command = data.command
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.InputText::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.InputText::class.java
            Action.InputText(
                id = data.id,
                note = data.note, text = data.text
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowToast::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ShowToast::class.java
            Action.ShowToast(
                id = data.id,
                note = data.note, message = data.message
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowDanmu::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ShowDanmu::class.java
            Action.ShowDanmu(
                id = data.id,
                note = data.note, text = data.text, icon = data.icon
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.Delay::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.Delay::class.java
            Action.Delay(
                id = data.id,
                note = data.note, time = data.time
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ExecuteMVEL::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.ExecuteMVEL::class.java
            Action.MVEL(
                id = data.id,
                note = data.note, expression = data.expression
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.RemoteExecuteMVEL::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.RemoteExecuteMVEL::class.java
            Action.RemoteMVEL(
                id = data.id,
                note = data.note, action = data.toModel()
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowOverlayButton::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowOverlayButton::class.java
            Action.ShowOverlay(
                id = data.id,
                note = data.note,
                autoCollapse = data.autoCollapse,
                settings = data.buttonSettingsList,
                tag = data.tag
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.HideOverlayButton::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.HideOverlayButton::class.java
            if (data.overlayTagsList.isEmpty()) {
                Action.HideAllOverlay(id = data.id)
            } else {
                Action.HideOverlay(
                    id = data.id,
                    note = data.note,
                    tags = data.overlayTagsList
                )
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.LaunchApp::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.LaunchApp::class.java
            val pkg = Pkg(data.appPkg.pkgName, userId = data.appPkg.userId)
            Action.LaunchApp(
                id = data.id,
                note = data.note,
                app = App(
                    pkg = pkg,
                    label = shortXManager.getAppLabel(pkg),
                    tornaco.apps.shortx.core.proto.common.AppCategory.User
                )
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetWifiEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetWifiEnabled::class.java
            if (data.enable) {
                Action.EnableWifi(id = data.id)
            } else {
                Action.DisableWifi(id = data.id)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetSensorsOffEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetSensorsOffEnabled::class.java
            if (data.enable) {
                Action.EnableSensorsOff(id = data.id)
            } else {
                Action.DisableSensorsOff(id = data.id)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetBTEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetBTEnabled::class.java
            if (data.enable) {
                Action.EnableBT(id = data.id)
            } else {
                Action.DisableBT(id = data.id)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetNFCEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetNFCEnabled::class.java
            if (data.enable) {
                Action.EnableNFC(id = data.id)
            } else {
                Action.DisableNFC(id = data.id)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetDNDEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetDNDEnabled::class.java
            if (data.enable) {
                Action.EnableDND(id = data.id)
            } else {
                Action.DisableDND(id = data.id)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetLocationEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetLocationEnabled::class.java
            if (data.enable) {
                Action.EnableLocation(id = data.id)
            } else {
                Action.DisableLocation(id = data.id)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetDataEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetDataEnabled::class.java
            if (data.enable) {
                Action.EnableData(id = data.id)
            } else {
                Action.DisableData(id = data.id)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetDarkModeEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetDarkModeEnabled::class.java
            if (data.enable) {
                Action.EnableDarkMode(id = data.id)
            } else {
                Action.DisableDarkMode(id = data.id)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetFlashLightEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetFlashLightEnabled::class.java
            if (data.enable) {
                Action.EnableFlashLight(id = data.id)
            } else {
                Action.DisableFlashLight(id = data.id)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetHotSpotEnabled::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetHotSpotEnabled::class.java
            if (data.enable) {
                Action.EnableHotSpot(id = data.id)
            } else {
                Action.DisableHotSpot(id = data.id)
            }
        }

        this is_ tornaco.apps.shortx.core.proto.action.StopApp::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.StopApp::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.StopApp(
                id = data.id,
                note = data.note,
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartAppProcess::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StartAppProcess::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.StartAppProcess(
                id = data.id,
                note = data.note,
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAppInactive::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetAppInactive::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.SetAppInactive(
                id = data.id,
                note = data.note,
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAppEnabled::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetAppEnabled::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            data.enable.select(
                Action.EnableApp(
                    id = data.id,
                    note = data.note,
                    apps = models.first,
                    pkgSets = models.second
                ),
                Action.DisableApp(
                    id = data.id,
                    note = data.note,
                    apps = models.first,
                    pkgSets = models.second
                )
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAppSuspend::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetAppSuspend::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            data.suspend.select(
                Action.SuspendApp(
                    id = data.id,
                    note = data.note,
                    apps = models.first,
                    pkgSets = models.second
                ),
                Action.UnSuspendApp(
                    id = data.id,
                    note = data.note,
                    apps = models.first,
                    pkgSets = models.second
                )
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.BreakActionExecute::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.BreakActionExecute::class.java
            Action.Brk(id = data.id)
        }

        this is_ tornaco.apps.shortx.core.proto.action.ShowAlertDialog::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ShowAlertDialog::class.java
            Action.ShowAlertDialog(
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
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartActivity::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.StartActivity::class.java
            Action.StartActivity(
                id = data.id,
                note = data.note,
                componentNameAsString = data.componentNameAsString
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartActivityIntent::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StartActivityIntent::class.java
            Action.StartActivityIntent(
                id = data.id,
                note = data.note,
                userId = data.userId,
                action = data.intent.action,
                pkgName = data.intent.pkgName,
                className = data.intent.className,
                data = data.intent.data,
                flags = data.intent.flags,
                extras = data.intent.extrasList
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StartActivityUrlSchema::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StartActivityUrlSchema::class.java
            Action.StartActivityUrlSchema(
                id = data.id,
                note = data.note,
                userId = data.userId,
                urlSchema = data.urlSchema
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.ExpandNotification::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.ExpandNotification::class.java
            Action.ExpandNotification(id = data.id)
        }

        this is_ tornaco.apps.shortx.core.proto.action.WhileLoop::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.WhileLoop::class.java
            Action.WhileLoop(
                id = data.id,
                note = data.note,
                delay = data.delay.toLong(),
                repeat = data.repeatTimes,
                conditions = data.conditionsList.mapNotNull { it.toCondition(shortXManager) },
                actions = data.actionsList.mapNotNull { it.toAction(shortXManager) },
                op = data.condOp.toOp(data.condOpPayload)
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WriteGlobalVar::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.WriteGlobalVar::class.java
            Action.WriteGV(
                id = data.id,
                note = data.note,
                varName = data.varName,
                valueAsString = data.valueAsString,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.CreateGlobalVar::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.CreateGlobalVar::class.java
            Action.CreateGV(
                id = data.id,
                note = data.note,
                globalVar = data.globalVar
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.DeleteGlobalVar::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.DeleteGlobalVar::class.java
            Action.DeleteGV(
                id = data.id,
                note = data.note,
                varName = data.varName,
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.MapNav::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.MapNav::class.java
            Action.MapNav(
                id = data.id,
                note = data.note,
                longitude = data.loc.longitude,
                latitude = data.loc.latitude,
                poi = data.poi,
                mapApp = data.app
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.PostNotification::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.PostNotification::class.java
            Action.PostNotification(
                id = data.id,
                note = data.note,
                tag = data.tag,
                message = data.message,
                title = data.title,
                largeIcon = data.largeIcon,
                smallIcon = data.smallIcon,
                vibrate = data.vibrate,
                sound = data.sound,
                isImportant = data.isImportant
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.FromDA::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.FromDA::class.java
            val da = shortXManager.getDirectActionById(data.daId)
            Action.FromDA(
                id = data.id,
                note = data.note, daId = data.daId,
                title = da?.title,
                description = da?.description
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.FindAndClickViewByText::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.FindAndClickViewByText::class.java
            Action.FindAndClickViewByText(
                id = data.id,
                note = data.note,
                text = data.text,
                isRegex = data.isRegex,
                timeout = data.timeout
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.InputTap::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.InputTap::class.java
            Action.InputTap(
                id = data.id,
                note = data.note,
                x = data.x,
                y = data.y
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.InputSwipe::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.InputSwipe::class.java
            Action.InputSwipe(
                id = data.id,
                note = data.note,
                startX = data.startX,
                startY = data.startY,
                endX = data.endX,
                endY = data.endY,
                swipeTime = data.swipeTime
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.WaitForIdle::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.WaitForIdle::class.java
            Action.WaitForIdle(
                id = data.id,
                note = data.note
            )
        }

        this is_ IfThenElse::class.java -> {
            val data = this unpack_ IfThenElse::class.java
            Action.IfThenElse(
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
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.CreatePkgSet::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.CreatePkgSet::class.java
            Action.CreatePkgSet(
                id = data.id,
                note = data.note,
                label = data.label,
                description = data.description
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetRingerMode::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetRingerMode::class.java
            Action.SetRingerMode(
                id = data.id,
                note = data.note,
                mode = data.mode
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetBrightness::class.java -> {
            val data = this unpack_ tornaco.apps.shortx.core.proto.action.SetBrightness::class.java
            Action.SetBrightness(
                id = data.id,
                note = data.note,
                value = data.value
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.SetAutoBrightness::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.SetAutoBrightness::class.java
            data.enable.select(
                Action.EnableAutoBrightness(
                    id = data.id,
                    note = data.note,
                ),
                Action.DisableAutoBrightness(
                    id = data.id,
                    note = data.note,
                )
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.StopService::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.StopService::class.java
            Action.StopService(
                id = data.id,
                note = data.note,
                services = data.servicesList.distinct()
            )
        }

        this is_ tornaco.apps.shortx.core.proto.action.RemoveTasks::class.java -> {
            val data =
                this unpack_ tornaco.apps.shortx.core.proto.action.RemoveTasks::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.RemoveTasks(
                id = data.id,
                note = data.note,
                apps = models.first,
                pkgSets = models.second
            )
        }

        else -> null
    }
}