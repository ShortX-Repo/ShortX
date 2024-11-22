package tornaco.apps.shortx.services.rule.actor.impl

import android.app.Dialog
import android.content.Context
import com.google.gson.Gson
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.SHOW_LIST_DIALOG_JSON_ICON_KEY
import tornaco.apps.shortx.core.proto.SHOW_LIST_DIALOG_JSON_VALUE_KEY
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.core.proto.action.ShowListDialog
import tornaco.apps.shortx.core.proto.action.ShowListDialogDataType
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
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
import tornaco.apps.shortx.ui.dialog.ListItem

object ShowListDialog : Actor {
    private val gson by lazy { Gson() }

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
        val isJson = d.dataType == ShowListDialogDataType.ShowListDialogDataType_Json

        val listItems: List<ListItem> = if (isJson) {
            val type =
                object : com.google.gson.reflect.TypeToken<List<Map<String, String>>>() {}.type
            val data: List<Map<String, String>> =
                gson.fromJson(d.data.compileContextAndVars(cv), type)
            logger.v("data: $data")
            data.mapIndexed { index, map ->
                ListItem(
                    id = index,
                    value = map[SHOW_LIST_DIALOG_JSON_VALUE_KEY]
                        ?: "Missing value in json. e.g. { $SHOW_LIST_DIALOG_JSON_VALUE_KEY: your value }",
                    icon = map[SHOW_LIST_DIALOG_JSON_ICON_KEY],
                    lines = map.filter {
                        it.key != SHOW_LIST_DIALOG_JSON_VALUE_KEY
                                && it.key != SHOW_LIST_DIALOG_JSON_ICON_KEY
                    }.values.toList()
                )
            }
        } else {
            val delimiterList = d.delimiterList.toTypedArray()
            logger.d("delimiterList: $delimiterList")
            d.data.compileContextAndVars(cv).split(*delimiterList).mapIndexed { index, s ->
                ListItem(
                    id = index,
                    value = s,
                    lines = listOf(s)
                )
            }
        }


        logger.v("listItems: $listItems")

        val selectedItemIdState = mutableListOf<Int>()

        val result: DialogAction =
            showMultipleChoiceDialog(
                context = context,
                style = d.style,
                listItems = listItems,
                title = d.title.compileContextAndVars(cv),
                isMultipleChoice = d.isMultipleChoice,
                cancelable = d.cancelable,
                needConfirmAction = d.needConfirmAction,
                selectedItemState = selectedItemIdState
            )

        val selectedItemValues = listItems.filter {
            selectedItemIdState.contains(it.id)
        }.map { it.value }

        logger.d("ShowListDialog, result is: $result $selectedItemIdState $selectedItemValues")

        withContext(Dispatchers.Default) {
            logger.d("ShowListDialog, selectedItemValues is: $selectedItemValues")
            contextData.add(
                d.customContextDataKey.forKey(ContextDataMapping.ShowListDialog.selectedListItem.name)
                    .fallbackOnEmpty { ContextDataMapping.ShowListDialog.selectedListItem.name },
                selectedItemValues
            )
        }
    }

    private suspend fun showMultipleChoiceDialog(
        context: Context,
        style: DialogUiStyleSettings,
        listItems: List<ListItem>,
        title: String,
        isMultipleChoice: Boolean,
        cancelable: Boolean,
        needConfirmAction: Boolean,
        selectedItemState: MutableList<Int>,
    ): DialogAction {
        return suspendCancellableCoroutine { continuation ->
            val result: DialogAction = DialogAction.None

            val androidDialog = Dialog(context)

            val guideLineView =
                ListDialogView(
                    context = context,
                    i18N = i18NIns,
                    style = style,
                    title = title,
                    multipleSelection = isMultipleChoice,
                    defaultSelectedItems = emptyList(),
                    listItems = listItems,
                    onSelectionChange = {
                        selectedItemState.clear()
                        selectedItemState.addAll(it)
                    },
                    needConfirmAction = needConfirmAction,
                    onDismissRequest = {
                        androidDialog.dismiss()
                    }
                )
            guideLineView.initView()

            androidDialog.showInSystemServer(guideLineView, cancelable) {
                continuation.resume(result) {}
            }

            continuation.invokeOnCancellation {
                androidDialog.dismiss()
            }
        }
    }
}

