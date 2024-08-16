/*
 * Copyright (C) 2022 The Android Open Source Project
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

package tornaco.apps.shortx.services.overlay.clipboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * Utility class for showing a simple clipboard toast on copy.
 */
// Android 30+
@SuppressLint("NewApi")
class ClipboardToast extends Toast.Callback {
    private final Context mContext;
    private Toast mCopiedToast;


    ClipboardToast(Context context) {
        mContext = context;
    }

    void showCopiedToast() {
        if (mCopiedToast != null) {
            mCopiedToast.cancel();
        }
        mCopiedToast = Toast.makeText(mContext, "Clipboard overlay text copied", Toast.LENGTH_SHORT);
        mCopiedToast.addCallback(this);
        mCopiedToast.show();
    }

    boolean isShowing() {
        return mCopiedToast != null;
    }

    @Override // Toast.Callback
    public void onToastHidden() {
        super.onToastHidden();
        mCopiedToast = null;
    }
}
