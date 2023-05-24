package tornaco.apps.shortx.ui.addrule.action.model

import tornaco.apps.shortx.core.proto.action.BreakActionExecute
import tornaco.apps.shortx.core.proto.action.CreateGlobalVar
import tornaco.apps.shortx.core.proto.action.Delay
import tornaco.apps.shortx.core.proto.action.DeleteGlobalVar
import tornaco.apps.shortx.core.proto.action.ExecuteMVEL
import tornaco.apps.shortx.core.proto.action.ExpandNotification
import tornaco.apps.shortx.core.proto.action.FindAndClickViewByText
import tornaco.apps.shortx.core.proto.action.FromDA
import tornaco.apps.shortx.core.proto.action.HideOverlayButton
import tornaco.apps.shortx.core.proto.action.IfThenElse
import tornaco.apps.shortx.core.proto.action.InputText
import tornaco.apps.shortx.core.proto.action.LaunchApp
import tornaco.apps.shortx.core.proto.action.MapNav
import tornaco.apps.shortx.core.proto.action.NoAction
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.action.SetAppEnabled
import tornaco.apps.shortx.core.proto.action.SetAppSuspend
import tornaco.apps.shortx.core.proto.action.SetBTEnabled
import tornaco.apps.shortx.core.proto.action.SetDNDEnabled
import tornaco.apps.shortx.core.proto.action.SetDarkModeEnabled
import tornaco.apps.shortx.core.proto.action.SetDataEnabled
import tornaco.apps.shortx.core.proto.action.SetFlashLightEnabled
import tornaco.apps.shortx.core.proto.action.SetHotSpotEnabled
import tornaco.apps.shortx.core.proto.action.SetLocationEnabled
import tornaco.apps.shortx.core.proto.action.SetNFCEnabled
import tornaco.apps.shortx.core.proto.action.SetWifiEnabled
import tornaco.apps.shortx.core.proto.action.ShellCommand
import tornaco.apps.shortx.core.proto.action.ShowAlertDialog
import tornaco.apps.shortx.core.proto.action.ShowDanmu
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.action.ShowToast
import tornaco.apps.shortx.core.proto.action.StartActivity
import tornaco.apps.shortx.core.proto.action.StartAppProcess
import tornaco.apps.shortx.core.proto.action.StopApp
import tornaco.apps.shortx.core.proto.action.TakeScreenshot
import tornaco.apps.shortx.core.proto.action.WaitForIdle
import tornaco.apps.shortx.core.proto.action.WhileLoop
import tornaco.apps.shortx.core.proto.action.WriteGlobalVar
import tornaco.apps.shortx.core.proto.common.Location
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.ui.addrule.condition.model.toProtoCondition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.condition.op.toConditionOperator
import tornaco.apps.shortx.ui.remote.toProto


fun Action.toProtoAction(overrideId: String? = null, overrideNote: String? = null): ProtoMessage {
    return when (this) {
        is Action.TakeScreenshot -> {
            TakeScreenshot.newBuilder().setId(overrideId ?: id).setNote(overrideNote ?: note)
                .build()
        }

        is Action.ShellCommand -> {
            ShellCommand.newBuilder().setId(overrideId ?: id).setCommand(command)
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.InputText -> {
            InputText.newBuilder().setId(overrideId ?: id).setText(text)
                .setNote(overrideNote ?: note).build()
        }

        is Action.Delay -> {
            Delay.newBuilder().setId(overrideId ?: id).setTime(time).setNote(overrideNote ?: note)
                .build()
        }

        is Action.ShowToast -> {
            ShowToast.newBuilder().setId(overrideId ?: id).setMessage(message)
                .setNote(overrideNote ?: note).build()
        }

        is Action.ShowDanmu -> {
            ShowDanmu.newBuilder().setId(overrideId ?: id).setText(text).apply {
                this@toProtoAction.icon?.let {
                    icon = it
                }
            }.setNote(overrideNote ?: note).build()
        }

        is Action.MVEL -> {
            ExecuteMVEL.newBuilder().setId(overrideId ?: id).setExpression(expression)
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.RemoteMVEL -> {
            action.toProto()
        }

        is Action.HideAllOverlay -> {
            HideOverlayButton.newBuilder().setId(overrideId ?: id).setNote(overrideNote ?: note)
                .build()
        }

        is Action.HideOverlay -> {
            HideOverlayButton.newBuilder().setId(overrideId ?: id).addAllOverlayTags(tags)
                .setNote(overrideNote ?: note).build()
        }

        is Action.ShowOverlay -> {
            ShowOverlayButton.newBuilder()
                .setId(overrideId ?: id)
                .setAutoCollapse(autoCollapse)
                .setTag(tag)
                .addAllButtonSettings(settings)
                .setNote(overrideNote ?: note).build()
        }

        is Action.LaunchApp -> {
            LaunchApp.newBuilder().setId(overrideId ?: id).setAppPkg(app.pkg)
                .setNote(overrideNote ?: note).build()
        }

        is Action.DisableBT -> SetBTEnabled.newBuilder().setId(overrideId ?: id).setEnable(false)
            .setNote(overrideNote ?: note)
            .build()

        is Action.DisableDND -> SetDNDEnabled.newBuilder().setId(overrideId ?: id).setEnable(false)
            .setNote(overrideNote ?: note)
            .build()

        is Action.DisableLocation -> SetLocationEnabled.newBuilder().setId(overrideId ?: id)
            .setEnable(false)
            .setNote(overrideNote ?: note)
            .build()

        is Action.DisableDarkMode -> SetDarkModeEnabled.newBuilder().setId(overrideId ?: id)
            .setEnable(false)
            .setNote(overrideNote ?: note).build()

        is Action.DisableData -> SetDataEnabled.newBuilder().setId(overrideId ?: id)
            .setEnable(false)
            .setNote(overrideNote ?: note).build()

        is Action.DisableFlashLight -> SetFlashLightEnabled.newBuilder().setId(overrideId ?: id)
            .setEnable(false)
            .setNote(overrideNote ?: note).build()

        is Action.DisableHotSpot -> SetHotSpotEnabled.newBuilder().setId(overrideId ?: id)
            .setEnable(false)
            .setNote(overrideNote ?: note).build()

        is Action.DisableNFC -> SetNFCEnabled.newBuilder().setId(overrideId ?: id).setEnable(false)
            .setNote(overrideNote ?: note)
            .build()

        is Action.DisableWifi -> SetWifiEnabled.newBuilder().setId(overrideId ?: id)
            .setEnable(false)
            .setNote(overrideNote ?: note).build()

        is Action.EnableBT -> SetBTEnabled.newBuilder().setId(overrideId ?: id).setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableDND -> SetDNDEnabled.newBuilder().setId(overrideId ?: id).setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableLocation -> SetLocationEnabled.newBuilder().setId(overrideId ?: id)
            .setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableDarkMode -> SetDarkModeEnabled.newBuilder().setId(overrideId ?: id)
            .setEnable(true)
            .setNote(overrideNote ?: note).build()

        is Action.EnableData -> SetDataEnabled.newBuilder().setId(overrideId ?: id).setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableFlashLight -> SetFlashLightEnabled.newBuilder().setId(overrideId ?: id)
            .setEnable(true)
            .setNote(overrideNote ?: note).build()

        is Action.EnableHotSpot -> SetHotSpotEnabled.newBuilder().setId(overrideId ?: id)
            .setEnable(true)
            .setNote(overrideNote ?: note).build()

        is Action.EnableNFC -> SetNFCEnabled.newBuilder().setId(overrideId ?: id).setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableWifi -> SetWifiEnabled.newBuilder().setId(overrideId ?: id).setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.DisableApp -> {
            SetAppEnabled.newBuilder().setId(overrideId ?: id).setEnable(false)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.EnableApp -> {
            SetAppEnabled.newBuilder().setId(overrideId ?: id).setEnable(true)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.StartAppProcess -> {
            StartAppProcess.newBuilder().setId(overrideId ?: id)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.StopApp -> {
            StopApp.newBuilder().setId(overrideId ?: id)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.SuspendApp -> {
            SetAppSuspend.newBuilder().setId(overrideId ?: id).setSuspend(true)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.UnSuspendApp -> {
            SetAppSuspend.newBuilder().setId(overrideId ?: id).setSuspend(false)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.Brk -> {
            BreakActionExecute.newBuilder().setId(overrideId ?: id).setNote(overrideNote ?: note)
                .build()
        }

        is Action.ShowAlertDialog -> {
            ShowAlertDialog.newBuilder().setId(overrideId ?: id)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .apply {
                    positiveText?.let { positive = it }
                    negativeText?.let { negative = it }
                    neutralText?.let { neutral = it }
                }
                .addAllOnPositive(positiveActions.map { it.toProtoAction().pack_() })
                .addAllOnNegative(negativeActions.map { it.toProtoAction().pack_() })
                .addAllOnNeutral(neutralActions.map { it.toProtoAction().pack_() })
                .setNote(overrideNote ?: note).build()
        }

        is Action.StartActivity -> {
            StartActivity.newBuilder().setId(overrideId ?: id)
                .setComponentNameAsString(componentNameAsString)
                .setNote(overrideNote ?: note).build()
        }

        is Action.ExpandNotification -> {
            ExpandNotification.newBuilder().setId(overrideId ?: id).setNote(overrideNote ?: note)
                .build()
        }

        is Action.WhileLoop -> {
            WhileLoop.newBuilder().setId(overrideId ?: id)
                .setDelay(delay.toInt())
                .setRepeatTimes(repeat)
                .addAllActions(actions.map { it.toProtoAction().pack_() })
                .addAllConditions(conditions.map { it.toProtoCondition().pack_() })
                .setCondOp(op.toConditionOperator())
                .apply {
                    if (op is Op.MVEL) {
                        condOpPayload =
                            ConditionOperatorPayload
                                .newBuilder()
                                .setExpression(op.expression)
                                .build()
                    }
                }
                .setNote(overrideNote ?: note).build()
        }

        is Action.WriteGV -> {
            WriteGlobalVar.newBuilder()
                .setId(overrideId ?: id)
                .setVarName(varName)
                .setValueAsString(valueAsString)
                .setNote(overrideNote ?: note).build()
        }

        is Action.CreateGV -> {
            CreateGlobalVar.newBuilder()
                .setId(overrideId ?: id)
                .setNote(overrideNote ?: note)
                .setGlobalVar(globalVar)
                .build()
        }

        is Action.DeleteGV -> {
            DeleteGlobalVar.newBuilder()
                .setId(overrideId ?: id)
                .setNote(overrideNote ?: note)
                .setVarName(varName)
                .build()
        }

        is Action.MapNav -> {
            MapNav.newBuilder()
                .setId(overrideId ?: id)
                .setApp(mapApp)
                .setLoc(
                    Location.newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build()
                )
                .setPoi(poi)
                .setNote(overrideNote ?: note).build()
        }

        is Action.PostNotification -> {
            PostNotification.newBuilder()
                .setId(overrideId ?: id)
                .setTag(tag)
                .setMessage(message)
                .setTitle(title)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setIsImportant(isImportant)
                .setSound(sound)
                .setVibrate(vibrate)
                .setNote(overrideNote ?: note).build()
        }

        is Action.FromDA -> {
            FromDA.newBuilder().setId(overrideId ?: id).setDaId(daId).setNote(overrideNote ?: note)
                .build()
        }

        is Action.FindAndClickViewByText -> {
            FindAndClickViewByText.newBuilder().setId(overrideId ?: id).setText(text)
                .setIsRegex(isRegex)
                .setNote(overrideNote ?: note).build()
        }

        is Action.WaitForIdle -> {
            WaitForIdle.newBuilder()
                .setId(overrideId ?: id)
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.IfThenElse -> {
            IfThenElse.newBuilder()
                .setId(overrideId ?: id)
                .setNote(overrideNote ?: note)
                .addAllIf(ifConditions.map { it.toProtoCondition().pack_() })
                .setIfCondOp(ifConditionOp.toConditionOperator())
                .apply {
                    if (ifConditionOp is Op.MVEL) {
                        ifCondOpPayload =
                            ConditionOperatorPayload
                                .newBuilder()
                                .setExpression(ifConditionOp.expression)
                                .build()
                    }
                }
                .addAllIfActions(ifActions.map { it.toProtoAction().pack_() })
                .addAllElseActions(elseActions.map { it.toProtoAction().pack_() })
                .build()
        }

        is Action.NoAction -> {
            NoAction.newBuilder()
                .setId(overrideId ?: id)
                .setNote(overrideNote ?: note)
                .build()
        }
    }
}
