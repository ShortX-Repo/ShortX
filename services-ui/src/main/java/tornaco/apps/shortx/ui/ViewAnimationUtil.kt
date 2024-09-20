package tornaco.apps.shortx.ui

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

object ViewAnimationUtil {

    // 显示 View 的缩放动画
    fun showWithScaleAnimation(view: View, duration: Long = 300) {
        view.apply {
            visibility = View.VISIBLE
            scaleX = 0f
            scaleY = 0f
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(duration)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    // 隐藏 View 的缩放动画
    fun hideWithScaleAnimation(view: View, duration: Long = 300) {
        view.apply {
            animate()
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(duration)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    visibility = View.GONE
                }
                .start()
        }
    }
}
