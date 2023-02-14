@file:OptIn(ExperimentalAnimationApi::class)

package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.dropdown.Dropdown
import tornaco.apps.shortx.ui.base.dropdown.dropDownMenu

@Composable
fun ActionSelectorMenu(
    isOpen: Boolean = false, setIsOpen: (Boolean) -> Unit, itemSelected: (Class<out Action>) -> Unit
) {
    val i18N = LocalI18N.current
    val menu = dropDownMenu {
        item(
            Action.ShowToast::class.java,
            "UI" to null
        ) {
            icon(Icons.Outlined.Preview)

            item(
                Action.TakeScreenshot::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.TakeScreenshot::class.java)
            ) {
                icon(imageVectorForAction(Action.TakeScreenshot::class.java))
            }

            item(
                Action.ShowToast::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.ShowToast::class.java)
            ) {
                icon(imageVectorForAction(Action.ShowToast::class.java))
            }
        }

        item(
            Action.LaunchApp::class.java,
            labelAndDescriptionForActionSelector(i18N, Action.LaunchApp::class.java)
        ) {
            icon(imageVectorForAction(Action.LaunchApp::class.java))
        }

        item(
            Action.ShellCommand::class.java,
            "Misc" to "Ext features"
        ) {
            icon(Icons.Outlined.Extension)

            item(
                Action.ShellCommand::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.ShellCommand::class.java)
            ) {
                icon(imageVectorForAction(Action.ShellCommand::class.java))
            }

            item(
                Action.Delay::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.Delay::class.java)
            ) {
                icon(imageVectorForAction(Action.Delay::class.java))
            }

            item(
                Action.MVEL::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.MVEL::class.java)
            ) {
                icon(imageVectorForAction(Action.MVEL::class.java))
            }

            item(
                Action.ShowOverlay::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.ShowOverlay::class.java)
            ) {
                icon(imageVectorForAction(Action.ShowOverlay::class.java))
            }

            item(
                Action.HideOverlay::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.HideOverlay::class.java)
            ) {
                icon(imageVectorForAction(Action.HideOverlay::class.java))
            }
        }

        item(
            Action.EnableWifi::class.java,
            labelAndDescriptionForActionSelector(i18N, Action.EnableWifi::class.java)
        ) {
            icon(Icons.Outlined.Wifi)

            item(
                Action.EnableWifi::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.EnableWifi::class.java)
            ) {
                icon(imageVectorForAction(Action.EnableWifi::class.java))
            }

            item(
                Action.DisableWifi::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.DisableWifi::class.java)
            ) {
                icon(imageVectorForAction(Action.DisableWifi::class.java))
            }
        }

        item(
            Action.EnableBT::class.java,
            labelAndDescriptionForActionSelector(i18N, Action.EnableBT::class.java)
        ) {
            icon(Icons.Outlined.Bluetooth)

            item(
                Action.EnableBT::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.EnableBT::class.java)
            ) {
                icon(imageVectorForAction(Action.EnableBT::class.java))
            }

            item(
                Action.DisableBT::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.DisableBT::class.java)
            ) {
                icon(imageVectorForAction(Action.DisableBT::class.java))
            }
        }


        item(
            Action.EnableData::class.java,
            labelAndDescriptionForActionSelector(i18N, Action.EnableData::class.java)
        ) {
            icon(Icons.Outlined.SignalCellular0Bar)

            item(
                Action.EnableData::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.EnableData::class.java)
            ) {
                icon(imageVectorForAction(Action.EnableData::class.java))
            }

            item(
                Action.DisableData::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.DisableData::class.java)
            ) {
                icon(imageVectorForAction(Action.DisableData::class.java))
            }
        }

        item(
            Action.EnableHotSpot::class.java,
            labelAndDescriptionForActionSelector(i18N, Action.EnableHotSpot::class.java)
        ) {
            icon(Icons.Outlined.WifiTethering)

            item(
                Action.EnableHotSpot::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.EnableHotSpot::class.java)
            ) {
                icon(imageVectorForAction(Action.EnableHotSpot::class.java))
            }

            item(
                Action.DisableHotSpot::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.DisableHotSpot::class.java)
            ) {
                icon(imageVectorForAction(Action.DisableHotSpot::class.java))
            }
        }
    }

    Dropdown(
        isOpen = isOpen,
        menu = menu,
        onItemSelected = itemSelected,
        onDismiss = { setIsOpen(false) },
    )
}