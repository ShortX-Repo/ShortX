package tornaco.apps.shortx.services.rule.actor.impl

import android.app.Dialog
import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.action.DialogMenuItem
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.ui.dialog.MenuDialogView
import tornaco.apps.shortx.ui.dialog.MenuItem

object ShowMenuDialog : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowMenuDialog::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ShowMenuDialog::class.java
        evaluateShowDialogAction(
            context = androidUiContext,
            shortXContext = shortXContext,
            evaluateContext = evaluateContext,
            d = d,
            contextData = contextData,
            recursiveExecute = recursiveExecute,
            localVars = localVars,
            parameterInputs = parameterInputs
        )
    }

    private suspend fun evaluateShowDialogAction(
        context: Context,
        shortXContext: ShortXContext,
        evaluateContext: EvaluateContext,
        d: tornaco.apps.shortx.core.proto.action.ShowMenuDialog,
        contextData: ContextData,
        localVars: LocalVars,
        parameterInputs: List<FuncParameterInput>,

        recursiveExecute: suspend (
            evaluateContext: EvaluateContext,
            action: ProtoAny,
            contextData: ContextData,
            localVars: LocalVars,
            parameterInputs: List<FuncParameterInput>
        ) -> ActionEvaluator.EvaluateResult
    ) = withContext(Dispatchers.Main) {
        logger.d("ShowMenuDialog, showing: ${d.itemsList.size} ${d.cancelable}")
        val cv = RuleContextAndVars(localVars, contextData, shortXContext)

        val result = suspendCancellableCoroutine { continuation ->
            var clickedItem: DialogMenuItem? = null
            val androidDialog = Dialog(context)

            val dialogView =
                MenuDialogView(
                    context = context,
                    i18N = i18NIns,
                    style = d.style,
                    title = d.title.compileContextAndVars(cv),
                    menuItems = d.itemsList.mapIndexed { index, dialogMenuItem ->
                        MenuItem(
                            id = index,
                            title = dialogMenuItem.title.compileContextAndVars(cv),
                            message = dialogMenuItem.message.takeIf { it.isNotBlank() }
                                ?.compileContextAndVars(cv)
                        )
                    },
                    onItemClick = {
                        clickedItem = d.itemsList[it]
                        androidDialog.dismiss()
                    },
                    onDismissRequest = {
                        androidDialog.dismiss()
                    }
                )
            dialogView.initView()

            androidDialog.showInSystemServer(dialogView, d.cancelable) {
                continuation.resume(clickedItem) {}
            }

            continuation.invokeOnCancellation {
                androidDialog.dismiss()
            }
        }

        logger.d("ShowMenuDialog, result is: $result")

        withContext(Dispatchers.Default) {
            logger.d("handle result: $result")
            val actions = result?.clickActionsList
            run loop@{
                if (!actions.isNullOrEmpty()) actions.forEach {
                    val evaRes = recursiveExecute(
                        evaluateContext,
                        it,
                        contextData,
                        localVars,
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
    }
}