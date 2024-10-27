package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.NFCStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

class NFCStateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("NFCStateTrigger")

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            logger.d("handleReceive: $intent")

            if (intent.action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) {
                val state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF)
                when (state) {
                    NfcAdapter.STATE_OFF -> {
                        shortXContext.rule.publishFactChecked(
                            NFCStatusChanged.getDefaultInstance()
                        ) {
                            FactPublishData(
                                reason = "NFCStatusChanged",
                                fact = NFCStatusChanged.newBuilder().setOoa(OnOffAny.Off).build()
                            )
                        }
                    }

                    NfcAdapter.STATE_ON -> {
                        shortXContext.rule.publishFactChecked(
                            NFCStatusChanged.getDefaultInstance()
                        ) {
                            FactPublishData(
                                reason = "NFCStatusChanged",
                                fact = NFCStatusChanged.newBuilder().setOoa(OnOffAny.On).build()
                            )
                        }
                    }

                    NfcAdapter.STATE_TURNING_ON -> {
                        // NFC 正在打开
                    }

                    NfcAdapter.STATE_TURNING_OFF -> {
                        // NFC 正在关闭
                    }
                }
            }
        }
    }

    override fun register() {
        val filter = IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }
}