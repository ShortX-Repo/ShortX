package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.core.toComponentName
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.ActionCategory
import tornaco.apps.shortx.ui.addrule.base.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.SingleAppSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.base.rememberCategoryState
import tornaco.apps.shortx.ui.addrule.base.rememberSingleAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.condition.ComponentOrGVSelectorDialog
import tornaco.apps.shortx.ui.addrule.condition.rememberComponentOrGVSelectorState
import tornaco.apps.shortx.ui.addrule.fact.selector.AppComponentSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.ComponentType
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberAppComponentSelectorState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.toBaseSelectorActionOrNull
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun appActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val context = LocalContext.current

    val launchAppSelectorState =
        rememberSingleAppPkgSelectorState(onAppSelected = {
            val action = Action.LaunchApp(app = it)
            addAction(action)
        })
    SingleAppSelectorDialog(state = launchAppSelectorState)

    val launchAppByAppPkgSelectorState = rememberPkgAndUserListSelectorDialogState(
        title = LocalI18N.current["ui.action.launch.app.by.pkg"],
        onSelected = {
            addAction(Action.LaunchAppByPkg(stringPairs = it))
        })
    PackageNameAndUserSelectorDialog(state = launchAppByAppPkgSelectorState)

    val stopAppSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.StopApp(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.StopApp(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = stopAppSelectorState)

    val stopAppByPackageSelectorState = rememberPkgAndUserListSelectorDialogState(
        title = LocalI18N.current["ui.action.stop.app.by.pkg"],
        onSelected = {
            addAction(Action.StopAppByPkg(stringPairs = it))
        })
    PackageNameAndUserSelectorDialog(state = stopAppByPackageSelectorState)

    val enableAppByPkgSelectorState = rememberPkgAndUserListSelectorDialogState(
        title = LocalI18N.current["ui.action.enable.app.by.pkg"],
        onSelected = {
            addAction(Action.EnableAppByPkg(stringPairs = it))
        })
    PackageNameAndUserSelectorDialog(state = enableAppByPkgSelectorState)

    val disableAppByPkgSelectorState = rememberPkgAndUserListSelectorDialogState(
        title = LocalI18N.current["ui.action.disable.app.by.pkg"],
        onSelected = {
            addAction(Action.DisableAppByPkg(stringPairs = it))
        })
    PackageNameAndUserSelectorDialog(state = disableAppByPkgSelectorState)

    val removeTasksSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.RemoveTasks(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.RemoveTasks(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = removeTasksSelectorState)

    val removeTaskByPackageSelectorState = rememberPkgAndUserListSelectorDialogState(
        title = LocalI18N.current["ui.action.remove.tasks.by.pkg"],
        onSelected = {
            addAction(Action.RemoveTasksByPkg(stringPairs = it))
        })
    PackageNameAndUserSelectorDialog(state = removeTaskByPackageSelectorState)

    val startAppProcessSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.StartAppProcess(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.StartAppProcess(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = startAppProcessSelectorState)

    val startAppProcessByPkgSelectorState = rememberPkgAndUserListSelectorDialogState(
        title = LocalI18N.current["ui.action.start.app.process.by.pkg"],
        onSelected = {
            addAction(Action.StartAppProcessByPkg(stringPairs = it))
        })
    PackageNameAndUserSelectorDialog(state = startAppProcessByPkgSelectorState)

    val setAppInactiveSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.SetAppInactive(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.SetAppInactive(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = setAppInactiveSelectorState)

    val setAppInactiveByPkgSelectorState = rememberPkgAndUserListSelectorDialogState(
        title = LocalI18N.current["ui.action.set.app.inactive.by.pkg"],
        onSelected = {
            addAction(Action.SetAppInactiveByPkg(stringPairs = it))
        })
    PackageNameAndUserSelectorDialog(state = setAppInactiveByPkgSelectorState)

    val enableAppSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.EnableApp(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.EnableApp(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = enableAppSelectorState)

    val disableAppSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.DisableApp(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.DisableApp(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = disableAppSelectorState)

    val suspendAppSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.SuspendApp(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.SuspendApp(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = suspendAppSelectorState)

    val unsuspendAppSelectorState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.UnSuspendApp(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.UnSuspendApp(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = unsuspendAppSelectorState)

    val suspendAppByPkgSelectorState = rememberPkgAndUserListSelectorDialogState(
        title = LocalI18N.current["ui.action.suspend.app.by.pkg"],
        onSelected = {
            addAction(Action.SuspendAppByPkg(stringPairs = it))
        })
    PackageNameAndUserSelectorDialog(state = suspendAppByPkgSelectorState)

    val unsuspendAppByPkgSelectorState = rememberPkgAndUserListSelectorDialogState(
        title = LocalI18N.current["ui.action.unsuspend.app.by.pkg"],
        onSelected = {
            addAction(Action.UnSuspendAppByPkg(stringPairs = it))
        })
    PackageNameAndUserSelectorDialog(state = unsuspendAppByPkgSelectorState)

    val startActivityIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.StartActivityIntent>()
            action?.let {
                addAction(it)
            }
        }
    )

    val startServiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.StartService>()
            action?.let {
                addAction(it)
            }
        }
    )

    val startActivityComponentSelectorState = rememberAppComponentSelectorState(
        types = listOf(ComponentType.Activity),
        maxItemCount = 1,
        onAppComponentSelected = { infoList ->
            if (infoList.isNotEmpty()) {
                addAction(
                    Action.StartActivity(
                        componentNameAsString = infoList[0].component.toComponentName()
                            .flattenToString()
                    )
                )
            }
        }
    )
    AppComponentSelectorDialog(startActivityComponentSelectorState)

    val singleAppSelectorState = rememberSingleAppPkgSelectorState {
        startActivityComponentSelectorState.show(it.pkg)
    }
    SingleAppSelectorDialog(state = singleAppSelectorState)


    val startActivityUrlSchemaDialogState = rememberStartActivityUrlSchemaDialogState {
        addAction(it)
    }
    StartActivityUrlSchemaDialog(state = startActivityUrlSchemaDialogState)

    val startActivityIntentUrlSelectorState = rememberTextInputState(
        title = LocalI18N.current["ui.action.start.activity.intent.uri"],
        message = null,
        onSelected = {
            addAction(Action.StartActivityIntentUri(intentUri = it))
        }
    )
    TextInputDialog(state = startActivityIntentUrlSelectorState)


    val stopServiceSelectorState =
        rememberComponentOrGVSelectorState(
            types = listOf(ComponentType.Service),
            onSelected = { it, _ ->
                addAction(Action.StopService(services = it.components))
            })
    ComponentOrGVSelectorDialog(state = stopServiceSelectorState)

    val killProcessSelectorState = rememberProcessListSelectorState(onSelected = {
        addAction(Action.KillProcessByName(processList = it))
    })
    ProcessListSelectorDialog(state = killProcessSelectorState)


    val pickShortcutLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.AppShortcut>()
            action?.let {
                addAction(it)
            }
        }
    )

    val foreachPkgSetLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.ForEachPkgSet>()
            action?.let {
                addAction(it)
            }
        }
    )


    return { source: ActionSelectSource ->
        when (source.clazz) {
            Action.StartActivityIntent::class -> {
                startActivityIntentLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getActionAs<Action.StartActivityIntent>(),
                        StartActivityIntentSelectorActivity::class.java
                    )
                )
                true
            }

            Action.StartActivityUrlSchema::class -> {
                startActivityUrlSchemaDialogState.show(source.getActionAs())
                true
            }

            Action.StartActivityIntentUri::class -> {
                startActivityIntentUrlSelectorState.show(source.getActionAs<Action.StartActivityIntentUri>()?.intentUri)
                true
            }

            Action.StartActivity::class -> {
                singleAppSelectorState.show()
                true
            }

            Action.LaunchApp::class -> {
                launchAppSelectorState.show()
                true
            }

            Action.LaunchAppByPkg::class -> {
                launchAppByAppPkgSelectorState.show(
                    source.getActionAs<Action.LaunchAppByPkg>()?.stringPairs
                )
                true
            }

            Action.StopApp::class -> {
                stopAppSelectorState.show(
                    source.getActionAs<Action.StopApp>()?.apps,
                    source.getActionAs<Action.StopApp>()?.pkgSets
                )
                true
            }

            Action.StopAppByPkg::class -> {
                stopAppByPackageSelectorState.show(
                    source.getActionAs<Action.StopAppByPkg>()?.stringPairs
                )
                true
            }

            Action.RemoveTasks::class -> {
                removeTasksSelectorState.show(
                    source.getActionAs<Action.RemoveTasks>()?.apps,
                    source.getActionAs<Action.RemoveTasks>()?.pkgSets
                )
                true
            }

            Action.RemoveTasksByPkg::class -> {
                removeTaskByPackageSelectorState.show(
                    source.getActionAs<Action.RemoveTasksByPkg>()?.stringPairs,
                )
                true
            }

            Action.StartAppProcess::class -> {
                startAppProcessSelectorState.show(
                    source.getActionAs<Action.StartAppProcess>()?.apps,
                    source.getActionAs<Action.StartAppProcess>()?.pkgSets
                )
                true
            }

            Action.StartAppProcessByPkg::class -> {
                startAppProcessByPkgSelectorState.show(
                    source.getActionAs<Action.StartAppProcessByPkg>()?.stringPairs
                )
                true
            }

            Action.SetAppInactive::class -> {
                setAppInactiveSelectorState.show(
                    source.getActionAs<Action.SetAppInactive>()?.apps,
                    source.getActionAs<Action.SetAppInactive>()?.pkgSets
                )
                true
            }

            Action.SetAppInactiveByPkg::class -> {
                setAppInactiveByPkgSelectorState.show(
                    source.getActionAs<Action.SetAppInactiveByPkg>()?.stringPairs
                )
                true
            }

            Action.EnableApp::class -> {
                enableAppSelectorState.show(
                    source.getActionAs<Action.EnableApp>()?.apps,
                    source.getActionAs<Action.EnableApp>()?.pkgSets
                )
                true
            }

            Action.DisableApp::class -> {
                disableAppSelectorState.show(
                    source.getActionAs<Action.DisableApp>()?.apps,
                    source.getActionAs<Action.DisableApp>()?.pkgSets
                )
                true
            }

            Action.EnableAppByPkg::class -> {
                enableAppByPkgSelectorState.show(
                    source.getActionAs<Action.EnableAppByPkg>()?.stringPairs
                )
                true
            }

            Action.DisableAppByPkg::class -> {
                disableAppByPkgSelectorState.show(
                    source.getActionAs<Action.DisableAppByPkg>()?.stringPairs
                )
                true
            }

            Action.SuspendApp::class -> {
                suspendAppSelectorState.show(
                    source.getActionAs<Action.SuspendApp>()?.apps,
                    source.getActionAs<Action.SuspendApp>()?.pkgSets
                )
                true
            }

            Action.UnSuspendApp::class -> {
                unsuspendAppSelectorState.show(
                    source.getActionAs<Action.UnSuspendApp>()?.apps,
                    source.getActionAs<Action.UnSuspendApp>()?.pkgSets
                )
                true
            }

            Action.SuspendAppByPkg::class -> {
                suspendAppByPkgSelectorState.show(
                    source.getActionAs<Action.SuspendAppByPkg>()?.stringPairs
                )
                true
            }

            Action.UnSuspendAppByPkg::class -> {
                unsuspendAppByPkgSelectorState.show(
                    source.getActionAs<Action.UnSuspendAppByPkg>()?.stringPairs
                )
                true
            }

            Action.StopService::class -> {
                stopServiceSelectorState.show(source.getActionAs<Action.StopService>()?.services)
                true
            }

            Action.StartService::class -> {
                startServiceLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getActionAs<Action.StartService>(),
                        StartServiceSelectorActivity::class.java
                    )
                )
                true
            }

            Action.AppShortcut::class -> {
                pickShortcutLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getActionAs<Action.AppShortcut>(),
                        AppShortcutSelectorActivity::class.java
                    )
                )
                true
            }

            Action.KillProcessByName::class -> {
                killProcessSelectorState.show(source.getActionAs<Action.KillProcessByName>()?.processList)
                true
            }

            Action.StartLastApp::class -> {
                addAction(Action.StartLastApp())
                true
            }

            Action.StopCurrentApp::class -> {
                addAction(Action.StopCurrentApp())
                true
            }

            Action.ForEachPkgSet::class -> {
                foreachPkgSetLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getActionAs<Action.ForEachPkgSet>(),
                        ForEachPkgSetSelectorActivity::class.java
                    )
                )
                true
            }

            else -> false
        }
    }
}

@Composable
fun AppActions(keyword: String, subscribeState: LVLStateHolder.State, addAction: (Action) -> Unit) {
    val i18N = LocalI18N.current
    val actionItems = arrayOf(
        Action.StartActivity::class,
        Action.StartActivityIntent::class,
        Action.StartActivityUrlSchema::class,
        Action.StartActivityIntentUri::class,
        Action.LaunchApp::class,
        Action.LaunchAppByPkg::class,
        Action.StartLastApp::class,
        Action.StopApp::class,
        Action.StopAppByPkg::class,
        Action.StopCurrentApp::class,
        Action.StartAppProcess::class,
        Action.StartAppProcessByPkg::class,
        Action.SetAppInactive::class,
        Action.SetAppInactiveByPkg::class,
        Action.EnableApp::class,
        Action.EnableAppByPkg::class,
        Action.DisableApp::class,
        Action.DisableAppByPkg::class,
        Action.SuspendApp::class,
        Action.UnSuspendApp::class,
        Action.SuspendAppByPkg::class,
        Action.UnSuspendAppByPkg::class,
        Action.StartService::class,
        Action.StopService::class,
        Action.RemoveTasks::class,
        Action.RemoveTasksByPkg::class,
        Action.KillProcessByName::class,
        Action.AppShortcut::class,
        Action.ForEachPkgSet::class,
    ).map {
        SelectorItem(
            type = it,
            labelAndDescription = labelAndDescriptionForActionSelector(i18N, it),
            icon = imageVectorForAction(it),
            isForSubscribedOnly = remember(subscribeState) {
                !subscribeState.isSubscribed && isForSubscribedOnly(it)
            },
            iconColor = colorForAction(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    val categoryState = rememberCategoryState(categoryTitle = i18N[ActionCategory.Apps.i18NKey])
    if (actionItems.isNotEmpty()) {
        ActionCategory(i18N[ActionCategory.Apps.i18NKey], actionItems.size, categoryState)
    }

    val selectHandler = appActionSelectHandler(addAction = addAction)

    AnimatedVisibility(visible = categoryState.isExpand) {
        SelectorList(
            actionItems = actionItems,
            itemSelected = { it, _ ->
                selectHandler(ActionSelectSource.FromClass(it))
            }
        )
    }
}