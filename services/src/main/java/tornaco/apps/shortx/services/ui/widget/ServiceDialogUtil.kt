package tornaco.apps.shortx.services.ui.widget

import android.app.Dialog
import android.view.View
import android.view.Window
import android.view.WindowManager

fun Dialog.showInSystemServer(view: View, cancelable: Boolean = true, onDismiss: () -> Unit = {}) {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(view)
    setCancelable(cancelable)
    setOnDismissListener { onDismiss() }
    window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG)

    val lp = WindowManager.LayoutParams()
    lp.copyFrom(window.attributes)
    lp.width = WindowManager.LayoutParams.MATCH_PARENT
    lp.height = WindowManager.LayoutParams.WRAP_CONTENT

    show()
    window.attributes = lp;
}