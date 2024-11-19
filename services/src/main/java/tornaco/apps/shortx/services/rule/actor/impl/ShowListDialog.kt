package tornaco.apps.shortx.services.rule.actor.impl

import android.app.Dialog
import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.core.proto.action.ShowListDialog
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.ui.dialog.ListDialogView

object ShowListDialog : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return ShowListDialog::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ ShowListDialog::class.java
        evaluateShowDialogAction(
            context = androidUiContext,
            shortXContext = shortXContext,
            d = d,
            contextData = contextData,
            localVars = localVars
        )
    }

    private suspend fun evaluateShowDialogAction(
        context: Context,
        shortXContext: ShortXContext,
        d: ShowListDialog,
        contextData: ContextData,
        localVars: LocalVars
    ) = withContext(Dispatchers.Main) {
        logger.d("ShowListDialog, showing: ${d.data}")
        val cv = RuleContextAndVars(localVars, contextData, shortXContext)
        val delimiterList = d.delimiterList.toTypedArray()
        logger.d("delimiterList: $delimiterList")
        val listItems = d.data.compileContextAndVars(cv).split(*delimiterList)
        logger.v("listItems: $listItems")

        val selectedItemState = mutableListOf<String>()
        val result: DialogAction =
            showMultipleChoiceDialog(context, listItems, d, cv, selectedItemState)

        logger.d("ShowListDialog, result is: $result $selectedItemState")

        withContext(Dispatchers.Default) {
            logger.d("ShowListDialog, selectedItemState is: $selectedItemState")
            contextData.add(
                d.customContextDataKey.forKey(ContextDataMapping.ShowListDialog.selectedListItem.name)
                    .fallbackOnEmpty { ContextDataMapping.ShowListDialog.selectedListItem.name },
                selectedItemState
            )
        }
    }

    private suspend fun showMultipleChoiceDialog(
        context: Context,
        listItems: List<String>,
        d: ShowListDialog,
        cv: RuleContextAndVars,
        selectedItemState: MutableList<String>,
    ): DialogAction {
        return suspendCancellableCoroutine { continuation ->
            val result: DialogAction = DialogAction.None

            val androidDialog = Dialog(context)

            val guideLineView =
                ListDialogView(
                    context = context,
                    i18N = i18NIns,
                    title = d.title.compileContextAndVars(cv),
                    multipleSelection = d.isMultipleChoice,
                    defaultSelectedItems = emptyList(),
                    listItems = listItems,
                    onSelectionChange = {
                        selectedItemState.clear()
                        selectedItemState.addAll(it)
                    },
                    onDismissRequest = {
                        androidDialog.dismiss()
                    }
                )
            guideLineView.initView()

            androidDialog.showInSystemServer(guideLineView, d.cancelable) {
                continuation.resume(result) {}
            }

            continuation.invokeOnCancellation {
                androidDialog.dismiss()
            }
        }
    }
}

