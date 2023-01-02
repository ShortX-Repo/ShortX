/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tornaco.apps.shortx.services.gesture;

import static android.view.DisplayCutout.BOUNDS_POSITION_BOTTOM;
import static android.view.DisplayCutout.BOUNDS_POSITION_LEFT;
import static android.view.DisplayCutout.BOUNDS_POSITION_RIGHT;
import static android.view.DisplayCutout.BOUNDS_POSITION_TOP;
import static tornaco.apps.shortx.core.BuildProp.APP_IS_DEBUG;

import android.annotation.NonNull;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.DisplayInfo;
import android.view.InputDevice;
import android.view.MotionEvent;

import java.io.PrintWriter;

import tornaco.apps.shortx.core.util.OsUtils;
import tornaco.apps.shortx.core.util.ScreenUtilsKt;
import tornaco.apps.shortx.services.ResExtKt;

/**
 * Listens for system-wide input gestures, firing callbacks when detected.
 */
public class SystemGesturesPointerEventListener {
    private static final String TAG = "SystemGestures";
    private static final boolean DEBUG = APP_IS_DEBUG;
    private static final long SWIPE_TIMEOUT_MS = 500;
    private static final int MAX_TRACKED_POINTERS = 32;  // max per input system
    private static final int UNTRACKED_POINTER = -1;

    private static final int SWIPE_NONE = 0;
    private static final int SWIPE_FROM_TOP = 1;
    private static final int SWIPE_FROM_BOTTOM = 2;
    private static final int SWIPE_FROM_RIGHT = 3;
    private static final int SWIPE_FROM_LEFT = 4;

    private final Context mContext;
    private int mDisplayCutoutTouchableRegionSize;
    // The thresholds for each edge of the display
    private final Rect mSwipeStartThreshold = new Rect();
    private int mSwipeDistanceThreshold;
    private final Callbacks mCallbacks;
    private final int[] mDownPointerId = new int[MAX_TRACKED_POINTERS];
    private final float[] mDownX = new float[MAX_TRACKED_POINTERS];
    private final float[] mDownY = new float[MAX_TRACKED_POINTERS];
    private final long[] mDownTime = new long[MAX_TRACKED_POINTERS];

    int screenHeight;
    int screenWidth;
    private int mDownPointers;
    private boolean mSwipeFireable;
    private boolean mDebugFireable;
    private boolean mMouseHoveringAtEdge;

    public SystemGesturesPointerEventListener(Context context, Callbacks callbacks) {
        mContext = checkNull("context", context);
        mCallbacks = checkNull("callbacks", callbacks);
        onConfigurationChanged();
    }

    void onDisplayInfoChanged(DisplayInfo info) {
        screenWidth = info.logicalWidth;
        screenHeight = info.logicalHeight;
        onConfigurationChanged();
    }

    void onConfigurationChanged() {
        final Resources r = mContext.getResources();

        // r.getDimensionPixelSize(com.android.internal.R.dimen.system_gestures_start_threshold)
        int defaultThreshold = ResExtKt.getDp(12);
        try {
            int resId = Resources.getSystem().getIdentifier("system_gestures_start_threshold", "dimen", "android");
            // /2 means we use less than system distance to block some touch events.
            defaultThreshold = r.getDimensionPixelSize(resId) / 2;
            Log.w(TAG, "system_gestures_start_threshold: " + defaultThreshold);
        } catch (Throwable ignored) {

        }
        mSwipeStartThreshold.set(defaultThreshold, defaultThreshold, defaultThreshold,
                defaultThreshold);
        mSwipeDistanceThreshold = defaultThreshold;

        if (OsUtils.isQOrAbove()) {
            final Display display = ScreenUtilsKt.defaultDisplay(mContext);
            final DisplayCutout displayCutout = display.getCutout();
            if (displayCutout != null) {
                // Expand swipe start threshold such that we can catch touches that just start beyond
                // the notch area
                try {
                    int resId = Resources.getSystem().getIdentifier("display_cutout_touchable_region_size", "dimen", "android");
                    mDisplayCutoutTouchableRegionSize = r.getDimensionPixelSize(resId);
                    Log.w(TAG, "display_cutout_touchable_region_size: " + defaultThreshold);
                } catch (Throwable ignored) {
                }
                final Rect[] bounds = displayCutout.getBoundingRectsAll();
                if (bounds[BOUNDS_POSITION_LEFT] != null) {
                    mSwipeStartThreshold.left = Math.max(mSwipeStartThreshold.left,
                            bounds[BOUNDS_POSITION_LEFT].width() + mDisplayCutoutTouchableRegionSize);
                }
                if (bounds[BOUNDS_POSITION_TOP] != null) {
                    mSwipeStartThreshold.top = Math.max(mSwipeStartThreshold.top,
                            bounds[BOUNDS_POSITION_TOP].height() + mDisplayCutoutTouchableRegionSize);
                }
                if (bounds[BOUNDS_POSITION_RIGHT] != null) {
                    mSwipeStartThreshold.right = Math.max(mSwipeStartThreshold.right,
                            bounds[BOUNDS_POSITION_RIGHT].width() + mDisplayCutoutTouchableRegionSize);
                }
                if (bounds[BOUNDS_POSITION_BOTTOM] != null) {
                    mSwipeStartThreshold.bottom = Math.max(mSwipeStartThreshold.bottom,
                            bounds[BOUNDS_POSITION_BOTTOM].height()
                                    + mDisplayCutoutTouchableRegionSize);
                }
            }
        }
        if (DEBUG) Log.d(TAG, "mSwipeStartThreshold=" + mSwipeStartThreshold
                + " mSwipeDistanceThreshold=" + mSwipeDistanceThreshold);
    }

    private static <T> T checkNull(String name, T arg) {
        if (arg == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return arg;
    }

    public boolean onPointerEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (DEBUG) Log.d(TAG, "DOWN event.getDownTime: " + event.getDownTime());
                mSwipeFireable = true;
                mDebugFireable = true;
                mDownPointers = 0;
                captureDown(event, 0);
                if (mMouseHoveringAtEdge) {
                    mMouseHoveringAtEdge = false;
                    mCallbacks.onMouseLeaveFromEdge();
                }
                mCallbacks.onDown();
                return false;
            case MotionEvent.ACTION_POINTER_DOWN:
                captureDown(event, event.getActionIndex());
                if (mDebugFireable) {
                    mDebugFireable = event.getPointerCount() < 5;
                    if (!mDebugFireable) {
                        if (DEBUG) Log.d(TAG, "Firing debug");
                        mCallbacks.onDebug();
                    }
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (DEBUG) Log.d(TAG, "MOVE event.getDownTime: " + event.getDownTime());
                if (mSwipeFireable) {
                    final int swipe = detectSwipe(event);
                    mSwipeFireable = swipe == SWIPE_NONE;
                    if (swipe == SWIPE_FROM_TOP) {
                        if (DEBUG) Log.d(TAG, "Firing onSwipeFromTop");
                        return mCallbacks.onSwipeFromTop();
                    } else if (swipe == SWIPE_FROM_BOTTOM) {
                        if (DEBUG) Log.d(TAG, "Firing onSwipeFromBottom");
                        int pointerId = event.getPointerId(0);
                        final int i = findIndex(pointerId);
                        if (i >= 0 && i < mDownX.length) {
                            int x = (int) mDownX[i];
                            int y = (int) mDownY[i];
                            return mCallbacks.onSwipeFromBottom(x, y);
                        }
                    } else if (swipe == SWIPE_FROM_RIGHT) {
                        if (DEBUG) Log.d(TAG, "Firing onSwipeFromRight");
                        int pointerId = event.getPointerId(0);
                        final int i = findIndex(pointerId);
                        if (i >= 0 && i < mDownX.length) {
                            int x = (int) mDownX[i];
                            int y = (int) mDownY[i];
                            return mCallbacks.onSwipeFromRight(x, y);
                        }
                    } else if (swipe == SWIPE_FROM_LEFT) {
                        if (DEBUG) Log.d(TAG, "Firing onSwipeFromLeft");
                        int pointerId = event.getPointerId(0);
                        final int i = findIndex(pointerId);
                        if (i >= 0 && i < mDownX.length) {
                            int x = (int) mDownX[i];
                            int y = (int) mDownY[i];
                            return mCallbacks.onSwipeFromLeft(x, y);
                        }
                    }
                }
                return false;
            case MotionEvent.ACTION_HOVER_MOVE:
                if (event.isFromSource(InputDevice.SOURCE_MOUSE)) {
                    if (!mMouseHoveringAtEdge && event.getY() == 0) {
                        mCallbacks.onMouseHoverAtTop();
                        mMouseHoveringAtEdge = true;
                    } else if (!mMouseHoveringAtEdge && event.getY() >= screenHeight - 1) {
                        mCallbacks.onMouseHoverAtBottom();
                        mMouseHoveringAtEdge = true;
                    } else if (mMouseHoveringAtEdge
                            && (event.getY() > 0 && event.getY() < screenHeight - 1)) {
                        mCallbacks.onMouseLeaveFromEdge();
                        mMouseHoveringAtEdge = false;
                    }
                }
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (DEBUG) Log.d(TAG, "UP/CANCEL event.getDownTime: " + event.getDownTime());
                mSwipeFireable = false;
                mDebugFireable = false;
                mCallbacks.onUpOrCancel();
                return false;
            default:
                if (DEBUG) Log.d(TAG, "Ignoring " + event);
                return false;
        }
    }

    private void captureDown(MotionEvent event, int pointerIndex) {
        final int pointerId = event.getPointerId(pointerIndex);
        final int i = findIndex(pointerId);
        if (DEBUG) Log.d(TAG, "pointer " + pointerId
                + " down pointerIndex=" + pointerIndex + " trackingIndex=" + i);
        if (i != UNTRACKED_POINTER) {
            mDownX[i] = event.getX(pointerIndex);
            mDownY[i] = event.getY(pointerIndex);
            mDownTime[i] = event.getEventTime();
            if (DEBUG) Log.d(TAG, "pointer " + pointerId
                    + " down x=" + mDownX[i] + " y=" + mDownY[i]);
        }
    }

    protected boolean currentGestureStartedInRegion(Region r) {
        return r.contains((int) mDownX[0], (int) mDownY[0]);
    }

    private int findIndex(int pointerId) {
        for (int i = 0; i < mDownPointers; i++) {
            if (mDownPointerId[i] == pointerId) {
                return i;
            }
        }
        if (mDownPointers == MAX_TRACKED_POINTERS || pointerId == MotionEvent.INVALID_POINTER_ID) {
            return UNTRACKED_POINTER;
        }
        mDownPointerId[mDownPointers++] = pointerId;
        return mDownPointers - 1;
    }

    private int detectSwipe(MotionEvent move) {
        final int historySize = move.getHistorySize();
        final int pointerCount = move.getPointerCount();
        for (int p = 0; p < pointerCount; p++) {
            final int pointerId = move.getPointerId(p);
            final int i = findIndex(pointerId);
            if (i != UNTRACKED_POINTER) {
                for (int h = 0; h < historySize; h++) {
                    final long time = move.getHistoricalEventTime(h);
                    final float x = move.getHistoricalX(p, h);
                    final float y = move.getHistoricalY(p, h);
                    final int swipe = detectSwipe(i, time, x, y);
                    if (swipe != SWIPE_NONE) {
                        return swipe;
                    }
                }
                final int swipe = detectSwipe(i, move.getEventTime(), move.getX(p), move.getY(p));
                if (swipe != SWIPE_NONE) {
                    return swipe;
                }
            }
        }
        return SWIPE_NONE;
    }

    private int detectSwipe(int i, long time, float x, float y) {
        final float fromX = mDownX[i];
        final float fromY = mDownY[i];
        final long elapsed = time - mDownTime[i];
        if (DEBUG) Log.d(TAG, "pointer " + mDownPointerId[i]
                + " moved (" + fromX + "->" + x + "," + fromY + "->" + y + ") in " + elapsed);
        if (fromY <= mSwipeStartThreshold.top
                && y > fromY + mSwipeDistanceThreshold
                && elapsed < SWIPE_TIMEOUT_MS) {
            return SWIPE_FROM_TOP;
        }
        if (fromY >= screenHeight - mSwipeStartThreshold.bottom
                && y < fromY - mSwipeDistanceThreshold
                && elapsed < SWIPE_TIMEOUT_MS) {
            return SWIPE_FROM_BOTTOM;
        }
        if (fromX >= screenWidth - mSwipeStartThreshold.right
                && x < fromX - mSwipeDistanceThreshold
                && elapsed < SWIPE_TIMEOUT_MS) {
            return SWIPE_FROM_RIGHT;
        }
        if (fromX <= mSwipeStartThreshold.left
                && x > fromX + mSwipeDistanceThreshold
                && elapsed < SWIPE_TIMEOUT_MS) {
            return SWIPE_FROM_LEFT;
        }
        return SWIPE_NONE;
    }

    public void dump(@NonNull PrintWriter pw, @NonNull String prefix) {
        final String inner = prefix + "  ";
        pw.println(prefix + TAG + ":");
        pw.print(inner);
        pw.print("mDisplayCutoutTouchableRegionSize=");
        pw.println(mDisplayCutoutTouchableRegionSize);
        pw.print(inner);
        pw.print("mSwipeStartThreshold=");
        pw.println(mSwipeStartThreshold);
        pw.print(inner);
        pw.print("mSwipeDistanceThreshold=");
        pw.println(mSwipeDistanceThreshold);
    }

    interface Callbacks {
        boolean onSwipeFromTop();

        boolean onSwipeFromBottom(int x, int y);

        boolean onSwipeFromRight(int x, int y);

        boolean onSwipeFromLeft(int x, int y);

        void onDown();

        void onUpOrCancel();

        void onMouseHoverAtTop();

        void onMouseHoverAtBottom();

        void onMouseLeaveFromEdge();

        void onDebug();
    }

    public static class CallbacksAdapter implements Callbacks {
        @Override
        public boolean onSwipeFromTop() {
            return false;
        }

        @Override
        public boolean onSwipeFromBottom(int x, int y) {
            return false;
        }

        @Override
        public boolean onSwipeFromRight(int x, int y) {
            return false;
        }

        @Override
        public boolean onSwipeFromLeft(int x, int y) {
            return false;
        }

        @Override
        public void onDown() {

        }

        @Override
        public void onUpOrCancel() {

        }

        @Override
        public void onMouseHoverAtTop() {

        }

        @Override
        public void onMouseHoverAtBottom() {

        }

        @Override
        public void onMouseLeaveFromEdge() {

        }

        @Override
        public void onDebug() {

        }
    }
}