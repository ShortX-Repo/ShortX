package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.ActionCategory
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.rememberCategoryState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.MDIconSelectorDialog
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberMDIconSelectorState
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly


@Composable
fun miscActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val noActionIconSelectorState = rememberMDIconSelectorState(onSelected = {
        addAction(Action.NoAction(icon = it))
    })
    MDIconSelectorDialog(state = noActionIconSelectorState)

    val ttsDialogState = rememberTextInputState(
        title = LocalI18N.current["ui.action.tts"],
        message = null,
        onSelected = {
            addAction(Action.TTS(text = it))
        })
    TextInputDialog(state = ttsDialogState)

    val downloadFileSelectorState = rememberDownloadFileSelectorState {
        addAction(it)
    }
    DownloadFileSelectorDialog(state = downloadFileSelectorState)

    return { source ->
        when (source.clazz) {
            Action.NoAction::class -> {
                noActionIconSelectorState.show()
                true
            }

            Action.TTS::class -> {
                ttsDialogState.show(initialValue = source.getActionAs<Action.TTS>()?.text)
                true
            }

            Action.DownloadFile::class -> {
                downloadFileSelectorState.show(current = source.getActionAs())
                true
            }

            else -> {
                false
            }
        }
    }
}

@Composable
fun MiscActions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    selectedFilter: ActionCategory,
    addAction: (Action) -> Unit
) {
    val i18N = LocalI18N.current

    val actionItems = listOf(
        Action.TTS::class,
        Action.DownloadFile::class,
        Action.NoAction::class,
    ).map {
        SelectorItem(
            it,
            labelAndDescriptionForActionSelector(i18N, it),
            imageVectorForAction(it),
            isForSubscribedOnly = remember(subscribeState) {
                !subscribeState.isSubscribed && isForSubscribedOnly(it)
            },
            iconColor = colorForAction(it)
        )
    }.filter {
        keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
    }

    val categoryState = rememberCategoryState(categoryTitle = i18N[ActionCategory.Misc.i18NKey])
    if (actionItems.isNotEmpty() && selectedFilter == ActionCategory.All) {
        ActionCategory(i18N[ActionCategory.Misc.i18NKey], actionItems.size, categoryState)
    }

    val selectHandler = miscActionSelectHandler(addAction = addAction)

    val isVisible =
        (categoryState.isExpand && selectedFilter == ActionCategory.All) || selectedFilter == ActionCategory.Misc
    AnimatedVisibility(visible = isVisible) {
        SelectorList(
            items = actionItems,
            itemSelected = { it ->
                selectHandler(ActionSelectSource.FromClass(it))
            }
        )
    }
}