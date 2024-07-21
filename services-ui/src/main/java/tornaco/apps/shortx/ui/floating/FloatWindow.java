package tornaco.apps.shortx.ui.floating;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

public class FloatWindow {
    private Context mContext;
    private String mTag;

    private View mView;
    private boolean isShow = true;
    private boolean isRemove = false;
    private int mX;
    private int mY;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private final FloatWindowOption mWindowOption;
    private final int mSlop;
    private ValueAnimator mAnimator;
    /**
     * 是否正在拽托
     */
    private boolean isDragging = false;

    public FloatWindow(Context context, String mTag, View view, FloatWindowOption option) {
        this.mContext = context;
        this.mTag = mTag;
        this.mView = view;
        mWindowOption = option;
        mWindowManager = getWindowManager();
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.flags =
                //在此模式下，系统会将当前Window区域以外的单击事件传递给底层的Window，
                // 当前Window区域以内的单击事件则自己处理，这个标记很重要，
                // 一般来说都需要开启此标记，否则其他Window将无法收到单击事件
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        //表示Window不需要获取焦点，也不需要接收各种输入事件，最终事件会直接传递给下层的具有焦点的Window
                        //不加入该Flag能响应返回键回调，但是返回键一直被屏蔽，加入后又不能收到监听
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        //加入该Flag，让浮窗层级在软键盘之下，否则如果软键盘覆盖弹窗后，点击软键盘和浮窗重合位置会被浮窗响应
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                        //忽略周围的装饰，例如状态栏。解决切换全屏模式时，位置上移的问题
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        //允许悬浮窗范围越界到屏幕外
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        //设置该Flag，当触摸事件在悬浮窗以外区域时，发送一个MotionEvent.ACTION_OUTSIDE事件
                        //不会接收到悬浮窗区域以外的move、up事件，只有一次ACTION_OUTSIDE事件
                        //这里设置这个Flag，来关闭悬浮窗
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        //允许窗体浮动在锁屏之上，国产ROM无效
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        if (option.isNotTouchable()) {
            //表示这个Window不会收到点击事件，否则点击事件无法穿透
            mLayoutParams.flags = mLayoutParams.flags | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        mLayoutParams.windowAnimations = 0;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.x = option.getX();
        this.mX = mLayoutParams.x;
        mLayoutParams.y = option.getY();
        this.mY = mLayoutParams.y;
        mLayoutParams.width = option.getWidth();
        mLayoutParams.height = option.getHeight();
        //8.0使用新的Type类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mSlop = ViewConfiguration.get(context.getApplicationContext()).getScaledTouchSlop();
        //处理手势
        handleGesture();
        mWindowManager.addView(mView, mLayoutParams);
        if (!mWindowOption.isShow()) {
            hide();
        }
    }

    private void handleGesture() {
        final FloatMoveEnum moveType = this.mWindowOption.getMoveType();
        switch (moveType) {
            case INACTIVE:
                break;
            default:
                getView().setOnTouchListener(new View.OnTouchListener() {
                    private float downX;
                    private float downY;
                    private float lastX;
                    private float lastY;
                    private float changeX;
                    private float changeY;
                    private int newX;
                    private int newY;
                    private long downTime;
                    private boolean isCanDrag;
                    private long lastDownTime;
                    private boolean isLongPress;
                    private boolean isTouching;
                    private boolean isMoving;

                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            if (mWindowOption.getViewStateCallback() != null) {
                                mWindowOption.getViewStateCallback()
                                        .onClickFloatOutsideArea(event.getRawX(), event.getRawY());
                            }
                            return false;
                        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            isTouching = true;
                            downTime = System.currentTimeMillis();
                            downX = event.getRawX();
                            downY = event.getRawY();
                            lastX = event.getRawX();
                            lastY = event.getRawY();
                            lastDownTime = System.currentTimeMillis();
                            cancelAnimator();
                            //准备拽托前，回调给外面
                            if (mWindowOption.getViewStateCallback() != null) {
                                mWindowOption.getViewStateCallback().onPrepareDrag();
                            }
                            //判断是否是长按
                            if (mWindowOption.getViewStateCallback() != null
                                    && mWindowOption.getViewStateCallback().isCanLongPress()) {
                                getView().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        long timeMillis = System.currentTimeMillis();
                                        if (isTouching && !isMoving && (timeMillis - lastDownTime >= 300)) {
                                            //长按
                                            isLongPress = true;
                                            if (mWindowOption.getViewStateCallback() != null) {
                                                mWindowOption.getViewStateCallback().onLongPress();
                                            }
                                        }
                                    }
                                }, 300);
                            }
                            return false;
                        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            isMoving = true;
                            float moveX = event.getRawX();
                            float moveY = event.getRawY();
                            //长按了就不能算移动了
                            isLongPress = false;
                            if (mWindowOption.getViewStateCallback() != null) {
                                //移动前，回调给外面，如果外面限制不能拖动，则不拖动
                                isCanDrag = mWindowOption.getViewStateCallback().isCanDrag(moveX, moveY);
                                if (!isCanDrag) {
                                    return true;
                                }
                                isDragging = true;
                                //拽托中回调
                                if (mWindowOption.getViewStateCallback() != null) {
                                    mWindowOption.getViewStateCallback().onDragging(moveX, moveY);
                                }
                            }
                            int oldX = (int) lastX;
                            int oldY = (int) lastY;
                            changeX = moveX - lastX;
                            changeY = moveY - lastY;
                            newX = (int) (getX() + changeX);
                            newY = (int) (getY() + changeY);
                            updateXY(newX, newY);
                            if (mWindowOption.getViewStateCallback() != null) {
                                mWindowOption.getViewStateCallback().onPositionUpdate(oldX, oldY, newX, newY);
                            }
                            lastX = event.getRawX();
                            lastY = event.getRawY();
                            return false;
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                            isMoving = false;
                            isTouching = false;
                            long upTime = System.currentTimeMillis();
                            //点击时间
                            long touchDuration = upTime - downTime;
                            float upX = event.getRawX();
                            float upY = event.getRawY();
                            //长按是不移动的，所以直接返回
                            if (isLongPress) {
                                isLongPress = false;
                                return true;
                            }
                            //判断是点击还是移动，这里记录时间，避免移动过去又移动回来被当时点击的情况
                            boolean isClick = touchDuration < 400 &&
                                    getDistanceBetween2Points(new PointF(downX, downY), new PointF(upX, upY)) <= mSlop;
                            //是移动的情况
                            if (!isClick) {
                                if (!isCanDrag) {
                                    return true;
                                }
                                //如果是自动贴边模式
                                if (moveType == FloatMoveEnum.SLIDE) {
                                    final int startX = (int) upX;
                                    int endX;
                                    boolean isLeft = ScreenUtil.isScreenLeft(getContext(), startX);
                                    if (isLeft) {
                                        endX = mWindowOption.getBoundOffset();
                                    } else {
                                        endX = ScreenUtil.getScreenWidth(getContext())
                                                - (getView().getWidth())
                                                - mWindowOption.getBoundOffset();
                                    }
                                    mAnimator = ValueAnimator.ofInt(startX, endX);
                                    mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        int preX = startX;

                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            int x = (int) animation.getAnimatedValue();
                                            updateX(x);
                                            if (mWindowOption.getViewStateCallback() != null) {
                                                mWindowOption.getViewStateCallback().onPositionUpdate(preX, getY(), x, getY());
                                            }
                                            preX = x;
                                        }
                                    });
                                    mAnimator.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            super.onAnimationStart(animation);
                                            if (mWindowOption.getViewStateCallback() != null) {
                                                mWindowOption.getViewStateCallback().onMoveAnimStart();
                                            }
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            isDragging = false;
                                            if (mWindowOption.getViewStateCallback() != null) {
                                                mWindowOption.getViewStateCallback().onDragFinish();
                                            }
                                            if (mWindowOption.getViewStateCallback() != null) {
                                                mWindowOption.getViewStateCallback().onMoveAnimEnd();
                                            }
                                        }
                                    });
                                    startAnimator();
                                    //如果是点击，返回false才能让OnClick执行，移动则返回true
                                    return true;
                                } else {
                                    //其他模式
                                    isDragging = false;
                                    if (mWindowOption.getViewStateCallback() != null) {
                                        mWindowOption.getViewStateCallback().onDragFinish();
                                    }
                                    return true;
                                }
                            } else {
                                //点击的情况
                                return false;
                            }
                        }
                        return true;
                    }
                });
                break;
        }
    }

    /**
     * 计算两点之间的距离
     */
    private static float getDistanceBetween2Points(PointF pointOne, PointF pointTwo) {
        return (float) Math.sqrt(Math.pow(pointOne.y - pointTwo.y, 2) + Math.pow(pointOne.x - pointTwo.x, 2));
    }

    private void startAnimator() {
        TimeInterpolator animatorInterpolator = mWindowOption.getInterpolator() == null ?
                new DecelerateInterpolator() : mWindowOption.getInterpolator();
        mAnimator.setInterpolator(animatorInterpolator);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimator.removeAllUpdateListeners();
                mAnimator.removeAllListeners();
                mAnimator = null;
                if (mWindowOption.getViewStateCallback() != null) {
                    mWindowOption.getViewStateCallback().onMoveAnimEnd();
                }
            }
        });
        mAnimator.setDuration(mWindowOption.getDuration()).start();
        if (mWindowOption.getViewStateCallback() != null) {
            mWindowOption.getViewStateCallback().onMoveAnimStart();
        }
    }

    private void cancelAnimator() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    protected WindowManager getWindowManager() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 展示悬浮窗
     */
    public void show() {
        if (!isShow) {
            getView().setVisibility(View.VISIBLE);
            isShow = !isShow;
            if (this.mWindowOption.getViewStateCallback() != null) {
                this.mWindowOption.getViewStateCallback().onShow();
            }
        }
    }

    /**
     * 隐藏悬浮窗
     */
    public void hide() {
        if (isShow) {
            getView().setVisibility(View.GONE);
            isShow = !isShow;
            if (this.mWindowOption.getViewStateCallback() != null) {
                this.mWindowOption.getViewStateCallback().onHide();
            }
        }
    }

    public void remove() {
        if (mView != null) {
            getWindowManager().removeView(mView);
            isRemove = true;
            mView = null;
            if (this.mWindowOption.getViewStateCallback() != null) {
                this.mWindowOption.getViewStateCallback().onRemove();
            }
        }
    }

    /**
     * 悬浮窗当前是否展示
     */
    public boolean isShowing() {
        return isShow;
    }

    /**
     * 是否已经移除
     */
    public boolean isRemove() {
        return isRemove;
    }

    public void setSize(int width, int height) {
        mLayoutParams.width = width;
        mLayoutParams.height = height;
    }

    /**
     * 获取悬浮床当前的X坐标
     */
    public int getX() {
        return mX;
    }

    /**
     * 获取悬浮窗当前的Y坐标
     */
    public int getY() {
        return mY;
    }

    /**
     * 更新悬浮窗的X坐标
     */
    public void updateX(int newX) {
        if (isRemove) {
            return;
        }
        mLayoutParams.x = newX;
        mX = newX;
        getWindowManager().updateViewLayout(mView, mLayoutParams);
    }

    /**
     * 更新悬浮窗的Y坐标
     */
    public void updateY(int newY) {
        if (isRemove) {
            return;
        }
        mLayoutParams.y = newY;
        mY = newY;
        getWindowManager().updateViewLayout(mView, mLayoutParams);
    }

    public void updateXY(int newX, int newY) {
        if (isRemove) {
            return;
        }
        mLayoutParams.x = newX;
        mLayoutParams.y = newY;
        mX = newX;
        mY = newY;
        getWindowManager().updateViewLayout(mView, mLayoutParams);
    }

    /**
     * 获取悬浮窗View
     */
    public View getView() {
        return this.mView;
    }

    public Context getContext() {
        return this.mContext;
    }

    /**
     * 获取当前悬浮窗的Tag
     */
    public String getTag() {
        return this.mTag;
    }

    /**
     * 是否正在拽托
     */
    public boolean isDragging() {
        return isDragging;
    }
}