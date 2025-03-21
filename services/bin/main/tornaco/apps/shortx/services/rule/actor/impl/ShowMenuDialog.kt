package tornaco.apps.shortx.services.rule.actor.impl

import android.app.Dialog
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.action.DialogMenuItem
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.util.runSubActions
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
    ): Actor.Res {
        with(params) {
            val d = action unpack_ tornaco.apps.shortx.core.proto.action.ShowMenuDialog::class.java

            withContext(Dispatchers.Main) {
                logger.d("ShowMenuDialog, showing: ${d.itemsList.size} ${d.cancelable}")
                val cv = RuleContextAndVars(localVars, contextData, shortXContext)

                val result = suspendCancellableCoroutine { continuation ->
                    var clickedItem: DialogMenuItem? = null
                    val androidDialog = Dialog(androidUiContext)

                    val dialogView =
                        MenuDialogView(
                            context = androidUiContext,
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
                    actions?.let { runSubActions(it) }
                }
            }
            return Actor.Res.EMPTY
        }
    }
}