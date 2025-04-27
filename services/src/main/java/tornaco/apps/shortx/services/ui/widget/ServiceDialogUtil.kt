package tornaco.apps.shortx.services.ui.widget

import android.app.Dialog
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import tornaco.apps.shortx.core.util.screenSize
import kotlin.math.roundToInt

fun Dialog.showInSystemServer(
    view: View,
    cancelable: Boolean = true,
    gravity: Int = Gravity.CENTER,
    width: (Point) -> Int = { (it.x * 0.86f).roundToInt() },
    onWindow: Window.() -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(view)
    setCancelable(true)
    setCanceledOnTouchOutside(cancelable)
    setOnDismissListener { onDismiss() }
    window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG)
    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    window.setLayout(width(context.screenSize()), WindowManager.LayoutParams.WRAP_CONTENT)
    window.setGravity(gravity)
    onWindow(window)
    show()
}