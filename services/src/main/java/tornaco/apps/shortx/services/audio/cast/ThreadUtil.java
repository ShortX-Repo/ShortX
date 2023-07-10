/*
 * Copyright (C) 2015 The Android Open Source Project
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

package tornaco.apps.shortx.services.audio.cast;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class ThreadUtil {
    private static final Handler sHandler = new Handler(Looper.getMainLooper());
    private static Handler sWorkThreadHandler;

    public static Handler getMainThreadHandler() {
        return sHandler;
    }

    public synchronized static Handler getWorkThreadHandler() {
        if (sWorkThreadHandler == null) {
            HandlerThread ht = new HandlerThread("worker.thread.handler");
            ht.start();
            sWorkThreadHandler = new Handler(ht.getLooper());
        }
        return sWorkThreadHandler;
    }

    public static Thread newThread(Runnable runnable) {
        Thread t = new Thread(runnable, "screencast.thread");
        t.setDaemon(false);
        t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
