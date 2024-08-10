package tornaco.apps.shortx.services.ui.floating.controller

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.compat.DrawableCompat
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.util.Logger
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
    private val floatServiceUtil: FloatServiceUtil,
    private val floatWindowManager: FloatWindowManager,
    private val panelWindowController: FloatPanelWindowController,
    private val onPosChange: (Int, Int) -> Unit,
) : BaseFloatWindowController(context) {
    private val logger = Logger("FloatButtonWindowController")

    private lateinit var floatButtonView: FloatButton
    private var currentStatus = STATUS_OFF

    private var statusChangeListener: OnStatusChangeListener? = null
    private var onFloatButtonPositionUpdateListener: OnFloatButtonPositionUpdateListener? = null

    private var openAnimatorSet: AnimatorSet? = null
    private var offAnimatorSet: AnimatorSet? = null
    private var idleAnimatorSet: AnimatorSet? = null
    private val mainHandler = Handler((Looper.getMainLooper()))

    private val idleRunnable = {
        logger.d("idleRunnable run")
        if (idleAnimatorSet != null && idleAnimatorSet!!.isRunning) {
            idleAnimatorSet!!.end()
        }
        idleAnimatorSet = AnimatorSet()
        val alphaAnimator = ObjectAnimator.ofFloat(floatButtonView, View.ALPHA, ALPHA_IDLE)
        idleAnimatorSet!!
            .play(alphaAnimator)
        idleAnimatorSet!!.start()
    }

    init {
        init()
    }

    private fun init() {
        floatButtonView = FloatButton(context)
        val appRes = AppResources(context, BuildPropInternal.APP_PACKAGE_NAME)
        val tintedDrawable = DrawableCompat.wrap(appRes.getDrawable("service_ui_ic_float"))
        DrawableCompat.setTint(tintedDrawable, Color.WHITE)
        floatButtonView.setImageDrawable(tintedDrawable)
        val iconBgDrawable = appRes.getDrawable("float_icon_bg")
        floatButtonView.background = iconBgDrawable
        floatButtonView.scaleType = ImageView.ScaleType.CENTER

        initListener()
        attachFloatWindow()
        mainHandler.postDelayed(idleRunnable, IDLE_DELAY)
    }

    private fun initListener() {
        floatButtonView.setOnClickListener { floatServiceUtil.toggleFloatButton() }
    }

    private fun attachFloatWindow() {
        val iconSize = ScreenUtil.dip2px(context, SIZE)
        Log.d("attachFloatWindow", "x-y: ${floatServiceUtil.x} ${floatServiceUtil.y}")
        floatWindowManager
            .makeFloatWindow(
                floatButtonView,
                TAG_BUTTON,
                FloatWindowOption.create(
                    FloatWindowOption.Builder()
                        .setX(floatServiceUtil.x)
                        .setY(floatServiceUtil.y)
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
                                mainHandler.postDelayed({
                                    // Save newX newY
                                    floatServiceUtil.x = newX
                                    floatServiceUtil.y = newY
                                    onPosChange(newX, newY)
                                }, 550)
                                //让面板跟随按钮
                                if (onFloatButtonPositionUpdateListener != null) {
                                    onFloatButtonPositionUpdateListener!!.onFloatButtonPositionUpdate(
                                        newX,
                                        newY
                                    )
                                }

                                logger.d("onPositionUpdate")
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
                                mainHandler.removeCallbacks(idleRunnable)
                            }

                            override fun onDragFinish() {
                                super.onDragFinish()
                                // 将Alpha调整比较容易可见的值
                                if (view.alpha != ALPHA_SHOW) {
                                    view.alpha = ALPHA_SHOW
                                }
                                logger.d("onDragFinish")
                                mainHandler.removeCallbacks(idleRunnable)
                                if (!isOpen) mainHandler.postDelayed(idleRunnable, IDLE_DELAY)
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
                                    floatServiceUtil.closeFloatButton()
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
        mainHandler.removeCallbacks(idleRunnable)
        logger.d("open")
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
        logger.d("Off")
        mainHandler.removeCallbacks(idleRunnable)
        if (this.currentStatus != STATUS_OFF) {
            floatButtonView.isSelected = false
            this.currentStatus = STATUS_OFF
            if (offAnimatorSet != null && offAnimatorSet!!.isRunning) {
                offAnimatorSet!!.end()
            }
            offAnimatorSet = AnimatorSet()
            val scaleXAnimator = ObjectAnimator.ofFloat(floatButtonView, View.SCALE_X, 1f)
            val scaleYAnimator = ObjectAnimator.ofFloat(floatButtonView, View.SCALE_Y, 1f).apply {
                addListener(object : AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {
                        logger.d("onAnimationStart")
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        logger.d("onAnimationEnd")
                        mainHandler.removeCallbacks(idleRunnable)
                        mainHandler.postDelayed(idleRunnable, IDLE_DELAY)
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationRepeat(animation: Animator?) {
                    }
                })
            }
            offAnimatorSet!!
                .play(scaleXAnimator)
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

        const val ALPHA_SHOW: Float = 0.76f
        const val ALPHA_IDLE: Float = 0.09f

        const val SIZE = 42f

        const val IDLE_DELAY = 2000L
    }
}