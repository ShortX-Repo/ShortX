package tornaco.apps.shortx.services.rule.actor.impl

import android.app.Dialog
import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.core.proto.action.ShowAlertDialog
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.ui.dialog.AlertDialogView
import tornaco.apps.shortx.ui.dialog.DialogButton


object ShowAlertDialog : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowAlertDialog::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ ShowAlertDialog::class.java
        val cv = RuleContextAndVars(params)
        evaluateShowDialogAction(
            context = androidUiContext,
            evaluateContext = evaluateContext,
            d = d,
            parameterInputs = parameterInputs,
            recursiveExecute = recursiveExecute,
            cv = cv
        )
    }

    private suspend fun evaluateShowDialogAction(
        context: Context,
        evaluateContext: EvaluateContext,
        d: ShowAlertDialog,
        cv: RuleContextAndVars,
        parameterInputs: List<FuncParameterInput>,

        recursiveExecute: suspend (
            evaluateContext: EvaluateContext,
            action: ProtoAny,
            contextData: ContextData,
            localVars: LocalVars,
            parameterInputs: List<FuncParameterInput>
        ) -> ActionEvaluator.EvaluateResult
    ) = withContext(Dispatchers.Main) {
        logger.d("ShowAlertDialog, showing")
        val result = suspendCancellableCoroutine { continuation ->
            var result: DialogAction = DialogAction.None
            val androidDialog = Dialog(context)

            val dialogView =
                AlertDialogView(
                    context = context,
                    i18N = i18NIns,
                    style = d.style,
                    title = d.title.compileContextAndVars(cv),
                    message = d.message.compileContextAndVars(cv),
                    positiveText = d.positive.takeIf { it.isNotBlank() }?.compileContextAndVars(cv),
                    negativeText = d.negative.takeIf { it.isNotBlank() }?.compileContextAndVars(cv),
                    neutralText = d.neutral.takeIf { it.isNotBlank() }?.compileContextAndVars(cv),
                    onDismissRequest = { btn ->
                        result = when (btn) {
                            DialogButton.Positive -> {
                                DialogAction.Positive
                            }

                            DialogButton.Negative -> {
                                DialogAction.Negative
                            }

                            DialogButton.Neutral -> {
                                DialogAction.Neutral
                            }
                        }
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

        logger.d("ShowAlertDialog, result is: $result")

        withContext(Dispatchers.Default) {
            logger.d("handle result: $result")
            when (result) {
                DialogAction.Positive -> {
                    run loop@{
                        if (d.onPositiveList.isNullOrEmpty().not()) d.onPositiveList.forEach {
                            val evaRes =
                                recursiveExecute(
                                    evaluateContext,
                                    it,
                                    cv.contextData,
                                    cv.localVars,
                                    parameterInputs
                                )
                            if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                                logger.e(evaRes.error, "evaluate fail")
                                if (evaRes.shouldContinue) {
                                    logger.d("Action on error is continue execute.")
                                } else {
                                    return@loop
                                }
                            } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                                logger.w("evaluate break!")
                                return@loop
                            }
                        }
                    }
                }

                DialogAction.Negative -> {
                    run loop@{
                        if (d.onNegativeList.isNullOrEmpty().not()) d.onNegativeList.forEach {
                            val evaRes =
                                recursiveExecute(
                                    evaluateContext,
                                    it,
                                    cv.contextData,
                                    cv.localVars,
                                    parameterInputs
                                )
                            if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                                logger.e(evaRes.error, "evaluate fail")
                                if (evaRes.shouldContinue) {
                                    logger.d("Action on error is continue execute.")
                                } else {
                                    return@loop
                                }
                            } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                                logger.w("evaluate break!")
                                return@loop
                            }
                        }
                    }
                }

                DialogAction.Neutral -> {
                    run loop@{
                        if (d.onNeutralList.isNullOrEmpty().not()) d.onNeutralList.forEach {
                            val evaRes =
                                recursiveExecute(
                                    evaluateContext,
                                    it,
                                    cv.contextData,
                                    cv.localVars,
                                    parameterInputs
                                )
                            if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                                logger.e(evaRes.error, "evaluate fail")
                                if (evaRes.shouldContinue) {
                                    logger.d("Action on error is continue execute.")
                                } else {
                                    return@loop
                                }
                            } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                                logger.w("evaluate break!")
                                return@loop
                            }
                        }
                    }
                }

                DialogAction.UNRECOGNIZED, DialogAction.None -> {
                    // Noop.
                }
            }
        }
    }
}
