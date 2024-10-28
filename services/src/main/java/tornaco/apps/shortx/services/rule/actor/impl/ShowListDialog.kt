package tornaco.apps.shortx.services.rule.actor.impl

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
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
            val adapter = CheckboxAdapter(
                context,
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
                    val selectedItems = adapter.getSelectedItems()
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


// 自定义适配器
class CheckboxAdapter(context: Context, private val items: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, items) {

    // 用于保存每个项目的选中状态
    private val checkedStates = BooleanArray(items.size)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // 创建根布局
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 8, 8, 8)
        }

        // 创建复选框
        val checkBox = CheckBox(context).apply {
            isChecked = checkedStates[position]
        }

        // 创建文本视图
        val textView = TextView(context).apply {
            text = items[position]
            textSize = 16f
        }

        // 设置点击事件来更新复选状态
        val onItemClick: (View) -> Unit = {
            checkedStates[position] = !checkedStates[position]
            checkBox.isChecked = checkedStates[position]
        }
        checkBox.setOnClickListener(onItemClick)
        textView.setOnClickListener(onItemClick)  // 点击文本也触发选择

        // 将复选框和文本视图添加到布局中
        layout.addView(checkBox)
        layout.addView(textView)

        return layout
    }

    // 获取选中项
    fun getSelectedItems(): List<String> {
        return items.filterIndexed { index, _ -> checkedStates[index] }
    }
}