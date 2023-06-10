package tornaco.apps.shortx.services.rule.fact.trigger

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import com.elvishew.xlog.XLog
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.BTStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext

class BTStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("BTStateTrigger")

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state =
                    intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                logger.d("Profile: btEventReceiver state: $state")
                if (state == BluetoothAdapter.STATE_OFF) {
                    shortXContext.rule.publishFact(
                        "btStateChanged to btStateOff",
                        BTStatusChanged.newBuilder()
                            .setOoa(OnOffAny.Off)
                            .build()
                    )
                }
                if (state == BluetoothAdapter.STATE_ON) {
                    shortXContext.rule.publishFact(
                        "btStateChanged to btStateOn",
                        BTStatusChanged.newBuilder()
                            .setOoa(OnOffAny.On)
                            .build()
                    )
                }
            }
        }
    }


    override fun register() {
        val btFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        btFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        ContextCompat.registerReceiver(context, receiver, btFilter, ContextCompat.RECEIVER_EXPORTED)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }

    @SuppressLint("MissingPermission")
    private fun getBoundedDeviceAddresses(): List<String> {
        val res = HashSet<String>()
        BluetoothAdapter.getDefaultAdapter().bondedDevices.forEach {
            res.add(it.address)
        }
        return res.toList()
    }

    @SuppressLint("NewApi", "MissingPermission")
    private fun getBoundedDeviceAliasNames(): List<String> {
        val res = HashSet<String>()
        BluetoothAdapter.getDefaultAdapter().bondedDevices.forEach {
            var alias = kotlin.runCatching {
                it.getAlias()
            }.getOrElse { error ->
                XLog.e("Get bt device alias error", error)
                ""
            }
            if (TextUtils.isEmpty(alias)) {
                alias = it.name
            }
            // it.aliasName
            // No virtual method getAliasName()Ljava/lang/String; in class Landroid/bluetooth/BluetoothDevice
            res.add(alias)
        }
        return res.toList()
    }

    @SuppressLint("MissingPermission")
    private fun getBoundedDeviceBatteryLevels(): List<Int> {
        val res = HashSet<Int>()
        BluetoothAdapter.getDefaultAdapter().bondedDevices.forEach {
            res.add(it.batteryLevel)
        }
        return res.toList()
    }
}