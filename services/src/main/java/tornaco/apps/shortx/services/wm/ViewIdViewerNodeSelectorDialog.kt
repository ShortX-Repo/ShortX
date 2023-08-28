package tornaco.apps.shortx.services.wm


import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import tornaco.apps.shortx.core.ui.ViewNode
import tornaco.apps.shortx.core.util.clipboardManager

class ViewIdViewerNodeSelectorDialog(
    private val context: Context,
    private val nodes: List<ViewNode>
) {
    fun show() {
        AlertDialog.Builder(context)
            .setTitle("Click to copy view detail")
            .setAdapter(
                object : ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    nodes.map {
                        it.shortViewId
                    }
                ) {
                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup?
                    ): View {
                        return super.getDropDownView(position, convertView, parent).apply {
                            findViewById<android.widget.TextView>(android.R.id.text1)
                                .setTextColor(nodes[position].color)
                        }
                    }

                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup?
                    ): View {
                        return super.getView(position, convertView, parent).apply {
                            findViewById<android.widget.TextView>(android.R.id.text1)
                                .setTextColor(nodes[position].color)
                        }
                    }
                }
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