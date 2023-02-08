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
fun FactSelector(state: AddRuleState, selectFact: (Fact) -> Unit) {
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
                        icon = { Icon(imageVector = imageVectorForFact(it)) })
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
    itemSelected: (Fact) -> Unit
) {
    val i18N = LocalI18N.current
    val menu = dropDownMenu {
        state.selectableFacts.forEach {
            item(it, labelForFactSelector(i18N, it)) {
                icon(imageVectorForFact(fact = it))
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

private fun imageVectorForFact(fact: Fact): ImageVector {
    return when (fact) {
        is Fact.ActivityStarted -> Icons.Filled.Update
        is Fact.EdgeGesture -> Icons.Filled.Gesture
        is Fact.KeyEvent -> Icons.Filled.Keyboard
        Fact.ScreenOff -> Icons.Filled.Lock
        Fact.ScreenOn -> Icons.Filled.Smartphone
        Fact.UserPresent -> Icons.Filled.LockOpen
        is Fact.Alarm -> Icons.Filled.Alarm
        is Fact.AppBecomeBackground -> Icons.Filled.FiberDvr
        is Fact.AppBecomeForeground -> Icons.Filled.Aod
    }
}

