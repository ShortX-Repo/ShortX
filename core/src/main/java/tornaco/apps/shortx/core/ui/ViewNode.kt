package tornaco.apps.shortx.core.ui

import android.graphics.RectF

data class ViewNode(
    val rootPackageName: String,
    val viewId: String,
    val className: String,
    val position: RectF,
    val color: Int
) {
    val shortViewId = viewId.substringAfter("$rootPackageName:id/")
}

data class TextViewNode(
    val text: String,
    val viewId: String,
    val position: RectF
)
