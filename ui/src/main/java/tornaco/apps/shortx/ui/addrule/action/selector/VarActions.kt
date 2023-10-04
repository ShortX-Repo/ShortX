package tornaco.apps.shortx.ui.addrule.action.selector


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import tornaco.apps.shortx.core.rule.GlobalVarType
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.ui.addrule.action.mapper.colorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.imageVectorForAction
import tornaco.apps.shortx.ui.addrule.action.mapper.labelAndDescriptionForActionSelector
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.base.ActionCategory
import tornaco.apps.shortx.ui.addrule.base.SelectorItem
import tornaco.apps.shortx.ui.addrule.base.SelectorList
import tornaco.apps.shortx.ui.addrule.base.rememberCategoryState
import tornaco.apps.shortx.ui.base.LocalI18N
import tornaco.apps.shortx.ui.gvar.NewGlobalVar
import tornaco.apps.shortx.ui.gvar.NewGlobalVarDialog
import tornaco.apps.shortx.ui.gvar.NewLocalVarDialog
import tornaco.apps.shortx.ui.gvar.rememberNewGlobalVarState
import tornaco.apps.shortx.ui.gvar.rememberNewLocalVarState
import tornaco.apps.shortx.ui.gvar.toGlobalVar
import tornaco.apps.shortx.ui.gvar.toLocalVar
import tornaco.apps.shortx.ui.subscribe.LVLStateHolder
import tornaco.apps.shortx.ui.subscribe.isForSubscribedOnly

@Composable
fun varActionSelectHandler(addAction: (Action) -> Unit): (ActionSelectSource) -> Boolean {
    val writeGVSelectorState = rememberWriteGVDialogState {
        addAction(it)
    }
    WriteGVDialog(writeGVSelectorState)

    val deleteGVSelectorState = rememberDeleteGVDialogState {
        addAction(it)
    }
    DeleteGVDialog(deleteGVSelectorState)

    val writeLocalVarSelectorState = rememberWriteLocalVarDialogState {
        addAction(it)
    }
    WriteLocalVarDialog(writeLocalVarSelectorState)

    val addGlobalVarDialogState =
        rememberNewGlobalVarState {
            addAction(
                Action.CreateGV(
                    globalVar = it.toGlobalVar()
                )
            )
        }
    NewGlobalVarDialog(addGlobalVarDialogState)

    val addLocalVarDialogState =
        rememberNewLocalVarState {
            addAction(
                Action.CreateLocalVar(
                    localVar = it.toLocalVar()
                )
            )
        }
    NewLocalVarDialog(addLocalVarDialogState)

    return { source ->
        when (source.clazz) {
            Action.WriteGV::class -> {
                writeGVSelectorState.show(source.getActionAs())
                true
            }

            Action.DeleteGV::class -> {
                deleteGVSelectorState.show(source.getActionAs())
                true
            }

            Action.CreateGV::class -> {
                addGlobalVarDialogState.show(source.getActionAs<Action.CreateGV>()?.let {
                    NewGlobalVar(
                        name = it.globalVar.name,
                        type = (it.globalVar.type.toType() ?: GlobalVarType.String),
                        isSecret = it.globalVar.isSecret
                    )
                })
                true
            }

            Action.CreateLocalVar::class -> {
                addLocalVarDialogState.show(source.getActionAs<Action.CreateLocalVar>()?.let {
                    it.localVar.name to (it.localVar.type.toType() ?: GlobalVarType.String)
                })
                true
            }

            Action.WriteLocalVar::class -> {
                writeLocalVarSelectorState.show(source.getActionAs())
                true
            }

            else -> {
                false
            }
        }

    }
}

@Composable
fun VarActions(keyword: String, subscribeState: LVLStateHolder.State, addAction: (Action) -> Unit) {
    val i18N = LocalI18N.current
    val actionItems = listOf(
        Action.WriteGV::class,
        Action.CreateGV::class,
        Action.DeleteGV::class,
        Action.CreateLocalVar::class,
        Action.WriteLocalVar::class,
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

    val categoryState = rememberCategoryState(categoryTitle = i18N[ActionCategory.Vars.i18NKey])
    if (actionItems.isNotEmpty()) {
        ActionCategory(i18N[ActionCategory.Vars.i18NKey], actionItems.size, categoryState)
    }

    val itemSelected = varActionSelectHandler(addAction = addAction)

    AnimatedVisibility(visible = categoryState.isExpand) {
        SelectorList(
            items = actionItems,
            itemSelected = { it ->
                itemSelected(ActionSelectSource.FromClass(it))
            }
        )
    }
}