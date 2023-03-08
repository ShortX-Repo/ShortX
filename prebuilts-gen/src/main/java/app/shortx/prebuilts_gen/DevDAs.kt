package app.shortx.prebuilts_gen

import tornaco.apps.shortx.core.proto.action.*
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.rule.pack_
import java.util.concurrent.atomic.AtomicInteger


object DevDAs {
    private val idGen = AtomicInteger(0)

    private fun nextId() = "DevDA-${idGen.getAndIncrement()}"

    val all = listOf(
        DirectAction.newBuilder().setTitle("NoAction")
            .setId(nextId())
            .addActions(NoAction.newBuilder().build().pack_()).build(),
        DirectAction.newBuilder().setTitle("TakeScreenshot")
            .setId(nextId())
            .addActions(TakeScreenshot.newBuilder().build().pack_()).build(),
        DirectAction.newBuilder().setTitle("ExpandNotification")
            .setId(nextId())
            .addActions(ExpandNotification.newBuilder().build().pack_()).build(),
        DirectAction.newBuilder().setTitle("StartActivity")
            .setId(nextId())
            .addActions(
                StartActivity.newBuilder()
                    .setComponentNameAsString("com.android.settings/.Settings").build().pack_()
            ).build(),

        DirectAction.newBuilder().setTitle("ShellCommand")
            .setId(nextId())
            .addActions(ShellCommand.newBuilder().setCommand("input keyevent 4").build().pack_())
            .build(),

        DirectAction.newBuilder().setTitle("InputText")
            .setId(nextId())
            .addActions(InputText.newBuilder().setText("Hello world.").build().pack_()).build(),

        DirectAction.newBuilder().setTitle("ShowToast")
            .setId(nextId())
            .addActions(ShowToast.newBuilder().setMessage("Hello how are you~").build().pack_())
            .build(),

        DirectAction.newBuilder().setTitle("Delay")
            .setId(nextId())
            .addActions(Delay.newBuilder().build().pack_()).build(),

        DirectAction.newBuilder().setTitle("ExecuteMVEL")
            .setId(nextId())
            .addActions(
                ExecuteMVEL.newBuilder().setExpression("ui.showShortToast(\"TOAST EXAMPLE\")")
                    .build().pack_()
            ).build(),

        DirectAction.newBuilder().setTitle("HideOverlayButton")
            .setId(nextId())
            .addActions(HideOverlayButton.newBuilder().build().pack_()).build(),

        DirectAction.newBuilder().setTitle("ShowOverlayButton")
            .setId(nextId())
            .addActions(
                ShowOverlayButton.newBuilder()
                    .addAllButtonSettings(
                        listOf(
                            OverlayButtonSetting.newBuilder()
                                .setLabel("Button1")
                                .setIcon("Apps")
                                .addActions(
                                    ShowToast.newBuilder().setMessage("Button clicked.").build()
                                        .pack_()
                                )
                                .build()
                        )
                    ).build()
                    .pack_()
            ).build(),

        DirectAction.newBuilder().setTitle("LaunchApp")
            .setId(nextId())
            .addActions(
                LaunchApp.newBuilder().setAppPkg(
                    AppPkg.newBuilder().setPkgName("com.android.settings").setUserId(0).build()
                ).build().pack_()
            ).build(),

        DirectAction.newBuilder().setTitle("StopApp")
            .setId(nextId())
            .addActions(
                StopApp.newBuilder().addAppPkg(
                    AppPkg.newBuilder().setPkgName("com.android.settings").setUserId(0).build()
                ).build().pack_()
            ).build(),


        DirectAction.newBuilder().setTitle("SetAppEnabled-true")
            .setId(nextId())
            .addActions(
                SetAppEnabled.newBuilder()
                    .setEnable(true)
                    .addAppPkg(AppPkg.newBuilder().setUserId(0).setPkgName("com.android.camera"))
                    .build().pack_()
            ).build(),

        DirectAction.newBuilder().setTitle("SetAppEnabled-false")
            .setId(nextId())
            .addActions(
                SetAppEnabled.newBuilder()
                    .setEnable(false)
                    .addAppPkg(AppPkg.newBuilder().setUserId(0).setPkgName("com.android.camera"))
                    .build().pack_()
            ).build(),

        DirectAction.newBuilder().setTitle("SetAppSuspend-true")
            .setId(nextId())
            .addActions(
                SetAppSuspend.newBuilder()
                    .setSuspend(true)
                    .addAppPkg(AppPkg.newBuilder().setUserId(0).setPkgName("com.android.camera"))
                    .build().pack_()
            ).build(),
        DirectAction.newBuilder().setTitle("SetAppSuspend-false")
            .setId(nextId())
            .addActions(
                SetAppSuspend.newBuilder()
                    .setSuspend(false)
                    .addAppPkg(AppPkg.newBuilder().setUserId(0).setPkgName("com.android.camera"))
                    .build().pack_()
            ).build(),
        DirectAction.newBuilder().setTitle("StartAppProcess")
            .setId(nextId())
            .addActions(
                StartAppProcess.newBuilder()
                    .addAppPkg(AppPkg.newBuilder().setUserId(0).setPkgName("com.android.camera"))
                    .build().pack_()
            ).build(),


        DirectAction.newBuilder().setTitle("SetWifiEnabled-true")
            .setId(nextId())
            .addActions(SetWifiEnabled.newBuilder().setEnable(true).build().pack_()).build(),
        DirectAction.newBuilder().setTitle("SetWifiEnabled-false")
            .setId(nextId())
            .addActions(SetWifiEnabled.newBuilder().setEnable(false).build().pack_()).build(),

        DirectAction.newBuilder().setTitle("SetBTEnabled-true")
            .setId(nextId())
            .addActions(SetBTEnabled.newBuilder().setEnable(true).build().pack_()).build(),
        DirectAction.newBuilder().setTitle("SetBTEnabled-false")
            .setId(nextId())
            .addActions(SetBTEnabled.newBuilder().setEnable(false).build().pack_()).build(),

        DirectAction.newBuilder().setTitle("SetHotSpotEnabled-true")
            .setId(nextId())
            .addActions(SetHotSpotEnabled.newBuilder().setEnable(true).build().pack_()).build(),
        DirectAction.newBuilder().setTitle("SetHotSpotEnabled-false")
            .setId(nextId())
            .addActions(SetHotSpotEnabled.newBuilder().setEnable(false).build().pack_()).build(),

        DirectAction.newBuilder().setTitle("SetGPSEnabled-true")
            .setId(nextId())
            .addActions(SetGPSEnabled.newBuilder().setEnable(true).build().pack_()).build(),
        DirectAction.newBuilder().setTitle("SetGPSEnabled-false")
            .setId(nextId())
            .addActions(SetGPSEnabled.newBuilder().setEnable(false).build().pack_()).build(),

        DirectAction.newBuilder().setTitle("SetDataEnabled-true")
            .setId(nextId())
            .addActions(SetDataEnabled.newBuilder().setEnable(true).build().pack_()).build(),
        DirectAction.newBuilder().setTitle("SetDataEnabled-false")
            .setId(nextId())
            .addActions(SetDataEnabled.newBuilder().setEnable(false).build().pack_()).build(),

        DirectAction.newBuilder().setTitle("SetNFCEnabled-true")
            .setId(nextId())
            .addActions(SetNFCEnabled.newBuilder().setEnable(true).build().pack_()).build(),
        DirectAction.newBuilder().setTitle("SetNFCEnabled-false")
            .setId(nextId())
            .addActions(SetNFCEnabled.newBuilder().setEnable(false).build().pack_()).build(),

        DirectAction.newBuilder().setTitle("SetDNDEnabled-true")
            .setId(nextId())
            .addActions(SetDNDEnabled.newBuilder().setEnable(true).build().pack_()).build(),
        DirectAction.newBuilder().setTitle("SetDNDEnabled-false")
            .setId(nextId())
            .addActions(SetDNDEnabled.newBuilder().setEnable(false).build().pack_()).build(),

        DirectAction.newBuilder().setTitle("SetDarkModeEnabled-true")
            .setId(nextId())
            .addActions(SetDarkModeEnabled.newBuilder().setEnable(true).build().pack_()).build(),
        DirectAction.newBuilder().setTitle("SetDarkModeEnabled-false")
            .setId(nextId())
            .addActions(SetDarkModeEnabled.newBuilder().setEnable(false).build().pack_()).build(),

        DirectAction.newBuilder().setTitle("SetFlashLightEnabled-true")
            .setId(nextId())
            .addActions(SetFlashLightEnabled.newBuilder().setEnable(true).build().pack_()).build(),
        DirectAction.newBuilder().setTitle("SetFlashLightEnabled-false")
            .setId(nextId())
            .addActions(SetFlashLightEnabled.newBuilder().setEnable(false).build().pack_()).build(),

        DirectAction.newBuilder().setTitle("SetLocationEnabled-true")
            .setId(nextId())
            .addActions(SetLocationEnabled.newBuilder().setEnable(true).build().pack_()).build(),
        DirectAction.newBuilder().setTitle("SetLocationEnabled-false")
            .setId(nextId())
            .addActions(SetLocationEnabled.newBuilder().setEnable(false).build().pack_()).build(),
    )
}