package tornaco.apps.shortx.services.ui.floating.controller;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import tornaco.apps.shortx.services.ui.floating.FloatMoveEnum;
import tornaco.apps.shortx.services.ui.floating.FloatServiceUtil;
import tornaco.apps.shortx.services.ui.floating.FloatWindow;
import tornaco.apps.shortx.services.ui.floating.FloatWindowManager;
import tornaco.apps.shortx.services.ui.floating.FloatWindowOption;
import tornaco.apps.shortx.services.ui.floating.ScreenUtil;
import tornaco.apps.shortx.services.ui.floating.SimpleFloatWindowViewStateCallback;
import tornaco.apps.shortx.services.ui.floating.widget.FloatButton;

/**
 * <b>Package:</b> com.zh.touchassistant.controller <br>
 * <b>FileName:</b> FloatButtonViewController <br>
 * <b>Create Date:</b> 2018/12/7  下午10:27 <br>
 * <b>Author:</b> zihe <br>
 * <b>Description:</b>  <br>
 */
public class FloatButtonWindowController extends BaseFloatWindowController {
    private static final String TAG_BUTTON = "button_tag";

    public static final int STATUS_OFF = 0;
    public static final int STATUS_OPEN = 1;

    public static final float ALPHA_SHOW = 1f;

    private FloatButton mFloatButtonView;
    private int mCurrentStatus = STATUS_OFF;

    private OnStatusChangeListener mStatusChangeListener;
    private OnFloatButtonPositionUpdateListener mButtonPositionUpdateListener;
    private FloatWindowManager mFloatWindowManager;
    private FloatPanelWindowController mPanelViewController;
    private AnimatorSet mOpenAnimatorSet;
    private AnimatorSet mOffAnimatorSet;
    private final Handler mMainHandler;

    public FloatButtonWindowController(Context context, FloatWindowManager floatWindowManager, FloatPanelWindowController panelViewController) {
        super(context);
        this.mFloatWindowManager = floatWindowManager;
        this.mPanelViewController = panelViewController;
        mMainHandler = new Handler((Looper.getMainLooper()));
        init();
    }

    private void init() {
        mFloatButtonView = new FloatButton(mContext);
        mFloatButtonView.setImageResource(android.R.drawable.ic_dialog_info);
        initListener();
        attachFloatWindow();
    }

    private void initListener() {
        mFloatButtonView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FloatServiceUtil.toggleFloatButton();
            }
        });
    }

    private void attachFloatWindow() {
        mFloatWindowManager
                .makeFloatWindow(
                        mFloatButtonView,
                        TAG_BUTTON,
                        FloatWindowOption.create(new FloatWindowOption.Builder()
                                .setX(200)
                                .setY(800)
                                .setFloatMoveType(FloatMoveEnum.SLIDE)
                                .setDuration(250)
                                .setBoundOffset(ScreenUtil.dip2px(mContext, 5f))
                                .setViewStateCallback(new SimpleFloatWindowViewStateCallback() {

                                    @Override
                                    public void onPositionUpdate(int oldX, int oldY, final int newX, final int newY) {
                                        super.onPositionUpdate(oldX, oldY, newX, newY);
                                        mMainHandler.removeCallbacksAndMessages(null);
                                        mMainHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Save newX newY
                                                FloatServiceUtil.x = newX;
                                                FloatServiceUtil.y = newY;
                                            }
                                        }, 550);
                                        //让面板跟随按钮
                                        if (mButtonPositionUpdateListener != null) {
                                            mButtonPositionUpdateListener.onFloatButtonPositionUpdate(newX, newY);
                                        }
                                    }

                                    @Override
                                    public void onPrepareDrag() {
                                    }

                                    @Override
                                    public boolean isCanLongPress() {
                                        //展开时，不算长按
                                        boolean isOpen = isOpen();
                                        if (isOpen) {
                                            return false;
                                        } else {
                                            return true;
                                        }
                                    }

                                    @Override
                                    public void onLongPress() {
                                        super.onLongPress();
                                        //长按
                                        //Toast.makeText(mContext.getApplicationContext(), "长按了", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onDragging(float moveX, float moveY) {
                                        super.onDragging(moveX, moveY);
                                        // 将Alpha调整比较容易可见的值
                                        if (getView().getAlpha() != ALPHA_SHOW) {
                                            getView().setAlpha(ALPHA_SHOW);
                                        }
                                    }

                                    @Override
                                    public void onDragFinish() {
                                        super.onDragFinish();
                                        // 将Alpha调整比较容易可见的值
                                        if (getView().getAlpha() != ALPHA_SHOW) {
                                            getView().setAlpha(ALPHA_SHOW);
                                        }
                                    }

                                    @Override
                                    public boolean isCanDrag(float moveX, float moveY) {
                                        //准备拽托时，如果是打开状态，不能拽托
                                        boolean isOpen = isOpen();
                                        if (isOpen) {
                                            return false;
                                        }
                                        //限制移动的位置，不能比面板高度还要小
//                                        int screenHeight = ScreenUtil.getScreenHeight(getApplicationContext());
//                                        int halfHeight = mPanelViewController.getView().getHeight() / 2;
//                                        if (screenHeight - moveY < halfHeight) {
//                                            return false;
//                                        }
                                        return true;
                                    }

                                    @Override
                                    public void onClickFloatOutsideArea(float x, float y) {
                                        super.onClickFloatOutsideArea(x, y);
                                        //因为控制面板也是一个悬浮窗，点击面板的按钮时，也算点击悬浮按钮以外区域
                                        // 所以当面板打开时，不算点击以外范围，并且如果点击的在面板区域内也不算
                                        if (mPanelViewController.isOpen()
                                                && mPanelViewController.isInPanelArea(x, y)) {
                                            return;
                                        }
                                        //当点击悬浮按钮区域外时，如果是打开状态，则关闭
                                        if (isOpen()) {
                                            FloatServiceUtil.closeFloatButton();
                                        }
                                    }
                                })));
    }

    public interface OnFloatButtonPositionUpdateListener {
        void onFloatButtonPositionUpdate(int newX, int newY);
    }

    public void setOnFloatButtonPositionUpdateListener(OnFloatButtonPositionUpdateListener buttonPositionUpdateListener) {
        this.mButtonPositionUpdateListener = buttonPositionUpdateListener;
    }

    @Override
    public FloatButton getView() {
        return mFloatButtonView;
    }

    public void toggle() {
        if (mCurrentStatus == STATUS_OPEN) {
            off();
        } else {
            open();
        }
    }

    public boolean isOpen() {
        return mCurrentStatus == STATUS_OPEN;
    }

    public FloatWindow getFloatWindow() {
        return this.mFloatWindowManager
                .getFloatWindow(TAG_BUTTON);
    }

    public void showFloatWindow() {
        this.mFloatWindowManager
                .getFloatWindow(TAG_BUTTON)
                .show();
    }

    public void hideFloatWindow() {
        this.mFloatWindowManager
                .getFloatWindow(TAG_BUTTON)
                .hide();
    }

    public void open() {
        if (mOffAnimatorSet != null && mOffAnimatorSet.isRunning()) {
            mOffAnimatorSet.end();
        }
        if (this.mCurrentStatus != STATUS_OPEN) {
            if (mStatusChangeListener != null) {
                //不能改变则打断
                boolean isCanChange = mStatusChangeListener.onPrepareStatusChange(STATUS_OPEN);
                if (!isCanChange) {
                    return;
                }
            }
            mFloatButtonView.setSelected(true);
            this.mCurrentStatus = STATUS_OPEN;
            if (mOpenAnimatorSet != null && mOpenAnimatorSet.isRunning()) {
                mOpenAnimatorSet.end();
            }
            mOpenAnimatorSet = new AnimatorSet();
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mFloatButtonView, View.SCALE_X, 0.8f);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mFloatButtonView, View.SCALE_Y, 0.8f);
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mFloatButtonView, View.ALPHA, ALPHA_SHOW);
            mOpenAnimatorSet
                    .play(scaleXAnimator)
                    .with(scaleYAnimator)
                    .with(alphaAnimator);
            mOpenAnimatorSet.start();
            if (mStatusChangeListener != null) {
                mStatusChangeListener.onStatusChange(this.mCurrentStatus);
            }
        }
    }

    public void off() {
        if (this.mCurrentStatus != STATUS_OFF) {
            mFloatButtonView.setSelected(false);
            this.mCurrentStatus = STATUS_OFF;
            if (mOffAnimatorSet != null && mOffAnimatorSet.isRunning()) {
                mOffAnimatorSet.end();
            }
            mOffAnimatorSet = new AnimatorSet();
            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mFloatButtonView, View.SCALE_X, 1f);
            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mFloatButtonView, View.SCALE_Y, 1f);
            mOffAnimatorSet
                    .play(scaleXAnimator)
                    //2个渐变动画要一起执行
                    .with(scaleYAnimator);
            mOffAnimatorSet.start();
            if (mStatusChangeListener != null) {
                mStatusChangeListener.onStatusChange(this.mCurrentStatus);
            }
        }
    }

    public interface OnStatusChangeListener {
        /**
         * 当准备状态改变时回调
         *
         * @param prepareStatus 准备改变的状态
         * @return 返回true代表允许改变，false代表拦截改变
         */
        boolean onPrepareStatusChange(int prepareStatus);

        /**
         * 状态改变时回调
         *
         * @param newStatus 新状态
         */
        void onStatusChange(int newStatus);
    }

    public void setOnStatusChangeListener(OnStatusChangeListener listener) {
        this.mStatusChangeListener = listener;
    }
}