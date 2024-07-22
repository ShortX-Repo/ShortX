package tornaco.apps.shortx.services.ui.floating.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import tornaco.apps.shortx.services.ui.floating.FloatMoveEnum;
import tornaco.apps.shortx.services.ui.floating.FloatServiceUtil;
import tornaco.apps.shortx.services.ui.floating.FloatWindow;
import tornaco.apps.shortx.services.ui.floating.FloatWindowManager;
import tornaco.apps.shortx.services.ui.floating.FloatWindowOption;
import tornaco.apps.shortx.services.ui.floating.ScreenUtil;
import tornaco.apps.shortx.services.ui.floating.widget.ControlPanelView;
import tornaco.apps.shortx.services.ui.floating.widget.FloatActionButton;

/**
 * <b>Package:</b> com.zh.touchassistant.controller <br>
 * <b>FileName:</b> FloatPanelViewController <br>
 * <b>Create Date:</b> 2018/12/7  下午11:30 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class FloatPanelWindowController extends BaseFloatWindowController {
    private static final String TAG_PANEL = "panel_tag";

    private boolean isOpen = false;

    private FrameLayout mPanelContainerLayout;
    private ControlPanelView mFloatControlPanelView;
    private FloatWindowManager mFloatWindowManager;

    public FloatPanelWindowController(Context context, FloatWindowManager floatWindowManager) {
        super(context);
        this.mFloatWindowManager = floatWindowManager;
        init();
    }

    private BroadcastReceiver mUpdatePanelActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 更新Action数据，先移除，再添加
            mFloatControlPanelView.removeAllViews();
            addActionButton();
        }
    };

    private void init() {
        FrameLayout.LayoutParams rootParams
                = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mPanelContainerLayout = new FrameLayout(mContext);
        mPanelContainerLayout.setLayoutParams(rootParams);

        mFloatControlPanelView = new ControlPanelView(mContext);
        FrameLayout.LayoutParams panelParams
                = new FrameLayout.LayoutParams(ScreenUtil.dip2px(mContext, 150f), ScreenUtil.dip2px(mContext, 300f));
        mFloatControlPanelView.setLayoutParams(panelParams);

        mPanelContainerLayout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                // TODO
                // Listen for update actions.
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
            }
        });
        // 根据数据添加子View，并先隐藏
        addActionButton();
        // TODO 恢复上一次保存的位置
        mFloatControlPanelView.setOrientation(false);
        mPanelContainerLayout.addView(mFloatControlPanelView);

        initListener();
        attachFloatWindow();
    }

    private void attachFloatWindow() {
        mFloatWindowManager
                .makeFloatWindow(
                        mPanelContainerLayout,
                        TAG_PANEL,
                        FloatWindowOption
                                .create(new FloatWindowOption.Builder()
                                        .setX(FloatServiceUtil.getFloatPanelX())
                                        .setY(FloatServiceUtil.getFloatPanelY())
                                        .setShow(false)
                                        .setFloatMoveType(FloatMoveEnum.INACTIVE)));
    }

    private void initListener() {
        mFloatControlPanelView.setOnTogglePanelListener(new ControlPanelView.OnTogglePanelListener() {
            @Override
            public void onToggleChange(boolean isOpen) {
                //最新为打开
                if (isOpen) {
                    mFloatWindowManager
                            .getFloatWindow(TAG_PANEL)
                            .show();
                } else {
                    mFloatWindowManager
                            .getFloatWindow(TAG_PANEL)
                            .hide();
                }
            }
        });
    }

    public void setOnPanelSizeChangeCallback(ControlPanelView.OnPanelSizeChangeCallback onPanelSizeChangeCallback) {
        mFloatControlPanelView.setOnPanelSizeChangeCallback(onPanelSizeChangeCallback);
    }

    private void addActionButton() {
        for (int i = 0; i < 4; i++) {
            FloatActionButton actionView = new FloatActionButton(mContext);
            int iconSize = 100;
            int iconPadding = 16;
            FrameLayout.LayoutParams params = new ControlPanelView.LayoutParams(iconSize, iconSize);
            params.gravity = Gravity.CENTER;
            actionView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
            actionView.setImageResource(android.R.drawable.ic_dialog_info);
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FloatServiceUtil.toggleFloatButton();
                }
            });
            actionView.setVisibility(View.GONE);
            mFloatControlPanelView.addView(actionView, params);
        }
    }

    @Override
    public View getView() {
        return mPanelContainerLayout;
    }

    /**
     * 跟随浮动按钮的位置
     */
    public void followButtonPosition(int buttonX, int buttonY) {
        // 切换方向设置
        ControlPanelView panelView = mFloatControlPanelView;
        if (panelView.isAnimationRunning()) {
            return;
        }
        // 判断在屏幕左边还是右边，切换位置
        boolean isLeft = ScreenUtil.isScreenLeft(mContext, buttonX);
        panelView.setOrientation(isLeft);
        // TODO
        // Property.getDefault().setProperty(AccessibilityConstant.Config.KEY_FLOAT_WINDOW_IS_LEFT, isLeft);
        // 更新浮窗
        FloatWindow panelWindow = mFloatWindowManager.getFloatWindow(TAG_PANEL);
        int[] result = panelView.followButtonPosition(buttonX, buttonY);
        int fixX = result[0];
        int fixY = result[1];
        panelWindow.updateXY(fixX, fixY);
        // 记录位置
        FloatServiceUtil.px = fixX;
        FloatServiceUtil.py = fixY;
    }

    /**
     * 是否可以进行状态改变
     */
    public boolean isCanChangeStatus() {
        //动画还未结束时，不能改变
        return !mFloatControlPanelView.isAnimationRunning();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void open() {
        if (!mFloatControlPanelView.isOpen()) {
            mFloatControlPanelView.openNow();
            this.isOpen = !isOpen;
        }
    }

    public void off() {
        if (mFloatControlPanelView.isOpen()) {
            mFloatControlPanelView.offNow();
            this.isOpen = !isOpen;
        }
    }

    public void showFloatWindow() {
        mFloatWindowManager
                .getFloatWindow(TAG_PANEL)
                .show();
    }

    public void hideFloatWindow() {
        mFloatWindowManager
                .getFloatWindow(TAG_PANEL)
                .hide();
    }

    /**
     * 判断点是否在控制面板区域内
     *
     * @param x 点的x坐标
     * @param y 点的y坐标
     */
    public boolean isInPanelArea(float x, float y) {
        int[] areaPoint = new int[2];
        View panelView = getView();
        panelView.getLocationOnScreen(areaPoint);
        int panelX = areaPoint[0];
        int panelY = areaPoint[1];
        int panelRightBound = panelView.getRight() + areaPoint[0];
        int panelBottomBound = panelView.getBottom() + areaPoint[1];
        //点的x大于等于面板的x，并且小于等于面板的右边界
        //点的y大于等于面板的y，并且小于等于面板的底部边界
        return (x >= panelX && x <= panelRightBound) && (y >= panelY && y <= panelBottomBound);
    }
}