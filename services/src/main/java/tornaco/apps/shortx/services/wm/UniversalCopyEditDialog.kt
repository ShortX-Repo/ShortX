package tornaco.apps.shortx.services.wm

import android.app.AlertDialog
import android.content.ClipData.newPlainText
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import tornaco.apps.shortx.core.util._dp
import tornaco.apps.shortx.core.util.clipboardManager
import tornaco.apps.shortx.services.ui.i18NIns

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
    fun show() {
        val editText = EditText(context)
        editText.setText(text)
        val messageText = TextView(context)
        messageText.text = i18NIns["ui.action.enable.universal.copy.edit.message"]
        val linearLayout = LinearLayout(context)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = Gravity.CENTER
        linearLayout.addView(messageText, commonElementLP)
        linearLayout.addView(editText, commonElementLP)

        AlertDialog.Builder(context)
            .setTitle(i18NIns["ui.action.enable.universal.copy"])
            .setView(linearLayout)
            .setCancelable(true)
            .setPositiveButton(
                android.R.string.ok
            ) { _, _ ->
                val textToCopy = editText.editableText.toString()
                if (textToCopy.isNotEmpty()) {
                    context.clipboardManager.primaryClip = newPlainText("ShortX-Copy", textToCopy)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .apply {
                window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG)
            }
            .show()
    }
}