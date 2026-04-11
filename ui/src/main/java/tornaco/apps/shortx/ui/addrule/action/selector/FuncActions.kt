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
import tornaco.apps.shortx.ui.base.InputValidateResult
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.base.TextInputDialog
import tornaco.apps.shortx.ui.base.rememberTextInputState
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly


@Composable
fun funcActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val matchRegexSelectorState = rememberMatchRegexState {
        addAction(it)
    }
    MatchRegexSelectorDialog(state = matchRegexSelectorState)

    val executeFunctionSelectorState = rememberExecuteFunctionDialogState {
        addAction(it)
    }
    ExecuteFunctionSelectorDialog(state = executeFunctionSelectorState)

    val replaceRegexState = rememberReplaceRegexState {
        addAction(it)
    }
    ReplaceRegexSelectorDialog(state = replaceRegexState)

    val setFunctionReturnValueState = rememberTextInputState(
        title = LocalI18N.current["ui.action.set.function.return.value"],
        message = null,
        inputValidator = { InputValidateResult.OK },
        onSelected = {
            addAction(Action.SetFunctionReturnValue(value = it))
        }
    )
    TextInputDialog(state = setFunctionReturnValueState)

    return { source ->
        when (source.clazz) {
            Action.ExecuteFunction::class -> {
                executeFunctionSelectorState.show(source.getActionAs())
                true
            }

            Action.MatchRegex::class -> {
                matchRegexSelectorState.show(source.getActionAs())
                true
            }

            Action.ReplaceRegex::class -> {
                replaceRegexState.show(source.getActionAs())
                true
            }

            Action.SetFunctionReturnValue::class -> {
                setFunctionReturnValueState.show(source.getActionAs<Action.SetFunctionReturnValue>()?.value)
                true
            }

            else -> {
                false
            }
        }
    }
}

@Composable
fun FuncActions(
    keyword: String,
    subscribeState: LVLStateHolder.State,
    selectedFilter: ActionCategory,
    addAction: (Action) -> Unit
) {
    val i18N = LocalI18N.current

    val actionItems = listOf(
        Action.ExecuteFunction::class,
        Action.SetFunctionReturnValue::class,
        Action.MatchRegex::class,
        Action.ReplaceRegex::class,
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
    }
        .filter {
            keyword.isBlank() || it.labelAndDescription.first.contains(keyword, ignoreCase = true)
        }

    val categoryState =
        rememberCategoryState(categoryTitle = i18N[ActionCategory.Functions.i18NKey])
    if (actionItems.isNotEmpty() && selectedFilter == ActionCategory.All) {
        ActionCategory(i18N[ActionCategory.Functions.i18NKey], actionItems.size, categoryState)
    }

    val selectHandler = funcActionSelectHandler(addAction = addAction)

    val isVisible =
        (categoryState.isExpand && selectedFilter == ActionCategory.All) || selectedFilter == ActionCategory.Functions
    AnimatedVisibility(visible = isVisible) {
        SelectorList(
            items = actionItems,
            itemSelected = {
                selectHandler(ActionSelectSource.FromClass(it))
            }
        )
    }
}