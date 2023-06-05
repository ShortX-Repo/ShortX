package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.addrule.condition.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.base.LocalI18N
import java.time.LocalTime

@Composable
fun factSelectHandler(setFact: (Fact) -> Unit): (FactSelectSource) -> Boolean {
    // Fact selectors
    val keyEventSelectorState = rememberKeyEventSelectorState(selected = { code ->
        setFact(Fact.KeyEvent(code))
    })
    KeyEventSelector(keyEventSelectorState)

    val edgeGestureSelectorState = rememberEdgeGestureSelectorState(
        selected = { eg ->
            setFact(Fact.EdgeGesture(eg.first, eg.second))
        },
        showEdgeHint = {
            shortXManager.showEdgeHint(it)
        })
    EdgeGestureSelector(edgeGestureSelectorState)

    val timeOfADaySelector = rememberAlarmSelectorState(
        initialRepeat = emptyArray(),
        initialTime = LocalTime.now()
    ) { alarm ->
        // Use rule id as alarm tag.
        setFact(Fact.Alarm(timeOfADay = alarm.triggerAt, repeatDays = alarm.repeat))
    }
    AlarmSelector(timeOfADaySelector)

    // Condition selectors
    val appBgPkgSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { pkg ->
                setFact(Fact.AppBecomeBackground(apps = pkg))
            }, onPkgSetSelected = {
                setFact(Fact.AppBecomeBackground(pkgSets = it))
            })
    AppPkgSelectorDialog(appBgPkgSelectorState)

    val appFgPkgSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { pkg ->
                setFact(Fact.AppBecomeForeground(apps = pkg))
            }, onPkgSetSelected = {
                setFact(Fact.AppBecomeForeground(pkgSets = it))
            })
    AppPkgSelectorDialog(appFgPkgSelectorState)

    val activityStartAppComponentSelectorState = rememberAppComponentSelectorState(
        types = listOf(ComponentType.Activity),
        onAppComponentSelected = { infoList ->
            setFact(Fact.ActivityStarted(infoList.map { it.component }))
        }
    )
    AppComponentSelectorDialog(activityStartAppComponentSelectorState)

    val activityStartSingleAppSelectorState = rememberSingleAppPkgSelectorState {
        activityStartAppComponentSelectorState.show(it.pkg)
    }
    SingleAppSelectorDialog(state = activityStartSingleAppSelectorState)


    val wifiSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.wifi.state.changed"]) {
            setFact(Fact.WifiStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(wifiSelectorState)

    val btSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.bt.state.changed"]) {
            setFact(Fact.BTStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(btSelectorState)

    val dndSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.dnd.state.changed"]) {
            setFact(Fact.DNDStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(dndSelectorState)

    val locationSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.location.state.changed"]) {
            setFact(Fact.LocationStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(locationSelectorState)

    val darkModeSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.dark.mode.state.changed"]) {
            setFact(Fact.DarkModeStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(darkModeSelectorState)

    val mobileDataSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.mobile.data.state.changed"]) {
            setFact(Fact.MobileDataStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(mobileDataSelectorState)

    val flashLightSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.flash.light.state.changed"]) {
            setFact(Fact.FlashLightStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(flashLightSelectorState)

    val nfcSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.nfc.state.changed"]) {
            setFact(Fact.NFCStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(nfcSelectorState)

    val hotspotSelectorState =
        rememberOnOffAnyStateSelectorState(title = LocalI18N.current["ui.fact.hotspot.state.changed"]) {
            setFact(Fact.HotSpotStatusChanged(it))
        }
    OnOffAnyStateSelectorDialog(hotspotSelectorState)


    val taskRemovedAppSelectorState = rememberAppPkgSelectorState(
        onPkgSetSelected = {
            setFact(
                Fact.TaskRemoved(pkgSets = it)
            )
        },
        onAppListSelected = {
            setFact(
                Fact.TaskRemoved(apps = it)
            )
        })
    AppPkgSelectorDialog(state = taskRemovedAppSelectorState)


    val appRemovedSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { pkg ->
                setFact(Fact.AppRemoved(apps = pkg))
            }, onPkgSetSelected = {
                setFact(Fact.AppRemoved(pkgSets = it))
            })
    AppPkgSelectorDialog(appRemovedSelectorState)

    val appUpdatedSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { pkg ->
                setFact(Fact.AppUpdated(apps = pkg))
            }, onPkgSetSelected = {
                setFact(Fact.AppUpdated(pkgSets = it))
            })
    AppPkgSelectorDialog(appUpdatedSelectorState)


    return { source ->
        when (source.clazz) {
            Fact.KeyEvent::class -> {
                keyEventSelectorState.show()
                true
            }

            Fact.ActivityStarted::class -> {
                activityStartSingleAppSelectorState.show()
                true
            }

            Fact.AnyActivityStarted::class -> {
                setFact(Fact.AnyActivityStarted)
                true
            }

            Fact.TaskRemoved::class -> {
                taskRemovedAppSelectorState.show(
                    source.getFactAs<Fact.TaskRemoved>()?.apps,
                    source.getFactAs<Fact.TaskRemoved>()?.pkgSets
                )
                true
            }

            Fact.TaskRemovedAny::class -> {
                setFact(Fact.TaskRemovedAny)
                true
            }

            Fact.EdgeGesture::class -> {
                edgeGestureSelectorState.show()
                true
            }

            Fact.ScreenOff::class -> {
                setFact(Fact.ScreenOff)
                true
            }

            Fact.ScreenOn::class -> {
                setFact(Fact.ScreenOn)
                true
            }

            Fact.UserPresent::class -> {
                setFact(Fact.UserPresent)
                true
            }

            Fact.SystemReady::class -> {
                setFact(Fact.SystemReady)
                true
            }

            Fact.Alarm::class -> {
                timeOfADaySelector.show()
                true
            }

            Fact.AppBecomeBackground::class -> {
                appBgPkgSelectorState.show(
                    source.getFactAs<Fact.AppBecomeBackground>()?.apps,
                    source.getFactAs<Fact.AppBecomeBackground>()?.pkgSets
                )
                true
            }

            Fact.AppBecomeForeground::class -> {
                appFgPkgSelectorState.show(
                    source.getFactAs<Fact.AppBecomeForeground>()?.apps,
                    source.getFactAs<Fact.AppBecomeForeground>()?.pkgSets
                )
                true
            }

            Fact.WifiStatusChanged::class -> {
                wifiSelectorState.show()
                true
            }

            Fact.BTStatusChanged::class -> {
                btSelectorState.show()
                true
            }

            Fact.DNDStatusChanged::class -> {
                dndSelectorState.show()
                true
            }

            Fact.DarkModeStatusChanged::class -> {
                darkModeSelectorState.show()
                true
            }

            Fact.FlashLightStatusChanged::class -> {
                flashLightSelectorState.show()
                true
            }

            Fact.HotSpotStatusChanged::class -> {
                hotspotSelectorState.show()
                true
            }

            Fact.LocationStatusChanged::class -> {
                locationSelectorState.show()
                true
            }

            Fact.MobileDataStatusChanged::class -> {
                mobileDataSelectorState.show()
                true
            }

            Fact.NFCStatusChanged::class -> {
                nfcSelectorState.show()
                true
            }

            Fact.NotificationPosted::class -> {
                setFact(
                    Fact.NotificationPosted(
                        n = Fact.Notification(
                            apps = emptyList(),
                            pkgSets = emptyList()
                        )
                    )
                )
                true
            }

            Fact.NotificationUpdated::class -> {
                setFact(
                    Fact.NotificationUpdated(
                        n = Fact.Notification(
                            apps = emptyList(),
                            pkgSets = emptyList()
                        )
                    )
                )
                true
            }

            Fact.NotificationRemoved::class -> {
                setFact(
                    Fact.NotificationRemoved(
                        n = Fact.Notification(
                            apps = emptyList(),
                            pkgSets = emptyList()
                        )
                    )
                )
                true
            }

            Fact.AppAdded::class -> {
                setFact(
                    Fact.AppAdded
                )
                true
            }

            Fact.AppRemoved::class -> {
                appRemovedSelectorState.show(
                    source.getFactAs<Fact.AppRemoved>()?.apps,
                    source.getFactAs<Fact.AppRemoved>()?.pkgSets
                )
                true
            }

            Fact.AppUpdated::class -> {
                appUpdatedSelectorState.show(
                    source.getFactAs<Fact.AppUpdated>()?.apps,
                    source.getFactAs<Fact.AppUpdated>()?.pkgSets
                )
                true
            }

            else -> false
        }
    }
}
