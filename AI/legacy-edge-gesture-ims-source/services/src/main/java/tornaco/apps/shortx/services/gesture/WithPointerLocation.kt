package tornaco.apps.shortx.services.gesture

import android.app.ActivityManager
import android.content.Context
import android.graphics.PixelFormat
import android.view.MotionEvent
import android.view.WindowManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.services.ui.PointerLocationViewCompat

open class WithPointerLocation(val context: Context) {
    private var pointerLocationViewCompat: PointerLocationViewCompat? = null

    suspend fun injectOnPointerEvent(event: MotionEvent) {
        withContext(Dispatchers.Main) {
            pointerLocationViewCompat?.onPointerEvent(event)
        }
    }

    suspend fun enablePointerLocation() = withContext(Dispatchers.Main) {
        if (pointerLocationViewCompat != null) {
            return@withContext
        }
        pointerLocationViewCompat = PointerLocationViewCompat(context)
        pointerLocationViewCompat!!.setPrintCoords(false)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.type = WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY
        lp.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        if (OsUtils.isROrAbove()) {
            lp.fitInsetsTypes = 0
            lp.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
        if (ActivityManager.isHighEndGfx()) {
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            lp.privateFlags =
                lp.privateFlags or WindowManager.LayoutParams.PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED
        }
        lp.format = PixelFormat.TRANSLUCENT
        lp.title = "GestureRecorder-ShortX"
        lp.inputFeatures =
            lp.inputFeatures or WindowManager.LayoutParams.INPUT_FEATURE_NO_INPUT_CHANNEL
        val wm: WindowManager = context.getSystemService(WindowManager::class.java)
        wm.addView(pointerLocationViewCompat!!.view, lp)
    }

    suspend fun disablePointerLocation() {
        withContext(Dispatchers.Main) {
            if (pointerLocationViewCompat == null) {
                return@withContext
            }
            val wm: WindowManager = context.getSystemService(WindowManager::class.java)
            wm.removeView(pointerLocationViewCompat!!.view)
            pointerLocationViewCompat = null
        }
    }
}