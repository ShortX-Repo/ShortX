package tornaco.apps.shortx.services.rule.actor.impl

import android.app.AlertDialog
import android.content.Context
import android.view.WindowManager
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.core.proto.action.ShowAlertDialog
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ShowAlertDialog : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowAlertDialog::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ ShowAlertDialog::class.java
        evaluateShowDialogAction(
            context = context,
            d = d,
            contextData = contextData,
            recursiveExecute = recursiveExecute
        )
    }

    private suspend fun evaluateShowDialogAction(
        context: Context,
        d: ShowAlertDialog,
        contextData: ContextData,
        recursiveExecute: suspend (logTag: String, action: ProtoAny, contextData: ContextData) -> Unit
    ) = withContext(Dispatchers.Main) {
        logger.d("ShowAlertDialog, showing")
        val result = ShowDialog(context).invoke(
            positiveButtonText = d.positive,
            negativeButtonText = d.negative,
            neutralButtonText = d.neutral
        ) {
            AlertDialog.Builder(it).setTitle(d.title).setMessage(d.message)
                .setCancelable(d.cancelable)
        }

        logger.d("ShowAlertDialog, result is: $result")

        withContext(Dispatchers.Default) {
            logger.d("handle result: $result")
            when (result) {
                DialogAction.Positive -> {
                    if (d.onPositiveList.isNullOrEmpty().not()) d.onPositiveList.forEach {
                        recursiveExecute("ShowAlertDialog", it, contextData)
                    }
                }

                DialogAction.Negative -> {
                    if (d.onNegativeList.isNullOrEmpty().not()) d.onNegativeList.forEach {
                        recursiveExecute("ShowAlertDialog", it, contextData)
                    }
                }

                DialogAction.Neutral -> {
                    if (d.onNeutralList.isNullOrEmpty().not()) d.onNeutralList.forEach {
                        recursiveExecute("ShowAlertDialog", it, contextData)
                    }
                }

                DialogAction.UNRECOGNIZED, DialogAction.None -> {
                    // Noop.
                }
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private class ShowDialog(val context: Context) {

    suspend inline fun invoke(
        positiveButtonText: CharSequence? = null,
        negativeButtonText: CharSequence? = null,
        neutralButtonText: CharSequence? = null,
        crossinline builder: (Context) -> AlertDialog.Builder
    ) = suspendCancellableCoroutine { continuation ->
        val dialogBuilder = builder(context)

        var result: DialogAction = DialogAction.None

        if (positiveButtonText != null) dialogBuilder.setPositiveButton(positiveButtonText) { _, _ ->
            result = DialogAction.Positive
        }

        if (negativeButtonText != null) dialogBuilder.setNegativeButton(negativeButtonText) { _, _ ->
            result = DialogAction.Negative
        }

        if (neutralButtonText != null) dialogBuilder.setNeutralButton(neutralButtonText) { _, _ ->
            result = DialogAction.Neutral
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