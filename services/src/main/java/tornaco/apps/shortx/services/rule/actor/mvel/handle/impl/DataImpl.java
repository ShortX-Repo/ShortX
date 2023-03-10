package tornaco.apps.shortx.services.rule.actor.mvel.handle.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import tornaco.apps.shortx.core.util.Logger;
import tornaco.apps.shortx.services.ShortXContext;
import tornaco.apps.shortx.services.rule.actor.mvel.handle.IData;

@SuppressLint({"NewApi", "MissingPermission"})
public
class DataImpl implements IData {
    private final Logger logger = new Logger("DataImpl");

    private final Context context;
    private final ShortXContext s;

    public DataImpl(Context context, ShortXContext s) {
        this.context = context;
        this.s = s;
    }

    // Is it hidden before O?
    @Override
    public void setDataEnabled(boolean enable) {
        logger.w("setDataEnabled " + enable);
        TelephonyManager telephonyManager = TelephonyManager.from(context);
        telephonyManager.setDataEnabled(enable);
    }

    @Override
    public void setDataEnabled(int subId, boolean enable) {
        logger.w("setDataEnabled " + subId + ", " + enable);
        TelephonyManager telephonyManager = TelephonyManager.from(context);
        telephonyManager.setDataEnabled(subId, enable);
    }

    @Override
    public void setDataEnabledForSlot(int slotId, boolean enable) {
        logger.w("setDataEnabledForSlot " + slotId + ", " + enable);
        SubscriptionInfo si = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(slotId);
        if (si == null) {
            return;
        }
        int subId = si.getSubscriptionId();
        setDataEnabled(subId, enable);
    }

    // Is it hidden before O?
    @SuppressLint("NewApi")
    @Override
    public boolean isDataEnabled() {
        TelephonyManager telephonyManager = TelephonyManager.from(context);
        return telephonyManager.isDataEnabled();
    }

    @Override
    public boolean isDataEnabledForSlot(int slotId) {
        SubscriptionInfo si = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(slotId);
        if (si == null) {
            return false;
        }
        TelephonyManager telephonyManager = TelephonyManager.from(context);
        int subId = si.getSubscriptionId();
        return telephonyManager.getDataEnabled(subId);
    }

    @Override
    public void setDataNetworkType(int type) {
        logger.w("setDataNetworkType " + type);
        TelephonyManager telephonyManager = TelephonyManager.from(context);
        telephonyManager.setDataNetworkType(type);
    }

    @Override
    public void setDataNetworkTypeForPhone(int phoneId, int type) {
        logger.w("setDataNetworkTypeForPhone " + phoneId + ", " + type);
        TelephonyManager telephonyManager = TelephonyManager.from(context);
        telephonyManager.setDataNetworkTypeForPhone(phoneId, type);
    }

    @Override
    public void setDataNetworkTypeForSlot(int slotId, int type) {
        logger.w("setDataNetworkTypeForSlot " + slotId + ", " + type);
        SubscriptionInfo si = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(slotId);
        if (si == null) {
            return;
        }
        int subId = si.getSubscriptionId();
        int phoneId = SubscriptionManager.getPhoneId(subId);
        setDataNetworkTypeForPhone(phoneId, type);
    }

    @SuppressLint("NewApi")
    @Override
    public int getCurrentPreferredNetworkMode() {
        final int subId = SubscriptionManager.getDefaultDataSubscriptionId();
        return getCurrentPreferredNetworkMode(subId);
    }

    @Override
    public int getCurrentPreferredNetworkMode(int subId) {
        return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.PREFERRED_NETWORK_MODE + subId, -1);
    }

    @Override
    public int getCurrentPreferredNetworkModeForSlot(int slotId) {
        SubscriptionInfo si = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(slotId);
        if (si == null) {
            return Integer.MIN_VALUE;
        }
        int subId = si.getSubscriptionId();
        return getCurrentPreferredNetworkMode(subId);
    }

    @Override
    public boolean isAirplaneModeEnabled() {
        return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
}
