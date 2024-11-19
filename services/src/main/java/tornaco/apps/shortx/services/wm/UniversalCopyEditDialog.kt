package tornaco.apps.shortx.services.wm

import android.app.Dialog
import android.content.ClipData.newPlainText
import android.content.Context
import android.widget.LinearLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.ui.dialog.DialogButton
import tornaco.apps.shortx.ui.dialog.EditTextDialogView

private val commonElementLP = LinearLayout.LayoutParams(
    LinearLayout.LayoutParams.MATCH_PARENT,
    LinearLayout.LayoutParams.WRAP_CONTENT
).apply {
    setMargins(16._dp, 16._dp, 16._dp, 16._dp)
}

class UniversalCopyEditDialog(
    private val context: Context,
    private val text: String
) {
    suspend fun show() {
        withContext(Dispatchers.Main) {
            val androidDialog = Dialog(context)
            var userValue = text

            val guideLineView =
                EditTextDialogView(
                    context = context,
                    i18N = i18NIns,
                    title = i18NIns["ui.action.enable.universal.copy"],
                    value = text,
                    onValueChange = {
                        userValue = it
                    },
                    onDismissRequest = {
                        androidDialog.dismiss()

                        if (it == DialogButton.OK && userValue.isNotEmpty()) {
                            context.clipboardManager.setPrimaryClip(
                                newPlainText(
                                    "ShortX-Copy",
                                    userValue
                                )
                            )
                        }
                    }
                )
            guideLineView.initView()

            androidDialog.showInSystemServer(guideLineView)
        }
    }
}