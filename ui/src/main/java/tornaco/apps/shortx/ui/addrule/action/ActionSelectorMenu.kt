@file:OptIn(ExperimentalAnimationApi::class)

package tornaco.apps.shortx.ui.addrule.action

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.models.Action
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.dropdown.Dropdown
import tornaco.apps.shortx.ui.base.dropdown.dropDownMenu


val selectableActions = mapOf(
    "UI" to listOf(
        Action.TakeScreenshot::class.java,
        Action.ShowToast::class.java,
    ),
    "App" to listOf(
        Action.LaunchApp::class.java,
    ),
    "Misc" to listOf(
        Action.ShellCommand::class.java,
        Action.Delay::class.java,
        Action.MVEL::class.java,
        Action.ShowOverlay::class.java,
        Action.HideOverlay::class.java
    )
)

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
            icon(Icons.Filled.Colorize)

            item(
                Action.TakeScreenshot::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.TakeScreenshot::class.java)
            ) {
                icon(Icons.Filled.Devices)
            }

            item(
                Action.ShowToast::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.ShowToast::class.java)
            ) {
                icon(Icons.Filled.Message)
            }
        }

        item(
            Action.LaunchApp::class.java,
            labelAndDescriptionForActionSelector(i18N, Action.LaunchApp::class.java)
        ) {
            icon(Icons.Filled.OpenInNew)
        }

        item(
            Action.ShellCommand::class.java,
            "Misc" to "Ext features"
        ) {
            icon(Icons.Filled.Extension)

            item(
                Action.ShellCommand::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.ShellCommand::class.java)
            ) {
                icon(Icons.Filled.Tag)
            }

            item(
                Action.Delay::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.Delay::class.java)
            ) {
                icon(Icons.Filled.Timer)
            }

            item(
                Action.MVEL::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.MVEL::class.java)
            ) {
                icon(Icons.Filled.Code)
            }

            item(
                Action.ShowOverlay::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.ShowOverlay::class.java)
            ) {
                icon(Icons.Filled.Window)
            }

            item(
                Action.HideOverlay::class.java,
                labelAndDescriptionForActionSelector(i18N, Action.HideOverlay::class.java)
            ) {
                icon(Icons.Filled.Window)
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