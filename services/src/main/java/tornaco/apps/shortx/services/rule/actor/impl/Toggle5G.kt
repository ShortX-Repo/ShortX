package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionManager
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.telephonyManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

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

        val targetSubInfo =
            SubscriptionManager.from(androidContext).activeSubscriptionInfoList.firstOrNull {
                it.simSlotIndex == data.slotId
            }
        requireNotNull(targetSubInfo) {
            "Sub not found for slot: ${data.slotId}"
        }
        logger.d("Toggle5G targetSubInfo.subId: ${targetSubInfo.subscriptionId}")

        val switcher = getFiveGSwitcher()
        val isCurrent5G = switcher.is5GEnabled(androidContext, targetSubInfo.subscriptionId)
        when (onOff) {
            OnOffToggle.OnOffToggle_On -> {
                switcher.set5GEnabled(true, androidContext, targetSubInfo.subscriptionId)
            }

            OnOffToggle.OnOffToggle_Off -> {
                switcher.set5GEnabled(false, androidContext, targetSubInfo.subscriptionId)
            }

            OnOffToggle.OnOffToggle_Toggle -> {
                switcher.set5GEnabled(!isCurrent5G, androidContext, targetSubInfo.subscriptionId)
            }

            OnOffToggle.UNRECOGNIZED -> {
                throw Error("UNRECOGNIZED")
            }
        }
    }

    private fun getFiveGSwitcher(): FiveGSwitcher {
        return object : FiveGSwitcher {
            @SuppressLint("MissingPermission")
            override fun is5GEnabled(context: Context, subId: Int): Boolean {
                val networkMode = context.telephonyManager.getPreferredNetworkType(subId)
                logger.d("getPreferredNetworkType, networkMode: $networkMode")
                val is5G = networkMode >= 23
                return is5G
            }

            override fun set5GEnabled(enable: Boolean, context: Context, subId: Int) {
                val mode = if (enable) 33 else 22
                logger.d("set5GEnabled, mode: $mode")
                context.telephonyManager.setPreferredNetworkType(subId, mode)
            }
        }
    }

    interface FiveGSwitcher {
        fun is5GEnabled(context: Context, subId: Int): Boolean
        fun set5GEnabled(enable: Boolean, context: Context, subId: Int)
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