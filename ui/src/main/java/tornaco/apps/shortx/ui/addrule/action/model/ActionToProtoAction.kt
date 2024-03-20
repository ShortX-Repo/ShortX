package tornaco.apps.shortx.ui.addrule.action.model

import android.content.Intent
import tornaco.apps.shortx.core.proto.action.AdjustVolume
import tornaco.apps.shortx.core.proto.action.AppShortcut
import tornaco.apps.shortx.core.proto.action.AudioRecording
import tornaco.apps.shortx.core.proto.action.BiometricVerify
import tornaco.apps.shortx.core.proto.action.BreakActionExecute
import tornaco.apps.shortx.core.proto.action.ClickNotification
import tornaco.apps.shortx.core.proto.action.ClickNotificationActionButton
import tornaco.apps.shortx.core.proto.action.ClickTile
import tornaco.apps.shortx.core.proto.action.ConnectWifi
import tornaco.apps.shortx.core.proto.action.CreateGlobalVar
import tornaco.apps.shortx.core.proto.action.CreateLocalVar
import tornaco.apps.shortx.core.proto.action.CreatePkgSet
import tornaco.apps.shortx.core.proto.action.Delay
import tornaco.apps.shortx.core.proto.action.DeleteGlobalVar
import tornaco.apps.shortx.core.proto.action.DisconnectCurrentWifi
import tornaco.apps.shortx.core.proto.action.DownloadFile
import tornaco.apps.shortx.core.proto.action.EnableUniversalCopy
import tornaco.apps.shortx.core.proto.action.EnableViewIdViewer
import tornaco.apps.shortx.core.proto.action.ExecuteJS
import tornaco.apps.shortx.core.proto.action.ExecuteMVEL
import tornaco.apps.shortx.core.proto.action.ExpandNotification
import tornaco.apps.shortx.core.proto.action.FindAndClickMatchedView
import tornaco.apps.shortx.core.proto.action.FindAndClickViewById
import tornaco.apps.shortx.core.proto.action.FindAndClickViewByText
import tornaco.apps.shortx.core.proto.action.ForEachPkgSet
import tornaco.apps.shortx.core.proto.action.FromDA
import tornaco.apps.shortx.core.proto.action.GetTextFromScreenNode
import tornaco.apps.shortx.core.proto.action.HideOverlayButton
import tornaco.apps.shortx.core.proto.action.HttpRequest
import tornaco.apps.shortx.core.proto.action.HttpRequestHeaderBodyJsonMapAdapter
import tornaco.apps.shortx.core.proto.action.HttpRequestJsonMapAdapter
import tornaco.apps.shortx.core.proto.action.HttpRequestRawAdapter
import tornaco.apps.shortx.core.proto.action.IfThenElse
import tornaco.apps.shortx.core.proto.action.InjectCombineKeyCode
import tornaco.apps.shortx.core.proto.action.InjectGestureRecording
import tornaco.apps.shortx.core.proto.action.InjectKeyCode
import tornaco.apps.shortx.core.proto.action.InputSwipe
import tornaco.apps.shortx.core.proto.action.InputTap
import tornaco.apps.shortx.core.proto.action.InputText
import tornaco.apps.shortx.core.proto.action.KillProcessByName
import tornaco.apps.shortx.core.proto.action.LaunchApp
import tornaco.apps.shortx.core.proto.action.LaunchAppByPkg
import tornaco.apps.shortx.core.proto.action.LockDeviceNow
import tornaco.apps.shortx.core.proto.action.MapNav
import tornaco.apps.shortx.core.proto.action.MapQueryBus
import tornaco.apps.shortx.core.proto.action.MediaPlayback
import tornaco.apps.shortx.core.proto.action.NoAction
import tornaco.apps.shortx.core.proto.action.PerformContextMenuAction
import tornaco.apps.shortx.core.proto.action.PlayRingtone
import tornaco.apps.shortx.core.proto.action.PostNotification
import tornaco.apps.shortx.core.proto.action.ReadClipboard
import tornaco.apps.shortx.core.proto.action.RemoveNotification
import tornaco.apps.shortx.core.proto.action.RemoveNotificationForPackage
import tornaco.apps.shortx.core.proto.action.RemoveNotificationForPackageByPkg
import tornaco.apps.shortx.core.proto.action.RemoveTasks
import tornaco.apps.shortx.core.proto.action.RemoveTasksByPkg
import tornaco.apps.shortx.core.proto.action.ScrollViewTo
import tornaco.apps.shortx.core.proto.action.SendSMS
import tornaco.apps.shortx.core.proto.action.SetAPMModeEnabled
import tornaco.apps.shortx.core.proto.action.SetAppEnabled
import tornaco.apps.shortx.core.proto.action.SetAppEnabledByPkg
import tornaco.apps.shortx.core.proto.action.SetAppInactive
import tornaco.apps.shortx.core.proto.action.SetAppInactiveByPkg
import tornaco.apps.shortx.core.proto.action.SetAppSuspend
import tornaco.apps.shortx.core.proto.action.SetAppSuspendByPkg
import tornaco.apps.shortx.core.proto.action.SetAutoBrightness
import tornaco.apps.shortx.core.proto.action.SetBTEnabled
import tornaco.apps.shortx.core.proto.action.SetBrightness
import tornaco.apps.shortx.core.proto.action.SetDNDEnabled
import tornaco.apps.shortx.core.proto.action.SetDarkModeEnabled
import tornaco.apps.shortx.core.proto.action.SetDataEnabled
import tornaco.apps.shortx.core.proto.action.SetFlashLightEnabled
import tornaco.apps.shortx.core.proto.action.SetHotSpotEnabled
import tornaco.apps.shortx.core.proto.action.SetLocationEnabled
import tornaco.apps.shortx.core.proto.action.SetMasterSync
import tornaco.apps.shortx.core.proto.action.SetNFCEnabled
import tornaco.apps.shortx.core.proto.action.SetRingerMode
import tornaco.apps.shortx.core.proto.action.SetRuleEnabled
import tornaco.apps.shortx.core.proto.action.SetScreenRotate
import tornaco.apps.shortx.core.proto.action.SetScreenTimeout
import tornaco.apps.shortx.core.proto.action.SetSensorsOffEnabled
import tornaco.apps.shortx.core.proto.action.SetVolume
import tornaco.apps.shortx.core.proto.action.SetWallpaper
import tornaco.apps.shortx.core.proto.action.SetWifiEnabled
import tornaco.apps.shortx.core.proto.action.ShellCommand
import tornaco.apps.shortx.core.proto.action.ShowAlertDialog
import tornaco.apps.shortx.core.proto.action.ShowClipboardView
import tornaco.apps.shortx.core.proto.action.ShowDanmu
import tornaco.apps.shortx.core.proto.action.ShowDrawBoard
import tornaco.apps.shortx.core.proto.action.ShowMenuDialog
import tornaco.apps.shortx.core.proto.action.ShowOverlayButton
import tornaco.apps.shortx.core.proto.action.ShowTextFieldDialog
import tornaco.apps.shortx.core.proto.action.ShowToast
import tornaco.apps.shortx.core.proto.action.SleepScreen
import tornaco.apps.shortx.core.proto.action.StartActivity
import tornaco.apps.shortx.core.proto.action.StartActivityIntent
import tornaco.apps.shortx.core.proto.action.StartActivityIntentUri
import tornaco.apps.shortx.core.proto.action.StartActivityUrlSchema
import tornaco.apps.shortx.core.proto.action.StartAppProcess
import tornaco.apps.shortx.core.proto.action.StartAppProcessByPkg
import tornaco.apps.shortx.core.proto.action.StartGestureRecording
import tornaco.apps.shortx.core.proto.action.StartLastApp
import tornaco.apps.shortx.core.proto.action.StartService
import tornaco.apps.shortx.core.proto.action.StayAwake
import tornaco.apps.shortx.core.proto.action.StopAllActions
import tornaco.apps.shortx.core.proto.action.StopApp
import tornaco.apps.shortx.core.proto.action.StopAppByPkg
import tornaco.apps.shortx.core.proto.action.StopAudioRecording
import tornaco.apps.shortx.core.proto.action.StopCurrentApp
import tornaco.apps.shortx.core.proto.action.StopGestureRecording
import tornaco.apps.shortx.core.proto.action.StopService
import tornaco.apps.shortx.core.proto.action.SwitchCase
import tornaco.apps.shortx.core.proto.action.SwitchCase_Case
import tornaco.apps.shortx.core.proto.action.SwitchMobileDataSlot
import tornaco.apps.shortx.core.proto.action.TTS
import tornaco.apps.shortx.core.proto.action.TakeScreenshot
import tornaco.apps.shortx.core.proto.action.Toggle5G
import tornaco.apps.shortx.core.proto.action.ToggleGestureRecording
import tornaco.apps.shortx.core.proto.action.Vibrate
import tornaco.apps.shortx.core.proto.action.WaitForIdle
import tornaco.apps.shortx.core.proto.action.WaitUtilConditionMatch
import tornaco.apps.shortx.core.proto.action.WakeupScreen
import tornaco.apps.shortx.core.proto.action.WhileLoop
import tornaco.apps.shortx.core.proto.action.WriteClipboard
import tornaco.apps.shortx.core.proto.action.WriteGlobalVar
import tornaco.apps.shortx.core.proto.action.WriteLocalVar
import tornaco.apps.shortx.core.proto.common.AndroidIntent
import tornaco.apps.shortx.core.proto.common.Location
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.condition.model.toProtoCondition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.condition.op.toConditionOperator
import tornaco.apps.shortx.ui.addrule.fact.model.toProtoFact
import tornaco.apps.shortx.ui.addrule.toCustomContextDataKey
import tornaco.apps.shortx.ui.remote.toProto


fun Action.toProtoAction(overrideId: String? = null, overrideNote: String? = null): ProtoMessage {
    return when (this) {
        is Action.TakeScreenshot -> {
            TakeScreenshot.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.ShellCommand -> {
            ShellCommand.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setCommand(command)
                .setSingleShot(singleShot)
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.InputText -> {
            InputText.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setText(text)
                .setNote(overrideNote ?: note).build()
        }

        is Action.PerformContextMenuAction -> {
            PerformContextMenuAction.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setAction(action)
                .setNote(overrideNote ?: note).build()
        }

        is Action.Delay -> {
            Delay.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setTimeString(time)
                .setUseAlarm(useAlarm)
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.ShowToast -> {
            ShowToast.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setMessage(message)
                .setNote(overrideNote ?: note).build()
        }

        is Action.ShowDanmu -> {
            ShowDanmu.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setText(text).apply {
                    this@toProtoAction.icon?.let {
                        icon = it
                    }
                }.setNote(overrideNote ?: note).build()
        }

        is Action.ExecuteJS -> {
            ExecuteJS.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setExpression(expression)
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.MVEL -> {
            ExecuteMVEL.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setExpression(expression)
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.RemoteMVEL -> {
            action.toProto()
        }

        is Action.HideAllOverlay -> {
            HideOverlayButton.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.HideOverlay -> {
            HideOverlayButton.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .addAllOverlayTags(tags)
                .setNote(overrideNote ?: note).build()
        }

        is Action.ShowOverlay -> {
            ShowOverlayButton.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setAutoCollapse(autoCollapse)
                .setTag(tag.fallbackOnEmpty(DateUtils.formatLongForMessageTime(System.currentTimeMillis())))
                .addAllButtonSettings(settings)
                .setMaxHeightInDp(maxHeight)
                .setMaxWidthInDp(maxWidth)
                .setButtonMinWidth(buttonMinWidth)
                .setBackgroundAlpha(backgroundAlpha)
                .setBackgroundColor(backgroundColor)
                .setOverlayPaddingH(overlayPaddingH)
                .setOverlayPaddingV(overlayPaddingV)
                .setEnableGlobalDrag(enableGlobalDrag)
                .setDisableAutoEdge(disableAutoEdge)
                .setNote(overrideNote ?: note).build()
        }

        is Action.LaunchApp -> {
            LaunchApp.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setAppPkg(app.pkg)
                .setNote(overrideNote ?: note).build()
        }

        is Action.LaunchAppByPkg -> {
            LaunchAppByPkg.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .addAllPkgAndUsers(stringPairs)
                .setNote(overrideNote ?: note).build()
        }

        is Action.DisableBT -> SetBTEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(false)
            .setNote(overrideNote ?: note)
            .build()

        is Action.DisableDND -> SetDNDEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(false)
            .setNote(overrideNote ?: note)
            .build()

        is Action.DisableLocation -> SetLocationEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(false)
            .setNote(overrideNote ?: note)
            .build()

        is Action.DisableDarkMode -> SetDarkModeEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(false)
            .setNote(overrideNote ?: note).build()

        is Action.DisableData -> SetDataEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(false)
            .setNote(overrideNote ?: note).build()

        is Action.DisableFlashLight -> SetFlashLightEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(false)
            .setNote(overrideNote ?: note).build()

        is Action.DisableHotSpot -> SetHotSpotEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(false)
            .setNote(overrideNote ?: note).build()

        is Action.DisableNFC -> SetNFCEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(false)
            .setNote(overrideNote ?: note)
            .build()

        is Action.DisableWifi -> SetWifiEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(false)
            .setNote(overrideNote ?: note).build()

        is Action.DisableSensorsOff -> SetSensorsOffEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(false)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableBT -> SetBTEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableDND -> SetDNDEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableLocation -> SetLocationEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableDarkMode -> SetDarkModeEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(true)
            .setNote(overrideNote ?: note).build()

        is Action.EnableData -> SetDataEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableFlashLight -> SetFlashLightEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(true)
            .setNote(overrideNote ?: note).build()

        is Action.EnableHotSpot -> SetHotSpotEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(true)
            .setNote(overrideNote ?: note).build()

        is Action.EnableNFC -> SetNFCEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableWifi -> SetWifiEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.EnableSensorsOff -> SetSensorsOffEnabled.newBuilder().setId(overrideId ?: id)
            .setIsDisabled(!isEnabled)
            .setActionOnError(actionOnError)
            .setCustomContextDataKey(contextData.toCustomContextDataKey())
            .setEnable(true)
            .setNote(overrideNote ?: note)
            .build()

        is Action.DisableApp -> {
            SetAppEnabled.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setEnable(false)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.EnableApp -> {
            SetAppEnabled.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setEnable(true)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.DisableAppByPkg -> {
            SetAppEnabledByPkg.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setEnable(false)
                .addAllPkgAndUsers(stringPairs)
                .setNote(overrideNote ?: note).build()
        }

        is Action.EnableAppByPkg -> {
            SetAppEnabledByPkg.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setEnable(true)
                .addAllPkgAndUsers(stringPairs)
                .setNote(overrideNote ?: note).build()
        }

        is Action.StartAppProcess -> {
            StartAppProcess.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.StartAppProcessByPkg -> {
            StartAppProcessByPkg.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .addAllPkgAndUsers(stringPairs)
                .setNote(overrideNote ?: note).build()
        }

        is Action.SetAppInactive -> {
            SetAppInactive.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.SetAppInactiveByPkg -> {
            SetAppInactiveByPkg.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .addAllPkgAndUsers(stringPairs)
                .setNote(overrideNote ?: note).build()
        }

        is Action.StopApp -> {
            StopApp.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.StopAppByPkg -> {
            StopAppByPkg.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .addAllPkgAndUsers(stringPairs)
                .setNote(overrideNote ?: note).build()
        }

        is Action.SuspendApp -> {
            SetAppSuspend.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setSuspend(true)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.UnSuspendApp -> {
            SetAppSuspend.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setSuspend(false)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .setNote(overrideNote ?: note).build()
        }

        is Action.SuspendAppByPkg -> {
            SetAppSuspendByPkg.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setSuspend(true)
                .addAllPkgAndUsers(stringPairs)
                .setNote(overrideNote ?: note).build()
        }

        is Action.UnSuspendAppByPkg -> {
            SetAppSuspendByPkg.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setSuspend(false)
                .addAllPkgAndUsers(stringPairs)
                .setNote(overrideNote ?: note).build()
        }

        is Action.Brk -> {
            BreakActionExecute.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setScope(scope)
                .build()
        }

        is Action.ShowAlertDialog -> {
            ShowAlertDialog.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
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

        is Action.ShowMenuDialog -> {
            ShowMenuDialog.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .addAllItems(items)
                .setNote(overrideNote ?: note).build()
        }

        is Action.StartActivity -> {
            StartActivity.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setComponentNameAsString(componentNameAsString)
                .setNote(overrideNote ?: note).build()
        }

        is Action.StartActivityIntent -> {
            StartActivityIntent.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setIntent(AndroidIntent.newBuilder()
                    .apply {
                        this@toProtoAction.action?.let { action = it }
                        this@toProtoAction.pkgName?.let { pkgName = it }
                        this@toProtoAction.className?.let { className = it }
                        this@toProtoAction.data?.let { data = it }
                    }
                    .setFlags(flags)
                    .addAllExtras(extras)
                    .build())
                .setUserId(userId)
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.StartActivityUrlSchema -> {
            StartActivityUrlSchema.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setUrlSchema(urlSchema)
                .setUserId(userId)
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.StartActivityIntentUri -> {
            StartActivityIntentUri.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setIntentUri(intentUri)
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.ExpandNotification -> {
            ExpandNotification.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.WhileLoop -> {
            WhileLoop.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
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

        is Action.ForEachPkgSet -> {
            ForEachPkgSet.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .addAllAction(actions.map { it.toProtoAction().pack_() })
                .setPkgSet(pkgSet.label)
                .setNote(overrideNote ?: note).build()
        }

        is Action.WriteGV -> {
            WriteGlobalVar.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setVarName(varName)
                .setValueAsString(valueAsString)
                .setOp(op)
                .setNote(overrideNote ?: note).build()
        }

        is Action.CreateGV -> {
            CreateGlobalVar.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setGlobalVar(globalVar)
                .build()
        }

        is Action.DeleteGV -> {
            DeleteGlobalVar.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setVarName(varName)
                .build()
        }

        is Action.MapNav -> {
            MapNav.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setApp(mapApp)
                .setNavType(navType)
                .setLoc(
                    Location.newBuilder()
                        .setLatitude(latitude)
                        .setLongitude(longitude)
                        .build()
                )
                .setPoi(poi)
                .setNote(overrideNote ?: note).build()
        }

        is Action.MapQueryBus -> {
            MapQueryBus.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setApp(mapApp)
                .setBus(bus)
                .setCity(city)
                .setNote(overrideNote ?: note).build()
        }

        is Action.PostNotification -> {
            PostNotification.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setTag(tag)
                .setMessage(message)
                .setOverrideAppName(overrideAppName)
                .setTitle(title)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setIsImportant(isImportant)
                .setSound(sound)
                .setVibrate(vibrate)
                .addAllButton(buttons)
                .addAllClickAction(onClickActions.map { it.toProtoAction().pack_() })
                .setNote(overrideNote ?: note).build()
        }

        is Action.RemoveNotification -> {
            RemoveNotification.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNotification(n.toProtoNotification())
                .setNote(overrideNote ?: note).build()
        }

        is Action.ClickNotification -> {
            ClickNotification.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNotification(n.toProtoNotification())
                .setNote(overrideNote ?: note).build()
        }

        is Action.ClickNotificationActionButton -> {
            ClickNotificationActionButton.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNotification(n.toProtoNotification())
                .setButton(button)
                .setNote(overrideNote ?: note).build()
        }

        is Action.FromDA -> {
            FromDA.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setDaId(daId)
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.FindAndClickViewByText -> {
            FindAndClickViewByText.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setText(text)
                .setIsRegex(isRegex)
                .setTimeout(timeout)
                .setNote(overrideNote ?: note).build()
        }

        is Action.FindAndClickViewById -> {
            FindAndClickViewById.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setViewId(viewId)
                .setIsRegex(isRegex)
                .setTimeout(timeout)
                .setNote(overrideNote ?: note).build()
        }

        is Action.FindAndClickMatchedView -> {
            FindAndClickMatchedView.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note).build()
        }

        is Action.InputTap -> {
            InputTap.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setXs(x)
                .setYs(y)
                .setNote(overrideNote ?: note).build()
        }

        is Action.InputSwipe -> {
            InputSwipe.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setStartXS(startX)
                .setStartYS(startY)
                .setEndXS(endX)
                .setEndYS(endY)
                .setSwipeTimeS(swipeTime)
                .setNote(overrideNote ?: note).build()
        }

        is Action.WaitForIdle -> {
            WaitForIdle.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.IfThenElse -> {
            IfThenElse.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
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

        is Action.SwitchCase -> {
            SwitchCase.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .addAllCase(cases.map { case ->
                    SwitchCase_Case.newBuilder()
                        .addAllCase(case.conditions.map { it.toProtoCondition().pack_() })
                        .setCaseCondOp(case.conditionOp.toConditionOperator())
                        .apply {
                            if (case.conditionOp is Op.MVEL) {
                                caseCondOpPayload =
                                    ConditionOperatorPayload
                                        .newBuilder()
                                        .setExpression(case.conditionOp.expression)
                                        .build()
                            }
                        }
                        .addAllAction(case.actions.map { it.toProtoAction().pack_() })
                        .setDescription(case.description)
                        .setIsBreak(case.isBreak)
                        .setIsDisabled(case.isDisabled)
                        .setId(case.id)
                        .build()
                })
                .build()
        }

        is Action.WaitUtilConditionMatch -> {
            WaitUtilConditionMatch.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .addAllCondition(conditions.map { it.toProtoCondition().pack_() })
                .setConditionOp(op.toConditionOperator())
                .apply {
                    if (op is Op.MVEL) {
                        condOpPayload =
                            ConditionOperatorPayload
                                .newBuilder()
                                .setExpression(op.expression)
                                .build()
                    }
                }
                .addAllQuitFacts(quitFacts.map { it.toProtoFact().pack_() })
                .addAllQuitCondition(quitConditions.map { it.toProtoCondition().pack_() })
                .setQuitConditionOp(quitOp.toConditionOperator())
                .apply {
                    if (quitOp is Op.MVEL) {
                        quitCondOpPayload =
                            ConditionOperatorPayload
                                .newBuilder()
                                .setExpression(quitOp.expression)
                                .build()
                    }
                }
                .setQuitEnabled(isQuitEnabled)
                .setTimeout(timeout)
                .build()
        }

        is Action.ReadClipboard -> {
            ReadClipboard.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.GetTextFromScreenNode -> {
            GetTextFromScreenNode.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setNodeId(nodeId)
                .build()
        }

        is Action.WriteClipboard -> {
            WriteClipboard.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setText(text)
                .build()
        }

        is Action.CreatePkgSet -> {
            CreatePkgSet.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setLabel(label)
                .apply { description?.let { description = it } }
                .build()
        }

        is Action.SetRingerMode -> {
            SetRingerMode.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setMode(mode)
                .build()
        }

        is Action.EnableAutoBrightness -> {
            SetAutoBrightness.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setEnable(true)
                .build()
        }

        is Action.DisableAutoBrightness -> {
            SetAutoBrightness.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setEnable(false)
                .build()
        }

        is Action.SetBrightness -> {
            SetBrightness.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setValue(value)
                .build()
        }

        is Action.StopService -> {
            StopService.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .addAllServices(services.distinct())
                .build()
        }

        is Action.StartService -> {
            StartService.newBuilder().setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setIntent(AndroidIntent.newBuilder()
                    .apply {
                        this@toProtoAction.action?.let { action = it }
                        this@toProtoAction.pkgName?.let { pkgName = it }
                        this@toProtoAction.className?.let { className = it }
                        this@toProtoAction.data?.let { data = it }
                    }
                    .setFlags(flags)
                    .addAllExtras(extras)
                    .build())
                .setUserId(userId)
                .setNote(overrideNote ?: note)
                .setIsForegroundService(isFG)
                .build()
        }

        is Action.RemoveTasks -> {
            RemoveTasks.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .build()
        }

        is Action.RemoveTasksByPkg -> {
            RemoveTasksByPkg.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .addAllPkgAndUsers(stringPairs)
                .build()
        }

        is Action.RemoveNotificationForPackage -> {
            RemoveNotificationForPackage.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .addAllPkgSets(pkgSets.map { it.label }).addAllAppPkg(apps.map { it.pkg })
                .build()
        }

        is Action.RemoveNotificationForPackageByPkg -> {
            RemoveNotificationForPackageByPkg.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .addAllPkgAndUsers(stringPairs)
                .build()
        }

        is Action.KillProcessByName -> {
            KillProcessByName.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .addAllProcesses(processList.distinct())
                .build()
        }

        is Action.HttpRequest -> {
            HttpRequest.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setAdapter(
                    when (this.adapter) {
                        is HttpRequestAdapter.BodyJsonMap -> {
                            HttpRequestJsonMapAdapter.newBuilder()
                                .addAllExpressions(this.adapter.expressions)
                                .build().pack_()
                        }

                        is HttpRequestAdapter.HeaderBodyJsonMap -> {
                            HttpRequestHeaderBodyJsonMapAdapter.newBuilder()
                                .addAllExpressions(this.adapter.expressions)
                                .build().pack_()
                        }

                        else -> {
                            HttpRequestRawAdapter.newBuilder().build().pack_()
                        }
                    }
                )
                .setUrl(url)
                .setMethod(method)
                .addAllHeaders(headers)
                .setRequestBody(requestBody)
                .setWithCookieJar(withCookieJar)
                .setTrustAllCerts(trustAllCerts)
                .setExecuteInAppProcess(executeInAppProcess)
                .build()
        }

        is Action.InjectKeyCode -> {
            InjectKeyCode.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setKeyCode(code)
                .build()
        }

        is Action.InjectCombineKeyCode -> {
            InjectCombineKeyCode.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .addAllKeyCode(codes)
                .build()
        }

        is Action.SleepScreen -> {
            SleepScreen.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.WakeupScreen -> {
            WakeupScreen.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.StartLastApp -> {
            StartLastApp.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.StopCurrentApp -> {
            StopCurrentApp.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.AdjustVolume -> {
            AdjustVolume.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setDirection(direction)
                .setShowUI(true)
                .build()
        }

        is Action.SetVolume -> {
            SetVolume.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setType(type)
                .setIndex(index)
                .build()
        }

        is Action.Vibrate -> {
            Vibrate.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setVib1(vib1)
                .setVib2(vib2)
                .setVib3(vib3)
                .build()
        }

        is Action.AudioRecording -> {
            AudioRecording.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setSrc(src)
                .setFileNamePrefix(fileNamePrefix)
                .build()
        }

        is Action.StopAudioRecording -> {
            StopAudioRecording.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.SwitchMobileDataSlot -> {
            SwitchMobileDataSlot.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setSlotId(slotId)
                .build()
        }

        is Action.CreateLocalVar -> {
            CreateLocalVar.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setLocalVar(localVar)
                .build()
        }

        is Action.WriteLocalVar -> {
            WriteLocalVar.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setVarName(varName)
                .setValueAsString(valueAsString)
                .build()
        }

        is Action.StartGestureRecording -> {
            StartGestureRecording.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.StopGestureRecording -> {
            StopGestureRecording.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.ToggleGestureRecording -> {
            ToggleGestureRecording.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.InjectGestureRecording -> {
            InjectGestureRecording.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setSpeed(speed)
                .setGestureId(gestureRecord.id)
                .setShowGesturePathView(showGesturePathView)
                .build()
        }

        is Action.EnableUniversalCopy -> {
            EnableUniversalCopy.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.ShowDrawBoard -> {
            ShowDrawBoard.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.ShowClipboardView -> {
            ShowClipboardView.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.EnableViewIdViewer -> {
            EnableViewIdViewer.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.ShowTextFieldDialog -> {
            ShowTextFieldDialog.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .addAllTextFields(textFields)
                .build()
        }

        is Action.TTS -> {
            TTS.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setText(text)
                .build()
        }

        is Action.SetWallpaper -> {
            SetWallpaper.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setFileUrl(url)
                .setCrop(crop)
                .build()
        }

        is Action.SetRuleEnabled -> {
            SetRuleEnabled.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setIsEnable(enableRule)
                .setRuleId(ruleId)
                .build()
        }

        is Action.ClickTile -> {
            ClickTile.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setTile(tile)
                .setIsLongClick(isLongClick)
                .build()
        }

        is Action.MediaPlayback -> {
            MediaPlayback.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setAction(action)
                .build()
        }

        is Action.BiometricVerify -> {
            BiometricVerify.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setTitle(title)
                .setSubTitle(subtitle)
                .addAllAllowActions(allowActions.map { it.toProtoAction().pack_() })
                .addAllDenyActions(denyActions.map { it.toProtoAction().pack_() })
                .build()
        }

        is Action.AppShortcut -> {
            AppShortcut.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setIntentUri(intent.toUri(Intent.URI_INTENT_SCHEME))
                .setLabel(label)
                .build()
        }

        is Action.SetAPMModeEnabled -> {
            SetAPMModeEnabled.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setIsEnable(isEnableAPM)
                .build()
        }

        is Action.SetScreenTimeout -> {
            SetScreenTimeout.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setTimeoutMillis(timeout)
                .build()
        }

        is Action.SetScreenRotate -> {
            SetScreenRotate.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setDegree(degree)
                .build()
        }

        is Action.ScrollViewTo -> {
            ScrollViewTo.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setLocation(location)
                .build()
        }

        is Action.ConnectWifi -> {
            ConnectWifi.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setSsid(ssid)
                .build()
        }

        is Action.DisconnectCurrentWifi -> {
            DisconnectCurrentWifi.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.StayAwake -> {
            StayAwake.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setIsStay(stay)
                .build()
        }

        is Action.Toggle5G -> {
            Toggle5G.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setOnOff(onOff)
                .setSlotId(slotId)
                .build()
        }

        is Action.LockDeviceNow -> {
            LockDeviceNow.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }

        is Action.SetMasterSync -> {
            SetMasterSync.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setSync(sync)
                .build()
        }

        is Action.PlayRingtone -> {
            PlayRingtone.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setRingtone(ringtone)
                .build()
        }

        is Action.DownloadFile -> {
            DownloadFile.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setSubject(subject)
                .build()
        }

        is Action.SendSMS -> {
            SendSMS.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setSlotId(slotId)
                .setMessage(message)
                .setTo(to)
                .build()
        }

        is Action.StopAllActions -> {
            StopAllActions.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .build()
        }


        is Action.NoAction -> {
            NoAction.newBuilder()
                .setId(overrideId ?: id)
                .setIsDisabled(!isEnabled)
                .setActionOnError(actionOnError)
                .setCustomContextDataKey(contextData.toCustomContextDataKey())
                .setNote(overrideNote ?: note)
                .setIcon(icon)
                .build()
        }
    }
}
