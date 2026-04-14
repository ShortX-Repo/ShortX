package tornaco.apps.shortx.services.ui.floating.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

public class ControlPanelView extends FrameLayout {
    private static final float MIN_BUTTON_GAP_DP = 8f;
    private static final float EDGE_FADE_DEGREES = 12f;
    /**
     * 开关状态
     */
    private boolean isOpen = false;
    /**
     * 面板的当前半径，开关动画时会用到
     */
    private int mPanelRadius;
    /**
     * 半圆的原心位置到子控件之间的距离
     */
    int mRadius;
    int mMaxRadius;
    float mRadiusScale = 1f;
    /**
     * 子控件的半径
     */
    int mChildRadius;
    /**
     * 按钮的中心点X,Y
     */
    int mButtonCenterX;
    int mButtonCenterY;
    /**
     * 打开的动画
     */
    private ValueAnimator mOpenAnimator;
    /**
     * 关闭的动画
     */
    private ValueAnimator mOffAnimator;

    //------------ 自定义属性 ------------
    /**
     * 第一个按钮的角度
     */
    int mStartAngle = -90;
    private float mOverflowScrollDegrees = 0f;
    private float mLastTouchY;
    private float mDownTouchX;
    private float mDownTouchY;
    private boolean mIsDraggingOverflow;
    private final int mTouchSlop;
    /**
     * 面板宽高
     */
    private int mWidth;
    private int mHeight;
    /**
     * 是否在屏幕左边
     */
    private boolean isLeft = false;
    /**
     * 左边或右边的偏移量
     */
    private int mOffset;
    private OnTogglePanelListener mTogglePanelListener;
    private OnPanelSizeChangeCallback mOnPanelSizeChangeCallback;

    public ControlPanelView(Context context) {
        this(context, null);
    }

    public ControlPanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
//        this.mOffset = dip2px(getContext(), 15f);
        this.mOffset = 0;
        setStartAngle();
    }

    private void setStartAngle() {
        if (isLeft) {
            mStartAngle = -90;
        } else {
            mStartAngle = 90;
        }
    }

    public void setPanelRadiusScale(float scale) {
        mRadiusScale = scale;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        if (mOnPanelSizeChangeCallback != null) {
            mOnPanelSizeChangeCallback.onPanelSizeChange(mWidth, mHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int count = getChildCount();
        //最后一个作为中间的开关按钮
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            //计算子控件的半径。求法：子控件的宽高中最大的一半，暂时我们子空间宽高都是一致的
            mChildRadius = Math.max(childView.getMeasuredWidth(), childView.getMeasuredHeight()) / 2;
        }
        //计算出半圆的圆心心到子控件圆心的距离，就是我们半圆的半径
        //基本是高的一半减去子控件的2倍半径
        int baseRadius = (int) (mChildRadius * 2.5f * mRadiusScale);
        int availableHorizontalRadius = Math.max(0, widthSize - (mChildRadius * 2));
        int availableVerticalRadius = Math.max(0, (heightSize / 2) - mChildRadius);
        mMaxRadius = Math.min(availableHorizontalRadius, availableVerticalRadius);
        mRadius = Math.min(mMaxRadius, Math.max(baseRadius, getRequiredRadiusForEvenSpacing(count)));
        //计算按钮的位置，左、右的中间
        if (isLeft) {
            mButtonCenterX = mOffset;
            mButtonCenterY = mWidth / 2;
        } else {
            mButtonCenterX = mWidth - mOffset;
            mButtonCenterY = mWidth / 2;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            return;
        }
        int childCount = getChildCount();
        //一开始，是关闭状态，子View全部隐藏
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int count = getChildCount();
        if (count == 0) return;
        float stepDegrees = getStepDegrees(count);
        float maxOverflowScroll = getMaxOverflowScrollDegrees(count);
        if (mOverflowScrollDegrees > maxOverflowScroll) {
            mOverflowScrollDegrees = maxOverflowScroll;
        }
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            float progressDegrees = (i * stepDegrees) - mOverflowScrollDegrees;
            float edgeAlpha = computeEdgeAlpha(progressDegrees);
            boolean isVisibleInArc = edgeAlpha > 0f;
            childView.setVisibility(isVisibleInArc ? View.VISIBLE : View.INVISIBLE);
            childView.setAlpha(isVisibleInArc ? edgeAlpha : 0f);
            childView.setEnabled(progressDegrees >= 0f && progressDegrees <= 180f);
            if (!isVisibleInArc) {
                continue;
            }
            float angle = isLeft ? (-90f + progressDegrees) : (270f - progressDegrees);
            float[] point = getCoordinatePoint(mPanelRadius, angle);
            int childViewWidth = childView.getMeasuredWidth();
            int childViewHeight = childView.getMeasuredHeight();
            //int halfWidth = childViewWidth / 2;
            int halfHeight = childViewHeight / 2;
            //布局子控件
            int pointX = (int) point[0];
            int pointY = (int) point[1];
            if (isLeft) {
                childView.layout(pointX, pointY - halfHeight, pointX + childViewWidth, pointY + halfHeight);
            } else {
                childView.layout(pointX - childViewWidth, (pointY - halfHeight), (pointX), (pointY + halfHeight));
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!canRotateOverflowButtons()) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownTouchX = ev.getX();
                mDownTouchY = ev.getY();
                mLastTouchY = ev.getY();
                mIsDraggingOverflow = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - mDownTouchX;
                float dy = ev.getY() - mDownTouchY;
                if (!mIsDraggingOverflow && Math.abs(dy) > mTouchSlop && Math.abs(dy) > Math.abs(dx)) {
                    mIsDraggingOverflow = true;
                    mLastTouchY = ev.getY();
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsDraggingOverflow = false;
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canRotateOverflowButtons()) {
            return super.onTouchEvent(event);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownTouchX = event.getX();
                mDownTouchY = event.getY();
                mLastTouchY = event.getY();
                mIsDraggingOverflow = false;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - mDownTouchX;
                float dy = event.getY() - mDownTouchY;
                if (!mIsDraggingOverflow && Math.abs(dy) > mTouchSlop && Math.abs(dy) > Math.abs(dx)) {
                    mIsDraggingOverflow = true;
                    mLastTouchY = event.getY();
                }
                if (mIsDraggingOverflow) {
                    float deltaY = event.getY() - mLastTouchY;
                    mLastTouchY = event.getY();
                    updateOverflowScrollByDrag(deltaY);
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsDraggingOverflow) {
                    mIsDraggingOverflow = false;
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     *
     * @param angle 每个子控件和面板圆心的夹角
     */
    public float[] getCoordinatePoint(int panelRadius, float angle) {
        float[] point = new float[2];
        //Math类的三角函数是弧度制，所以要将角度转换为弧度才能进行计算
        double arcAngle = Math.toRadians(angle);
        //求子控件的X坐标，邻边 / 斜边，斜边的值刚好就是半径，cos值乘以斜边，就能求出邻边，而这个邻边的长度，就是点的x坐标
        point[0] = (float) (mButtonCenterX + Math.cos(arcAngle) * panelRadius);
        //求子控件的Y坐标，对边 / 斜边，斜边的值刚好就是半径，sin值乘以斜边，就能求出对边，而这个对边的长度，就是点的y坐标
        point[1] = (float) (mButtonCenterY + (getWidth() / 2) + Math.sin(arcAngle) * panelRadius);
        return point;
    }

    public void offNow() {
        if (isOpen) {
            startOffAnimation();
            isOpen = !isOpen;
        }
    }

    public void openNow() {
        if (!isOpen) {
            startOpenAnimation();
            isOpen = !isOpen;
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isAnimationRunning() {
        if (mOpenAnimator != null && mOpenAnimator.isRunning()) {
            return true;
        }
        return mOffAnimator != null && mOffAnimator.isRunning();
    }

    /**
     * 打开动画
     */
    private void startOpenAnimation() {
        if (mOpenAnimator != null && mOpenAnimator.isRunning()) {
            return;
        }
        //不断放大开关到子控件圆心的距离，从而形成散开的效果
        mOpenAnimator = ValueAnimator.ofInt(0, mRadius);
        mOpenAnimator.setDuration(250);
        mOpenAnimator.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.decelerate_interpolator));
        mOpenAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int cValue = (int) valueAnimator.getAnimatedValue();
                float alpha = cValue * 1f / mRadius;
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    getChildAt(i).setAlpha(alpha);
                }
                mPanelRadius = cValue;
                requestLayout();
            }
        });
        mOpenAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    getChildAt(i).setVisibility(View.VISIBLE);
                }
                if (mTogglePanelListener != null) {
                    mTogglePanelListener.onToggleChange(true);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mOpenAnimator = null;
            }
        });
        mOpenAnimator.start();
    }

    /**
     * 关闭动画
     */
    private void startOffAnimation() {
        if (mOffAnimator != null && mOffAnimator.isRunning()) {
            return;
        }
        //不断缩小开关到子控件圆心的距离，从而形成缩小的效果
        mOffAnimator = ValueAnimator.ofInt(mRadius, 0);
        mOffAnimator = ValueAnimator.ofInt(mRadius, 0);
        mOffAnimator.setDuration(200);
        mOffAnimator.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.decelerate_interpolator));
        mOffAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int cValue = (int) valueAnimator.getAnimatedValue();
                //线性比值，交叉相乘，1/200 = x / 1
                float alpha = cValue * 1f / mRadius;
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    getChildAt(i).setAlpha(alpha);
                }
                mPanelRadius = cValue;
                requestLayout();
            }
        });
        mOffAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    getChildAt(i).setVisibility(View.GONE);
                }
                if (mTogglePanelListener != null) {
                    mTogglePanelListener.onToggleChange(false);
                }
                mOffAnimator = null;
            }
        });
        mOffAnimator.start();
    }

    public void setOrientation(boolean isLeft) {
        if (this.isLeft != isLeft) {
            this.isLeft = isLeft;
            setStartAngle();
            //右边屏幕展示时，元素是从逆时针的，将元素反转
            requestLayout();
        }
    }

    /**
     * 修正跟随位置
     */
    public int[] followButtonPosition(int x, int y) {
        int[] result = new int[2];
        if (isLeft) {
            result[0] = x;
        } else {
            result[0] = x - mWidth + (mChildRadius * 2);
        }
        result[1] = y - (mHeight / 2) + mChildRadius;
        return result;
    }

    public interface OnTogglePanelListener {
        /**
         * 当切换开关状态时回调
         *
         * @param isOpen 当前是否是打开
         */
        void onToggleChange(boolean isOpen);
    }

    public interface OnPanelSizeChangeCallback {
        void onPanelSizeChange(int newWidth, int newHeight);
    }

    public void setOnPanelSizeChangeCallback(OnPanelSizeChangeCallback onPanelSizeChangeCallback) {
        this.mOnPanelSizeChangeCallback = onPanelSizeChangeCallback;
    }

    public void setOnTogglePanelListener(OnTogglePanelListener togglePanelListener) {
        this.mTogglePanelListener = togglePanelListener;
    }

    private boolean canRotateOverflowButtons() {
        return isOpen && !isAnimationRunning() && getChildCount() > 1 && getMaxOverflowScrollDegrees(getChildCount()) > 0f;
    }

    private void updateOverflowScrollByDrag(float deltaY) {
        float maxOverflowScroll = getMaxOverflowScrollDegrees(getChildCount());
        if (maxOverflowScroll <= 0f) {
            return;
        }
        float degreesPerPixel = getDegreesPerPixel();
        if (degreesPerPixel <= 0f) {
            return;
        }
        float next = mOverflowScrollDegrees - deltaY * degreesPerPixel;
        if (next < 0f) {
            next = 0f;
        } else if (next > maxOverflowScroll) {
            next = maxOverflowScroll;
        }
        if (next != mOverflowScrollDegrees) {
            mOverflowScrollDegrees = next;
            requestLayout();
        }
    }

    private float getDegreesPerPixel() {
        int radiusForArc = Math.max(mPanelRadius, mRadius);
        if (radiusForArc <= 0) {
            return 0f;
        }
        double arcPerDegree = (Math.PI * radiusForArc) / 180d;
        if (arcPerDegree <= 0d) {
            return 0f;
        }
        return (float) (1d / arcPerDegree);
    }

    private float getStepDegrees(int count) {
        if (count <= 1) {
            return 180f;
        }
        float evenlyDistributed = 180f / (count - 1);
        return Math.max(evenlyDistributed, getMinSpacingDegrees());
    }

    private float getMaxOverflowScrollDegrees(int count) {
        if (count <= 1) {
            return 0f;
        }
        float totalDegrees = getStepDegrees(count) * (count - 1);
        return Math.max(0f, totalDegrees - 180f);
    }

    private int getRequiredRadiusForEvenSpacing(int count) {
        if (count <= 1) {
            return 0;
        }
        float evenlyDistributed = 180f / (count - 1);
        double radians = Math.toRadians(evenlyDistributed);
        if (radians <= 0d) {
            return 0;
        }
        return (int) Math.ceil(getMinButtonArcSpacing() / radians);
    }

    private float getMinSpacingDegrees() {
        if (mRadius <= 0) {
            return 24f;
        }
        return (float) Math.toDegrees(getMinButtonArcSpacing() / mRadius);
    }

    private float getMinButtonArcSpacing() {
        return (mChildRadius * 2f) + dip2px(getContext(), MIN_BUTTON_GAP_DP);
    }

    private float computeEdgeAlpha(float progressDegrees) {
        if (progressDegrees >= 0f && progressDegrees <= 180f) {
            if (progressDegrees < EDGE_FADE_DEGREES) {
                return progressDegrees / EDGE_FADE_DEGREES;
            }
            if (progressDegrees > (180f - EDGE_FADE_DEGREES)) {
                return (180f - progressDegrees) / EDGE_FADE_DEGREES;
            }
            return 1f;
        }
        if (progressDegrees >= -EDGE_FADE_DEGREES) {
            return (progressDegrees + EDGE_FADE_DEGREES) / EDGE_FADE_DEGREES;
        }
        if (progressDegrees <= (180f + EDGE_FADE_DEGREES)) {
            return ((180f + EDGE_FADE_DEGREES) - progressDegrees) / EDGE_FADE_DEGREES;
        }
        return 0f;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    public static int spToPixel(Context context, float spValue) {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }
}