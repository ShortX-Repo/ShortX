package tornaco.apps.shortx.core.proto

import tornaco.apps.shortx.core.proto.common.Rect

fun Rect.toAndroidRect(): android.graphics.Rect {
    return android.graphics.Rect(
        left,
        top,
        right,
        bottom
    )
}

fun Rect.toAndroidRectF(): android.graphics.RectF {
    return android.graphics.RectF(
        left.toFloat(),
        top.toFloat(),
        right.toFloat(),
        bottom.toFloat()
    )
}

fun android.graphics.Rect.toProtoRect(): Rect {
    return Rect.newBuilder().setLeft(left).setRight(right).setTop(top).setBottom(bottom).build()
}