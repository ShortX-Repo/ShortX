package tornaco.apps.shortx.services.rule.actor

import android.app.AlertDialog
import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import tornaco.apps.shortx.core.proto.action.DialogAction


@OptIn(ExperimentalCoroutinesApi::class)
class ShowDialog(val context: Context) {

    suspend inline fun show(
        positiveButtonText: CharSequence? = null,
        negativeButtonText: CharSequence? = null,
        neutralButtonText: CharSequence? = null,
        crossinline builder: (Context) -> AlertDialog.Builder
    ) = suspendCancellableCoroutine { continuation ->
        val dialogBuilder = builder(context)

        var result: DialogAction = DialogAction.None

        dialogBuilder.setPositiveButton(positiveButtonText) { _, _ ->
            result = DialogAction.Positive
        }

        dialogBuilder.setNegativeButton(negativeButtonText) { _, _ ->
            result = DialogAction.Negative
        }

        dialogBuilder.setNeutralButton(neutralButtonText) { _, _ ->
            result = DialogAction.Neutral
        }

        dialogBuilder.setOnDismissListener {
            continuation.resume(result) {}
        }

        val dialog = dialogBuilder.show()

        continuation.invokeOnCancellation {
            dialog.dismiss()
        }
    }
}