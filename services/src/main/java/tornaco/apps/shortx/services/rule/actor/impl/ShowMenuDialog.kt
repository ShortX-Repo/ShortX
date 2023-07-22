package tornaco.apps.shortx.services.rule.actor.impl

import android.R
import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.action.DialogMenuItem
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object ShowMenuDialog : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowMenuDialog::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ShowMenuDialog::class.java
        evaluateShowDialogAction(
            context = context,
            shortXContext = shortXContext,
            d = d,
            contextData = contextData,
            recursiveExecute = recursiveExecute
        )
    }

    private suspend fun evaluateShowDialogAction(
        context: Context,
        shortXContext: ShortXContext,
        d: tornaco.apps.shortx.core.proto.action.ShowMenuDialog,
        contextData: ContextData,
        recursiveExecute: suspend (logTag: String, action: ProtoAny, contextData: ContextData) -> ActionEvaluator.EvaluateResult
    ) = withContext(Dispatchers.Main) {
        logger.d("ShowAlertDialog, showing")
        val allGlobalVars = { shortXContext.rule.getAllGlobalVars() }
        val result = ShowDialog(context).invoke(d.itemsList) {
            AlertDialog.Builder(it)
                .setTitle(
                    d.title.inflateContextMap(contextData)
                        .replaceGlobalVarValues(allGlobalVars)
                )
                .setMessage(
                    d.message.inflateContextMap(contextData)
                        .replaceGlobalVarValues(allGlobalVars)
                )
                .setCancelable(d.cancelable)
        }

        logger.d("ShowAlertDialog, result is: $result")

        withContext(Dispatchers.Default) {
            logger.d("handle result: $result")

        }
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
private class ShowDialog(val context: Context) {

    suspend inline fun invoke(
        menuItems: List<DialogMenuItem>,
        crossinline builder: (Context) -> AlertDialog.Builder
    ) = suspendCancellableCoroutine { continuation ->
        val dialogBuilder = builder(context)

        var result: DialogMenuItem? = null

        dialogBuilder.setAdapter(
            ArrayAdapter(
                context,
                R.layout.simple_list_item_2,
                listOf(mapOf("name" to "Z"), mapOf("age" to 18))
            )
        ) { dialog, which ->

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
