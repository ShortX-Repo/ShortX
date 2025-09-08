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

fun android.graphics.Rect.toProtoRect(): Rect {
    return Rect.newBuilder().setLeft(left).setRight(right).setTop(top).setBottom(bottom).build()
}