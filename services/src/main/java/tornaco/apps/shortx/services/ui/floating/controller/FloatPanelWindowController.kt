package tornaco.apps.shortx.services.ui.floating.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.compat.DrawableCompat
import tornaco.apps.shortx.core.proto.action.ShowFloatWindow
import tornaco.apps.shortx.core.proto.settings.OverlayButtonSetting
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.res.remixResName
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ui.floating.FloatMoveEnum
import tornaco.apps.shortx.services.ui.floating.FloatServiceUtil
import tornaco.apps.shortx.services.ui.floating.FloatWindowManager
import tornaco.apps.shortx.services.ui.floating.FloatWindowOption
import tornaco.apps.shortx.services.ui.floating.ScreenUtil
import tornaco.apps.shortx.services.ui.floating.widget.ControlPanelView
import tornaco.apps.shortx.services.ui.floating.widget.ControlPanelView.OnPanelSizeChangeCallback
import tornaco.apps.shortx.services.ui.floating.widget.FloatActionButton


class FloatPanelWindowController(
    context: Context,
    private val floatServiceUtil: FloatServiceUtil,
    private val floatWindowManager: FloatWindowManager,
    private val action: ShowFloatWindow,
    private val onButtonClick: (OverlayButtonSetting) -> Unit,
    private val onButtonLongClick: (OverlayButtonSetting) -> Unit,
) :
    BaseFloatWindowController(context) {
    private val logger = Logger("FloatPanelWindowController")

    var isOpen: Boolean = false
        private set

    private lateinit var panelContainerLayout: FrameLayout
    private lateinit var floatControlPanelView: ControlPanelView

    private val mUpdatePanelActionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // 更新Action数据，先移除，再添加
            floatControlPanelView.removeAllViews()
            addActionButton()
        }
    }

    init {
        init()
        logger.d("init, floatWindowManager: $floatWindowManager")
    }

    private fun init() {
        val rootParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        panelContainerLayout = FrameLayout(context)
        panelContainerLayout.layoutParams = rootParams

        floatControlPanelView = ControlPanelView(context)
        val panelParams = FrameLayout.LayoutParams(
            ScreenUtil.dip2px(context, 150f),
            ScreenUtil.dip2px(context, 300f)
        )
        floatControlPanelView.layoutParams = panelParams

        panelContainerLayout.addOnAttachStateChangeListener(object :
            View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                // TODO
                // Listen for update actions.
            }

            override fun onViewDetachedFromWindow(v: View) {
            }
        })
        // 根据数据添加子View，并先隐藏
        addActionButton()
        // TODO 恢复上一次保存的位置
        floatControlPanelView.setOrientation(false)
        panelContainerLayout.addView(floatControlPanelView)

        initListener()
        attachFloatWindow()
    }

    private fun attachFloatWindow() {
        floatWindowManager
            .makeFloatWindow(
                panelContainerLayout,
                TAG_PANEL,
                FloatWindowOption
                    .create(
                        FloatWindowOption.Builder()
                            .setX(floatServiceUtil.px)
                            .setY(floatServiceUtil.py)
                            .setShow(false)
                            .setFloatMoveType(FloatMoveEnum.INACTIVE)
                    )
            )
    }

    private fun initListener() {
        floatControlPanelView.setOnTogglePanelListener { isOpen -> //最新为打开
            if (isOpen) {
                floatWindowManager
                    .getFloatWindow(TAG_PANEL)
                    .show()
            } else {
                floatWindowManager
                    .getFloatWindow(TAG_PANEL)
                    .hide()
            }
        }
    }

    fun setOnPanelSizeChangeCallback(onPanelSizeChangeCallback: OnPanelSizeChangeCallback?) {
        floatControlPanelView.setOnPanelSizeChangeCallback(onPanelSizeChangeCallback)
    }

    private fun addActionButton() {
        val appRes = AppResources(context, BuildPropInternal.APP_PACKAGE_NAME)
        logger.d("addActionButton- ${this.hashCode()} ${action.buttonSettingsList.size}")
        action.buttonSettingsList.forEach { button ->
            val actionView = FloatActionButton(context)
            val iconSize = ScreenUtil.dip2px(context, 52f)
            val iconPadding = ScreenUtil.dip2px(context, 12f)

            val params = FrameLayout.LayoutParams(iconSize, iconSize)
            params.gravity = Gravity.CENTER
            actionView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)

            val iconResName =
                context.remixResName(button.icon) ?: context.remixResName(Remix.Animals.dog)
            val tintedDrawable = DrawableCompat.wrap(appRes.getDrawable(iconResName))
            DrawableCompat.setTint(tintedDrawable, Color.WHITE)
            actionView.setImageDrawable(tintedDrawable)

            // 创建一个 GradientDrawable 并设置为圆形
            val gradientDrawable = GradientDrawable()
            gradientDrawable.shape = GradientDrawable.OVAL
            gradientDrawable.setColor(Color.GRAY)
            actionView.background = gradientDrawable
            actionView.scaleType = ImageView.ScaleType.CENTER

            actionView.setOnClickListener {
                floatServiceUtil.toggleFloatButton()
                onButtonClick(button)
            }
            actionView.setOnLongClickListener {
                floatServiceUtil.toggleFloatButton()
                onButtonLongClick(button)
                true
            }

            actionView.visibility = View.GONE
            actionView.alpha = FloatButtonWindowController.ALPHA_SHOW

            floatControlPanelView.addView(actionView, params)
        }
    }

    override fun getView(): View {
        return panelContainerLayout
    }

    /**
     * 跟随浮动按钮的位置
     */
    fun followButtonPosition(buttonX: Int, buttonY: Int) {
        // 切换方向设置
        val panelView = floatControlPanelView
        if (panelView.isAnimationRunning) {
            return
        }
        // 判断在屏幕左边还是右边，切换位置
        val isLeft = ScreenUtil.isScreenLeft(context, buttonX)
        panelView.setOrientation(isLeft)
        // TODO
        // Property.getDefault().setProperty(AccessibilityConstant.Config.KEY_FLOAT_WINDOW_IS_LEFT, isLeft);
        // 更新浮窗
        val panelWindow = floatWindowManager.getFloatWindow(TAG_PANEL)
        val result = panelView.followButtonPosition(buttonX, buttonY)
        val fixX = result[0]
        val fixY = result[1]
        panelWindow.updateXY(fixX, fixY)
        // 记录位置
        floatServiceUtil.px = fixX
        floatServiceUtil.py = fixY
    }

    val isCanChangeStatus: Boolean
        /**
         * 是否可以进行状态改变
         */
        get() =//动画还未结束时，不能改变
            !floatControlPanelView.isAnimationRunning

    fun open() {
        if (!floatControlPanelView.isOpen) {
            floatControlPanelView.openNow()
            this.isOpen = !isOpen
        }
    }

    fun off() {
        if (floatControlPanelView.isOpen) {
            floatControlPanelView.offNow()
            this.isOpen = !isOpen
        }
    }

    fun showFloatWindow() {
        floatWindowManager
            .getFloatWindow(TAG_PANEL)
            .show()
    }

    fun hideFloatWindow() {
        floatWindowManager
            .getFloatWindow(TAG_PANEL)
            .hide()
    }

    /**
     * 判断点是否在控制面板区域内
     *
     * @param x 点的x坐标
     * @param y 点的y坐标
     */
    fun isInPanelArea(x: Float, y: Float): Boolean {
        val areaPoint = IntArray(2)
        val panelView = view
        panelView.getLocationOnScreen(areaPoint)
        val panelX = areaPoint[0]
        val panelY = areaPoint[1]
        val panelRightBound = panelView.right + areaPoint[0]
        val panelBottomBound = panelView.bottom + areaPoint[1]
        //点的x大于等于面板的x，并且小于等于面板的右边界
        //点的y大于等于面板的y，并且小于等于面板的底部边界
        return (x >= panelX && x <= panelRightBound) && (y >= panelY && y <= panelBottomBound)
    }

    companion object {
        private const val TAG_PANEL = "panel_tag"
    }
}