@file:OptIn(ExperimentalAnimationApi::class)

package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.base.I18N
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.dropdown.DropDownMenuBuilder
import tornaco.apps.shortx.ui.base.dropdown.Dropdown
import tornaco.apps.shortx.ui.base.dropdown.dropDownMenu
import kotlin.reflect.KClass

@Composable
fun ActionSelectorMenu(
    isOpen: Boolean = false,
    setIsOpen: (Boolean) -> Unit,
    itemSelected: (KClass<out Action>) -> Unit
) {
    val i18N = LocalI18N.current
    val menu = dropDownMenu {
        item(
            Action.ShowToast::class,
            "UI"
        ) {
            icon(Icons.Outlined.Preview)

            item(
                Action.TakeScreenshot::class,
                labelAndDescriptionForActionSelector(i18N, Action.TakeScreenshot::class)
            ) {
                icon(imageVectorForAction(Action.TakeScreenshot::class))
            }

            item(
                Action.ShowToast::class,
                labelAndDescriptionForActionSelector(i18N, Action.ShowToast::class)
            ) {
                icon(imageVectorForAction(Action.ShowToast::class))
            }
        }

        appItems(i18N)

        item(
            Action.ShellCommand::class,
            i18N["ui.action.advanced"]
        ) {
            icon(Icons.Outlined.Extension)

            item(
                Action.ShellCommand::class,
                labelAndDescriptionForActionSelector(i18N, Action.ShellCommand::class)
            ) {
                icon(imageVectorForAction(Action.ShellCommand::class))
            }

            item(
                Action.Delay::class,
                labelAndDescriptionForActionSelector(i18N, Action.Delay::class)
            ) {
                icon(imageVectorForAction(Action.Delay::class))
            }

            item(
                Action.MVEL::class,
                labelAndDescriptionForActionSelector(i18N, Action.MVEL::class)
            ) {
                icon(imageVectorForAction(Action.MVEL::class))
            }

            item(
                Action.ShowOverlay::class,
                labelAndDescriptionForActionSelector(i18N, Action.ShowOverlay::class)
            ) {
                icon(imageVectorForAction(Action.ShowOverlay::class))
            }

            item(
                Action.HideOverlay::class,
                labelAndDescriptionForActionSelector(i18N, Action.HideOverlay::class)
            ) {
                icon(imageVectorForAction(Action.HideOverlay::class))
            }
        }

        item(
            Action.EnableWifi::class,
            i18N["ui.action.system.settings"]
        ) {
            icon(Icons.Outlined.ToggleOn)
            systemSettingsItems(i18N)
        }
    }

    Dropdown(
        isOpen = isOpen,
        menu = menu,
        onItemSelected = itemSelected,
        onDismiss = { setIsOpen(false) },
    )
}

private fun DropDownMenuBuilder<KClass<out Action>>.appItems(
    i18N: I18N
) {
    item(
        Action.LaunchApp::class,
        i18N["ui.action.category.app"]
    ) {
        icon(Icons.Outlined.Apps)

        arrayOf(
            Action.LaunchApp::class,
            Action.StopApp::class,
            Action.StartAppProcess::class,
            Action.EnableApp::class,
            Action.DisableApp::class,
            Action.SuspendApp::class,
            Action.UnsuspendApp::class,
        ).forEach {
            item(
                it,
                labelAndDescriptionForActionSelector(i18N, it)
            ) {
                icon(imageVectorForAction(it))
            }
        }
    }
}

private fun DropDownMenuBuilder<KClass<out Action>>.systemSettingsItems(
    i18N: I18N
) {
    item(
        Action.EnableWifi::class,
        i18N["ui.action.wifi"]
    ) {
        icon(Icons.Outlined.Wifi)

        item(
            Action.EnableWifi::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableWifi::class)
        ) {
            icon(imageVectorForAction(Action.EnableWifi::class))
        }

        item(
            Action.DisableWifi::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableWifi::class)
        ) {
            icon(imageVectorForAction(Action.DisableWifi::class))
        }
    }

    item(
        Action.EnableBT::class,
        i18N["ui.action.bt"]
    ) {
        icon(Icons.Outlined.Bluetooth)

        item(
            Action.EnableBT::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableBT::class)
        ) {
            icon(imageVectorForAction(Action.EnableBT::class))
        }

        item(
            Action.DisableBT::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableBT::class)
        ) {
            icon(imageVectorForAction(Action.DisableBT::class))
        }
    }


    item(
        Action.EnableData::class,
        i18N["ui.action.data"]
    ) {
        icon(Icons.Outlined.SignalCellular0Bar)

        item(
            Action.EnableData::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableData::class)
        ) {
            icon(imageVectorForAction(Action.EnableData::class))
        }

        item(
            Action.DisableData::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableData::class)
        ) {
            icon(imageVectorForAction(Action.DisableData::class))
        }
    }

    item(
        Action.EnableHotSpot::class,
        i18N["ui.action.hotspot"]
    ) {
        icon(Icons.Outlined.WifiTethering)

        item(
            Action.EnableHotSpot::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableHotSpot::class)
        ) {
            icon(imageVectorForAction(Action.EnableHotSpot::class))
        }

        item(
            Action.DisableHotSpot::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableHotSpot::class)
        ) {
            icon(imageVectorForAction(Action.DisableHotSpot::class))
        }
    }

    item(
        Action.EnableNFC::class,
        i18N["ui.action.nfc"]
    ) {
        icon(Icons.Outlined.Nfc)

        item(
            Action.EnableNFC::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableNFC::class)
        ) {
            icon(imageVectorForAction(Action.EnableNFC::class))
        }

        item(
            Action.DisableNFC::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableNFC::class)
        ) {
            icon(imageVectorForAction(Action.DisableNFC::class))
        }
    }

    item(
        Action.EnableDND::class,
        i18N["ui.action.dnd"]
    ) {
        icon(Icons.Outlined.DoNotDisturb)

        item(
            Action.EnableDND::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableDND::class)
        ) {
            icon(imageVectorForAction(Action.EnableDND::class))
        }

        item(
            Action.DisableDND::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableDND::class)
        ) {
            icon(imageVectorForAction(Action.DisableDND::class))
        }
    }

    item(
        Action.EnableFlashLight::class,
        i18N["ui.action.flashlight"]
    ) {
        icon(Icons.Outlined.FlashlightOn)

        item(
            Action.EnableFlashLight::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableFlashLight::class)
        ) {
            icon(imageVectorForAction(Action.EnableFlashLight::class))
        }

        item(
            Action.DisableFlashLight::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableFlashLight::class)
        ) {
            icon(imageVectorForAction(Action.DisableFlashLight::class))
        }
    }

    item(
        Action.EnableDarkMode::class,
        i18N["ui.action.dark.mode"]
    ) {
        icon(Icons.Outlined.DarkMode)

        item(
            Action.EnableDarkMode::class,
            labelAndDescriptionForActionSelector(i18N, Action.EnableDarkMode::class)
        ) {
            icon(imageVectorForAction(Action.EnableDarkMode::class))
        }

        item(
            Action.DisableDarkMode::class,
            labelAndDescriptionForActionSelector(i18N, Action.DisableDarkMode::class)
        ) {
            icon(imageVectorForAction(Action.DisableDarkMode::class))
        }
    }
}