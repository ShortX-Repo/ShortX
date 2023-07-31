package tornaco.apps.shortx.services.rule.actor.impl

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShowTextFieldDialog : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowTextFieldDialog::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ShowTextFieldDialog::class.java
        evaluateShowDialogAction(
            context = context,
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
        val linearLayout = LinearLayout(context)

        val allGlobalVars = { shortXContext.rule.getAllGlobalVars() }
        val result = ShowDialog(context).invoke { dialogContext ->
            val messageText = TextView(context)
            messageText.text = d.message
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.gravity = Gravity.CENTER
            linearLayout.addView(messageText, commonElementLP)

            d.textFieldsList.forEachIndexed { index, field ->
                val editText = EditText(context)
                editText.hint = field.placeholder
                editText.id = 10086 + index
                editText.tag =
                    ContextDataMapping.ShowTextFieldDialog.textFieldInput.name + (index + 1)
                linearLayout.addView(editText, commonElementLP)
            }

            AlertDialog.Builder(dialogContext)
                .setTitle(
                    d.title.inflateContextMap(contextData)
                        .replaceLocalVarValues(localVars)
                        .replaceGlobalVarValues(allGlobalVars)
                )
                .setCancelable(d.cancelable)
                .setView(linearLayout)
        }

        logger.d("ShowAlertDialog, result is: $result")

        withContext(Dispatchers.Default) {
            logger.d("handle result: $result")
            when (result) {
                DialogAction.Positive -> {
                    d.textFieldsList.forEachIndexed { index, _ ->
                        val editText = linearLayout.findViewById<EditText>(10086 + index)
                        val text = editText.editableText.toString()
                        val tag = editText.tag.toString()
                        logger.d("editText: $editText, text: $text, tag: $tag")
                        contextData.add(tag, text)
                    }
                }

                else -> {
                    // Noop.
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private class ShowDialog(val context: Context) {

        suspend inline fun invoke(
            crossinline builder: (Context) -> AlertDialog.Builder
        ) = suspendCancellableCoroutine { continuation ->
            val dialogBuilder = builder(context)

            var result: DialogAction = DialogAction.None

            dialogBuilder.setPositiveButton(android.R.string.ok) { _, _ ->
                result = DialogAction.Positive
            }

            dialogBuilder.setNegativeButton(android.R.string.cancel) { _, _ ->
                result = DialogAction.Negative
            }

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


private val commonElementLP = LinearLayout.LayoutParams(
    LinearLayout.LayoutParams.MATCH_PARENT,
    LinearLayout.LayoutParams.WRAP_CONTENT
).apply {
    setMargins(16._dp, 16._dp, 16._dp, 16._dp)
}