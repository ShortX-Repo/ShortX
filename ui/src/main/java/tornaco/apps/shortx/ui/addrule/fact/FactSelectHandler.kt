package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.action.selector.ProcessListSelectorDialog
import tornaco.apps.shortx.ui.addrule.action.selector.rememberProcessListSelectorState
import tornaco.apps.shortx.ui.addrule.base.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.base.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.AppComponentSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.ComponentType
import tornaco.apps.shortx.ui.addrule.fact.selector.HasFoundNodeOnScreenDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.NotificationAppAndMatcherSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAppComponentSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberHasFoundNodeOnScreenState
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberNotificationAppAndMatcherSelectorState
import tornaco.apps.shortx.ui.base.LocalI18N

@Composable
fun appFactSelectHandler(setFact: (Fact) -> Unit): (FactSelectSource) -> Boolean {
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

    val appGainWindowFocusSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { pkg ->
                setFact(Fact.AppGainWindowFocus(apps = pkg))
            }, onPkgSetSelected = {
                setFact(Fact.AppGainWindowFocus(pkgSets = it))
            })
    AppPkgSelectorDialog(appGainWindowFocusSelectorState)

    val appLostWindowFocusSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { pkg ->
                setFact(Fact.AppLostWindowFocus(apps = pkg))
            }, onPkgSetSelected = {
                setFact(Fact.AppLostWindowFocus(pkgSets = it))
            })
    AppPkgSelectorDialog(appLostWindowFocusSelectorState)

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

    val pkgStopRunningSelectorState = rememberAppPkgSelectorState(
        onPkgSetSelected = {
            setFact(
                Fact.PkgStopRunning(pkgSets = it)
            )
        },
        onAppListSelected = {
            setFact(
                Fact.PkgStopRunning(apps = it)
            )
        })
    AppPkgSelectorDialog(state = pkgStopRunningSelectorState)

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

    val audioFocusGainSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { pkg ->
                setFact(Fact.AudioFocusGain(apps = pkg))
            }, onPkgSetSelected = {
                setFact(Fact.AudioFocusGain(pkgSets = it))
            })
    AppPkgSelectorDialog(audioFocusGainSelectorState)

    val audioFocusLostSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = { pkg ->
                setFact(Fact.AudioFocusLost(apps = pkg))
            }, onPkgSetSelected = {
                setFact(Fact.AudioFocusLost(pkgSets = it))
            })
    AppPkgSelectorDialog(audioFocusLostSelectorState)

    val notificationPostedSelectorState =
        rememberNotificationAppAndMatcherSelectorState(LocalI18N.current["ui.fact.n.posted"]) {
            setFact(Fact.NotificationPosted(n = it))
        }
    NotificationAppAndMatcherSelectorDialog(notificationPostedSelectorState)

    val notificationUpdatedSelectorState =
        rememberNotificationAppAndMatcherSelectorState(LocalI18N.current["ui.fact.n.updated"]) {
            setFact(Fact.NotificationUpdated(n = it))
        }
    NotificationAppAndMatcherSelectorDialog(notificationUpdatedSelectorState)


    val notificationRemovedSelectorState =
        rememberNotificationAppAndMatcherSelectorState(LocalI18N.current["ui.fact.n.removed"]) {
            setFact(Fact.NotificationRemoved(n = it))
        }
    NotificationAppAndMatcherSelectorDialog(notificationRemovedSelectorState)

    val hasNodeOnScreenState = rememberHasFoundNodeOnScreenState(
        selectorTitle = LocalI18N.current["ui.fact.has.found.node.on.screen"],
        onSelected = {
            setFact(it)
        })
    HasFoundNodeOnScreenDialog(state = hasNodeOnScreenState)

    val appProcessStartedSelectorState = rememberProcessListSelectorState(onSelected = {
        setFact(Fact.AppProcessStarted(processNames = it))
    })
    ProcessListSelectorDialog(state = appProcessStartedSelectorState)


    return { source ->
        when (source.clazz) {
            Fact.AppProcessStarted::class -> {
                appProcessStartedSelectorState.show(source.getFactAs<Fact.AppProcessStarted>()?.processNames)
                true
            }

            Fact.ActivityStarted::class -> {
                activityStartSingleAppSelectorState.show()
                true
            }

            Fact.AnyActivityStarted::class -> {
                setFact(Fact.AnyActivityStarted())
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
                setFact(Fact.TaskRemovedAny())
                true
            }

            Fact.PkgStopRunning::class -> {
                pkgStopRunningSelectorState.show(
                    source.getFactAs<Fact.PkgStopRunning>()?.apps,
                    source.getFactAs<Fact.PkgStopRunning>()?.pkgSets
                )
                true
            }

            Fact.PkgStopRunningAny::class -> {
                setFact(Fact.PkgStopRunningAny())
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

            Fact.AppGainWindowFocus::class -> {
                appGainWindowFocusSelectorState.show(
                    source.getFactAs<Fact.AppGainWindowFocus>()?.apps,
                    source.getFactAs<Fact.AppGainWindowFocus>()?.pkgSets
                )
                true
            }

            Fact.AppLostWindowFocus::class -> {
                appLostWindowFocusSelectorState.show(
                    source.getFactAs<Fact.AppLostWindowFocus>()?.apps,
                    source.getFactAs<Fact.AppLostWindowFocus>()?.pkgSets
                )
                true
            }

            Fact.NotificationPosted::class -> {
                notificationPostedSelectorState.show(
                    source.getFactAs<Fact.NotificationPosted>()?.n
                )
                true
            }

            Fact.NotificationUpdated::class -> {
                notificationUpdatedSelectorState.show(
                    source.getFactAs<Fact.NotificationUpdated>()?.n
                )
                true
            }

            Fact.NotificationRemoved::class -> {
                notificationRemovedSelectorState.show(
                    source.getFactAs<Fact.NotificationRemoved>()?.n
                )
                true
            }

            Fact.AppAdded::class -> {
                setFact(
                    Fact.AppAdded()
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

            Fact.AudioFocusGain::class -> {
                audioFocusGainSelectorState.show(
                    source.getFactAs<Fact.AudioFocusGain>()?.apps,
                    source.getFactAs<Fact.AudioFocusGain>()?.pkgSets
                )
                true
            }

            Fact.AudioFocusLost::class -> {
                audioFocusLostSelectorState.show(
                    source.getFactAs<Fact.AudioFocusLost>()?.apps,
                    source.getFactAs<Fact.AudioFocusLost>()?.pkgSets
                )
                true
            }

            Fact.AudioFocusChanged::class -> {
                setFact(Fact.AudioFocusChanged())
                true
            }

            Fact.HasFoundNodeOnScreen::class -> {
                hasNodeOnScreenState.show(source.getFactAs())
                true
            }

            else -> false
        }
    }
}
