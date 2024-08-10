/*
 * Copyright (C) 2020 The exTHmUI Open Source Project
 * Copyright (C) 2021 AOSP-Krypton Project
 * Copyright (C) 2022 Nameless-AOSP Project
 * Copyright (C) 2023 the risingOS android Project
 * Copyright (C) 2022-2024 crDroid Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tornaco.apps.shortx.services.ui.danmu

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.TextView
import tornaco.apps.shortx.core.util._dp
import java.util.LinkedList

interface DanmakuServiceInterface {
    fun showNotificationAsOverlay(danmakuText: String)
}

// Android R+ Build.VERSION_CODES.R
class DanmakuService(
    private val context: Context,
) : DanmakuServiceInterface {
    private val notificationOverlay = TextView(context).apply {
        gravity = Gravity.CENTER
        maxLines = 2
        setTextColor(Color.WHITE)
        isFocusable = false
        isClickable = false
    }

    private val windowManager: WindowManager = context.getSystemService(WindowManager::class.java)!!

    private val handler = Handler(Looper.getMainLooper())

    private val notificationStack = LinkedList<String>()

    private var layoutParams: LayoutParams = LayoutParams().apply {
        height = LayoutParams.WRAP_CONTENT
        flags = flags or LayoutParams.FLAG_NOT_FOCUSABLE or
                LayoutParams.FLAG_NOT_TOUCHABLE or
                LayoutParams.FLAG_HARDWARE_ACCELERATED
        type = LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY
        format = PixelFormat.TRANSLUCENT
        gravity = Gravity.TOP
    }

    private var isPortrait: Boolean =
        context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    private var verticalOffsetLandscape = 0
    private var verticalOffsetPortrait = 0

    private var overlayAlphaAnimator: ValueAnimator? = null
    private var overlayPositionAnimator: ValueAnimator? = null

    fun init() {
        updateParams()
    }

    fun updateConfiguration(newConfig: Configuration) {
        isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT
        overlayAlphaAnimator?.end()
        overlayPositionAnimator?.end()
        updateParams()
        updateViewLayoutSafely(layoutParams)
    }

    fun destroy() {
        overlayAlphaAnimator?.cancel()
        overlayPositionAnimator?.cancel()
        removeViewSafely()
    }

    @SuppressLint("NewApi")
    private fun updateParams() {
        with(context.resources) {
            verticalOffsetLandscape = 48._dp
            verticalOffsetPortrait = 36._dp
        }
        layoutParams.y = getOffsetForPosition()
        layoutParams.width =
            (NOTIFICATION_MAX_WIDTH * windowManager.currentWindowMetrics.bounds.width()) / 100
        notificationOverlay.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            (if (isPortrait) NOTIFICATION_SIZE_PORTRAIT else NOTIFICATION_SIZE_LANDSCAPE).toFloat()
        )
    }

    private fun getOffsetForPosition(): Int {
        return if (isPortrait) verticalOffsetPortrait else verticalOffsetLandscape
    }

    override fun showNotificationAsOverlay(notification: String) {
        if (notificationOverlay.parent == null) {
            notificationOverlay.alpha = 0f
            notificationOverlay.text = notification
            windowManager.addView(notificationOverlay, layoutParams)
            pushNotification()
        } else {
            notificationStack.add(notification)
        }
    }

    private fun pushNotification() {
        val end = getOffsetForPosition().toFloat()
        val start = end * (1 - SLIDE_ANIMATION_DISTANCE_FACTOR)
        overlayPositionAnimator = getPositionAnimator(APPEAR_ANIMATION_DURATION, start, end).also {
            it.addListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    handler.postDelayed({
                        popNotification()
                    }, DISPLAY_NOTIFICATION_DURATION)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
            it.start()
        }
        startAlphaAnimation(APPEAR_ANIMATION_DURATION, 0f, 1f)
    }

    private fun popNotification() {
        val start = getOffsetForPosition().toFloat()
        val end = start * (1 + SLIDE_ANIMATION_DISTANCE_FACTOR)
        overlayPositionAnimator =
            getPositionAnimator(DISAPPEAR_ANIMATION_DURATION, start, end).also {
                it.addListener(object : AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        if (notificationStack.isEmpty()) {
                            removeViewSafely()
                        } else {
                            notificationOverlay.alpha = 0f
                            notificationOverlay.text = notificationStack.pop()
                            pushNotification()
                        }
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationRepeat(animation: Animator?) {
                    }
                })
                it.start()
            }
        startAlphaAnimation(DISAPPEAR_ANIMATION_DURATION, 1f, 0f)
    }

    private fun getPositionAnimator(duration: Long, vararg values: Float): ValueAnimator {
        val lpCopy = LayoutParams().also { it.copyFrom(layoutParams) }
        return ValueAnimator.ofFloat(*values).apply {
            this.duration = duration
            addUpdateListener {
                lpCopy.y = (it.animatedValue as Float).toInt()
                updateViewLayoutSafely(lpCopy)
            }
        }
    }

    private fun startAlphaAnimation(duration: Long, vararg values: Float) {
        overlayAlphaAnimator = ValueAnimator.ofFloat(*values).apply {
            this.duration = duration
            addUpdateListener {
                notificationOverlay.alpha = it.animatedValue as Float
            }
        }.also { it.start() }
    }

    private fun updateViewLayoutSafely(layoutParams: LayoutParams) {
        if (notificationOverlay.parent != null)
            windowManager.updateViewLayout(notificationOverlay, layoutParams)
    }

    private fun removeViewSafely() {
        if (notificationOverlay.parent != null)
            windowManager.removeViewImmediate(notificationOverlay)
    }

    companion object {
        private const val TAG = "DanmakuService"

        private const val SLIDE_ANIMATION_DISTANCE_FACTOR = 0.5f

        private const val APPEAR_ANIMATION_DURATION = 500L
        private const val DISPLAY_NOTIFICATION_DURATION = 2000L
        private const val DISAPPEAR_ANIMATION_DURATION = 300L

        private const val NOTIFICATION_SIZE_LANDSCAPE = 60
        private const val NOTIFICATION_SIZE_PORTRAIT = 60

        private const val NOTIFICATION_MAX_WIDTH = 75

        private const val NOTIFICATIONS_MAX_CACHED = 99
    }
}
