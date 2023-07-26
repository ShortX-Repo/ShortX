/*
 * Copyright (C) 2012 The Android Open Source Project
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
import android.bluetooth.BluetoothMapClient;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;

import tornaco.apps.shortx.core.util.Logger;

/**
 * MapClientProfile handles Bluetooth MAP profile.
 */
@SuppressLint("MissingPermission")
public final class MapClientProfile implements LocalBluetoothProfile {
    private static final Logger LOGGER = new Logger("MapClientProfile");
    private BluetoothMapClient mService;
    private boolean mIsProfileReady;

    private final LocalBluetoothAdapter mLocalAdapter;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private final LocalBluetoothProfileManager mProfileManager;

    static final ParcelUuid[] UUIDS = {
            BluetoothUuid.MAP,
            BluetoothUuid.MNS,
            BluetoothUuid.MAS,
    };

    static final String NAME = "MAP Client";

    // Order of this profile in device profiles list
    private static final int ORDINAL = 0;

    // These callbacks run on the main thread.
    private final class MapClientServiceListener
            implements BluetoothProfile.ServiceListener {

        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            LOGGER.d("Bluetooth service connected");
            mService = (BluetoothMapClient) proxy;
            // We just bound to the service, so refresh the UI for any connected MAP devices.
            List<BluetoothDevice> deviceList = mService.getConnectedDevices();
            while (!deviceList.isEmpty()) {
                BluetoothDevice nextDevice = deviceList.remove(0);
                CachedBluetoothDevice device = mDeviceManager.findDevice(nextDevice);
                // we may add a new device here, but generally this should not happen
                if (device == null) {
                    LOGGER.w("MapProfile found new device: " + nextDevice);
                    device = mDeviceManager.addDevice(mLocalAdapter, mProfileManager, nextDevice);
                }
                device.onProfileStateChanged(MapClientProfile.this,
                        BluetoothProfile.STATE_CONNECTED);
                device.refresh();
            }

            mProfileManager.callServiceConnectedListeners();
            mIsProfileReady = true;
        }

        public void onServiceDisconnected(int profile) {
            LOGGER.d("Bluetooth service disconnected");
            mProfileManager.callServiceDisconnectedListeners();
            mIsProfileReady = false;
        }
    }

    public boolean isProfileReady() {
        LOGGER.d("isProfileReady(): " + mIsProfileReady);
        return mIsProfileReady;
    }

    @Override
    public int getProfileId() {
        return BluetoothProfile.MAP_CLIENT;
    }

    MapClientProfile(Context context, LocalBluetoothAdapter adapter,
                     CachedBluetoothDeviceManager deviceManager,
                     LocalBluetoothProfileManager profileManager) {
        mLocalAdapter = adapter;
        mDeviceManager = deviceManager;
        mProfileManager = profileManager;
        mLocalAdapter.getProfileProxy(context, new MapClientServiceListener(),
                BluetoothProfile.MAP_CLIENT);
    }

    public boolean isConnectable() {
        return true;
    }

    public boolean isAutoConnectable() {
        return true;
    }

    public int getConnectionStatus(BluetoothDevice device) {
        if (mService == null) return BluetoothProfile.STATE_DISCONNECTED;

        return mService.getConnectionState(device);
    }

    public boolean isPreferred(BluetoothDevice device) {
        if (mService == null) return false;
        return mService.getPriority(device) > BluetoothProfile.PRIORITY_OFF;
    }

    public int getPreferred(BluetoothDevice device) {
        if (mService == null) return BluetoothProfile.PRIORITY_OFF;
        return mService.getPriority(device);
    }

    public void setPreferred(BluetoothDevice device, boolean preferred) {
        if (mService == null) return;
        if (preferred) {
            if (mService.getPriority(device) < BluetoothProfile.PRIORITY_ON) {
                mService.setPriority(device, BluetoothProfile.PRIORITY_ON);
            }
        } else {
            mService.setPriority(device, BluetoothProfile.PRIORITY_OFF);
        }
    }

    public List<BluetoothDevice> getConnectedDevices() {
        if (mService == null) return new ArrayList<BluetoothDevice>(0);
        return mService.getDevicesMatchingConnectionStates(
                new int[]{BluetoothProfile.STATE_CONNECTED,
                        BluetoothProfile.STATE_CONNECTING,
                        BluetoothProfile.STATE_DISCONNECTING});
    }

    public String toString() {
        return NAME;
    }

    public int getOrdinal() {
        return ORDINAL;
    }

    protected void finalize() {
        LOGGER.d("finalize()");
        if (mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.MAP_CLIENT,
                        mService);
                mService = null;
            } catch (Throwable t) {
                LOGGER.e(t, "Error cleaning up MAP Client proxy");
            }
        }
    }
}
