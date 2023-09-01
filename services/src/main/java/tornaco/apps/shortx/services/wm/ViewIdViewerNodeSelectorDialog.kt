package tornaco.apps.shortx.services.wm


import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.view.WindowManager
import tornaco.apps.shortx.core.ui.ViewNode
import tornaco.apps.shortx.core.util.clipboardManager

class ViewIdViewerNodeSelectorDialog(
    private val context: Context,
    private val nodes: List<ViewNode>
) {
    fun show() {
        AlertDialog.Builder(context)
            .setTitle("Click to copy view id")
            .setItems(
                nodes.map {
                    it.shortViewId
                }.toTypedArray()
            ) { _, which ->
                context.clipboardManager.primaryClip =
                    ClipData.newPlainText(
                        "ShortX-ViewId",
                        "ViewId: ${nodes[which].viewId} ClassName: ${nodes[which].className}"
                    )
            }
            .setCancelable(true)
            .create()
            .apply {
                window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG)
            }
            .show()
    }
}