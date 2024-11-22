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
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.ui.dialog.ChoiceDialogView
import tornaco.apps.shortx.ui.dialog.ChoiceItem

object ShowChoiceDialog : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowChoiceDialog::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ShowChoiceDialog::class.java
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
        d: tornaco.apps.shortx.core.proto.action.ShowChoiceDialog,
        contextData: ContextData,
        localVars: LocalVars
    ) = withContext(Dispatchers.Main) {
        logger.d("ShowChoiceDialog, showing: ${d.choicesList}")
        require(d.choicesList.isNotEmpty()) { "No choices!" }
        val cv = RuleContextAndVars(localVars, contextData, shortXContext)
        val checkedState = mutableListOf<Int>()

        suspendCancellableCoroutine { continuation ->
            val result: DialogAction = DialogAction.None
            val androidDialog = Dialog(context)

            val dialogView =
                ChoiceDialogView(
                    context = context,
                    i18N = i18NIns,
                    style = d.style,
                    title = d.title.compileContextAndVars(cv),
                    choices = d.choicesList.mapIndexed { index, choiceProp ->
                        ChoiceItem(
                            id = index,
                            label = choiceProp.label.compileContextAndVars(cv),
                            value = choiceProp.value.compileContextAndVars(cv),
                            isSelectedByDefault = choiceProp.isSelectedByDefault,
                            summary = null,
                        )
                    },
                    multipleSelection = d.isMultipleChoice,
                    onSelectionChange = {
                        checkedState.clear()
                        checkedState.addAll(it)
                    },
                    onDismissRequest = {
                        androidDialog.dismiss()
                    }
                )
            dialogView.initView()

            androidDialog.showInSystemServer(dialogView, d.cancelable) {
                continuation.resume(result) {}
            }

            continuation.invokeOnCancellation {
                androidDialog.dismiss()
            }
        }

        logger.d("ShowChoiceDialog dismiss, checkedState: $checkedState")

        withContext(Dispatchers.Default) {
            logger.d("ShowChoiceDialog, checkedState is: $checkedState")
            val values = d.choicesList.filterIndexed { index, _ ->
                checkedState.contains(index)
            }.map {
                it.value.compileContextAndVars(cv)
            }
            logger.d("ShowChoiceDialog handle values: $values")

            contextData.add(
                d.customContextDataKey.forKey(ContextDataMapping.ShowChoiceDialog.choices.name)
                    .fallbackOnEmpty { ContextDataMapping.ShowChoiceDialog.choices.name },
                values
            )
        }
    }
}