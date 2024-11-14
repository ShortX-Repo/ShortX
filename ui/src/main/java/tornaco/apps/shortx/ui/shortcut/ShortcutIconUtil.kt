package tornaco.apps.shortx.ui.shortcut

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.LayerDrawable
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import tornaco.apps.shortx.core.res.isBase64Image
import tornaco.apps.shortx.core.res.remixResId
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.ui.R

object ShortcutIconUtil {
    fun createIconForDA(context: Context, icon: String, @ColorInt tint: Int): Bitmap? {
        val isBase64 = icon.isBase64Image()
        return if (isBase64) {
            BitmapUtils.base64ToBitmap(icon)
        } else {
            val actionIconDrawable = ResourcesCompat.getDrawable(
                context.resources,
                context.remixResId(icon),
                null
            )?.apply {
                setTint(android.graphics.Color.WHITE)
            }

            val templateDrawable =
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ui_ic_shortcut_template,
                    null
                )
            val ld = templateDrawable as LayerDrawable
            ld.setTint(tint)
            ld.setDrawableByLayerId(R.id.settings_ic_foreground, actionIconDrawable)

            BitmapUtils.getBitmap(context, templateDrawable)
        }
    }
}