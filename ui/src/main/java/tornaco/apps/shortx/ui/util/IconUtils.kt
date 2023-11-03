package tornaco.apps.shortx.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat

object IconUtils {

    fun getCenteredAndColoredBitmap(
        context: Context,
        drawableId: Int,
        color: Int,
        width: Int,
        height: Int
    ): Bitmap {
        // 加载矢量图标
        val drawable = ContextCompat.getDrawable(context, drawableId)

        // 创建一个新的 Bitmap
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 计算绘制图标的新宽度和高度，使其居中
        val iconWidth = Math.min(drawable?.intrinsicWidth ?: width, width)
        val iconHeight = Math.min(drawable?.intrinsicHeight ?: height, height)
        val left = (width - iconWidth) / 2
        val top = (height - iconHeight) / 2
        val right = left + iconWidth
        val bottom = top + iconHeight

        // 设置绘制颜色
        drawable?.setTint(color)

        // 绘制到画布上
        drawable?.setBounds(left, top, right, bottom)
        drawable?.draw(canvas)

        return bitmap
    }
}