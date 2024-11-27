package tornaco.apps.shortx.services.rule.actor.impl

import android.app.Dialog
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.DialogAction
import tornaco.apps.shortx.core.proto.action.ShowAlertDialog
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.util.runSubActions
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

        withContext(Dispatchers.Main) {
            logger.d("ShowAlertDialog, showing")
            val result = suspendCancellableCoroutine { continuation ->
                var result: DialogAction = DialogAction.None
                val androidDialog = Dialog(androidUiContext)

                val dialogView =
                    AlertDialogView(
                        context = androidUiContext,
                        i18N = i18NIns,
                        style = d.style,
                        title = d.title.compileContextAndVars(cv),
                        message = d.message.compileContextAndVars(cv),
                        positiveText = d.positive.takeIf { it.isNotBlank() }
                            ?.compileContextAndVars(cv),
                        negativeText = d.negative.takeIf { it.isNotBlank() }
                            ?.compileContextAndVars(cv),
                        neutralText = d.neutral.takeIf { it.isNotBlank() }
                            ?.compileContextAndVars(cv),
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
                        runSubActions(d.onPositiveList)
                    }

                    DialogAction.Negative -> {
                        runSubActions(d.onNegativeList)
                    }

                    DialogAction.Neutral -> {
                        runSubActions(d.onNeutralList)
                    }

                    DialogAction.UNRECOGNIZED, DialogAction.None -> {
                        // Noop.
                    }
                }
            }
        }
    }


}
