/*
 * Copyright (C) 2019 The Android Open Source Project
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

package tornaco.apps.shortx.services.ui.gesturedetect;

/**
 * This class describes the state of the touch explorer as well as the state of received and
 * injected pointers. This data is accessed both for purposes of touch exploration and gesture
 * dispatch.
 */
public class TouchState {

    public boolean isGestureDetecting() {
        return true;
    }

    public boolean isClear() {
        return false;
    }
}
