package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.bundleToString
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.util.FivegControllerService
import tornaco.apps.shortx.services.util.PhoneUtils


object Toggle5G : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.Toggle5G::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.Toggle5G::class.java
        val onOff: OnOffToggle = data.onOff
        val slotId = data.slotId

        logger.d("Toggle5G slotId: $slotId")

        val switcher = getFiveGSwitcher()
        val isCurrent5G = switcher.is5GEnabled(androidContext, slotId)
        when (onOff) {
            OnOffToggle.OnOffToggle_On -> {
                switcher.set5GEnabled(true, androidContext, slotId)
            }

            OnOffToggle.OnOffToggle_Off -> {
                switcher.set5GEnabled(false, androidContext, slotId)
            }

            OnOffToggle.OnOffToggle_Toggle -> {
                switcher.run { set5GEnabled(!isCurrent5G, androidContext, slotId) }
            }

            OnOffToggle.UNRECOGNIZED -> {
                throw Error("UNRECOGNIZED")
            }
        }
    }

    private fun getFiveGSwitcher(): FiveGSwitcher {
        return if (OsUtils.isColorOS()) {
            ColorOSFiveGSwitcher
        } else FallbackFiveGSwitcher()
    }

    // Color OS
    //   <provider android:name="com.android.simsettings.demands.siminfo.fiveg.FiveGStatus1Provider" android:permission="com.oplus.permission.safe.SECURITY" android:exported="true" android:authorities="com.android.simsettings.demands.FiveG1_SwitchProvider" android:grantUriPermissions="true">
    //            <intent-filter>
    //                <action android:name="com.android.settings.MANUFACTURER_APPLICATION_SETTING"/>
    //            </intent-filter>
    //        </provider>
    //        <provider android:name="com.android.simsettings.demands.siminfo.fiveg.FiveGStatus2Provider" android:permission="com.oplus.permission.safe.SECURITY" android:exported="true" android:authorities="com.android.simsettings.demands.FiveG2_SwitchProvider" android:grantUriPermissions="true">
    //            <intent-filter>
    //                <action android:name="com.android.settings.MANUFACTURER_APPLICATION_SETTING"/>
    //            </intent-filter>
    //        </provider>

    interface FiveGSwitcher {
        fun is5GEnabled(context: Context, slotId: Int): Boolean
        fun set5GEnabled(enable: Boolean, context: Context, slotId: Int)
    }

    object ColorOSFiveGSwitcher : FiveGSwitcher {
        const val META_DATA_PREFERENCE_KEYHINT = "com.android.settings.keyhint"
        const val EXTRA_SWITCH_CHECKED_STATE = "checked_state"
        const val METHOD_IS_CHECKED = "isChecked"
        const val METHOD_ON_CHECKED_CHANGED = "onCheckedChanged"
        const val KEY1 = "5g_debug_key1"
        const val KEY2 = "5g_debug_key2"
        const val URL_1 = "content://com.android.simsettings.demands.FiveG1_SwitchProvider"
        const val URL_2 = "content://com.android.simsettings.demands.FiveG2_SwitchProvider"

        override fun is5GEnabled(context: Context, slotId: Int): Boolean {
            // content call --uri content://com.android.simsettings.demands.FiveG1_SwitchProvider --method isChecked --extra com.android.settings.keyhint:s:5g_debug_key1
            val contentResolver: ContentResolver = context.contentResolver
            val providerUri: Uri =
                Uri.parse(if (slotId == 0) URL_1 else URL_2)
            logger.d("ColorOSFiveGSwitcher is5GEnabled, call uri: $providerUri")
            val key = if (slotId == 0) KEY1 else KEY2
            val result: Bundle =
                contentResolver.call(providerUri, METHOD_IS_CHECKED, null, Bundle().apply {
                    putString(META_DATA_PREFERENCE_KEYHINT, key)
                })
            logger.d("ColorOSFiveGSwitcher is5GEnabled call res: ${result.bundleToString()}")
            return result.getBoolean(EXTRA_SWITCH_CHECKED_STATE)
        }

        override fun set5GEnabled(enable: Boolean, context: Context, slotId: Int) {
            // call --uri content://com.android.simsettings.demands.FiveG2_SwitchProvider --method onCheckedChanged --extra com.android.settings.keyhint:s:5g_debug_key2 --extra checked_state:b:true
            val contentResolver: ContentResolver = context.contentResolver
            val providerUri: Uri =
                Uri.parse(if (slotId == 0) URL_1 else URL_2)
            logger.d("ColorOSFiveGSwitcher set5GEnabled, call uri: $providerUri")
            val key = if (slotId == 0) KEY1 else KEY2
            val result: Bundle =
                contentResolver.call(providerUri, METHOD_ON_CHECKED_CHANGED, null, Bundle().apply {
                    putString(META_DATA_PREFERENCE_KEYHINT, key)
                    putBoolean(EXTRA_SWITCH_CHECKED_STATE, enable)
                })
            logger.d("ColorOSFiveGSwitcher set5GEnabled call res: ${result.bundleToString()}")
        }
    }

    class FallbackFiveGSwitcher : FiveGSwitcher {
        private val service by lazy { FivegControllerService() }

        @SuppressLint("MissingPermission")
        override fun is5GEnabled(context: Context, slotId: Int): Boolean {
            val subId = PhoneUtils.slotIdToSubIdOrThrow(context, slotId)
            return service.getFivegEnabled(subId)
        }

        override fun set5GEnabled(enable: Boolean, context: Context, slotId: Int) {
            val subId = PhoneUtils.slotIdToSubIdOrThrow(context, slotId)
            service.setFivegEnabled(subId, enable)
        }
    }
}

internal object AndroidRilConstants {
    /* NETWORK_MODE_* See ril.h RIL_REQUEST_SET_PREFERRED_NETWORK_TYPE */
    /* NETWORK_MODE_* See ril.h RIL_REQUEST_SET_PREFERRED_NETWORK_TYPE */
    /** GSM, WCDMA (WCDMA preferred)  */
    const val NETWORK_MODE_WCDMA_PREF = 0

    /** GSM only  */
    const val NETWORK_MODE_GSM_ONLY = 1

    /** WCDMA only  */
    const val NETWORK_MODE_WCDMA_ONLY = 2

    /** GSM, WCDMA (auto mode, according to PRL)  */
    const val NETWORK_MODE_GSM_UMTS = 3

    /** CDMA and EvDo (auto mode, according to PRL)  */
    const val NETWORK_MODE_CDMA = 4

    /** CDMA only  */
    const val NETWORK_MODE_CDMA_NO_EVDO = 5

    /** EvDo only  */
    const val NETWORK_MODE_EVDO_NO_CDMA = 6

    /** GSM, WCDMA, CDMA, and EvDo (auto mode, according to PRL)  */
    const val NETWORK_MODE_GLOBAL = 7

    /** LTE, CDMA and EvDo  */
    const val NETWORK_MODE_LTE_CDMA_EVDO = 8

    /** LTE, GSM and WCDMA  */
    const val NETWORK_MODE_LTE_GSM_WCDMA = 9

    /** LTE, CDMA, EvDo, GSM, and WCDMA  */
    const val NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA = 10

    /** LTE only mode.  */
    const val NETWORK_MODE_LTE_ONLY = 11

    /** LTE and WCDMA  */
    const val NETWORK_MODE_LTE_WCDMA = 12

    /** TD-SCDMA only  */
    const val NETWORK_MODE_TDSCDMA_ONLY = 13

    /** TD-SCDMA and WCDMA  */
    const val NETWORK_MODE_TDSCDMA_WCDMA = 14

    /** LTE and TD-SCDMA */
    const val NETWORK_MODE_LTE_TDSCDMA = 15

    /** TD-SCDMA and GSM  */
    const val NETWORK_MODE_TDSCDMA_GSM = 16

    /** TD-SCDMA, GSM and LTE  */
    const val NETWORK_MODE_LTE_TDSCDMA_GSM = 17

    /** TD-SCDMA, GSM and WCDMA  */
    const val NETWORK_MODE_TDSCDMA_GSM_WCDMA = 18

    /** LTE, TD-SCDMA and WCDMA  */
    const val NETWORK_MODE_LTE_TDSCDMA_WCDMA = 19

    /** LTE, TD-SCDMA, GSM, and WCDMA  */
    const val NETWORK_MODE_LTE_TDSCDMA_GSM_WCDMA = 20

    /** TD-SCDMA, CDMA, EVDO, GSM and WCDMA  */
    const val NETWORK_MODE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 21

    /** LTE, TDCSDMA, CDMA, EVDO, GSM and WCDMA  */
    const val NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 22

    /** NR 5G only mode  */
    const val NETWORK_MODE_NR_ONLY = 23

    /** NR 5G, LTE  */
    const val NETWORK_MODE_NR_LTE = 24

    /** NR 5G, LTE, CDMA and EvDo  */
    const val NETWORK_MODE_NR_LTE_CDMA_EVDO = 25

    /** NR 5G, LTE, GSM and WCDMA  */
    const val NETWORK_MODE_NR_LTE_GSM_WCDMA = 26

    /** NR 5G, LTE, CDMA, EvDo, GSM and WCDMA  */
    const val NETWORK_MODE_NR_LTE_CDMA_EVDO_GSM_WCDMA = 27

    /** NR 5G, LTE and WCDMA  */
    const val NETWORK_MODE_NR_LTE_WCDMA = 28

    /** NR 5G, LTE and TDSCDMA  */
    const val NETWORK_MODE_NR_LTE_TDSCDMA = 29

    /** NR 5G, LTE, TD-SCDMA and GSM  */
    const val NETWORK_MODE_NR_LTE_TDSCDMA_GSM = 30

    /** NR 5G, LTE, TD-SCDMA, WCDMA  */
    const val NETWORK_MODE_NR_LTE_TDSCDMA_WCDMA = 31

    /** NR 5G, LTE, TD-SCDMA, GSM and WCDMA  */
    const val NETWORK_MODE_NR_LTE_TDSCDMA_GSM_WCDMA = 32

    /** NR 5G, LTE, TD-SCDMA, CDMA, EVDO, GSM and WCDMA  */
    const val NETWORK_MODE_NR_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 33
}