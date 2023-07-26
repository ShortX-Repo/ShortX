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

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothHearingAid;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidHost;
import android.bluetooth.BluetoothMap;
import android.bluetooth.BluetoothMapClient;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothPbapClient;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tornaco.apps.shortx.core.util.Logger;
import tornaco.apps.shortx.core.util.OsUtils;


/**
 * LocalBluetoothProfileManager provides access to the LocalBluetoothProfile
 * objects for the available Bluetooth profiles.
 */
@SuppressLint("MissingPermission")
public class LocalBluetoothProfileManager {
    private static final Logger LOGGER = new Logger("LocalBluetoothProfileManager");
    /**
     * Singleton instance.
     */
    private static LocalBluetoothProfileManager sInstance;

    /**
     * An interface for notifying BluetoothHeadset IPC clients when they have
     * been connected to the BluetoothHeadset service.
     * Only used by com.android.settings.bluetooth.DockService.
     */
    public interface ServiceListener {
        /**
         * Called to notify the client when this proxy object has been
         * connected to the BluetoothHeadset service. Clients must wait for
         * this callback before making IPC calls on the BluetoothHeadset
         * service.
         */
        void onServiceConnected();

        /**
         * Called to notify the client that this proxy object has been
         * disconnected from the BluetoothHeadset service. Clients must not
         * make IPC calls on the BluetoothHeadset service after this callback.
         * This callback will currently only occur if the application hosting
         * the BluetoothHeadset service, but may be called more often in future.
         */
        void onServiceDisconnected();
    }

    private final Context mContext;
    private final LocalBluetoothAdapter mLocalAdapter;
    private final CachedBluetoothDeviceManager mDeviceManager;
    private final BluetoothEventManager mEventManager;

    private A2dpProfile mA2dpProfile;
    private A2dpSinkProfile mA2dpSinkProfile;
    private HeadsetProfile mHeadsetProfile;
    private HfpClientProfile mHfpClientProfile;
    private MapProfile mMapProfile;
    private MapClientProfile mMapClientProfile;
    private final HidProfile mHidProfile;
    private HidDeviceProfile mHidDeviceProfile;
    private OppProfile mOppProfile;
    private final PanProfile mPanProfile;
    private PbapClientProfile mPbapClientProfile;
    private final boolean mUsePbapPce = true;
    private final boolean mUseMapClient = true;
    private HearingAidProfile mHearingAidProfile;

    /**
     * Mapping from profile name, e.g. "HEADSET" to profile object.
     */
    private final Map<String, LocalBluetoothProfile>
            mProfileNameMap = new HashMap<String, LocalBluetoothProfile>();

    LocalBluetoothProfileManager(Context context,
                                 LocalBluetoothAdapter adapter,
                                 CachedBluetoothDeviceManager deviceManager,
                                 BluetoothEventManager eventManager) {
        mContext = context;

        mLocalAdapter = adapter;
        mDeviceManager = deviceManager;
        mEventManager = eventManager;
        // mUsePbapPce = mContext.getResources().getBoolean(R.bool.enable_pbap_pce_profile);
        // MAP Client is typically used in the same situations as PBAP Client
        // mUseMapClient = mContext.getResources().getBoolean(R.bool.enable_pbap_pce_profile);
        // pass this reference to adapter and event manager (circular dependency)
        mLocalAdapter.setProfileManager(this);
        mEventManager.setProfileManager(this);

        updateLocalProfiles();

        // Always add HID host, HID device, and PAN profiles
        mHidProfile = new HidProfile(context, mLocalAdapter, mDeviceManager, this);
        addProfile(mHidProfile, HidProfile.NAME,
                BluetoothHidHost.ACTION_CONNECTION_STATE_CHANGED);

        mPanProfile = new PanProfile(context, mLocalAdapter);
        addPanProfile(mPanProfile, PanProfile.NAME,
                BluetoothPan.ACTION_CONNECTION_STATE_CHANGED);

        if (OsUtils.isPOrAbove()) {
            mHidDeviceProfile = new HidDeviceProfile(context, mLocalAdapter, mDeviceManager, this);
            addProfile(mHidDeviceProfile, HidDeviceProfile.NAME, BluetoothHidDevice.ACTION_CONNECTION_STATE_CHANGED);
        }

        LOGGER.d("Adding local MAP profile");
        if (mUseMapClient) {
            mMapClientProfile = new MapClientProfile(mContext, mLocalAdapter, mDeviceManager, this);
            addProfile(mMapClientProfile, MapClientProfile.NAME,
                    BluetoothMapClient.ACTION_CONNECTION_STATE_CHANGED);
        } else {
            mMapProfile = new MapProfile(mContext, mLocalAdapter, mDeviceManager, this);
            addProfile(mMapProfile, MapProfile.NAME,
                    BluetoothMap.ACTION_CONNECTION_STATE_CHANGED);
        }

        LOGGER.d("LocalBluetoothProfileManager construction complete");
    }

    void updateLocalProfiles() {
        List<Integer> supportedList = BluetoothAdapter.getDefaultAdapter().getSupportedProfiles();
        if (supportedList == null || supportedList.isEmpty()) {
            LOGGER.d("supportedList is null");
            return;
        }
        if (mA2dpProfile == null && supportedList.contains(BluetoothProfile.A2DP)) {
            LOGGER.d("Adding local A2DP profile");
            mA2dpProfile = new A2dpProfile(mContext, mLocalAdapter, mDeviceManager, this);
            addProfile(mA2dpProfile, A2dpProfile.NAME,
                    BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        }
        if (mA2dpSinkProfile == null && supportedList.contains(BluetoothProfile.A2DP_SINK)) {
            LOGGER.d("Adding local A2DP SINK profile");
            mA2dpSinkProfile = new A2dpSinkProfile(mContext, mLocalAdapter, mDeviceManager, this);
            addProfile(mA2dpSinkProfile, A2dpSinkProfile.NAME,
                    BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED);
        }
        if (mHeadsetProfile == null && supportedList.contains(BluetoothProfile.HEADSET)) {
            LOGGER.d("Adding local HEADSET profile");
            mHeadsetProfile = new HeadsetProfile(mContext, mLocalAdapter, mDeviceManager, this);
            addHeadsetProfile(mHeadsetProfile, HeadsetProfile.NAME,
                    BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED,
                    BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED,
                    BluetoothHeadset.STATE_AUDIO_DISCONNECTED);
        }
        if (mHfpClientProfile == null && supportedList.contains(BluetoothProfile.HEADSET_CLIENT)) {
            LOGGER.d("Adding local HfpClient profile");
            mHfpClientProfile = new HfpClientProfile(mContext, mLocalAdapter, mDeviceManager, this);
            addProfile(mHfpClientProfile, HfpClientProfile.NAME,
                    BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED);
        }
        if (mMapClientProfile == null && supportedList.contains(BluetoothProfile.MAP_CLIENT)) {
            LOGGER.d("Adding local MAP CLIENT profile");
            mMapClientProfile = new MapClientProfile(mContext, mLocalAdapter, mDeviceManager, this);
            addProfile(mMapClientProfile, MapClientProfile.NAME,
                    BluetoothMapClient.ACTION_CONNECTION_STATE_CHANGED);
        }
        if (mMapProfile == null && supportedList.contains(BluetoothProfile.MAP)) {
            LOGGER.d("Adding local MAP profile");
            mMapProfile = new MapProfile(mContext, mLocalAdapter, mDeviceManager, this);
            addProfile(mMapProfile, MapProfile.NAME, BluetoothMap.ACTION_CONNECTION_STATE_CHANGED);
        }
        if (mOppProfile == null && supportedList.contains(BluetoothProfile.OPP)) {
            LOGGER.d("Adding local OPP profile");
            mOppProfile = new OppProfile();
            // Note: no event handler for OPP, only name map.
            mProfileNameMap.put(OppProfile.NAME, mOppProfile);
        }
        if (mHearingAidProfile == null && supportedList.contains(BluetoothProfile.HEARING_AID)) {
            if (OsUtils.isQOrAbove()) {
                LOGGER.d("Adding local Hearing Aid profile");
                mHearingAidProfile = new HearingAidProfile(mContext, mLocalAdapter, mDeviceManager,
                        this);
                addProfile(mHearingAidProfile, HearingAidProfile.NAME,
                        BluetoothHearingAid.ACTION_CONNECTION_STATE_CHANGED);
            }
        }

        if (mHidDeviceProfile == null && supportedList.contains(BluetoothProfile.HID_DEVICE)) {
            if (OsUtils.isPOrAbove()) {
                LOGGER.d("Adding local HID_DEVICE profile");
                mHidDeviceProfile = new HidDeviceProfile(mContext, mLocalAdapter, mDeviceManager, this);
                addProfile(mHidDeviceProfile, HidDeviceProfile.NAME,
                        BluetoothHidDevice.ACTION_CONNECTION_STATE_CHANGED);
            }
        }


        if (mPbapClientProfile == null && supportedList.contains(BluetoothProfile.PBAP_CLIENT)) {
            LOGGER.d("Adding local PBAP Client profile");
            mPbapClientProfile = new PbapClientProfile(mContext, mLocalAdapter, mDeviceManager, this);
            addProfile(mPbapClientProfile, PbapClientProfile.NAME,
                    BluetoothPbapClient.ACTION_CONNECTION_STATE_CHANGED);
        }
        mEventManager.registerProfileIntentReceiver();
    }

    private void addHeadsetProfile(LocalBluetoothProfile profile, String profileName,
                                   String stateChangedAction, String audioStateChangedAction, int audioDisconnectedState) {
        BluetoothEventManager.Handler handler = new HeadsetStateChangeHandler(
                profile, audioStateChangedAction, audioDisconnectedState);
        mEventManager.addProfileHandler(stateChangedAction, handler);
        mEventManager.addProfileHandler(audioStateChangedAction, handler);
        mProfileNameMap.put(profileName, profile);
    }

    private final Collection<ServiceListener> mServiceListeners =
            new ArrayList<ServiceListener>();

    private void addProfile(LocalBluetoothProfile profile,
                            String profileName, String stateChangedAction) {
        mEventManager.addProfileHandler(stateChangedAction, new StateChangedHandler(profile));
        mProfileNameMap.put(profileName, profile);
    }

    private void addPanProfile(LocalBluetoothProfile profile,
                               String profileName, String stateChangedAction) {
        mEventManager.addProfileHandler(stateChangedAction,
                new PanStateChangedHandler(profile));
        mProfileNameMap.put(profileName, profile);
    }

    public LocalBluetoothProfile getProfileByName(String name) {
        return mProfileNameMap.get(name);
    }

    // Called from LocalBluetoothAdapter when state changes to ON
    void setBluetoothStateOn() {
        updateLocalProfiles();
        mEventManager.readPairedDevices();
    }

    /**
     * Generic handler for connection state change events for the specified profile.
     */
    private class StateChangedHandler implements BluetoothEventManager.Handler {
        final LocalBluetoothProfile mProfile;

        StateChangedHandler(LocalBluetoothProfile profile) {
            mProfile = profile;
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice device) {
            CachedBluetoothDevice cachedDevice = mDeviceManager.findDevice(device);
            if (cachedDevice == null) {
                LOGGER.w("StateChangedHandler found new device: " + device);
                cachedDevice = mDeviceManager.addDevice(mLocalAdapter,
                        LocalBluetoothProfileManager.this, device);
            }
            onReceiveInternal(intent, cachedDevice);
        }

        protected void onReceiveInternal(Intent intent, CachedBluetoothDevice cachedDevice) {
            int newState = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
            int oldState = intent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, 0);
            if (newState == BluetoothProfile.STATE_DISCONNECTED &&
                    oldState == BluetoothProfile.STATE_CONNECTING) {
                LOGGER.i("Failed to connect " + mProfile + " device");
            }

            boolean isHearingAidProfile = (getHearingAidProfile() != null) &&
                    (mProfile instanceof HearingAidProfile);

            if (isHearingAidProfile && (newState == BluetoothProfile.STATE_CONNECTED)) {
                // Check if the HiSyncID has being initialized
                if (cachedDevice.getHiSyncId() == BluetoothHearingAid.HI_SYNC_ID_INVALID) {

                    long newHiSyncId = getHearingAidProfile().getHiSyncId(cachedDevice.getDevice());

                    if (newHiSyncId != BluetoothHearingAid.HI_SYNC_ID_INVALID) {
                        cachedDevice.setHiSyncId(newHiSyncId);
                        mDeviceManager.onHiSyncIdChanged(newHiSyncId);
                    }
                }
            }

            cachedDevice.onProfileStateChanged(mProfile, newState);
            cachedDevice.refresh();

            if (isHearingAidProfile) {
                CachedBluetoothDevice otherDevice =
                        mDeviceManager.getHearingAidOtherDevice(cachedDevice, cachedDevice.getHiSyncId());
                if (otherDevice != null) {
                    LOGGER.d("Refreshing other hearing aid=" + otherDevice
                            + ", newState=" + newState);
                    otherDevice.refresh();
                }
            }
            mEventManager.dispatchProfileConnectionStateChanged(cachedDevice, newState,
                    mProfile.getProfileId());
        }
    }

    /**
     * Connectivity and audio state change handler for headset profiles.
     */
    private class HeadsetStateChangeHandler extends StateChangedHandler {
        private final String mAudioChangeAction;
        private final int mAudioDisconnectedState;

        HeadsetStateChangeHandler(LocalBluetoothProfile profile, String audioChangeAction,
                                  int audioDisconnectedState) {
            super(profile);
            mAudioChangeAction = audioChangeAction;
            mAudioDisconnectedState = audioDisconnectedState;
        }

        @Override
        public void onReceiveInternal(Intent intent, CachedBluetoothDevice cachedDevice) {
            if (mAudioChangeAction.equals(intent.getAction())) {
                int newState = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                if (newState != mAudioDisconnectedState) {
                    cachedDevice.onProfileStateChanged(mProfile, BluetoothProfile.STATE_CONNECTED);
                }
                cachedDevice.refresh();
            } else {
                super.onReceiveInternal(intent, cachedDevice);
            }
        }
    }

    /**
     * State change handler for NAP and PANU profiles.
     */
    private class PanStateChangedHandler extends StateChangedHandler {

        PanStateChangedHandler(LocalBluetoothProfile profile) {
            super(profile);
        }

        @Override
        public void onReceive(Context context, Intent intent, BluetoothDevice device) {
            PanProfile panProfile = (PanProfile) mProfile;
            int role = intent.getIntExtra(BluetoothPan.EXTRA_LOCAL_ROLE, 0);
            panProfile.setLocalRole(device, role);
            super.onReceive(context, intent, device);
        }
    }

    // called from DockService
    public void addServiceListener(ServiceListener l) {
        mServiceListeners.add(l);
    }

    // called from DockService
    public void removeServiceListener(ServiceListener l) {
        mServiceListeners.remove(l);
    }

    // not synchronized: use only from UI thread! (TODO: verify)
    void callServiceConnectedListeners() {
        for (ServiceListener l : mServiceListeners) {
            l.onServiceConnected();
        }
    }

    // not synchronized: use only from UI thread! (TODO: verify)
    void callServiceDisconnectedListeners() {
        for (ServiceListener listener : mServiceListeners) {
            listener.onServiceDisconnected();
        }
    }

    // This is called by DockService, so check Headset and A2DP.
    public synchronized boolean isManagerReady() {
        // Getting just the headset profile is fine for now. Will need to deal with A2DP
        // and others if they aren't always in a ready state.
        LocalBluetoothProfile profile = mHeadsetProfile;
        if (profile != null) {
            return profile.isProfileReady();
        }
        profile = mA2dpProfile;
        if (profile != null) {
            return profile.isProfileReady();
        }
        profile = mA2dpSinkProfile;
        if (profile != null) {
            return profile.isProfileReady();
        }
        return false;
    }

    public A2dpProfile getA2dpProfile() {
        return mA2dpProfile;
    }

    public A2dpSinkProfile getA2dpSinkProfile() {
        if ((mA2dpSinkProfile != null) && (mA2dpSinkProfile.isProfileReady())) {
            return mA2dpSinkProfile;
        } else {
            return null;
        }
    }

    public HeadsetProfile getHeadsetProfile() {
        return mHeadsetProfile;
    }

    public HfpClientProfile getHfpClientProfile() {
        if ((mHfpClientProfile != null) && (mHfpClientProfile.isProfileReady())) {
            return mHfpClientProfile;
        } else {
            return null;
        }
    }

    public PbapClientProfile getPbapClientProfile() {
        return mPbapClientProfile;
    }

    public MapProfile getMapProfile() {
        return mMapProfile;
    }

    public MapClientProfile getMapClientProfile() {
        return mMapClientProfile;
    }

    public HearingAidProfile getHearingAidProfile() {
        return mHearingAidProfile;
    }

    HidProfile getHidProfile() {
        return mHidProfile;
    }

    HidDeviceProfile getHidDeviceProfile() {
        return mHidDeviceProfile;
    }

}
