package tornaco.apps.shortx.services.ui.widget

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import tornaco.apps.shortx.core.util.screenSize
import kotlin.math.roundToInt

fun Dialog.showInSystemServer(view: View, cancelable: Boolean = true, onDismiss: () -> Unit = {}) {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(view)
    setCancelable(cancelable)
    setOnDismissListener { onDismiss() }
    window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG)
    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val screenSize = context.screenSize().x
    window.setLayout((screenSize * 0.9f).roundToInt(), WindowManager.LayoutParams.WRAP_CONTENT)

    show()
}