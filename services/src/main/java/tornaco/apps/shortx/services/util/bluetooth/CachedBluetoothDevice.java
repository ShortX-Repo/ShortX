/*
 * Copyright (C) 2008 The Android Open Source Project
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

import static tornaco.apps.shortx.services.util.bluetooth.Utils.tryGetAliasName;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHearingAid;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import tornaco.apps.shortx.core.util.Logger;

/**
 * CachedBluetoothDevice represents a remote Bluetooth device. It contains
 * attributes of the device (such as the address, name, RSSI, etc.) and
 * functionality that can be performed on the device (connect, pair, disconnect,
 * etc.).
 */
@SuppressLint("MissingPermission")
public class CachedBluetoothDevice implements Comparable<CachedBluetoothDevice> {
    private static final Logger LOGGER = new Logger("CachedBluetoothDevice");

    private final Context mContext;
    private final LocalBluetoothAdapter mLocalAdapter;
    private final LocalBluetoothProfileManager mProfileManager;
    private final AudioManager mAudioManager;
    private final BluetoothDevice mDevice;
    //TODO: consider remove, BluetoothDevice.getName() is already cached
    private String mName;
    private long mHiSyncId;
    // Need this since there is no method for getting RSSI
    private short mRssi;
    //TODO: consider remove, BluetoothDevice.getBluetoothClass() is already cached
    private BluetoothClass mBtClass;
    private HashMap<LocalBluetoothProfile, Integer> mProfileConnectionState;

    private final List<LocalBluetoothProfile> mProfiles =
            new ArrayList<LocalBluetoothProfile>();

    // List of profiles that were previously in mProfiles, but have been removed
    private final List<LocalBluetoothProfile> mRemovedProfiles =
            new ArrayList<LocalBluetoothProfile>();

    // Device supports PANU but not NAP: remove PanProfile after device disconnects from NAP
    private boolean mLocalNapRoleConnected;

    private boolean mJustDiscovered;

    private int mMessageRejectionCount;

    private final Collection<Callback> mCallbacks = new ArrayList<Callback>();

    // Following constants indicate the user's choices of Phone book/message access settings
    // User hasn't made any choice or settings app has wiped out the memory
    public final static int ACCESS_UNKNOWN = 0;
    // User has accepted the connection and let Settings app remember the decision
    public final static int ACCESS_ALLOWED = 1;
    // User has rejected the connection and let Settings app remember the decision
    public final static int ACCESS_REJECTED = 2;

    // How many times user should reject the connection to make the choice persist.
    private final static int MESSAGE_REJECTION_COUNT_LIMIT_TO_PERSIST = 2;

    private final static String MESSAGE_REJECTION_COUNT_PREFS_NAME = "bluetooth_message_reject";

    /**
     * When we connect to multiple profiles, we only want to display a single
     * error even if they all fail. This tracks that state.
     */
    private boolean mIsConnectingErrorPossible;

    public long getHiSyncId() {
        return mHiSyncId;
    }

    public void setHiSyncId(long id) {
        LOGGER.d("setHiSyncId: mDevice " + mDevice + ", id " + id);
        mHiSyncId = id;
    }

    /**
     * Last time a bt profile auto-connect was attempted.
     * If an ACTION_UUID intent comes in within
     * MAX_UUID_DELAY_FOR_AUTO_CONNECT milliseconds, we will try auto-connect
     * again with the new UUIDs
     */
    private long mConnectAttempted;

    // See mConnectAttempted
    private static final long MAX_UUID_DELAY_FOR_AUTO_CONNECT = 5000;
    private static final long MAX_HOGP_DELAY_FOR_AUTO_CONNECT = 30000;

    // Active device state
    private boolean mIsActiveDeviceA2dp = false;
    private boolean mIsActiveDeviceHeadset = false;
    private boolean mIsActiveDeviceHearingAid = false;

    /**
     * Describes the current device and profile for logging.
     *
     * @param profile Profile to describe
     * @return Description of the device and profile
     */
    private String describe(LocalBluetoothProfile profile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Address:").append(mDevice);
        if (profile != null) {
            sb.append(" Profile:").append(profile);
        }

        return sb.toString();
    }

    void onProfileStateChanged(LocalBluetoothProfile profile, int newProfileState) {
        LOGGER.d("onProfileStateChanged: profile " + profile + ", device=" + mDevice
                + ", newProfileState " + newProfileState);
        if (mLocalAdapter.getBluetoothState() == BluetoothAdapter.STATE_TURNING_OFF) {
            LOGGER.d(" BT Turninig Off...Profile conn state change ignored...");
            return;
        }
        mProfileConnectionState.put(profile, newProfileState);
        if (newProfileState == BluetoothProfile.STATE_CONNECTED) {
            if (!mProfiles.contains(profile)) {
                mRemovedProfiles.remove(profile);
                mProfiles.add(profile);
                if (profile instanceof PanProfile &&
                        ((PanProfile) profile).isLocalRoleNap(mDevice)) {
                    // Device doesn't support NAP, so remove PanProfile on disconnect
                    mLocalNapRoleConnected = true;
                }
            }
        } else if (profile instanceof MapProfile &&
                newProfileState == BluetoothProfile.STATE_DISCONNECTED) {
        } else if (mLocalNapRoleConnected && profile instanceof PanProfile &&
                ((PanProfile) profile).isLocalRoleNap(mDevice) &&
                newProfileState == BluetoothProfile.STATE_DISCONNECTED) {
            LOGGER.d("Removing PanProfile from device after NAP disconnect");
            mProfiles.remove(profile);
            mRemovedProfiles.add(profile);
            mLocalNapRoleConnected = false;
        }
        fetchActiveDevices();
    }

    CachedBluetoothDevice(Context context,
                          LocalBluetoothAdapter adapter,
                          LocalBluetoothProfileManager profileManager,
                          BluetoothDevice device) {
        mContext = context;
        mLocalAdapter = adapter;
        mProfileManager = profileManager;
        mAudioManager = context.getSystemService(AudioManager.class);
        mDevice = device;
        mProfileConnectionState = new HashMap<LocalBluetoothProfile, Integer>();
        fillData();
        mHiSyncId = BluetoothHearingAid.HI_SYNC_ID_INVALID;
    }

    public int getProfileConnectionState(LocalBluetoothProfile profile) {
        if (mProfileConnectionState.get(profile) == null) {
            // If cache is empty make the binder call to get the state
            int state = profile.getConnectionStatus(mDevice);
            mProfileConnectionState.put(profile, state);
        }
        return mProfileConnectionState.get(profile);
    }

    public void clearProfileConnectionState() {
        LOGGER.d(" Clearing all connection state for dev:" + mDevice.getName());
        for (LocalBluetoothProfile profile : getProfiles()) {
            mProfileConnectionState.put(profile, BluetoothProfile.STATE_DISCONNECTED);
        }
    }

    // TODO: do any of these need to run async on a background thread?
    private void fillData() {
        fetchName();
        fetchBtClass();
        fetchActiveDevices();
        dispatchAttributesChanged();
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    /**
     * Convenience method that can be mocked - it lets tests avoid having to call getDevice() which
     * causes problems in tests since BluetoothDevice is final and cannot be mocked.
     *
     * @return the address of this device
     */
    public String getAddress() {
        return mDevice.getAddress();
    }

    public String getName() {
        return mName;
    }

    void refreshName() {
        fetchName();
        dispatchAttributesChanged();
    }

    private void fetchName() {
        mName = tryGetAliasName(mDevice);

        if (TextUtils.isEmpty(mName)) {
            mName = mDevice.getAddress();
            LOGGER.d("Device has no name (yet), use address: " + mName);
        }
    }

    /**
     * Get battery level from remote device
     *
     * @return battery level in percentage [0-100], or {@link BluetoothDevice#BATTERY_LEVEL_UNKNOWN}
     */
    public int getBatteryLevel() {
        return mDevice.getBatteryLevel();
    }

    void refresh() {
        dispatchAttributesChanged();
    }

    public void setJustDiscovered(boolean justDiscovered) {
        if (mJustDiscovered != justDiscovered) {
            mJustDiscovered = justDiscovered;
            dispatchAttributesChanged();
        }
    }

    public int getBondState() {
        return mDevice.getBondState();
    }

    /**
     * Update the device status as active or non-active per Bluetooth profile.
     *
     * @param isActive         true if the device is active
     * @param bluetoothProfile the Bluetooth profile
     */
    public void onActiveDeviceChanged(boolean isActive, int bluetoothProfile) {
        boolean changed = false;
        switch (bluetoothProfile) {
            case BluetoothProfile.A2DP:
                changed = (mIsActiveDeviceA2dp != isActive);
                mIsActiveDeviceA2dp = isActive;
                break;
            case BluetoothProfile.HEADSET:
                changed = (mIsActiveDeviceHeadset != isActive);
                mIsActiveDeviceHeadset = isActive;
                break;
            case BluetoothProfile.HEARING_AID:
                changed = (mIsActiveDeviceHearingAid != isActive);
                mIsActiveDeviceHearingAid = isActive;
                break;
            default:
                LOGGER.w("onActiveDeviceChanged: unknown profile " + bluetoothProfile +
                        " isActive " + isActive);
                break;
        }
        if (changed) {
            dispatchAttributesChanged();
        }
    }

    /**
     * Update the profile audio state.
     */
    void onAudioModeChanged() {
        dispatchAttributesChanged();
    }

    /**
     * Get the device status as active or non-active per Bluetooth profile.
     *
     * @param bluetoothProfile the Bluetooth profile
     * @return true if the device is active
     */
    public boolean isActiveDevice(int bluetoothProfile) {
        switch (bluetoothProfile) {
            case BluetoothProfile.A2DP:
                return mIsActiveDeviceA2dp;
            case BluetoothProfile.HEADSET:
                return mIsActiveDeviceHeadset;
            case BluetoothProfile.HEARING_AID:
                return mIsActiveDeviceHearingAid;
            default:
                LOGGER.w("getActiveDevice: unknown profile " + bluetoothProfile);
                break;
        }
        return false;
    }

    void setRssi(short rssi) {
        if (mRssi != rssi) {
            mRssi = rssi;
            dispatchAttributesChanged();
        }
    }

    /**
     * Checks whether we are connected to this device (any profile counts).
     *
     * @return Whether it is connected.
     */
    public boolean isConnected() {
        for (LocalBluetoothProfile profile : mProfiles) {
            int status = getProfileConnectionState(profile);
            if (status == BluetoothProfile.STATE_CONNECTED) {
                return true;
            }
        }

        return false;
    }

    public boolean isConnectedProfile(LocalBluetoothProfile profile) {
        int status = getProfileConnectionState(profile);
        return status == BluetoothProfile.STATE_CONNECTED;

    }

    public boolean isBusy() {
        for (LocalBluetoothProfile profile : mProfiles) {
            int status = getProfileConnectionState(profile);
            if (status == BluetoothProfile.STATE_CONNECTING
                    || status == BluetoothProfile.STATE_DISCONNECTING) {
                return true;
            }
        }
        return getBondState() == BluetoothDevice.BOND_BONDING;
    }

    /**
     * Fetches a new value for the cached BT class.
     */
    private void fetchBtClass() {
        mBtClass = mDevice.getBluetoothClass();
    }

    private void fetchActiveDevices() {
        A2dpProfile a2dpProfile = mProfileManager.getA2dpProfile();
        if (a2dpProfile != null) {
            mIsActiveDeviceA2dp = mDevice.equals(a2dpProfile.getActiveDevice());
        }
        HeadsetProfile headsetProfile = mProfileManager.getHeadsetProfile();
        if (headsetProfile != null) {
            mIsActiveDeviceHeadset = mDevice.equals(headsetProfile.getActiveDevice());
        }
        HearingAidProfile hearingAidProfile = mProfileManager.getHearingAidProfile();
        if (hearingAidProfile != null) {
            mIsActiveDeviceHearingAid = hearingAidProfile.getActiveDevices().contains(mDevice);
        }
    }

    /**
     * Refreshes the UI for the BT class, including fetching the latest value
     * for the class.
     */
    void refreshBtClass() {
        fetchBtClass();
        dispatchAttributesChanged();
    }

    /**
     * Refreshes the UI when framework alerts us of a UUID change.
     */
    void onUuidChanged() {
        dispatchAttributesChanged();
    }

    void onBondingStateChanged(int bondState) {
        if (bondState == BluetoothDevice.BOND_NONE) {
            mProfiles.clear();
            mMessageRejectionCount = 0;
        }

        refresh();
    }

    void setBtClass(BluetoothClass btClass) {
        if (btClass != null && mBtClass != btClass) {
            mBtClass = btClass;
            dispatchAttributesChanged();
        }
    }

    public BluetoothClass getBtClass() {
        return mBtClass;
    }

    public List<LocalBluetoothProfile> getProfiles() {
        return Collections.unmodifiableList(mProfiles);
    }

    public List<LocalBluetoothProfile> getConnectableProfiles() {
        List<LocalBluetoothProfile> connectableProfiles =
                new ArrayList<LocalBluetoothProfile>();
        for (LocalBluetoothProfile profile : mProfiles) {
            if (profile.isConnectable()) {
                connectableProfiles.add(profile);
            }
        }
        return connectableProfiles;
    }

    public List<LocalBluetoothProfile> getRemovedProfiles() {
        return mRemovedProfiles;
    }

    public void registerCallback(Callback callback) {
        synchronized (mCallbacks) {
            mCallbacks.add(callback);
        }
    }

    public void unregisterCallback(Callback callback) {
        synchronized (mCallbacks) {
            mCallbacks.remove(callback);
        }
    }

    private void dispatchAttributesChanged() {
        synchronized (mCallbacks) {
            for (Callback callback : mCallbacks) {
                callback.onDeviceAttributesChanged();
            }
        }
    }

    @Override
    public String toString() {
        return mDevice.toString();
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof CachedBluetoothDevice)) {
            return false;
        }
        return mDevice.equals(((CachedBluetoothDevice) o).mDevice);
    }

    @Override
    public int hashCode() {
        return mDevice.getAddress().hashCode();
    }

    // This comparison uses non-final fields so the sort order may change
    // when device attributes change (such as bonding state). Settings
    // will completely refresh the device list when this happens.
    public int compareTo(CachedBluetoothDevice another) {
        // Connected above not connected
        int comparison = (another.isConnected() ? 1 : 0) - (isConnected() ? 1 : 0);
        if (comparison != 0) return comparison;

        // Paired above not paired
        comparison = (another.getBondState() == BluetoothDevice.BOND_BONDED ? 1 : 0) -
                (getBondState() == BluetoothDevice.BOND_BONDED ? 1 : 0);
        if (comparison != 0) return comparison;

        // Just discovered above discovered in the past
        comparison = (another.mJustDiscovered ? 1 : 0) - (mJustDiscovered ? 1 : 0);
        if (comparison != 0) return comparison;

        // Stronger signal above weaker signal
        comparison = another.mRssi - mRssi;
        if (comparison != 0) return comparison;

        // Fallback on name
        return mName.compareTo(another.mName);
    }

    public interface Callback {
        void onDeviceAttributesChanged();
    }

    public int getMaxConnectionState() {
        int maxState = BluetoothProfile.STATE_DISCONNECTED;
        for (LocalBluetoothProfile profile : getProfiles()) {
            int connectionStatus = getProfileConnectionState(profile);
            if (connectionStatus > maxState) {
                maxState = connectionStatus;
            }
        }
        return maxState;
    }

    /**
     * @return {@code true} if {@code cachedBluetoothDevice} is a2dp device
     */
    public boolean isA2dpDevice() {
        A2dpProfile a2dpProfile = mProfileManager.getA2dpProfile();
        return a2dpProfile != null && a2dpProfile.getConnectionStatus(mDevice) ==
                BluetoothProfile.STATE_CONNECTED;
    }

    /**
     * @return {@code true} if {@code cachedBluetoothDevice} is HFP device
     */
    public boolean isHfpDevice() {
        HeadsetProfile headsetProfile = mProfileManager.getHeadsetProfile();
        return headsetProfile != null && headsetProfile.getConnectionStatus(mDevice) ==
                BluetoothProfile.STATE_CONNECTED;
    }

    /**
     * @return {@code true} if {@code cachedBluetoothDevice} is Hearing Aid device
     */
    public boolean isConnectedHearingAidDevice() {
        HearingAidProfile hearingAidProfile = mProfileManager.getHearingAidProfile();
        return hearingAidProfile != null && hearingAidProfile.getConnectionStatus(mDevice) ==
                BluetoothProfile.STATE_CONNECTED;
    }
}
