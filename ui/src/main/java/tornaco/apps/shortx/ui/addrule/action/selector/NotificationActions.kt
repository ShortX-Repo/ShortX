package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.ActionCategory
import tornaco.apps.shortx.ui.addrule.base.AppPkgSelectorDialog
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.rememberAppPkgSelectorState
import tornaco.apps.shortx.ui.addrule.base.rememberCategoryState
import tornaco.apps.shortx.ui.addrule.fact.selector.NotificationAppAndMatcherSelectorDialog
import tornaco.apps.shortx.ui.addrule.fact.selector.rememberNotificationAppAndMatcherSelectorState
import tornaco.apps.shortx.ui.base.BaseSelectorActivity
import tornaco.apps.shortx.ui.base.BaseSelectorActivity.Companion.toBaseSelectorActionOrNull
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun notificationActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val context = LocalContext.current

    val removeNotificationDialogState =
        rememberAppPkgSelectorState(
            onAppListSelected = {
                val action = Action.RemoveNotificationForPackage(apps = it)
                addAction(action)
            },
            onPkgSetSelected = {
                val action = Action.RemoveNotificationForPackage(pkgSets = it)
                addAction(action)
            })
    AppPkgSelectorDialog(state = removeNotificationDialogState)

    val removeNotificationByPkgSelectorState = rememberPkgAndUserListSelectorDialogState(
        title = LocalI18N.current["ui.action.remove.app.notification.by.pkg"],
        onSelected = {
            addAction(Action.RemoveNotificationForPackageByPkg(stringPairs = it))
        })
    PackageNameAndUserSelectorDialog(state = removeNotificationByPkgSelectorState)


    val postNotificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val action = result.toBaseSelectorActionOrNull<Action.PostNotification>()
            action?.let {
                addAction(it)
            }
        }
    )

    val removeNotificationState =
        rememberNotificationAppAndMatcherSelectorState(LocalI18N.current["ui.action.remove.notification"]) { it, _ ->
            addAction(Action.RemoveNotification(n = it))
        }
    NotificationAppAndMatcherSelectorDialog(removeNotificationState)

    val clickNotificationState =
        rememberNotificationAppAndMatcherSelectorState(LocalI18N.current["ui.action.click.notification"]) { it, _ ->
            addAction(Action.ClickNotification(n = it))
        }
    NotificationAppAndMatcherSelectorDialog(clickNotificationState)

    val clickNotificationButtonState =
        rememberNotificationAppAndMatcherSelectorState(
            LocalI18N.current["ui.action.click.notification.button"],
            canMatchButtonTitle = true
        ) { it, button ->
            addAction(Action.ClickNotificationActionButton(n = it, button = button))
        }
    NotificationAppAndMatcherSelectorDialog(clickNotificationButtonState)

    return { source: ActionSelectSource ->
        when (source.clazz) {
            Action.RemoveNotificationForPackage::class -> {
                removeNotificationDialogState.show(
                    source.getActionAs<Action.RemoveNotificationForPackage>()?.apps,
                    source.getActionAs<Action.RemoveNotificationForPackage>()?.pkgSets
                )
                true
            }

            Action.RemoveNotificationForPackageByPkg::class -> {
                removeNotificationByPkgSelectorState.show(
                    source.getActionAs<Action.RemoveNotificationForPackageByPkg>()?.stringPairs
                )
                true
            }

            Action.ExpandNotification::class -> {
                addAction(Action.ExpandNotification())
                true
            }

            Action.PostNotification::class -> {
                postNotificationLauncher.launch(
                    BaseSelectorActivity.newIntent(
                        context,
                        source.getActionAs<Action.PostNotification>(),
                        PostNotificationSelectorActivity::class.java
                    )
                )
                true
            }

            Action.RemoveNotification::class -> {
                removeNotificationState.show(source.getActionAs<Action.RemoveNotification>()?.n)
                true
            }

            Action.ClickNotification::class -> {
                clickNotificationState.show(source.getActionAs<Action.ClickNotification>()?.n)
                true
            }

            Action.ClickNotificationActionButton::class -> {
                clickNotificationButtonState.show(
                    source.getActionAs<Action.ClickNotificationActionButton>()?.n,
                    source.getActionAs<Action.ClickNotificationActionButton>()?.button
                )
                true
            }

            else -> false
        }
    }
}

@Composable
fun NotificationActions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    addAction: (Action) -> Unit
) {
    val i18N = LocalI18N.current
    val actionItems = arrayOf(
        Action.ExpandNotification::class,
        Action.PostNotification::class,
        Action.RemoveNotification::class,
        Action.ClickNotification::class,
        Action.ClickNotificationActionButton::class,
        Action.RemoveNotificationForPackage::class,
        Action.RemoveNotificationForPackageByPkg::class,
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

    val categoryState =
        rememberCategoryState(categoryTitle = i18N[ActionCategory.Notifications.i18NKey])
    if (actionItems.isNotEmpty()) {
        ActionCategory(i18N[ActionCategory.Notifications.i18NKey], actionItems.size, categoryState)
    }

    val selectHandler = notificationActionSelectHandler(addAction = addAction)

    AnimatedVisibility(visible = categoryState.isExpand) {
        SelectorList(
            actionItems = actionItems,
            itemSelected = { it, _ ->
                selectHandler(ActionSelectSource.FromClass(it))
            }
        )
    }
}