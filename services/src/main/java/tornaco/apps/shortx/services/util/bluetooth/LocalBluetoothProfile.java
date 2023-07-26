/*
 * Copyright (C) 2011 The Android Open Source Project
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

package tornaco.apps.shortx.services.util.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * LocalBluetoothProfile is an interface defining the basic
 * functionality related to a Bluetooth profile.
 */
public interface LocalBluetoothProfile {
    /**
     * Returns true if the user can initiate a connection, false otherwise.
     */
    boolean isConnectable();

    /**
     * Returns true if the user can enable auto connection for this profile.
     */
    boolean isAutoConnectable();

    int getConnectionStatus(BluetoothDevice device);

    boolean isPreferred(BluetoothDevice device);

    boolean isProfileReady();

    int getProfileId();

    /**
     * Display order for device profile settings.
     */
    int getOrdinal();
}
