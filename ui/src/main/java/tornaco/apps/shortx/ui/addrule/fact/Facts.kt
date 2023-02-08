@file:OptIn(ExperimentalAnimationApi::class)

package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.ui.addrule.AddRuleState
import tornaco.apps.shortx.ui.addrule.models.Fact
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.base.dropdown.*

@Composable
fun FactSelector(state: AddRuleState, selectFact: (Class<out Fact>) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle(LocalI18N.current["ui.fact.title"])

        Box {
            var showFactSelectorMenu by remember {
                mutableStateOf(false)
            }

            Column {
                state.fact?.let {
                    ListItem(
                        titleLines = labelForFact(fact = it),
                        icon = { Icon(imageVector = imageVectorForFact(it.javaClass)) })
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

            FactSelectorMenu(
                state = state,
                isOpen = showFactSelectorMenu,
                setIsOpen = { showFactSelectorMenu = it },
                itemSelected = {
                    showFactSelectorMenu = false
                    selectFact(it)
                }
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun FactSelectorMenu(
    state: AddRuleState,
    isOpen: Boolean = false,
    setIsOpen: (Boolean) -> Unit,
    itemSelected: (Class<out Fact>) -> Unit
) {
    val i18N = LocalI18N.current
    val menu = dropDownMenu<Class<out Fact>> {
        item(Fact.KeyEvent::class.java, labelForFactSelector(i18N, Fact.KeyEvent::class.java)) {
            icon(imageVectorForFact(Fact.KeyEvent::class.java))
        }
        item(
            Fact.ActivityStarted::class.java,
            labelForFactSelector(i18N, Fact.ActivityStarted::class.java)
        ) {
            icon(imageVectorForFact(Fact.ActivityStarted::class.java))
        }
        item(
            Fact.EdgeGesture::class.java,
            labelForFactSelector(i18N, Fact.EdgeGesture::class.java)
        ) {
            icon(imageVectorForFact(Fact.EdgeGesture::class.java))
        }

        item(Fact.Alarm::class.java, i18N["ui.fact.screen"]) {
            icon(imageVectorForFact(Fact.ScreenOn::class.java))
            item(Fact.ScreenOn::class.java, labelForFactSelector(i18N, Fact.ScreenOn::class.java)) {
                icon(imageVectorForFact(Fact.ScreenOn::class.java))
            }
            item(
                Fact.ScreenOff::class.java,
                labelForFactSelector(i18N, Fact.ScreenOff::class.java)
            ) {
                icon(imageVectorForFact(Fact.ScreenOff::class.java))
            }
            item(
                Fact.UserPresent::class.java,
                labelForFactSelector(i18N, Fact.UserPresent::class.java)
            ) {
                icon(imageVectorForFact(Fact.UserPresent::class.java))
            }
        }

        item(Fact.Alarm::class.java, labelForFactSelector(i18N, Fact.Alarm::class.java)) {
            icon(imageVectorForFact(Fact.Alarm::class.java))
        }

        item(Fact.AppBecomeForeground::class.java, i18N["ui.fact.app.bg.state"]) {
            icon(imageVectorForFact(Fact.AppBecomeForeground::class.java))
            item(
                Fact.AppBecomeForeground::class.java,
                labelForFactSelector(i18N, Fact.AppBecomeForeground::class.java)
            ) {
                icon(imageVectorForFact(Fact.AppBecomeForeground::class.java))
            }
            item(
                Fact.AppBecomeBackground::class.java,
                labelForFactSelector(i18N, Fact.AppBecomeBackground::class.java)
            ) {
                icon(imageVectorForFact(Fact.AppBecomeBackground::class.java))
            }
        }
    }

    Dropdown(
        isOpen = isOpen,
        menu = menu,
        onItemSelected = itemSelected,
        onDismiss = { setIsOpen(false) },
        offset = DpOffset(8.dp, 0.dp),
        enter = EnterAnimation.ElevationScale,
        exit = ExitAnimation.ElevationScale,
        easing = Easing.FastOutSlowInEasing,
        enterDuration = 400,
        exitDuration = 400

    )
}

fun getFactMenu(): MenuItem<String> {
    val menu = dropDownMenu<String> {
        item("about", "About") {
            icon(Icons.TwoTone.Language)
        }
        item("copy", "Copy") {
            icon(Icons.TwoTone.FileCopy)
        }
        item("share", "Share") {
            icon(Icons.TwoTone.Share)
            item("to_clipboard", "To clipboard") {
                item("pdf", "PDF")
                item("epub", "EPUB")
                item("web_page", "Web page")
                item("microsoft_word", "Microsoft word")
            }
            item("as_a_file", "As a file") {
                item("pdf", "PDF")
                item("epub", "EPUB")
                item("web_page", "Web page")
                item("microsoft_word", "Microsoft word")
            }
        }
        item("remove", "Remove") {
            icon(Icons.TwoTone.DeleteSweep)
            item("yep", "Yep") {
                icon(Icons.TwoTone.Done)
            }
            item("go_back", "Go back") {
                icon(Icons.TwoTone.Close)
            }
        }
    }
    return menu
}

private fun imageVectorForFact(fact: Class<out Fact>): ImageVector {
    return when (fact) {
        Fact.ActivityStarted::class.java -> Icons.Filled.Update
        Fact.EdgeGesture::class.java -> Icons.Filled.Gesture
        Fact.KeyEvent::class.java -> Icons.Filled.Keyboard
        Fact.ScreenOff::class.java -> Icons.Filled.Lock
        Fact.ScreenOn::class.java -> Icons.Filled.Smartphone
        Fact.UserPresent::class.java -> Icons.Filled.LockOpen
        Fact.Alarm::class.java -> Icons.Filled.Alarm
        Fact.AppBecomeBackground::class.java -> Icons.Filled.FiberDvr
        Fact.AppBecomeForeground::class.java -> Icons.Filled.Aod
        else -> {
            Icons.Filled.Warning
        }
    }
}

