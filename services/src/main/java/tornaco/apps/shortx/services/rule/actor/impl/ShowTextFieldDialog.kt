package tornaco.apps.shortx.services.rule.actor.impl

import android.app.Dialog
import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.ui.dialog.EditTextItem
import tornaco.apps.shortx.ui.dialog.EditTextListDialogView


object ShowTextFieldDialog : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowTextFieldDialog::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ShowTextFieldDialog::class.java
        evaluateShowDialogAction(
            context = androidContext,
            shortXContext = shortXContext,
            d = d,
            contextData = contextData,
            localVars = localVars
        )
    }

    private suspend fun evaluateShowDialogAction(
        context: Context,
        shortXContext: ShortXContext,
        d: tornaco.apps.shortx.core.proto.action.ShowTextFieldDialog,
        contextData: ContextData,
        localVars: LocalVars
    ) = withContext(Dispatchers.Main) {
        logger.d("ShowTextFieldDialog, showing")
        val cv = RuleContextAndVars(localVars, contextData, shortXContext)

        val tagAndValue = mutableMapOf<String, String>().apply {
            d.textFieldsList.forEachIndexed { index, textFieldProp ->
                val key =
                    ContextDataMapping.ShowTextFieldDialog.textFieldInput.name + (index + 1)
                val value = ""
                put(key, value)
            }
        }

        suspendCancellableCoroutine { continuation ->
            val result: DialogAction = DialogAction.None
            val androidDialog = Dialog(context)

            val dialogView =
                EditTextListDialogView(
                    context = context,
                    i18N = i18NIns,
                    style = d.style,
                    title = d.title.compileContextAndVars(cv),
                    items = d.textFieldsList.mapIndexed { index, textFieldProp ->
                        EditTextItem(
                            value = "",
                            placeholder = textFieldProp.placeholder.compileContextAndVars(cv),
                            onValueChange = {
                                val key =
                                    ContextDataMapping.ShowTextFieldDialog.textFieldInput.name + (index + 1)
                                tagAndValue[key] = it
                            },
                        )
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

        logger.d("tagAndValue: $tagAndValue")
        tagAndValue.forEach {
            contextData.add(it.key, it.value)
        }
    }
}