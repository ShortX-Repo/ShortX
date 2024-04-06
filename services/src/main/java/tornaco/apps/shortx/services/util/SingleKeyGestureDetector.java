package tornaco.apps.shortx.services.util;


/*
 * Copyright (C) 2021 The Android Open Source Project
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

import android.annotation.IntDef;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import tornaco.apps.shortx.core.BuildPropInternal;

/**
 * Detect single key gesture: press, long press, very long press and multi press.
 * <p>
 * Call {@link #reset} if current {@link KeyEvent} has been handled by another policy
 */
public final class SingleKeyGestureDetector {
    private static final String TAG = "SingleKeyGesture";
    private static final boolean DEBUG = BuildPropInternal.APP_IS_DEBUG;
    private static final int MSG_KEY_LONG_PRESS = 0;
    private static final int MSG_KEY_DELAYED_PRESS = 2;
    private static final int MSG_KEY_UNHANDLED_DOWN = 3;
    private int mKeyPressCounter;
    private boolean mBeganFromNonInteractive = false;
    private final ArrayList<SingleKeyRule> mRules = new ArrayList<>();
    private final Set<Integer> mRuleCodes = new HashSet<>();
    private SingleKeyRule mActiveRule = null;
    // Key code of current key down event, reset when key up.
    private int mDownKeyCode = KeyEvent.KEYCODE_UNKNOWN;
    private volatile boolean mHandledByLongPress = false;
    private final Handler mHandler;
    private long mLastDownTime = 0;
    /**
     * Supported gesture flags
     */
    public static final int KEY_LONGPRESS = 1 << 1;
    static final long MULTI_PRESS_TIMEOUT = 240;
    static long sDefaultLongPressTimeout;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(prefix = {"KEY_"}, value = {
            KEY_LONGPRESS,
    })
    public @interface KeyGestureFlag {
    }

    /**
     * Rule definition for single keys gesture.
     * E.g : define power key.
     * <pre class="prettyprint">
     *  SingleKeyRule rule =
     *      new SingleKeyRule(KEYCODE_POWER, KEY_LONGPRESS|KEY_VERYLONGPRESS) {
     *           int getMaxMultiPressCount() { // maximum multi press count. }
     *           void onPress(long downTime) { // short press behavior. }
     *           void onLongPress(long eventTime) { // long press behavior. }
     *           void onVeryLongPress(long eventTime) { // very long press behavior. }
     *           void onMultiPress(long downTime, int count) { // multi press behavior.  }
     *       };
     *  </pre>
     */
    public abstract static class SingleKeyRule {
        private final int mKeyCode;
        private final int mSupportedGestures;

        public SingleKeyRule(int keyCode, int supportedGestures) {
            mKeyCode = keyCode;
            mSupportedGestures = supportedGestures;
        }

        /**
         * True if the rule could intercept the key.
         */
        private boolean shouldInterceptKey(int keyCode) {
            return keyCode == mKeyCode;
        }

        /**
         * True if the rule support long press.
         */
        private boolean supportLongPress() {
            return (mSupportedGestures & KEY_LONGPRESS) != 0;
        }

        /**
         * Maximum count of multi presses.
         * Return 1 will trigger onPress immediately when {android.view.KeyEvent.ACTION_UP}.
         * Otherwise trigger onMultiPress immediately when reach max count when
         * { android.view.KeyEvent.ACTION_DOWN}.
         */
        public int getMaxMultiPressCount() {
            return 1;
        }

        /**
         * Called when short press has been detected.
         */
        public abstract void onPress(long downTime);

        /**
         * Callback when multi press (>= 2) has been detected.
         */
        public void onMultiPress(long downTime, int count) {
        }

        /**
         * Returns the timeout in milliseconds for a long press.
         * <p>
         * If multipress is also supported, this should always be greater than the multipress
         * timeout. If very long press is supported, this should always be less than the very long
         * press timeout.
         */
        public long getLongPressTimeoutMs() {
            return sDefaultLongPressTimeout;
        }

        /**
         * Callback when long press has been detected.
         */
        public void onLongPress(long eventTime) {
        }

        public void unHandledDown(KeyEvent keycode) {
        }

        @Override
        public String toString() {
            return "KeyCode=" + KeyEvent.keyCodeToString(mKeyCode)
                    + ", LongPress=" + supportLongPress()
                    + ", MaxMultiPressCount=" + getMaxMultiPressCount();
        }
    }

    public static SingleKeyGestureDetector get() {
        SingleKeyGestureDetector detector = new SingleKeyGestureDetector();
        // context.getResources().getInteger(
        //                com.android.internal.R.integer.config_globalActionsKeyTimeout);
        sDefaultLongPressTimeout = 500;
        return detector;
    }

    private SingleKeyGestureDetector() {
        mHandler = new KeyHandler();
    }

    public void addRule(SingleKeyRule rule) {
        mRules.add(rule);
        mRuleCodes.add(rule.mKeyCode);
    }

    public boolean hasRule(int keycode) {
        return mRuleCodes.contains(keycode);
    }

    public void clearRules() {
        mRules.clear();
        mRuleCodes.clear();
    }

    public void interceptKey(KeyEvent event, boolean interactive) {
        Log.d(TAG, "interceptKey: " + event);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            // Store the non interactive state when first down.
            if (mDownKeyCode == KeyEvent.KEYCODE_UNKNOWN || mDownKeyCode != event.getKeyCode()) {
                mBeganFromNonInteractive = !interactive;
            }
            interceptKeyDown(event);
        } else {
            interceptKeyUp(event);
        }
    }

    private void interceptKeyDown(KeyEvent event) {
        final int keyCode = event.getKeyCode();
        // same key down.
        if (mDownKeyCode == keyCode) {
            if (mActiveRule != null && (event.getFlags() & KeyEvent.FLAG_LONG_PRESS) != 0
                    && mActiveRule.supportLongPress() && !mHandledByLongPress) {
                if (DEBUG) {
                    Log.i(TAG, "Long press key " + KeyEvent.keyCodeToString(keyCode));
                }
                mHandledByLongPress = true;
                mHandler.removeMessages(MSG_KEY_LONG_PRESS);
                final Message msg = mHandler.obtainMessage(MSG_KEY_LONG_PRESS, mActiveRule.mKeyCode,
                        0, new MsgObj(mActiveRule, event));
                msg.setAsynchronous(true);
                mHandler.sendMessage(msg);
            }
            return;
        }
        // When a different key is pressed, stop processing gestures for the currently active key.
        if (mDownKeyCode != KeyEvent.KEYCODE_UNKNOWN
                || (mActiveRule != null && !mActiveRule.shouldInterceptKey(keyCode))) {
            if (DEBUG) {
                Log.i(TAG, "Press another key " + KeyEvent.keyCodeToString(keyCode));
            }
            reset();
        }
        mDownKeyCode = keyCode;
        // Picks a new rule, return if no rule picked.
        if (mActiveRule == null) {
            final int count = mRules.size();
            for (int index = 0; index < count; index++) {
                final SingleKeyRule rule = mRules.get(index);
                if (rule.shouldInterceptKey(keyCode)) {
                    if (DEBUG) {
                        Log.i(TAG, "Intercept key by rule " + rule);
                    }
                    mActiveRule = rule;
                    break;
                }
            }
            mLastDownTime = 0;
        }
        if (mActiveRule == null) {
            return;
        }
        final long keyDownInterval = event.getDownTime() - mLastDownTime;
        mLastDownTime = event.getDownTime();
        if (keyDownInterval >= MULTI_PRESS_TIMEOUT) {
            mKeyPressCounter = 1;
        } else {
            mKeyPressCounter++;
        }
        if (mKeyPressCounter == 1) {
            if (mActiveRule.supportLongPress()) {
                final Message msg = mHandler.obtainMessage(MSG_KEY_LONG_PRESS, keyCode, 0,
                        new MsgObj(mActiveRule, event));
                msg.setAsynchronous(true);
                mHandler.sendMessageDelayed(msg, mActiveRule.getLongPressTimeoutMs());
            } else {
                // Send unhandled down.
                final Message msg = mHandler.obtainMessage(MSG_KEY_UNHANDLED_DOWN, keyCode, 0,
                        new MsgObj(mActiveRule, event));
                msg.setAsynchronous(true);
                mHandler.sendMessageDelayed(msg, MULTI_PRESS_TIMEOUT);
            }

        } else {
            mHandler.removeMessages(MSG_KEY_LONG_PRESS);
            mHandler.removeMessages(MSG_KEY_DELAYED_PRESS);
            mHandler.removeMessages(MSG_KEY_UNHANDLED_DOWN);
            // Trigger multi press immediately when reach max count.( > 1)
            if (mActiveRule.getMaxMultiPressCount() > 1
                    && mKeyPressCounter == mActiveRule.getMaxMultiPressCount()) {
                if (DEBUG) {
                    Log.i(TAG, "Trigger multi press " + mActiveRule.toString() + " for it"
                            + " reached the max count " + mKeyPressCounter);
                }
                final Message msg = mHandler.obtainMessage(MSG_KEY_DELAYED_PRESS, keyCode,
                        mKeyPressCounter, new MsgObj(mActiveRule, event));
                msg.setAsynchronous(true);
                mHandler.sendMessage(msg);
            }
        }
    }

    private boolean interceptKeyUp(KeyEvent event) {
        mHandler.removeMessages(MSG_KEY_LONG_PRESS);
        mHandler.removeMessages(MSG_KEY_UNHANDLED_DOWN);
        mDownKeyCode = KeyEvent.KEYCODE_UNKNOWN;
        if (mActiveRule == null) {
            return false;
        }
        if (mHandledByLongPress) {
            mHandledByLongPress = false;
            mKeyPressCounter = 0;
            mActiveRule = null;
            return true;
        }
        if (event.getKeyCode() == mActiveRule.mKeyCode) {
            // Directly trigger short press when max count is 1.
            if (mActiveRule.getMaxMultiPressCount() == 1) {
                if (DEBUG) {
                    Log.i(TAG, "press key " + KeyEvent.keyCodeToString(event.getKeyCode()));
                }
                Message msg = mHandler.obtainMessage(MSG_KEY_DELAYED_PRESS, mActiveRule.mKeyCode,
                        1, new MsgObj(mActiveRule, event));
                msg.setAsynchronous(true);
                mHandler.sendMessage(msg);
                mActiveRule = null;
                return true;
            }
            // This could be a multi-press.  Wait a little bit longer to confirm.
            if (mKeyPressCounter < mActiveRule.getMaxMultiPressCount()) {
                Message msg = mHandler.obtainMessage(MSG_KEY_DELAYED_PRESS, mActiveRule.mKeyCode,
                        mKeyPressCounter, new MsgObj(mActiveRule, event));
                msg.setAsynchronous(true);
                mHandler.sendMessageDelayed(msg, MULTI_PRESS_TIMEOUT);
            }
            return true;
        }
        reset();
        return false;
    }

    public int getKeyPressCounter(int keyCode) {
        if (mActiveRule != null && mActiveRule.mKeyCode == keyCode) {
            return mKeyPressCounter;
        } else {
            return 0;
        }
    }

    public void reset() {
        if (mActiveRule != null) {
            if (mDownKeyCode != KeyEvent.KEYCODE_UNKNOWN) {
                mHandler.removeMessages(MSG_KEY_LONG_PRESS);
            }
            if (mKeyPressCounter > 0) {
                mHandler.removeMessages(MSG_KEY_DELAYED_PRESS);
                mKeyPressCounter = 0;
            }
            mActiveRule = null;
        }
        mHandler.removeMessages(MSG_KEY_UNHANDLED_DOWN);
        mHandledByLongPress = false;
        mDownKeyCode = KeyEvent.KEYCODE_UNKNOWN;
    }

    public boolean isKeyIntercepted(int keyCode) {
        return mActiveRule != null && mActiveRule.shouldInterceptKey(keyCode);
    }

    public boolean beganFromNonInteractive() {
        return mBeganFromNonInteractive;
    }

    public void dump(String prefix, PrintWriter pw) {
        pw.println(prefix + "SingleKey rules:");
        for (SingleKeyRule rule : mRules) {
            pw.println(prefix + "  " + rule);
        }
    }

    static class MsgObj {
        public SingleKeyRule rule;
        public KeyEvent keyEvent;

        public MsgObj(SingleKeyRule rule, KeyEvent keyEvent) {
            this.rule = rule;
            this.keyEvent = keyEvent;
        }
    }

    private class KeyHandler extends Handler {
        KeyHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            final MsgObj obj = (MsgObj) msg.obj;
            SingleKeyRule rule = obj.rule;
            if (rule == null) {
                Log.wtf(TAG, "No active rule.");
                return;
            }
            final int keyCode = msg.arg1;
            final int pressCount = msg.arg2;
            switch (msg.what) {
                case MSG_KEY_LONG_PRESS:
                    if (DEBUG) {
                        Log.i(TAG, "Detect long press " + KeyEvent.keyCodeToString(keyCode));
                    }
                    mHandledByLongPress = true;
                    rule.onLongPress(mLastDownTime);
                    break;
                case MSG_KEY_DELAYED_PRESS:
                    if (DEBUG) {
                        Log.i(TAG, "Detect press " + KeyEvent.keyCodeToString(keyCode)
                                + ", count " + pressCount);
                    }
                    if (pressCount == 1) {
                        rule.onPress(mLastDownTime);
                    } else {
                        rule.onMultiPress(mLastDownTime, pressCount);
                    }
                    break;
                case MSG_KEY_UNHANDLED_DOWN:
                    if (DEBUG) {
                        Log.i(TAG, "Unhandled down " + KeyEvent.keyCodeToString(keyCode)
                                + ", count " + pressCount);
                    }
                    reset();
                    rule.unHandledDown(obj.keyEvent);
                    break;
            }
        }
    }
}