package tornaco.apps.shortx.services.rule.actor.impl

import android.R
import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
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
            when (result) {
                DialogAction.Positive -> {
                    contextData.add(
                        d.customContextDataKey.forKey(ContextDataMapping.ShowListDialog.selectedListItem.name)
                            .fallbackOnEmpty { ContextDataMapping.ShowListDialog.selectedListItem.name },
                        selectedItemState
                    )
                }

                else -> {
                    // Noop.
                }
            }
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
            var result: DialogAction = DialogAction.None

            val listView = ListView(context).apply {
                choiceMode =
                    if (d.isMultipleChoice) {
                        ListView.CHOICE_MODE_MULTIPLE
                    } else {
                        ListView.CHOICE_MODE_SINGLE
                    }
            }
            val adapter = ArrayAdapter(
                context,
                if (d.isMultipleChoice) {
                    R.layout.simple_list_item_multiple_choice
                } else {
                    R.layout.simple_list_item_single_choice
                },
                listItems
            )
            listView.adapter = adapter

            val dialogBuilder = AlertDialog.Builder(context)
                .setTitle(
                    d.title.orEmpty().compileContextAndVars(cv)
                )
                .setView(listView)
                .setPositiveButton(R.string.ok) { _, _ ->
                    // 获取选中的项
                    val selectedItems = mutableListOf<String>()
                    for (i in 0 until listView.count) {
                        if (listView.isItemChecked(i)) {
                            selectedItems.add(listItems[i])
                        }
                    }
                    selectedItemState.clear()
                    selectedItemState.addAll(selectedItems)
                    result = DialogAction.Positive
                }
                .setCancelable(d.cancelable)


            dialogBuilder.setOnDismissListener {
                continuation.resume(result) {}
            }

            val dialog = dialogBuilder.create().apply {
                window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG)
                show()
            }

            continuation.invokeOnCancellation {
                dialog.dismiss()
            }

        }
    }
}