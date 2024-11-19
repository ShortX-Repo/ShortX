package tornaco.apps.shortx.services.ui.widget

import android.app.Dialog
import android.view.View
import android.view.Window
import android.view.WindowManager

fun Dialog.showInSystemServer(view: View, cancelable: Boolean = true, onDismiss: () -> Unit = {}) {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(view)
    window?.setLayout(
        (view.context.resources.displayMetrics.widthPixels * 0.8).toInt(), // 设置为屏幕宽度的 80%
        WindowManager.LayoutParams.WRAP_CONTENT // 高度自适应内容
    )
    setCancelable(cancelable)
    setOnDismissListener { onDismiss() }
    window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG)
    show()
}