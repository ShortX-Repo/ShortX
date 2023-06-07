@file:OptIn(ExperimentalAnimationApi::class)

package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.*
import tornaco.apps.shortx.ui.addrule.action.mapper.ActionColorBgAlpha
import tornaco.apps.shortx.ui.addrule.fact.mapper.colorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.imageVectorForFact
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelAndDescriptionForFactSelector
import tornaco.apps.shortx.ui.addrule.fact.mapper.labelForFact
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.contextData
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.base.dropdown.DropDownMenuBuilder
import tornaco.apps.shortx.ui.base.dropdown.Dropdown
import tornaco.apps.shortx.ui.base.dropdown.dropDownMenu
import kotlin.reflect.KClass


@Composable
fun FactSelectors(
    vm: AddRuleViewModel,
    state: AddRuleState,
    readOnly: Boolean = false,
) {
    val selectHandler = factSelectHandler {
        vm.setFact(it)
    }

    FactSelector(
        state = state,
        readOnly = readOnly,
        selectFact = { factClass ->
            selectHandler(FactSelectSource.FromClass(factClass))
        },
        editFact = { fact ->
            selectHandler(FactSelectSource.FromFact(fact))
        })
}


@Composable
private fun FactSelector(
    state: AddRuleState,
    readOnly: Boolean,
    selectFact: (KClass<out Fact>) -> Unit,
    editFact: (Fact) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle(LocalI18N.current["ui.fact.title"])

        Box {
            var showFactSelectorMenu by remember {
                mutableStateOf(false)
            }

            Column {
                state.fact?.let {
                    FactItem(fact = it, readOnly = readOnly, onClick = { editFact(it) })
                }

                TinySpacer()
                if (!readOnly) {
                    FilledTonalButton(onClick = {
                        showFactSelectorMenu = true
                    }) {
                        RemixIcon(
                            modifier = Modifier.size(16.dp),
                            remixName = Remix.System.add_line
                        )
                        TinySpacer()
                        Text(text = LocalI18N.current["ui.fact.select"])
                    }
                }
            }

            FactSelectorMenu(
                isOpen = showFactSelectorMenu,
                setIsOpen = { showFactSelectorMenu = it },
                itemSelected = {
                    showFactSelectorMenu = false
                    selectFact(it)
                })
        }
    }
}

@Composable
private fun FactItem(
    fact: Fact,
    readOnly: Boolean,
    onClick: (Fact) -> Unit
) {
    val contextDataInfoDialogState = rememberContextDataInfoDialogState()
    ContextDataInfoDialog(state = contextDataInfoDialogState)
    val i18N = LocalI18N.current

    ListItem(
        modifier = Modifier,
        titleLines = labelForFact(fact = fact),
        onClick = {
            onClick(fact)
        },
        icon = {
            ColoredIcon(
                size = 36.dp, color = colorForFact(fact::class).copy(
                    ActionColorBgAlpha
                )
            ) {
                RemixIcon(
                    modifier = Modifier.size(16.dp),
                    remixName = imageVectorForFact(fact::class),
                    tint = colorForFact(fact::class)
                )
            }
        },
        action1 = {
            if (fact.contextData.isNotEmpty()) {
                IconButton(onClick = {
                    contextDataInfoDialogState.show(
                        labelAndDescriptionForFactSelector(i18N, fact::class).first,
                        fact.contextData
                    )
                }) {
                    ContextDataIcon()
                }
                VerticalDivider(modifier = Modifier.height(24.dp))
            }

            if (!readOnly) {
                IconButton(onClick = {
                }) {
                    RemixIcon(remixName = Remix.System.close_line)
                }
            }
        }
    )
}

@ExperimentalAnimationApi
@Composable
private fun FactSelectorMenu(
    isOpen: Boolean = false,
    setIsOpen: (Boolean) -> Unit,
    itemSelected: (KClass<out Fact>) -> Unit
) {
    val i18N = LocalI18N.current
    val menu = dropDownMenu {
        fun KClass<out Fact>.factItem(builder: DropDownMenuBuilder<KClass<out Fact>> = this@dropDownMenu) =
            builder.item(
                this,
                labelAndDescriptionForFactSelector(i18N, this)
            ) {
                icon(imageVectorForFact(this@factItem), colorForFact(this@factItem))
            }

        Fact.KeyEvent::class.factItem()

        item(
            Fact.ActivityStarted::class,
            "Activity" to null
        ) {
            icon(imageVectorForFact(Fact.ActivityStarted::class))

            Fact.ActivityStarted::class.factItem(builder = this)
            Fact.AnyActivityStarted::class.factItem(builder = this)
        }

        item(
            Fact.TaskRemoved::class,
            i18N["ui.fact.task.removed"] to null
        ) {
            icon(imageVectorForFact(Fact.TaskRemoved::class))

            Fact.TaskRemoved::class.factItem(builder = this)
            Fact.TaskRemovedAny::class.factItem(builder = this)
        }

        Fact.EdgeGesture::class.factItem()

        item(Fact.ScreenOn::class, i18N["ui.fact.screen"] to null) {
            icon(imageVectorForFact(Fact.ScreenOn::class))

            Fact.ScreenOn::class.factItem(builder = this)
            Fact.ScreenOff::class.factItem(builder = this)
            Fact.UserPresent::class.factItem(builder = this)
        }

        Fact.SystemReady::class.factItem()

        Fact.Alarm::class.factItem()

        item(
            Fact.WifiStatusChanged::class,
            i18N["ui.fact.system.settings"] to null
        ) {
            icon(Remix.System.settings_3_line)

            listOf(
                Fact.WifiStatusChanged::class,
                Fact.BTStatusChanged::class,
                Fact.LocationStatusChanged::class,
                Fact.NFCStatusChanged::class,
                Fact.MobileDataStatusChanged::class,
                Fact.HotSpotStatusChanged::class,
                Fact.DarkModeStatusChanged::class,
                Fact.FlashLightStatusChanged::class,
            ).forEach {
                it.factItem(builder = this)
            }
        }

        item(Fact.AppBecomeForeground::class, i18N["ui.fact.app.bg.state"] to null) {
            icon(imageVectorForFact(Fact.AppBecomeForeground::class))
            Fact.AppBecomeForeground::class.factItem(builder = this)
            Fact.AppBecomeBackground::class.factItem(builder = this)
        }

        item(Fact.NotificationPosted::class, i18N["ui.fact.n"] to null) {
            icon(imageVectorForFact(Fact.NotificationPosted::class))
            Fact.NotificationPosted::class.factItem(builder = this)
            Fact.NotificationUpdated::class.factItem(builder = this)
            Fact.NotificationRemoved::class.factItem(builder = this)
        }

        item(Fact.AppAdded::class, i18N["ui.fact.app.installed"] to null) {
            icon(imageVectorForFact(Fact.AppAdded::class))
            Fact.AppAdded::class.factItem(builder = this)
            Fact.AppRemoved::class.factItem(builder = this)
            Fact.AppUpdated::class.factItem(builder = this)
        }

        item(
            Fact.PkgStopRunning::class,
            i18N["ui.fact.pkg.stop.running"] to null
        ) {
            icon(imageVectorForFact(Fact.PkgStopRunning::class))

            Fact.PkgStopRunning::class.factItem(builder = this)
            Fact.PkgStopRunningAny::class.factItem(builder = this)
        }
    }

    Dropdown(
        isOpen = isOpen,
        menu = menu,
        onItemSelected = itemSelected,
        onDismiss = { setIsOpen(false) },
    )
}