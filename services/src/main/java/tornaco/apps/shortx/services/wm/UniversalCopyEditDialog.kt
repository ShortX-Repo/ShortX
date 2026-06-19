package tornaco.apps.shortx.services.wm

import android.app.Dialog
import android.content.ClipData.newPlainText
import android.content.Context
import android.widget.LinearLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.DialogUiStyleSettings
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.services.ui.i18NIns
import tornaco.apps.shortx.services.ui.widget.showInSystemServer
import tornaco.apps.shortx.ui.dialog.DialogButton
import tornaco.apps.shortx.ui.dialog.EditTextDialogView
import tornaco.apps.shortx.core.i18n.I18N_R

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
                    style = DialogUiStyleSettings.newBuilder().setFontScale(1f).build(),
                    title = i18NIns[I18N_R.UI_ACTION_ENABLE_UNIVERSAL_COPY],
                    value = text,
                    onValueChange = {
                        userValue = it
                    },
                    onDismissRequest = {
                        androidDialog.dismiss()

                        if (it == DialogButton.Positive && userValue.isNotEmpty()) {
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