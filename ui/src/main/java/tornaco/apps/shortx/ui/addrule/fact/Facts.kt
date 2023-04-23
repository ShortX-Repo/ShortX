@file:OptIn(ExperimentalAnimationApi::class)

package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.ui.addrule.*
import tornaco.apps.shortx.ui.addrule.condition.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.model.contextData
import tornaco.apps.shortx.ui.base.*
import tornaco.apps.shortx.ui.base.dropdown.Dropdown
import tornaco.apps.shortx.ui.base.dropdown.dropDownMenu
import java.time.LocalTime
import kotlin.reflect.KClass


@Composable
fun FactSelectors(
    vm: AddRuleViewModel, state: AddRuleState
) {
    // Fact selectors
    val keyEventSelectorState = rememberKeyEventSelectorState(selected = { code ->
        vm.setFact(Fact.KeyEvent(code))
    })
    KeyEventSelector(keyEventSelectorState)

    val edgeGestureSelectorState = rememberEdgeGestureSelectorState(selected = { eg ->
        vm.setFact(Fact.EdgeGesture(eg.first, eg.second))
    }, showEdgeHint = {
        vm.showEdgeHint(it)
    })
    EdgeGestureSelector(edgeGestureSelectorState)

    val timeOfADaySelector = rememberAlarmSelectorState(
        initialRepeat = emptyArray(),
        initialTime = LocalTime.now()
    ) { alarm ->
        // Use rule id as alarm tag.
        vm.setFact(Fact.Alarm(state.id, alarm.triggerAt, alarm.repeat))
    }
    AlarmSelector(timeOfADaySelector)

    // Condition selectors
    val appBgPkgSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { pkg ->
                vm.setFact(Fact.AppBecomeBackground(apps = pkg))
            }, onPkgSetSelected = {
                vm.setFact(Fact.AppBecomeBackground(pkgSets = it))
            })
    AppPkgSelectorDialog(appBgPkgSelectorState)

    val appFgPkgSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { pkg ->
                vm.setFact(Fact.AppBecomeForeground(apps = pkg))
            }, onPkgSetSelected = {
                vm.setFact(Fact.AppBecomeForeground(pkgSets = it))
            })
    AppPkgSelectorDialog(appFgPkgSelectorState)

    val appComponentSelectorState = rememberAppComponentSelectorState(
        types = listOf(ComponentType.Activity),
        onAppComponentSelected = { infoList ->
            vm.setFact(Fact.ActivityStarted(infoList.map { it.component }))
        }
    )
    AppComponentSelectorDialog(appComponentSelectorState)

    val singleAppSelectorState = rememberSingleAppPkgSelectorState {
        appComponentSelectorState.show(it.pkg)
    }
    SingleAppSelectorDialog(state = singleAppSelectorState)


    val wifiSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.wifi.state.changed"]) {
            vm.setFact(Fact.WifiStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(wifiSelectorState)

    val btSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.bt.state.changed"]) {
            vm.setFact(Fact.BTStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(btSelectorState)

    val dndSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.dnd.state.changed"]) {
            vm.setFact(Fact.DNDStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(dndSelectorState)

    val locationSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.location.state.changed"]) {
            vm.setFact(Fact.LocationStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(locationSelectorState)

    val darkModeSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.dark.mode.state.changed"]) {
            vm.setFact(Fact.DarkModeStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(darkModeSelectorState)

    val mobileDataSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.mobile.data.state.changed"]) {
            vm.setFact(Fact.MobileDataStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(mobileDataSelectorState)

    val flashLightSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.flash.light.state.changed"]) {
            vm.setFact(Fact.FlashLightStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(flashLightSelectorState)

    val nfcSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.nfc.state.changed"]) {
            vm.setFact(Fact.NFCStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(nfcSelectorState)

    val hotspotSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.hotspot.state.changed"]) {
            vm.setFact(Fact.HotSpotStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(hotspotSelectorState)


    FactSelector(state) { fact ->
        when (fact) {
            Fact.KeyEvent::class -> {
                keyEventSelectorState.show()
            }
            Fact.ActivityStarted::class -> {
                singleAppSelectorState.show()
            }
            Fact.AnyActivityStarted::class -> {
                vm.setFact(Fact.AnyActivityStarted)
            }
            Fact.EdgeGesture::class -> {
                edgeGestureSelectorState.show()
            }
            Fact.ScreenOff::class -> {
                vm.setFact(Fact.ScreenOff)
            }
            Fact.ScreenOn::class -> {
                vm.setFact(Fact.ScreenOn)
            }
            Fact.UserPresent::class -> {
                vm.setFact(Fact.UserPresent)
            }
            Fact.Alarm::class -> {
                timeOfADaySelector.show()
            }
            Fact.AppBecomeBackground::class -> {
                appBgPkgSelectorState.show()
            }
            Fact.AppBecomeForeground::class -> {
                appFgPkgSelectorState.show()
            }
            Fact.WifiStatusChanged::class -> {
                wifiSelectorState.show()
            }
            Fact.BTStatusChanged::class -> {
                btSelectorState.show()
            }
            Fact.DNDStatusChanged::class -> {
                dndSelectorState.show()
            }
            Fact.DarkModeStatusChanged::class -> {
                darkModeSelectorState.show()
            }
            Fact.FlashLightStatusChanged::class -> {
                flashLightSelectorState.show()
            }
            Fact.HotSpotStatusChanged::class -> {
                hotspotSelectorState.show()
            }
            Fact.LocationStatusChanged::class -> {
                locationSelectorState.show()
            }
            Fact.MobileDataStatusChanged::class -> {
                mobileDataSelectorState.show()
            }
            Fact.NFCStatusChanged::class -> {
                nfcSelectorState.show()
            }
            Fact.NotificationPosted::class -> {
                vm.setFact(
                    Fact.NotificationPosted(
                        n = Fact.Notification(
                            apps = emptyList(),
                            pkgSets = emptyList()
                        )
                    )
                )
            }
            Fact.NotificationUpdated::class -> {
                vm.setFact(
                    Fact.NotificationUpdated(
                        n = Fact.Notification(
                            apps = emptyList(),
                            pkgSets = emptyList()
                        )
                    )
                )
            }
            Fact.NotificationRemoved::class -> {
                vm.setFact(
                    Fact.NotificationRemoved(
                        n = Fact.Notification(
                            apps = emptyList(),
                            pkgSets = emptyList()
                        )
                    )
                )
            }
        }
    }
}


@Composable
private fun FactSelector(state: AddRuleState, selectFact: (KClass<out Fact>) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        MediumTitle(LocalI18N.current["ui.fact.title"])

        Box {
            var showFactSelectorMenu by remember {
                mutableStateOf(false)
            }

            val contextDataInfoDialogState = rememberContextDataInfoDialogState()
            ContextDataInfoDialog(state = contextDataInfoDialogState)
            val i18N = LocalI18N.current

            Column {
                state.fact?.let {
                    ListItem(
                        modifier = Modifier,
                        titleLines = labelForFact(fact = it),
                        icon = {
                            ColoredIcon(size = 36.dp) {
                                RemixIcon(
                                    modifier = Modifier.size(16.dp),
                                    remixName = imageVectorForFact(it::class)
                                )
                            }
                        },
                        action1 = {
                            if (it.contextData.isNotEmpty()) {
                                IconButton(onClick = {
                                    contextDataInfoDialogState.show(
                                        labelAndDescriptionForFactSelector(i18N, it::class).first,
                                        it.contextData
                                    )
                                }) {
                                    ContextDataIcon()
                                }
                                VerticalDivider(modifier = Modifier.height(24.dp))
                            }

                            IconButton(onClick = {
                            }) {
                                RemixIcon(remixName = Remix.System.close_line)
                            }
                        }
                    )
                }

                TinySpacer()
                FilledTonalButton(onClick = {
                    showFactSelectorMenu = true
                }) {
                    RemixIcon(modifier = Modifier.size(16.dp), remixName = Remix.System.add_line)
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
            Fact.AnyActivityStarted::class,
            labelAndDescriptionForFactSelector(i18N, Fact.AnyActivityStarted::class)
        ) {
            icon(imageVectorForFact(Fact.AnyActivityStarted::class))
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
                item(
                    it,
                    labelAndDescriptionForFactSelector(i18N, it)
                ) {
                    icon(imageVectorForFact(it))
                }
            }
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

        item(Fact.NotificationPosted::class, i18N["ui.fact.n"] to null) {
            icon(imageVectorForFact(Fact.NotificationPosted::class))
            item(
                Fact.NotificationPosted::class,
                labelAndDescriptionForFactSelector(i18N, Fact.NotificationPosted::class)
            ) {
                icon(imageVectorForFact(Fact.NotificationPosted::class))
            }
            item(
                Fact.NotificationUpdated::class,
                labelAndDescriptionForFactSelector(i18N, Fact.NotificationUpdated::class)
            ) {
                icon(imageVectorForFact(Fact.NotificationUpdated::class))
            }
            item(
                Fact.NotificationRemoved::class,
                labelAndDescriptionForFactSelector(i18N, Fact.NotificationRemoved::class)
            ) {
                icon(imageVectorForFact(Fact.NotificationRemoved::class))
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
