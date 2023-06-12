package tornaco.apps.shortx.ui.addrule.fact

import androidx.compose.runtime.Composable
import tornaco.apps.shortx.ui.addrule.condition.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.condition.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.fact.model.Fact
import tornaco.apps.shortx.ui.addrule.fact.selector.AppComponentSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.ComponentType
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAppComponentSelectorState

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


    return { source ->
        when (source.clazz) {
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
                    source.getFactAs<Fact.AppUpdated>()?.apps,
                    source.getFactAs<Fact.AppUpdated>()?.pkgSets
                )
                true
            }

            Fact.AudioFocusLost::class -> {
                audioFocusLostSelectorState.show(
                    source.getFactAs<Fact.AppUpdated>()?.apps,
                    source.getFactAs<Fact.AppUpdated>()?.pkgSets
                )
                true
            }

            Fact.AudioFocusChanged::class -> {
                setFact(Fact.AudioFocusChanged())
                true
            }

            else -> false
        }
    }
}
