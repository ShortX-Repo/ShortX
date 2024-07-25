/**
 * Copyright (C) 2019 The Android Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tornaco.apps.shortx.services.input;

import android.annotation.NonNull;
import android.os.Looper;
import android.view.Choreographer;
import android.view.InputMonitor;

import tornaco.apps.shortx.core.util.Logger;

/**
 * @see android.view.InputMonitor
 */
public class InputMonitorCompat {
    static final Logger LOGGER = new Logger("InputMonitorCompat");
    private final InputMonitor mInputMonitor;
    private final String mName;

    /**
     * Monitor input on the specified display for gestures.
     */
    public InputMonitorCompat(@NonNull String name, int displayId) {
        mName = name + "-disp" + displayId;
        mInputMonitor = InputManagerGlobalFunc.INSTANCE.monitorGestureInput(name, displayId);
        LOGGER.d("Input monitor (" + mName + ") created");
    }

    /**
     * @see InputMonitor#pilferPointers()
     */
    public void pilferPointers() {
        mInputMonitor.pilferPointers();
    }


    /**
     * @see InputMonitor#dispose()
     */
    public void dispose() {
        mInputMonitor.dispose();
        LOGGER.i("Input monitor (" + mName + ") disposed");
    }

    /**
     * @see InputMonitor#getInputChannel()
     */
    public InputChannelCompat.InputEventReceiver getInputReceiver(Looper looper, Choreographer choreographer,
                                                                  InputChannelCompat.InputEventListener listener) {
        LOGGER.i("Input event receiver for monitor (" + mName + ") created");
        return new InputChannelCompat.InputEventReceiver(mName, mInputMonitor.getInputChannel(), looper, choreographer,
                listener);
    }
}