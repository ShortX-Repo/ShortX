package tornaco.apps.shortx.core.ui


data class ViewNode(
    val rootPackageName: String,
    val viewId: String,
    val className: String,
    val text: CharSequence?,
    val sourceNodeId: Long,
    val windowId: Int,
    val position: XRectF,
    val color: Int
) {
    val shortViewId = viewId.substringAfter("$rootPackageName:id/")
}

data class TextViewNode(
    val text: String,
    val viewId: String,
    val position: XRectF
)
