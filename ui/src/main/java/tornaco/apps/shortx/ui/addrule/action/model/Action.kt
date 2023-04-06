package tornaco.apps.shortx.ui.addrule.action.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import lang3.RandomStringUtils
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.action.*
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.condition.ConditionOperatorPayload
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.rule.*
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.ContextData
import tornaco.apps.shortx.ui.addrule.actionContextData
import tornaco.apps.shortx.ui.addrule.condition.model.Condition
import tornaco.apps.shortx.ui.addrule.condition.model.toCondition
import tornaco.apps.shortx.ui.addrule.condition.model.toProtoCondition
import tornaco.apps.shortx.ui.addrule.condition.op.Op
import tornaco.apps.shortx.ui.addrule.condition.op.toConditionOperator
import tornaco.apps.shortx.ui.addrule.condition.op.toOp
import tornaco.apps.shortx.ui.addrule.protoPkgsAndPkgSetsToModels
import tornaco.apps.shortx.ui.remote.RemoteExecuteMVELAction
import tornaco.apps.shortx.ui.remote.toModel
import tornaco.apps.shortx.ui.remote.toProto
import java.util.*

fun defaultNewActionId() = UUID.randomUUID().toString()

sealed interface Action : Parcelable {
    val id: String

    @Parcelize
    data class TakeScreenshot(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class ShellCommand(override val id: String = defaultNewActionId(), val command: String) :
        Action

    @Parcelize
    data class InputText(override val id: String = defaultNewActionId(), val text: String) : Action

    @Parcelize
    data class Delay(override val id: String = defaultNewActionId(), val time: Long) : Action

    @Parcelize
    data class ShowToast(override val id: String = defaultNewActionId(), val message: String) :
        Action

    @Parcelize
    data class ShowDanmu(
        override val id: String = defaultNewActionId(),
        val text: String,
        val icon: String?
    ) : Action

    @Parcelize
    data class MVEL(override val id: String = defaultNewActionId(), val expression: String) : Action

    @Parcelize
    data class RemoteMVEL(
        override val id: String = defaultNewActionId(),
        val action: RemoteExecuteMVELAction
    ) : Action

    @Parcelize
    data class ShowOverlay(
        override val id: String = defaultNewActionId(),
        val autoCollapse: Boolean,
        val settings: List<OverlayButtonSetting>,
        val tag: String = RandomStringUtils.randomAlphabetic(8).lowercase()
    ) : Action

    @Parcelize
    data class HideAllOverlay(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class HideOverlay(
        override val id: String = defaultNewActionId(),
        val tags: List<String>
    ) : Action

    @Parcelize
    data class LaunchApp(
        override val id: String = defaultNewActionId(),
        val appPkg: AppPkg,
        val appLabel: String
    ) : Action

    @Parcelize
    data class StopApp(
        override val id: String = defaultNewActionId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action


    @Parcelize
    data class StartActivity(
        override val id: String = defaultNewActionId(),
        val componentNameAsString: String
    ) : Action

    @Parcelize
    data class ExpandNotification(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class StartAppProcess(
        override val id: String = defaultNewActionId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action

    @Parcelize
    data class EnableApp(
        override val id: String = defaultNewActionId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action

    @Parcelize
    data class DisableApp(
        override val id: String = defaultNewActionId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action

    @Parcelize
    data class SuspendApp(
        override val id: String = defaultNewActionId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action

    @Parcelize
    data class UnSuspendApp(
        override val id: String = defaultNewActionId(),
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action

    @Parcelize
    data class EnableBT(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class EnableWifi(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class EnableHotSpot(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class EnableData(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class EnableNFC(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class EnableFlashLight(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class EnableDND(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class EnableGPS(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class EnableDarkMode(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class DisableBT(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class DisableWifi(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class DisableHotSpot(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class DisableData(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class DisableNFC(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class DisableFlashLight(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class DisableDND(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class DisableGPS(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class DisableDarkMode(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class Brk(override val id: String = defaultNewActionId()) : Action

    @Parcelize
    data class WhileLoop(
        override val id: String = defaultNewActionId(),
        val conditions: List<Condition>,
        val actions: List<Action>,
        val delay: Long,
        val repeat: Int,
        val op: Op
    ) : Action

    @Parcelize
    data class ShowAlertDialog(
        override val id: String = defaultNewActionId(),
        val title: String,
        val message: String,
        val cancelable: Boolean = true,
        val positiveText: String? = null,
        val negativeText: String? = null,
        val neutralText: String? = null,
        val positiveActions: List<Action> = emptyList(),
        val negativeActions: List<Action> = emptyList(),
        val neutralActions: List<Action> = emptyList(),
    ) : Action

    @Parcelize
    data class WriteGV(
        override val id: String = defaultNewActionId(),
        val valueAsString: String,
        val varId: String,
        val varName: String
    ) : Action
}

fun Action.toProtoAction(): ProtoMessage {
    return when (this) {
        is Action.TakeScreenshot -> {
            TakeScreenshot.newBuilder().setId(id).build()
        }
        is Action.ShellCommand -> {
            ShellCommand.newBuilder().setId(id).setCommand(command).build()
        }
        is Action.InputText -> {
            InputText.newBuilder().setId(id).setText(text).build()
        }
        is Action.Delay -> {
            Delay.newBuilder().setId(id).setTime(time).build()
        }
        is Action.ShowToast -> {
            ShowToast.newBuilder().setId(id).setMessage(message).build()
        }
        is Action.ShowDanmu -> {
            ShowDanmu.newBuilder().setId(id).setText(text).apply {
                this@toProtoAction.icon?.let {
                    icon = it
                }
            }.build()
        }
        is Action.MVEL -> {
            ExecuteMVEL.newBuilder().setId(id).setExpression(expression).build()
        }
        is Action.RemoteMVEL -> {
            action.toProto()
        }
        is Action.HideAllOverlay -> {
            HideOverlayButton.newBuilder().setId(id).build()
        }
        is Action.HideOverlay -> {
            HideOverlayButton.newBuilder().setId(id).addAllOverlayTags(tags).build()
        }
        is Action.ShowOverlay -> {
            ShowOverlayButton.newBuilder().setId(id).setAutoCollapse(autoCollapse)
                .addAllButtonSettings(settings).build()
        }
        is Action.LaunchApp -> {
            LaunchApp.newBuilder().setId(id).setAppPkg(appPkg).build()
        }
        is Action.DisableBT -> SetBTEnabled.newBuilder().setId(id).setEnable(false).build()
        is Action.DisableDND -> SetDNDEnabled.newBuilder().setId(id).setEnable(false).build()
        is Action.DisableGPS -> SetGPSEnabled.newBuilder().setId(id).setEnable(false).build()
        is Action.DisableDarkMode -> SetDarkModeEnabled.newBuilder().setId(id).setEnable(false)
            .build()
        is Action.DisableData -> SetDataEnabled.newBuilder().setId(id).setEnable(false).build()
        is Action.DisableFlashLight -> SetFlashLightEnabled.newBuilder().setId(id).setEnable(false)
            .build()
        is Action.DisableHotSpot -> SetHotSpotEnabled.newBuilder().setId(id).setEnable(false)
            .build()
        is Action.DisableNFC -> SetNFCEnabled.newBuilder().setId(id).setEnable(false).build()
        is Action.DisableWifi -> SetWifiEnabled.newBuilder().setId(id).setEnable(false).build()

        is Action.EnableBT -> SetBTEnabled.newBuilder().setId(id).setEnable(true).build()
        is Action.EnableDND -> SetDNDEnabled.newBuilder().setId(id).setEnable(true).build()
        is Action.EnableGPS -> SetGPSEnabled.newBuilder().setId(id).setEnable(true).build()
        is Action.EnableDarkMode -> SetDarkModeEnabled.newBuilder().setId(id).setEnable(true)
            .build()
        is Action.EnableData -> SetDataEnabled.newBuilder().setId(id).setEnable(true).build()
        is Action.EnableFlashLight -> SetFlashLightEnabled.newBuilder().setId(id).setEnable(true)
            .build()
        is Action.EnableHotSpot -> SetHotSpotEnabled.newBuilder().setId(id).setEnable(true).build()
        is Action.EnableNFC -> SetNFCEnabled.newBuilder().setId(id).setEnable(true).build()
        is Action.EnableWifi -> SetWifiEnabled.newBuilder().setId(id).setEnable(true).build()

        is Action.DisableApp -> {
            SetAppEnabled.newBuilder().setId(id).setEnable(false)
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
        is Action.EnableApp -> {
            SetAppEnabled.newBuilder().setId(id).setEnable(true)
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
        is Action.StartAppProcess -> {
            StartAppProcess.newBuilder().setId(id)
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
        is Action.StopApp -> {
            StopApp.newBuilder().setId(id)
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
        is Action.SuspendApp -> {
            SetAppSuspend.newBuilder().setId(id).setSuspend(true)
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
        is Action.UnSuspendApp -> {
            SetAppSuspend.newBuilder().setId(id).setSuspend(false)
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
        is Action.Brk -> {
            BreakActionExecute.newBuilder().setId(id).build()
        }
        is Action.ShowAlertDialog -> {
            ShowAlertDialog.newBuilder().setId(id)
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
                .build()
        }
        is Action.StartActivity -> {
            StartActivity.newBuilder().setId(id).setComponentNameAsString(componentNameAsString)
                .build()
        }
        is Action.ExpandNotification -> {
            ExpandNotification.newBuilder().setId(id).build()
        }
        is Action.WhileLoop -> {
            WhileLoop.newBuilder().setId(id)
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
                .build()
        }
        is Action.WriteGV -> {
            WriteGlobalVar.newBuilder()
                .setId(id)
                .setVarId(varId)
                .setValueAsString(valueAsString)
                .build()
        }
    }
}

fun ProtoAny.toAction(shortXManager: ShortXManager): Action? {
    return when {
        this is_ TakeScreenshot::class.java -> {
            val data = this unpack_ TakeScreenshot::class.java
            Action.TakeScreenshot(data.id)
        }
        this is_ ShellCommand::class.java -> {
            val data = this unpack_ ShellCommand::class.java
            Action.ShellCommand(id = data.id, command = data.command)
        }
        this is_ InputText::class.java -> {
            val data = this unpack_ InputText::class.java
            Action.InputText(id = data.id, text = data.text)
        }
        this is_ ShowToast::class.java -> {
            val data = this unpack_ ShowToast::class.java
            Action.ShowToast(id = data.id, message = data.message)
        }
        this is_ ShowDanmu::class.java -> {
            val data = this unpack_ ShowDanmu::class.java
            Action.ShowDanmu(id = data.id, text = data.text, icon = data.icon)
        }
        this is_ Delay::class.java -> {
            val data = this unpack_ Delay::class.java
            Action.Delay(id = data.id, time = data.time)
        }
        this is_ ExecuteMVEL::class.java -> {
            val data = this unpack_ ExecuteMVEL::class.java
            Action.MVEL(id = data.id, expression = data.expression)
        }
        this is_ RemoteExecuteMVEL::class.java -> {
            val data = this unpack_ RemoteExecuteMVEL::class.java
            Action.RemoteMVEL(id = data.id, action = data.toModel())
        }
        this is_ ShowOverlayButton::class.java -> {
            val data = this unpack_ ShowOverlayButton::class.java
            Action.ShowOverlay(
                id = data.id,
                autoCollapse = data.autoCollapse,
                settings = data.buttonSettingsList
            )
        }
        this is_ HideOverlayButton::class.java -> {
            val data = this unpack_ HideOverlayButton::class.java
            if (data.overlayTagsList.isEmpty()) {
                Action.HideAllOverlay(id = data.id)
            } else {
                Action.HideOverlay(
                    id = data.id,
                    tags = data.overlayTagsList
                )
            }
        }

        this is_ LaunchApp::class.java -> {
            val data = this unpack_ LaunchApp::class.java
            val pkg = Pkg(data.appPkg.pkgName, userId = data.appPkg.userId)
            val appLabel = shortXManager.getAppLabel(pkg)
            Action.LaunchApp(id = data.id, appPkg = data.appPkg, appLabel = appLabel)
        }

        this is_ SetWifiEnabled::class.java -> {
            val data = this unpack_ SetWifiEnabled::class.java
            if (data.enable) {
                Action.EnableWifi(id = data.id)
            } else {
                Action.DisableWifi(id = data.id)
            }
        }
        this is_ SetBTEnabled::class.java -> {
            val data = this unpack_ SetBTEnabled::class.java
            if (data.enable) {
                Action.EnableBT(id = data.id)
            } else {
                Action.DisableBT(id = data.id)
            }
        }
        this is_ SetNFCEnabled::class.java -> {
            val data = this unpack_ SetNFCEnabled::class.java
            if (data.enable) {
                Action.EnableNFC(id = data.id)
            } else {
                Action.DisableNFC(id = data.id)
            }
        }
        this is_ SetDNDEnabled::class.java -> {
            val data = this unpack_ SetDNDEnabled::class.java
            if (data.enable) {
                Action.EnableDND(id = data.id)
            } else {
                Action.DisableDND(id = data.id)
            }
        }
        this is_ SetGPSEnabled::class.java -> {
            val data = this unpack_ SetGPSEnabled::class.java
            if (data.enable) {
                Action.EnableGPS(id = data.id)
            } else {
                Action.DisableGPS(id = data.id)
            }
        }
        this is_ SetDataEnabled::class.java -> {
            val data = this unpack_ SetDataEnabled::class.java
            if (data.enable) {
                Action.EnableData(id = data.id)
            } else {
                Action.DisableData(id = data.id)
            }
        }
        this is_ SetDarkModeEnabled::class.java -> {
            val data = this unpack_ SetDarkModeEnabled::class.java
            if (data.enable) {
                Action.EnableDarkMode(id = data.id)
            } else {
                Action.DisableDarkMode(id = data.id)
            }
        }
        this is_ SetFlashLightEnabled::class.java -> {
            val data = this unpack_ SetFlashLightEnabled::class.java
            if (data.enable) {
                Action.EnableFlashLight(id = data.id)
            } else {
                Action.DisableFlashLight(id = data.id)
            }
        }
        this is_ SetHotSpotEnabled::class.java -> {
            val data = this unpack_ SetHotSpotEnabled::class.java
            if (data.enable) {
                Action.EnableHotSpot(id = data.id)
            } else {
                Action.DisableHotSpot(id = data.id)
            }
        }

        this is_ StopApp::class.java -> {
            val data = this unpack_ StopApp::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.StopApp(
                id = data.id,
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ StartAppProcess::class.java -> {
            val data = this unpack_ StartAppProcess::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.StartAppProcess(
                id = data.id,
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ SetAppEnabled::class.java -> {
            val data = this unpack_ SetAppEnabled::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            data.enable.select(
                Action.EnableApp(
                    id = data.id,
                    apps = models.first,
                    pkgSets = models.second
                ),
                Action.DisableApp(
                    id = data.id,
                    apps = models.first,
                    pkgSets = models.second
                )
            )
        }

        this is_ SetAppSuspend::class.java -> {
            val data = this unpack_ SetAppSuspend::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            data.suspend.select(
                Action.SuspendApp(
                    id = data.id,
                    apps = models.first,
                    pkgSets = models.second
                ),
                Action.UnSuspendApp(
                    id = data.id,
                    apps = models.first,
                    pkgSets = models.second
                )
            )
        }

        this is_ BreakActionExecute::class.java -> {
            val data = this unpack_ BreakActionExecute::class.java
            Action.Brk(id = data.id)
        }

        this is_ ShowAlertDialog::class.java -> {
            val data = this unpack_ ShowAlertDialog::class.java
            Action.ShowAlertDialog(
                id = data.id,
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

        this is_ StartActivity::class.java -> {
            val data = this unpack_ StartActivity::class.java
            Action.StartActivity(id = data.id, data.componentNameAsString)
        }
        this is_ ExpandNotification::class.java -> {
            val data = this unpack_ ExpandNotification::class.java
            Action.ExpandNotification(id = data.id)
        }
        this is_ WhileLoop::class.java -> {
            val data = this unpack_ WhileLoop::class.java
            Action.WhileLoop(
                id = data.id,
                delay = data.delay.toLong(),
                repeat = data.repeatTimes,
                conditions = data.conditionsList.mapNotNull { it.toCondition(shortXManager) },
                actions = data.actionsList.mapNotNull { it.toAction(shortXManager) },
                op = data.condOp.toOp(data.condOpPayload)
            )
        }
        this is_ WriteGlobalVar::class.java -> {
            val data = this unpack_ WriteGlobalVar::class.java
            Action.WriteGV(
                id = data.id,
                varId = data.varId,
                valueAsString = data.valueAsString,
                varName = shortXManager.getGlobalVarById(data.varId)?.name ?: "N/A"
            )
        }

        else -> null
    }
}

val Action.contextData: List<ContextData> get() = this::class.actionContextData()