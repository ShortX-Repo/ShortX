package tornaco.apps.shortx.services.wm

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import tornaco.apps.shortx.core.ui.ViewNode
import tornaco.apps.shortx.core.ui.XRectF

fun AccessibilityNodeInfo.toBasicViewNode(): ViewNode {
    val viewId: String = this.viewIdResourceName ?: ""
    val rect = this.boundsInScreen ?: Rect(0, 0, 0, 0)
    return ViewNode(
        viewId = viewId,
        className = this.className?.toString() ?: "",
        text = this.text ?: this.contentDescription ?: "",
        rootPackageName = this.packageName?.toString() ?: "android",
        sourceNodeId = this.sourceNodeId,
        windowId = this.windowId,
        position = XRectF(
            rect.left.toFloat(),
            rect.top.toFloat(),
            rect.right.toFloat(),
            rect.bottom.toFloat()
        ),
        color = 0
    )
}