package tornaco.apps.shortx.services.ui.floating.controller

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.compat.DrawableCompat
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.res.remixResName
import tornaco.apps.shortx.services.ui.floating.FloatMoveEnum
import tornaco.apps.shortx.services.ui.floating.FloatServiceUtil
import tornaco.apps.shortx.services.ui.floating.FloatWindow
import tornaco.apps.shortx.services.ui.floating.FloatWindowManager
import tornaco.apps.shortx.services.ui.floating.FloatWindowOption
import tornaco.apps.shortx.services.ui.floating.ScreenUtil
import tornaco.apps.shortx.services.ui.floating.SimpleFloatWindowViewStateCallback
import tornaco.apps.shortx.services.ui.floating.widget.FloatButton

class FloatButtonWindowController(
    context: Context,
    private val floatWindowManager: FloatWindowManager,
    private val panelWindowController: FloatPanelWindowController
) : BaseFloatWindowController(context) {
    private lateinit var floatButtonView: FloatButton
    private var currentStatus = STATUS_OFF

    private var statusChangeListener: OnStatusChangeListener? = null
    private var onFloatButtonPositionUpdateListener: OnFloatButtonPositionUpdateListener? = null

    private var openAnimatorSet: AnimatorSet? = null
    private var offAnimatorSet: AnimatorSet? = null
    private val mainHandler = Handler((Looper.getMainLooper()))

    init {
        init()
    }

    private fun init() {
        floatButtonView = FloatButton(context)
        val appRes = AppResources(context, BuildPropInternal.APP_PACKAGE_NAME)
        val iconResName = context.remixResName(Remix.Game.handle_round)
        val tintedDrawable = DrawableCompat.wrap(appRes.getDrawable(iconResName))
        DrawableCompat.setTint(tintedDrawable, Color.WHITE)
        floatButtonView.setImageDrawable(tintedDrawable)
        // 创建一个 GradientDrawable 并设置为圆形
        val gradientDrawable = GradientDrawable()
        gradientDrawable.shape = GradientDrawable.OVAL
        gradientDrawable.setColor(Color.GRAY)
        floatButtonView.background = gradientDrawable
        floatButtonView.scaleType = ImageView.ScaleType.CENTER

        initListener()
        attachFloatWindow()
    }

    private fun initListener() {
        floatButtonView.setOnClickListener { FloatServiceUtil.toggleFloatButton() }
    }

    private fun attachFloatWindow() {
        val iconSize = ScreenUtil.dip2px(context, 52f)
        floatWindowManager
            .makeFloatWindow(
                floatButtonView,
                TAG_BUTTON,
                FloatWindowOption.create(
                    FloatWindowOption.Builder()
                        .setX(200)
                        .setY(800)
                        .setWidth(iconSize)
                        .setHeight(iconSize)
                        .setFloatMoveType(FloatMoveEnum.SLIDE)
                        .setDuration(250)
                        .setBoundOffset(ScreenUtil.dip2px(context, 5f))
                        .setViewStateCallback(object : SimpleFloatWindowViewStateCallback() {
                            override fun onPositionUpdate(
                                oldX: Int,
                                oldY: Int,
                                newX: Int,
                                newY: Int
                            ) {
                                super.onPositionUpdate(oldX, oldY, newX, newY)
                                mainHandler.removeCallbacksAndMessages(null)
                                mainHandler.postDelayed({ // Save newX newY
                                    FloatServiceUtil.x = newX
                                    FloatServiceUtil.y = newY
                                }, 550)
                                //让面板跟随按钮
                                if (onFloatButtonPositionUpdateListener != null) {
                                    onFloatButtonPositionUpdateListener!!.onFloatButtonPositionUpdate(
                                        newX,
                                        newY
                                    )
                                }
                            }

                            override fun onPrepareDrag() {
                            }

                            override fun isCanLongPress(): Boolean {
                                //展开时，不算长按
                                return !isOpen
                            }

                            override fun onLongPress() {
                                super.onLongPress()
                                //长按
                                //Toast.makeText(mContext.getApplicationContext(), "长按了", Toast.LENGTH_SHORT).show();
                            }

                            override fun onDragging(moveX: Float, moveY: Float) {
                                super.onDragging(moveX, moveY)
                                // 将Alpha调整比较容易可见的值
                                if (view.alpha != ALPHA_SHOW) {
                                    view.alpha = ALPHA_SHOW
                                }
                            }

                            override fun onDragFinish() {
                                super.onDragFinish()
                                // 将Alpha调整比较容易可见的值
                                if (view.alpha != ALPHA_SHOW) {
                                    view.alpha = ALPHA_SHOW
                                }
                            }

                            override fun isCanDrag(moveX: Float, moveY: Float): Boolean {
                                //准备拽托时，如果是打开状态，不能拽托
                                if (isOpen) {
                                    return false
                                }
                                //限制移动的位置，不能比面板高度还要小
//                                        int screenHeight = ScreenUtil.getScreenHeight(getApplicationContext());
//                                        int halfHeight = mPanelViewController.getView().getHeight() / 2;
//                                        if (screenHeight - moveY < halfHeight) {
//                                            return false;
//                                        }
                                return true
                            }

                            override fun onClickFloatOutsideArea(x: Float, y: Float) {
                                super.onClickFloatOutsideArea(x, y)
                                //因为控制面板也是一个悬浮窗，点击面板的按钮时，也算点击悬浮按钮以外区域
                                // 所以当面板打开时，不算点击以外范围，并且如果点击的在面板区域内也不算
                                if (panelWindowController.isOpen
                                    && panelWindowController.isInPanelArea(x, y)
                                ) {
                                    return
                                }
                                //当点击悬浮按钮区域外时，如果是打开状态，则关闭
                                if (isOpen) {
                                    FloatServiceUtil.closeFloatButton()
                                }
                            }
                        })
                )
            )
    }

    interface OnFloatButtonPositionUpdateListener {
        fun onFloatButtonPositionUpdate(newX: Int, newY: Int)
    }

    fun setOnFloatButtonPositionUpdateListener(buttonPositionUpdateListener: OnFloatButtonPositionUpdateListener) {
        this.onFloatButtonPositionUpdateListener = buttonPositionUpdateListener
    }

    override fun getView(): FloatButton {
        return floatButtonView
    }

    fun toggle() {
        if (currentStatus == STATUS_OPEN) {
            off()
        } else {
            open()
        }
    }

    val isOpen: Boolean
        get() = currentStatus == STATUS_OPEN

    val floatWindow: FloatWindow
        get() = floatWindowManager
            .getFloatWindow(TAG_BUTTON)

    fun showFloatWindow() {
        floatWindowManager
            .getFloatWindow(TAG_BUTTON)
            .show()
    }

    fun hideFloatWindow() {
        floatWindowManager
            .getFloatWindow(TAG_BUTTON)
            .hide()
    }

    fun removeFloatWindow() {
        floatWindowManager
            .getFloatWindow(TAG_BUTTON)
            .remove()
    }

    fun open() {
        if (offAnimatorSet != null && offAnimatorSet!!.isRunning) {
            offAnimatorSet!!.end()
        }
        if (this.currentStatus != STATUS_OPEN) {
            if (statusChangeListener != null) {
                //不能改变则打断
                val isCanChange = statusChangeListener!!.onPrepareStatusChange(STATUS_OPEN)
                if (!isCanChange) {
                    return
                }
            }
            floatButtonView.isSelected = true
            this.currentStatus = STATUS_OPEN
            if (openAnimatorSet != null && openAnimatorSet!!.isRunning) {
                openAnimatorSet!!.end()
            }
            openAnimatorSet = AnimatorSet()
            val scaleXAnimator = ObjectAnimator.ofFloat(floatButtonView, View.SCALE_X, 0.8f)
            val scaleYAnimator = ObjectAnimator.ofFloat(floatButtonView, View.SCALE_Y, 0.8f)
            val alphaAnimator = ObjectAnimator.ofFloat(floatButtonView, View.ALPHA, ALPHA_SHOW)
            openAnimatorSet!!
                .play(scaleXAnimator)
                .with(scaleYAnimator)
                .with(alphaAnimator)
            openAnimatorSet!!.start()
            if (statusChangeListener != null) {
                statusChangeListener!!.onStatusChange(this.currentStatus)
            }
        }
    }

    fun off() {
        if (this.currentStatus != STATUS_OFF) {
            floatButtonView.isSelected = false
            this.currentStatus = STATUS_OFF
            if (offAnimatorSet != null && offAnimatorSet!!.isRunning) {
                offAnimatorSet!!.end()
            }
            offAnimatorSet = AnimatorSet()
            val scaleXAnimator = ObjectAnimator.ofFloat(floatButtonView, View.SCALE_X, 1f)
            val scaleYAnimator = ObjectAnimator.ofFloat(floatButtonView, View.SCALE_Y, 1f)
            offAnimatorSet!!
                .play(scaleXAnimator) //2个渐变动画要一起执行
                .with(scaleYAnimator)
            offAnimatorSet!!.start()
            if (statusChangeListener != null) {
                statusChangeListener!!.onStatusChange(this.currentStatus)
            }
        }
    }

    interface OnStatusChangeListener {
        /**
         * 当准备状态改变时回调
         *
         * @param prepareStatus 准备改变的状态
         * @return 返回true代表允许改变，false代表拦截改变
         */
        fun onPrepareStatusChange(prepareStatus: Int): Boolean

        /**
         * 状态改变时回调
         *
         * @param newStatus 新状态
         */
        fun onStatusChange(newStatus: Int)
    }

    fun setOnStatusChangeListener(listener: OnStatusChangeListener?) {
        this.statusChangeListener = listener
    }

    companion object {
        private const val TAG_BUTTON = "button_tag"

        const val STATUS_OFF: Int = 0
        const val STATUS_OPEN: Int = 1

        const val ALPHA_SHOW: Float = 1f
    }
}