package tornaco.apps.shortx.services.ui.pie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

public class AppPieView extends View {
    private static final int HAPTIC_FEEDBACK_CONFIRM =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.R
                    ? HapticFeedbackConstants.CONTEXT_CLICK
                    : HapticFeedbackConstants.CONFIRM;
    private static final int HAPTIC_FEEDBACK_DOWN =
            HapticFeedbackConstants.CLOCK_TICK;

    public static final int SEGMENT_TICK = 26;
    private static final int HAPTIC_FEEDBACK_CHOICE =
            Build.VERSION.SDK_INT < 34
                    ? HAPTIC_FEEDBACK_DOWN
                    : SEGMENT_TICK;
    private static final float FADE_DURATION = 200f;

    private final TextPaint paintText = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Point touch = new Point();
    private final Ripple ripple = new Ripple();
    private final long tapTimeout;
    private final long minLongPressDuration;
    private final int listPadding;
    private final float dp;
    private final float textHeight;
    private final float touchSlopSq;

    private Runnable rippleRunnable;
    private int viewWidth;
    private int viewHeight;
    private int deadZoneTop;
    private int deadZoneBottom;
    private int minRadius;
    private int maxRadius;
    private int radius;
    private int maxScrollY;
    private int lastScrollY;
    private int lastInsertAt;
    private int lastSelectedIcon;
    private long fadeInFrom;
    private long fadeOutFrom;

    public AppPieView(Context context) {
        super(context);

        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        dp = dm.density;
        float sp = dm.scaledDensity;
        listPadding = Math.round(16f * dp);

        paintText.setColor(Color.RED);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(14f * sp);
        textHeight = paintText.descent() - paintText.ascent();

        ViewConfiguration configuration = ViewConfiguration.get(context);
        float touchSlop = configuration.getScaledTouchSlop();
        touchSlopSq = touchSlop * touchSlop;
        tapTimeout = ViewConfiguration.getTapTimeout();
        minLongPressDuration = ViewConfiguration.getLongPressTimeout();
        ripple.setDuration(minLongPressDuration);
        initTouchListener();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            initMenu(right - left, bottom - top);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPieMenu(canvas);
        if (ripple.draw(canvas)) {
            invalidate();
        }
    }

    @Override
    protected int computeVerticalScrollRange() {
        return maxScrollY + getHeight();
    }

    @Override
    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return getScrollY();
    }

    private static Bitmap getBitmapFromDrawable(Resources res, int resId) {
        return Converter.getBitmapFromDrawable(getDrawable(res, resId));
    }

    private static Drawable getDrawable(Resources res, int resId) {
        return res.getDrawable(resId, null);
    }

    private void initTouchListener() {
        setOnTouchListener(new OnTouchListener() {
            private final FlingRunnable flingRunnable = new FlingRunnable();
            private final SparseArray<TouchReference> touchReferences =
                    new SparseArray<>();

            private VelocityTracker velocityTracker;
            private int primaryId;
            private int scrollOffset;
            private Runnable longPressRunnable;
            private Runnable performActionRunnable;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                touch.set(Math.round(event.getX()), Math.round(event.getY()));
                switch (event.getActionMasked()) {
                    default:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        fadeOutPieMenu();
                        hidePieMenu();
                        invalidate();
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_DOWN:
                        if (inDeadZone(v.getContext())) {
                            return false;
                        }
                        if (cancelPerformAction()) {
                            // Ignore additional ACTION_DOWN's when there
                            // was a pending action.
                            break;
                        }
                        addTouch(event);
                        setCenter(touch);
                        performHapticFeedback(HAPTIC_FEEDBACK_DOWN);
                        fadeInFrom = event.getEventTime();
                        resetFadeOutPieMenu();
                        invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        postPerformAction(v, event);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        fadeOutPieMenu();
                        hidePieMenu();
                        invalidate();
                        break;
                }
                return true;
            }

            private void addTouch(MotionEvent event) {
                int index = event.getActionIndex();
                primaryId = event.getPointerId(index);
                addTouchReference(event, primaryId, index);
            }

            private void updateReferences(MotionEvent event) {
                // Unfortunately, and contrary to the docs, some (old?) touch
                // screen drivers don't return the index of the touch that has
                // gone up in ACTION_POINTER_UP for getActionIndex() but an
                // index of a pointer that is still down.
                // So the only option is to update all references.
                for (int i = 0, l = event.getPointerCount(); i < l; ++i) {
                    int id = event.getPointerId(i);
                    addTouchReference(event, id, i);
                }
            }

            private void addTouchReference(MotionEvent event, int id,
                                           int index) {
                touchReferences.put(id, new TouchReference(
                        event.getX(index),
                        event.getY(index),
                        event.getEventTime()));
            }

            private TouchReference getTouchReference(MotionEvent event,
                                                     int index) {
                return index > -1 && index < event.getPointerCount()
                        ? touchReferences.get(event.getPointerId(index))
                        : null;
            }

            private boolean inDeadZone(Context context) {
                return false;
            }

            private boolean inTopDeadZone() {
                return touch.y < deadZoneTop;
            }

            private boolean inBottomDeadZone() {
                return touch.y > deadZoneBottom;
            }

            private void initScroll(MotionEvent event) {
                flingRunnable.stop();
                recycleVelocityTracker();
                velocityTracker = VelocityTracker.obtain();
                velocityTracker.addMovement(event);
                scrollOffset = getScrollY();
            }

            private int getPrimaryIndex(MotionEvent event) {
                int count = event.getPointerCount();
                int id = -1;
                for (int i = 0; i < count; ++i) {
                    id = event.getPointerId(i);
                    if (id == primaryId) {
                        return i;
                    }
                }
                // If the ID wasn't found, the pointer must have gone up.
                primaryId = id;
                return count - 1;
            }

            private void scroll(MotionEvent event) {
                int index = getPrimaryIndex(event);
                if (index < 0) {
                    return;
                }
                TouchReference tr = getTouchReference(event, index);
                if (tr == null) {
                    return;
                }
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                }
                int y = Math.round(event.getY(index));
                int scrollY = scrollOffset + (tr.pos.y - y);
                lastScrollY = clamp(scrollY, 0, maxScrollY);
                scrollList(lastScrollY, true);
                if (lastScrollY == 0 || lastScrollY == maxScrollY) {
                    // Move reference to current coordinate if this
                    // event wouldn't move the list to make movements
                    // in the opposite direction immediate.
                    tr.pos.y = y;
                    scrollOffset = getScrollY();
                }
            }

            private void keepScrolling(MotionEvent event) {
                if (event.getPointerCount() < 2 && velocityTracker != null) {
                    // 1000 means getYVelocity() will return pixels
                    // per second.
                    velocityTracker.computeCurrentVelocity(1000);
                    flingRunnable.start(Math.round(
                            velocityTracker.getYVelocity()));
                    recycleVelocityTracker();
                }
            }

            private void recycleVelocityTracker() {
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
            }


            private boolean isTap(MotionEvent event, long timeOut) {
                TouchReference tr = getTouchReference(event, 0);
                if (tr == null) {
                    return false;
                }
                return SystemClock.uptimeMillis() - tr.time <= timeOut &&
                        distSq(tr.pos, touch) <= touchSlopSq;
            }

            private void postPerformAction(final View v, MotionEvent event) {
                cancelPerformAction();
                final boolean wasTap = isTap(event, tapTimeout);
                final Point at = new Point(touch.x, touch.y);
                performActionRunnable = () -> {
                    v.performClick();
                    if (performAction(v.getContext(), at, wasTap)) {
                        v.performHapticFeedback(HAPTIC_FEEDBACK_CONFIRM);
                    }
                    performActionRunnable = null;
                    hidePieMenu();
                    invalidate();
                };
                // Wait a short time before performing the action
                // because some touch screen drivers send
                // ACTION_UP/ACTION_DOWN pairs for very short
                // touch interruptions what makes the menu jump
                // and execute multiple actions unintentionally.
                postDelayed(performActionRunnable, 16);
            }

            private boolean cancelPerformAction() {
                if (performActionRunnable != null) {
                    removeCallbacks(performActionRunnable);
                    performActionRunnable = null;
                    return true;
                }
                return false;
            }
        });
    }

    private void initMenu(int width, int height) {
        int viewMin = Math.min(width, height);
        int viewMax = Math.max(width, height);
        viewWidth = width;
        viewHeight = height;

        float maxIconSize = 64f * dp;
        if (Math.floor(viewMin * .28f) > maxIconSize) {
            viewMin = Math.round(maxIconSize / .28f);
        }

        maxRadius = Math.round(viewMin * .5f);
        minRadius = Math.round(maxRadius * .5f);
        radius = clampRadius(8);

        int pieBottom = viewMax / 2 + maxRadius;

        deadZoneTop = Math.min(height / 10, Math.round(64f * dp));
        deadZoneBottom = height - deadZoneTop;
    }

    private void addIconInteractively(AppMenu.Icon appIcon) {
        if (appIcon == null) {
            return;
        }
        resetScrollWithoutAnimation();
        invalidate();
    }

    private void resetScrollWithoutAnimation() {
        cancelRipple();
        scrollTo(0, 0);
    }

    private void scrollList(int y, boolean isScrolling) {
        scrollTo(0, y);
    }

    private void cancelRipple() {
        ripple.cancel();
        if (rippleRunnable != null) {
            removeCallbacks(rippleRunnable);
            rippleRunnable = null;
        }
    }

    private boolean performAction(Context context, Point at, boolean wasTap) {
        if (isPieVisible()) {
            boolean result = false;
            if (wasTap) {
            } else {
                // TODO Launcher SOME
                ripple.set(at);
                result = true;
            }
            fadeOutPieMenu();
            return result;
        }
        return false;
    }

    private void setCenter(Point point) {
        setCenter(point.x, point.y);
    }

    private void setCenter(int x, int y) {
        PieLauncherApp.appMenu.set(
                clamp(x, radius, viewWidth - radius),
                clamp(y, radius, viewHeight - radius),
                radius);
        lastSelectedIcon = -1;
    }

    private void drawPieMenu(Canvas canvas) {
        float f = 0;
        long now = SystemClock.uptimeMillis();
        if (fadeInFrom > 0) {
            f = Math.min(1f, (now - fadeInFrom) / FADE_DURATION);
        } else {
            long delta = now - fadeOutFrom;
            if (delta < FADE_DURATION) {
                // Ensure f < 1f so invalidate() is invoked.
                f = Math.min(.99999f, 1f - delta / FADE_DURATION);
            }
        }
        if (f > 0) {
            CanvasPieMenu.paint.setAlpha(Math.round(f * 255f));
            PieLauncherApp.appMenu.calculate(touch.x, touch.y);
            PieLauncherApp.appMenu.draw(canvas);
            if (f < 1f) {
                invalidate();
            }
            int selected = PieLauncherApp.appMenu.getSelectedIcon();
            if (selected != lastSelectedIcon) {
                lastSelectedIcon = selected;
                performHapticFeedback(HAPTIC_FEEDBACK_CHOICE);
            }
        }
    }

    private void resetFadeOutPieMenu() {
        fadeOutFrom = 0;
    }

    private void fadeOutPieMenu() {
        fadeOutFrom = isPieVisible() ? SystemClock.uptimeMillis() : 0;
    }

    private void hidePieMenu() {
        fadeInFrom = 0;
    }

    private boolean isPieVisible() {
        return fadeInFrom > 0;
    }

    private static float distSq(Point a, Point b) {
        return distSq(a.x, a.y, b.x, b.y);
    }

    private static float distSq(int ax, int ay, int bx, int by) {
        float dx = ax - bx;
        float dy = ay - by;
        return dx * dx + dy * dy;
    }

    private int clampRadius(int r) {
        return clamp(r, minRadius, maxRadius);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void scaleRadius(float factor) {
        radius *= factor;
        radius = clampRadius(radius);
        PieLauncherApp.appMenu.setRadius(radius);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (!detector.isInProgress()) {
                return false;
            }
            scaleRadius(detector.getScaleFactor());
            invalidate();
            return true;
        }
    }

    private class FlingRunnable implements Runnable {
        private final OverScroller scroller;
        private int maxY;
        private int pps;

        @Override
        public void run() {

        }

        private FlingRunnable() {
            scroller = new OverScroller(getContext());
        }

        private void start(int pixelsPerSecond) {
            pps = -pixelsPerSecond;
            maxY = maxScrollY;
            initFling(getScrollY());
            update();
        }

        private void initFling(int y) {
            scroller.fling(
                    0, y,
                    0, pps,
                    0, 0,
                    0, maxY,
                    0, listPadding);
        }

        private void stop() {
            scroller.forceFinished(true);
            removeCallbacks(this);
        }

        private void update() {
            invalidate();
            postOnAnimation(this);
        }
    }

    private static final class TouchReference {
        private final Point pos = new Point();
        private final long time;

        private TouchReference(float x, float y, long time) {
            this.pos.set(Math.round(x), Math.round(y));
            this.time = time;
        }
    }
}
