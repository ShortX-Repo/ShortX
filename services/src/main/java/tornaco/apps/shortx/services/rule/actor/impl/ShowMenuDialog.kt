package tornaco.apps.shortx.services.rule.actor.impl

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
import tornaco.apps.shortx.core.localvar.LocalVars
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
            recursiveExecute = recursiveExecute,
            localVars = localVars
        )
    }

    private suspend fun evaluateShowDialogAction(
        context: Context,
        shortXContext: ShortXContext,
        d: tornaco.apps.shortx.core.proto.action.ShowMenuDialog,
        contextData: ContextData,
        localVars: LocalVars,
        recursiveExecute: suspend (
            logTag: String,
            action: ProtoAny,
            contextData: ContextData,
            localVars: LocalVars
        ) -> ActionEvaluator.EvaluateResult
    ) = withContext(Dispatchers.Main) {
        logger.d("ShowMenuDialog, showing: ${d.itemsList.size} ${d.cancelable}")
        val allGlobalVars = { shortXContext.rule.getAllGlobalVars() }
        val result = ShowDialog(context, shortXContext, contextData).invoke(d.itemsList) {
            AlertDialog.Builder(it)
                .setTitle(
                    d.title.inflateContextMap(contextData)
                        .replaceGlobalVarValues(allGlobalVars)
                )
                .setCancelable(d.cancelable || d.itemsList.isEmpty())
        }

        logger.d("ShowMenuDialog, result is: $result")

        withContext(Dispatchers.Default) {
            logger.d("handle result: $result")
            val actions = result?.clickActionsList
            if (!actions.isNullOrEmpty()) actions.forEach {
                val evaRes = recursiveExecute("ShowMenuDialog", it, contextData, localVars)
                if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                    logger.e(evaRes.error, "evaluate fail")
                    if (evaRes.shouldContinue) {
                        logger.d("Action on error is continue execute.")
                    } else {
                        return@forEach
                    }
                } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                    logger.w("evaluate break!")
                    return@forEach
                }
            }

        }
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
private class ShowDialog(
    val context: Context,
    val shortXContext: ShortXContext,
    val contextData: ContextData
) {
    suspend inline fun invoke(
        menuItems: List<DialogMenuItem>,
        crossinline builder: (Context) -> AlertDialog.Builder
    ) = suspendCancellableCoroutine { continuation ->
        val dialogBuilder = builder(context)

        var result: DialogMenuItem? = null

        dialogBuilder.setAdapter(
            ArrayAdapter(
                context,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                menuItems.map {
                    it.title.inflateContextMap(contextData).replaceGlobalVarValues {
                        shortXContext.rule.getAllGlobalVars()
                    }
                }
            )
        ) { _, which ->
            result = menuItems[which]
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
