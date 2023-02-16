package tornaco.apps.shortx.ui.addrule.action.model

import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.ShortXManager
import tornaco.apps.shortx.core.proto.action.*
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.addrule.protoPkgsAndPkgSetsToModels

sealed interface Action {
    object TakeScreenshot : Action
    data class ShellCommand(val command: String) : Action
    data class Delay(val time: Long) : Action
    data class ShowToast(val message: String) : Action
    data class MVEL(val expression: String) : Action
    data class ShowOverlay(val autoCollapse: Boolean, val settings: List<OverlayButtonSetting>) :
        Action

    object HideOverlay : Action

    data class LaunchApp(val appPkg: AppPkg, val appLabel: String) : Action
    data class StopApp(
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action

    data class StartAppProcess(
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action

    data class EnableApp(
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action

    data class DisableApp(
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action

    data class SuspendApp(
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action

    data class UnsuspendApp(
        val apps: List<App> = emptyList(),
        val pkgSets: List<PkgSet> = emptyList()
    ) : Action

    object EnableBT : Action
    object EnableWifi : Action
    object EnableHotSpot : Action
    object EnableData : Action
    object EnableNFC : Action
    object EnableFlashLight : Action
    object EnableDND : Action
    object EnableDarkMode : Action

    object DisableBT : Action
    object DisableWifi : Action
    object DisableHotSpot : Action
    object DisableData : Action
    object DisableNFC : Action
    object DisableFlashLight : Action
    object DisableDND : Action
    object DisableDarkMode : Action
}

fun Action.toProtoAction(): ProtoMessage {
    return when (this) {
        is Action.TakeScreenshot -> {
            TakeScreenshot.newBuilder().build()
        }
        is Action.ShellCommand -> {
            ShellCommand.newBuilder().setCommand(command).build()
        }
        is Action.Delay -> {
            Delay.newBuilder().setTime(time).build()
        }
        is Action.ShowToast -> {
            ShowToast.newBuilder().setMessage(message).build()
        }
        is Action.MVEL -> {
            ExecuteMVEL.newBuilder().setExpression(expression).build()
        }
        Action.HideOverlay -> {
            HideOverlayButton.newBuilder().build()
        }
        is Action.ShowOverlay -> {
            ShowOverlayButton.newBuilder().setAutoCollapse(autoCollapse)
                .addAllButtonSettings(settings).build()
        }
        is Action.LaunchApp -> {
            LaunchApp.newBuilder().setAppPkg(appPkg).build()
        }
        Action.DisableBT -> SetBTEnabled.newBuilder().setEnable(false).build()
        Action.DisableDND -> SetDNDEnabled.newBuilder().setEnable(false).build()
        Action.DisableDarkMode -> SetDarkModeEnabled.newBuilder().setEnable(false).build()
        Action.DisableData -> SetDataEnabled.newBuilder().setEnable(false).build()
        Action.DisableFlashLight -> SetFlashLightEnabled.newBuilder().setEnable(false).build()
        Action.DisableHotSpot -> SetHotSpotEnabled.newBuilder().setEnable(false).build()
        Action.DisableNFC -> SetNFCEnabled.newBuilder().setEnable(false).build()
        Action.DisableWifi -> SetWifiEnabled.newBuilder().setEnable(false).build()

        Action.EnableBT -> SetBTEnabled.newBuilder().setEnable(true).build()
        Action.EnableDND -> SetDNDEnabled.newBuilder().setEnable(true).build()
        Action.EnableDarkMode -> SetDarkModeEnabled.newBuilder().setEnable(true).build()
        Action.EnableData -> SetDataEnabled.newBuilder().setEnable(true).build()
        Action.EnableFlashLight -> SetFlashLightEnabled.newBuilder().setEnable(true).build()
        Action.EnableHotSpot -> SetHotSpotEnabled.newBuilder().setEnable(true).build()
        Action.EnableNFC -> SetNFCEnabled.newBuilder().setEnable(true).build()
        Action.EnableWifi -> SetWifiEnabled.newBuilder().setEnable(true).build()

        is Action.DisableApp -> {
            SetAppEnabled.newBuilder().setEnable(false)
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
        is Action.EnableApp -> {
            SetAppEnabled.newBuilder().setEnable(true)
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
        is Action.StartAppProcess -> {
            StartAppProcess.newBuilder()
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
        is Action.StopApp -> {
            StopApp.newBuilder()
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
        is Action.SuspendApp -> {
            SetAppSuspend.newBuilder().setSuspend(true)
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
        is Action.UnsuspendApp -> {
            SetAppSuspend.newBuilder().setSuspend(false)
                .addAllPkgSets(pkgSets.map { it.id }).addAllAppPkg(apps.map { it.pkg }).build()
        }
    }
}

fun ProtoAny.toAction(shortXManager: ShortXManager): Action? {
    return when {
        this is_ TakeScreenshot::class.java -> {
            Action.TakeScreenshot
        }
        this is_ ShellCommand::class.java -> {
            val data = this unpack_ ShellCommand::class.java
            Action.ShellCommand(command = data.command)
        }
        this is_ ShowToast::class.java -> {
            val data = this unpack_ ShowToast::class.java
            Action.ShowToast(message = data.message)
        }
        this is_ Delay::class.java -> {
            val data = this unpack_ Delay::class.java
            Action.Delay(time = data.time)
        }
        this is_ ExecuteMVEL::class.java -> {
            val data = this unpack_ ExecuteMVEL::class.java
            Action.MVEL(expression = data.expression)
        }
        this is_ ShowOverlayButton::class.java -> {
            val data = this unpack_ ShowOverlayButton::class.java
            Action.ShowOverlay(autoCollapse = data.autoCollapse, settings = data.buttonSettingsList)
        }
        this is_ HideOverlayButton::class.java -> {
            Action.HideOverlay
        }

        this is_ LaunchApp::class.java -> {
            val data = this unpack_ LaunchApp::class.java
            val pkg = Pkg(data.appPkg.pkgName, userId = data.appPkg.userId)
            val appLabel = shortXManager.getAppLabel(pkg)
            Action.LaunchApp(data.appPkg, appLabel = appLabel)
        }

        this is_ SetWifiEnabled::class.java -> {
            val data = this unpack_ SetWifiEnabled::class.java
            if (data.enable) {
                Action.EnableWifi
            } else {
                Action.DisableWifi
            }
        }
        this is_ SetBTEnabled::class.java -> {
            val data = this unpack_ SetBTEnabled::class.java
            if (data.enable) {
                Action.EnableBT
            } else {
                Action.DisableBT
            }
        }
        this is_ SetNFCEnabled::class.java -> {
            val data = this unpack_ SetNFCEnabled::class.java
            if (data.enable) {
                Action.EnableNFC
            } else {
                Action.DisableNFC
            }
        }
        this is_ SetDNDEnabled::class.java -> {
            val data = this unpack_ SetDNDEnabled::class.java
            if (data.enable) {
                Action.EnableDND
            } else {
                Action.DisableDND
            }
        }
        this is_ SetDarkModeEnabled::class.java -> {
            val data = this unpack_ SetDarkModeEnabled::class.java
            if (data.enable) {
                Action.EnableDarkMode
            } else {
                Action.DisableDarkMode
            }
        }
        this is_ SetFlashLightEnabled::class.java -> {
            val data = this unpack_ SetFlashLightEnabled::class.java
            if (data.enable) {
                Action.EnableFlashLight
            } else {
                Action.DisableFlashLight
            }
        }
        this is_ SetHotSpotEnabled::class.java -> {
            val data = this unpack_ SetHotSpotEnabled::class.java
            if (data.enable) {
                Action.EnableHotSpot
            } else {
                Action.DisableHotSpot
            }
        }

        this is_ StopApp::class.java -> {
            val data = this unpack_ StopApp::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.StopApp(
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ StartAppProcess::class.java -> {
            val data = this unpack_ StartAppProcess::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            Action.StartAppProcess(
                apps = models.first,
                pkgSets = models.second
            )
        }

        this is_ SetAppEnabled::class.java -> {
            val data = this unpack_ SetAppEnabled::class.java
            val models = (data.appPkgList to data.pkgSetsList).protoPkgsAndPkgSetsToModels()
            data.enable.select(
                Action.EnableApp(
                    apps = models.first,
                    pkgSets = models.second
                ),
                Action.DisableApp(
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
                    apps = models.first,
                    pkgSets = models.second
                ),
                Action.UnsuspendApp(
                    apps = models.first,
                    pkgSets = models.second
                )
            )
        }

        else -> null
    }
}