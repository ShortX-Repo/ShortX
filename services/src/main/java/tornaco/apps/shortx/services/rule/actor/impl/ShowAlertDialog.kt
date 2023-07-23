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
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.core.proto.action.ShowAlertDialog
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.ActionEvaluator
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
        d: ShowAlertDialog,
        contextData: ContextData,
        localVars: LocalVars,
        recursiveExecute: suspend (
            logTag: String,
            action: ProtoAny,
            contextData: ContextData,
            localVars: LocalVars
        ) -> ActionEvaluator.EvaluateResult
    ) = withContext(Dispatchers.Main) {
        logger.d("ShowAlertDialog, showing")
        val allGlobalVars = { shortXContext.rule.getAllGlobalVars() }
        val result = ShowDialog(context).invoke(
            positiveButtonText = d.positive.inflateContextMap(contextData)
                .replaceGlobalVarValues(allGlobalVars),
            negativeButtonText = d.negative.inflateContextMap(contextData)
                .replaceGlobalVarValues(allGlobalVars),
            neutralButtonText = d.neutral.inflateContextMap(contextData)
                .replaceGlobalVarValues(allGlobalVars)
        ) {
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
            when (result) {
                DialogAction.Positive -> {
                    if (d.onPositiveList.isNullOrEmpty().not()) d.onPositiveList.forEach {
                        val evaRes = recursiveExecute("ShowAlertDialog", it, contextData, localVars)
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

                DialogAction.Negative -> {
                    if (d.onNegativeList.isNullOrEmpty().not()) d.onNegativeList.forEach {
                        val evaRes = recursiveExecute("ShowAlertDialog", it, contextData, localVars)
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

                DialogAction.Neutral -> {
                    if (d.onNeutralList.isNullOrEmpty().not()) d.onNeutralList.forEach {
                        val evaRes = recursiveExecute("ShowAlertDialog", it, contextData, localVars)
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

                DialogAction.UNRECOGNIZED, DialogAction.None -> {
                    // Noop.
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
}
