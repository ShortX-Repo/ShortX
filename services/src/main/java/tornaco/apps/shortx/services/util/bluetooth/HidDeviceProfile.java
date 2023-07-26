/*
 * Copyright (C) 2017 The Android Open Source Project
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

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import java.util.List;

import tornaco.apps.shortx.core.util.Logger;

/**
 * HidProfile handles Bluetooth HID profile.
 */
@SuppressLint({"MissingPermission", "NewApi"})
// API 28
public class HidDeviceProfile implements LocalBluetoothProfile {
    private static final Logger LOGGER = new Logger("HidDeviceProfile");
    // Order of this profile in device profiles list
    private static final int ORDINAL = 18;

    private final LocalBluetoothAdapter mLocalAdapter;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private final LocalBluetoothProfileManager mProfileManager;
    static final String NAME = "HID DEVICE";

    private BluetoothHidDevice mService;
    private boolean mIsProfileReady;

    HidDeviceProfile(Context context, LocalBluetoothAdapter adapter,
                     CachedBluetoothDeviceManager deviceManager,
                     LocalBluetoothProfileManager profileManager) {
        mLocalAdapter = adapter;
        mDeviceManager = deviceManager;
        mProfileManager = profileManager;
        adapter.getProfileProxy(context, new HidDeviceServiceListener(),
                BluetoothProfile.HID_DEVICE);
    }

    // These callbacks run on the main thread.
    private final class HidDeviceServiceListener
            implements BluetoothProfile.ServiceListener {

        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            LOGGER.d("Bluetooth service connected :-)");
            mService = (BluetoothHidDevice) proxy;
            // We just bound to the service, so refresh the UI for any connected HID devices.
            List<BluetoothDevice> deviceList = mService.getConnectedDevices();
            for (BluetoothDevice nextDevice : deviceList) {
                CachedBluetoothDevice device = mDeviceManager.findDevice(nextDevice);
                // we may add a new device here, but generally this should not happen
                if (device == null) {
                    LOGGER.w("HidProfile found new device: " + nextDevice);
                    device = mDeviceManager.addDevice(mLocalAdapter, mProfileManager, nextDevice);
                }
                LOGGER.d("Connection status changed: " + device);
                device.onProfileStateChanged(HidDeviceProfile.this,
                        BluetoothProfile.STATE_CONNECTED);
                device.refresh();
            }
            mIsProfileReady = true;
        }

        public void onServiceDisconnected(int profile) {
            LOGGER.d("Bluetooth service disconnected");
            mIsProfileReady = false;
        }
    }

    @Override
    public boolean isProfileReady() {
        return mIsProfileReady;
    }

    @Override
    public int getProfileId() {
        return BluetoothProfile.HID_DEVICE;
    }

    @Override
    public boolean isConnectable() {
        return true;
    }

    @Override
    public boolean isAutoConnectable() {
        return false;
    }

    @Override
    public int getConnectionStatus(BluetoothDevice device) {
        if (mService == null) {
            return BluetoothProfile.STATE_DISCONNECTED;
        }
        List<BluetoothDevice> deviceList = mService.getConnectedDevices();

        return !deviceList.isEmpty() && deviceList.contains(device)
                ? mService.getConnectionState(device)
                : BluetoothProfile.STATE_DISCONNECTED;
    }

    @Override
    public boolean isPreferred(BluetoothDevice device) {
        return getConnectionStatus(device) != BluetoothProfile.STATE_DISCONNECTED;
    }

    @Override
    public String toString() {
        return NAME;
    }

    @Override
    public int getOrdinal() {
        return ORDINAL;
    }

    protected void finalize() {
        LOGGER.d("finalize()");
        if (mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.HID_DEVICE,
                        mService);
                mService = null;
            } catch (Throwable t) {
                LOGGER.e(t, "Error cleaning up HID proxy");
            }
        }
    }
}
