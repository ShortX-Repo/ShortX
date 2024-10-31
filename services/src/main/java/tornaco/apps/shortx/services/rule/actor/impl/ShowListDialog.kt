package tornaco.apps.shortx.services.rule.actor.impl

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.android.internal.widget.LinearLayoutManager
import com.android.internal.widget.RecyclerView
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

            val recyclerView = RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
            }
            val adapter =
                CustomMultiSelectAdapter(
                    recyclerView,
                    context,
                    listItems,
                    isMultiSelect = d.isMultipleChoice
                )
            recyclerView.adapter = adapter

            val dialogBuilder = AlertDialog.Builder(context)
                .setTitle(
                    d.title.orEmpty().compileContextAndVars(cv)
                )
                .setView(recyclerView)
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


class CustomMultiSelectAdapter(
    private val recyclerView: RecyclerView,
    private val context: Context,
    private val items: List<String>,
    private val isMultiSelect: Boolean = true // 配置参数，默认为多选模式
) : RecyclerView.Adapter<CustomMultiSelectAdapter.ViewHolder>() {

    // 用于存储每个选项的选中状态
    private val selectedItems = BooleanArray(items.size) { false }

    // ViewHolder类，包含CheckBox和TextView
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = CheckBox(context)
        val textView: TextView = TextView(context)

        init {
            // 动态添加CheckBox和TextView到itemView
            (view as LinearLayout).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL // 垂直居中
                setPadding(16, 16, 16, 16)
                addView(checkBox)

                // 配置 TextView，添加权重确保其占用剩余空间
                addView(
                    textView,
                    LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                /* width = */ ViewGroup.LayoutParams.MATCH_PARENT,
                /* height = */ ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]
        holder.checkBox.isChecked = selectedItems[position]

        holder.itemView.setOnClickListener {
            holder.checkBox.performClick()
        }

        holder.itemView.setOnClickListener {
            holder.checkBox.performClick()
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isMultiSelect) {
                selectedItems[position] = isChecked
            } else {
                if (isChecked) {
                    for (i in selectedItems.indices) {
                        selectedItems[i] = i == position
                    }
                }
            }
            recyclerView.postDelayed({ notifyDataSetChanged() }, 300)
        }
    }

    override fun getItemCount(): Int = items.size

    // 返回选中的选项
    fun getSelectedItems(): List<String> {
        return items.filterIndexed { index, _ -> selectedItems[index] }
    }

    // 返回单选模式下选中的索引
    fun getSelectedIndex(): Int? {
        return selectedItems.indexOfFirst { it }.takeIf { it >= 0 }
    }
}

