package tornaco.apps.shortx.services.rule.actor.impl

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
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ShowListDialog : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowListDialog::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ShowListDialog::class.java
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
        d: tornaco.apps.shortx.core.proto.action.ShowListDialog,
        contextData: ContextData,
        localVars: LocalVars
    ) = withContext(Dispatchers.Main) {
        logger.d("ShowListDialog, showing: ${d.data}")
        val cv = RuleContextAndVars(localVars, contextData, shortXContext)
        val listItems = d.data.compileContextAndVars(cv).split(",")
        logger.v("listItems: $listItems")

        val selectedItemState = mutableListOf<String>()
        val result = if (d.isMultipleChoice) {
            suspendCancellableCoroutine { continuation ->
                var result: DialogAction = DialogAction.None

                val listView = ListView(context).apply {
                    choiceMode = ListView.CHOICE_MODE_MULTIPLE
                }
                val adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_list_item_multiple_choice,
                    listItems
                )
                listView.adapter = adapter

                val dialogBuilder = AlertDialog.Builder(context)
                    .setTitle(
                        d.title.orEmpty().compileContextAndVars(cv)
                    )
                    .setView(listView)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
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
        } else {
            suspendCancellableCoroutine { continuation ->
                var result: DialogAction = DialogAction.None

                val listView = ListView(context)

                val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, listItems)
                listView.adapter = adapter
                // 监听 ListView 的点击事件
                listView.setOnItemClickListener { _, _, position, _ ->
                    val selectedItem = listItems[position]
                    selectedItemState.clear()
                    selectedItemState.add(selectedItem)
                }

                val dialogBuilder = AlertDialog.Builder(context)
                    .setTitle(
                        d.title.orEmpty().compileContextAndVars(cv)
                    )
                    .setView(listView)
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



            ShowTextFieldDialog.ShowDialog(context).invoke { dialogContext ->
                val listView = ListView(context)

                val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, listItems)
                listView.adapter = adapter
                // 监听 ListView 的点击事件
                listView.setOnItemClickListener { _, _, position, _ ->
                    val selectedItem = listItems[position]
                    selectedItemState.clear()
                    selectedItemState.add(selectedItem)
                }

                AlertDialog.Builder(dialogContext)
                    .setTitle(
                        d.title.orEmpty().compileContextAndVars(cv)
                    )
                    .setView(listView)
                    .setCancelable(d.cancelable)
            }
        }

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
}