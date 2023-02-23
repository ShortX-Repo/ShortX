@file:OptIn(ExperimentalAnimationApi::class)

package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import tornaco.apps.shortx.ui.addrule.AddRuleState
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.base.dropdown.Dropdown
import tornaco.apps.shortx.ui.base.dropdown.dropDownMenu
import kotlin.reflect.KClass

@Composable
fun FactSelector(state: AddRuleState, selectFact: (KClass<out Fact>) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle(LocalI18N.current["ui.fact.title"])

        Box {
            var showFactSelectorMenu by remember {
                mutableStateOf(false)
            }

            Column {
                state.fact?.let {
                    ListItem(
                        modifier = Modifier,
                        titleLines = labelForFact(fact = it),
                        icon = { Icon(imageVector = imageVectorForFact(it::class)) })
                }

                FilledTonalButton(onClick = {
                    showFactSelectorMenu = true
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings, contentDescription = "Select"
                    )
                    TinySpacer()
                    Text(text = LocalI18N.current["ui.fact.select"])
                }
            }

            FactSelectorMenu(isOpen = showFactSelectorMenu,
                setIsOpen = { showFactSelectorMenu = it }) {
                showFactSelectorMenu = false
                selectFact(it)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun FactSelectorMenu(
    isOpen: Boolean = false, setIsOpen: (Boolean) -> Unit, itemSelected: (KClass<out Fact>) -> Unit
) {
    val i18N = LocalI18N.current
    val menu = dropDownMenu {
        item(
            Fact.KeyEvent::class,
            labelAndDescriptionForFactSelector(i18N, Fact.KeyEvent::class)
        ) {
            icon(imageVectorForFact(Fact.KeyEvent::class))
        }
        item(
            Fact.ActivityStarted::class,
            labelAndDescriptionForFactSelector(i18N, Fact.ActivityStarted::class)
        ) {
            icon(imageVectorForFact(Fact.ActivityStarted::class))
        }
        item(
            Fact.EdgeGesture::class,
            labelAndDescriptionForFactSelector(i18N, Fact.EdgeGesture::class)
        ) {
            icon(imageVectorForFact(Fact.EdgeGesture::class))
        }

        item(Fact.Alarm::class, i18N["ui.fact.screen"] to null) {
            icon(imageVectorForFact(Fact.ScreenOn::class))
            item(
                Fact.ScreenOn::class,
                labelAndDescriptionForFactSelector(i18N, Fact.ScreenOn::class)
            ) {
                icon(imageVectorForFact(Fact.ScreenOn::class))
            }
            item(
                Fact.ScreenOff::class,
                labelAndDescriptionForFactSelector(i18N, Fact.ScreenOff::class)
            ) {
                icon(imageVectorForFact(Fact.ScreenOff::class))
            }
            item(
                Fact.UserPresent::class,
                labelAndDescriptionForFactSelector(i18N, Fact.UserPresent::class)
            ) {
                icon(imageVectorForFact(Fact.UserPresent::class))
            }
        }

        item(
            Fact.Alarm::class, labelAndDescriptionForFactSelector(i18N, Fact.Alarm::class)
        ) {
            icon(imageVectorForFact(Fact.Alarm::class))
        }

        item(Fact.AppBecomeForeground::class, i18N["ui.fact.app.bg.state"] to null) {
            icon(imageVectorForFact(Fact.AppBecomeForeground::class))
            item(
                Fact.AppBecomeForeground::class,
                labelAndDescriptionForFactSelector(i18N, Fact.AppBecomeForeground::class)
            ) {
                icon(imageVectorForFact(Fact.AppBecomeForeground::class))
            }
            item(
                Fact.AppBecomeBackground::class,
                labelAndDescriptionForFactSelector(i18N, Fact.AppBecomeBackground::class)
            ) {
                icon(imageVectorForFact(Fact.AppBecomeBackground::class))
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

private fun imageVectorForFact(fact: KClass<out Fact>): ImageVector {
    return when (fact) {
        Fact.ActivityStarted::class -> Icons.Filled.Update
        Fact.EdgeGesture::class -> Icons.Filled.Gesture
        Fact.KeyEvent::class -> Icons.Filled.Keyboard
        Fact.ScreenOff::class -> Icons.Filled.Lock
        Fact.ScreenOn::class -> Icons.Filled.Smartphone
        Fact.UserPresent::class -> Icons.Filled.LockOpen
        Fact.Alarm::class -> Icons.Filled.Alarm
        Fact.AppBecomeBackground::class -> Icons.Filled.FiberDvr
        Fact.AppBecomeForeground::class -> Icons.Filled.Aod
        else -> {
            Icons.Filled.Warning
        }
    }
}

