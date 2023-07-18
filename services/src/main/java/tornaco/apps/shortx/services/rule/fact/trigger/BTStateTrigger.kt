package tornaco.apps.shortx.services.rule.fact.trigger

import android.annotation.SuppressLint
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import com.elvishew.xlog.XLog
import kotlinx.coroutines.flow.MutableStateFlow
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.BTBondedDevice
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.BTConnectedTo
import tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom
import tornaco.apps.shortx.core.proto.fact.BTStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.BTHelper.getBTBondedDevices
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver

class BTStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("BTStateTrigger")
    private val _boundDevices: MutableStateFlow<List<BTBondedDevice>> =
        MutableStateFlow(emptyList())

    private val receiver = object : SafeBroadcastReceiver(shortXContext) {
        override fun handleReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state =
                    intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                logger.d("btEventReceiver state: $state")
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

                checkConnectionState()
            } else {
                logger.d("BT Action: $action")
                checkConnectionState()
            }
        }
    }

    private fun checkConnectionState() {
        val eventDevices = getBTBondedDevices()
        logger.d("eventDevices: $eventDevices")

        val eventConnectedDevices = eventDevices.filter { it.isConnected }
        logger.d("eventConnectedDevices: $eventDevices")

        val currentBoundDevices = _boundDevices.value
        logger.d("currentBoundDevices: $currentBoundDevices")

        val newConnectedDevices = eventConnectedDevices.filter { ed ->
            !currentBoundDevices.any { it.address == ed.address }
        }
        logger.d("newConnectedDevices: $newConnectedDevices")

        val disconnectedDevices = currentBoundDevices.filter { cd ->
            !eventConnectedDevices.any { it.address == cd.address }
        }
        logger.d("disconnectedDevices: $disconnectedDevices")

        _boundDevices.value = eventConnectedDevices


        if (newConnectedDevices.isNotEmpty()) {
            newConnectedDevices.forEach {
                shortXContext.rule.publishFact(
                    reason = "BT device connected",
                    fact = BTConnectedTo.newBuilder().addAllDevice(
                        listOf(it.alias)
                    ).build(),
                    mapOf(
                        ContextDataMapping.BTDeviceConnected.btDeviceAlias.name to it.alias,
                        ContextDataMapping.BTDeviceConnected.btDeviceAddress.name to it.address,
                        ContextDataMapping.BTDeviceConnected.btDeviceBatteryLevel.name to it.batteryLevel
                    )
                )
            }
        }

        if (disconnectedDevices.isNotEmpty()) {
            disconnectedDevices.forEach {
                shortXContext.rule.publishFact(
                    reason = "BT device disconnected",
                    fact = BTDisconnectedFrom.newBuilder().addAllDevice(
                        listOf(it.alias)
                    ).build(),
                    mapOf(
                        ContextDataMapping.BTDeviceDisConnected.btDeviceAlias.name to it.alias,
                        ContextDataMapping.BTDeviceDisConnected.btDeviceAddress.name to it.address,
                    )
                )
            }
        }
    }


    override fun register() {
        //    addHandler(BluetoothDevice.ACTION_FOUND, new DeviceFoundHandler());
        //        addHandler(BluetoothDevice.ACTION_NAME_CHANGED, new NameChangedHandler());
        //        addHandler(BluetoothDevice.ACTION_ALIAS_CHANGED, new NameChangedHandler());
        //
        //        // Pairing broadcasts
        //        addHandler(BluetoothDevice.ACTION_BOND_STATE_CHANGED, new BondStateChangedHandler());
        //
        //        // Fine-grained state broadcasts
        //        addHandler(BluetoothDevice.ACTION_CLASS_CHANGED, new ClassChangedHandler());
        //        addHandler(BluetoothDevice.ACTION_UUID, new UuidChangedHandler());
        //        addHandler(BluetoothDevice.ACTION_BATTERY_LEVEL_CHANGED, new BatteryLevelChangedHandler());
        //
        //        // Active device broadcasts
        //        addHandler(BluetoothA2dp.ACTION_ACTIVE_DEVICE_CHANGED, new ActiveDeviceChangedHandler());
        //        addHandler(BluetoothHeadset.ACTION_ACTIVE_DEVICE_CHANGED, new ActiveDeviceChangedHandler());
        //        addHandler(BluetoothHearingAid.ACTION_ACTIVE_DEVICE_CHANGED,
        //                new ActiveDeviceChangedHandler());
        //        addHandler(BluetoothLeAudio.ACTION_LE_AUDIO_ACTIVE_DEVICE_CHANGED,
        //                   new ActiveDeviceChangedHandler());
        //
        //        // Headset state changed broadcasts
        //        addHandler(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED,
        //                new AudioModeChangedHandler());
        //        addHandler(TelephonyManager.ACTION_PHONE_STATE_CHANGED,
        //                new AudioModeChangedHandler());
        //
        //        // ACL connection changed broadcasts
        //        addHandler(BluetoothDevice.ACTION_ACL_CONNECTED, new AclStateChangedHandler());
        //        addHandler(BluetoothDevice.ACTION_ACL_DISCONNECTED, new AclStateChangedHandler());
        val btFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        btFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        btFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        btFilter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED)
        btFilter.addAction(BluetoothDevice.ACTION_BATTERY_LEVEL_CHANGED)
        btFilter.addAction(BluetoothDevice.ACTION_UUID)
        btFilter.addAction(BluetoothA2dp.ACTION_ACTIVE_DEVICE_CHANGED)
        btFilter.addAction(BluetoothHeadset.ACTION_ACTIVE_DEVICE_CHANGED)
        ContextCompat.registerReceiver(context, receiver, btFilter, ContextCompat.RECEIVER_EXPORTED)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }
}

object BTHelper {
    @SuppressLint("MissingPermission", "NewApi")
    fun getBTBondedDevices() = BluetoothAdapter.getDefaultAdapter().bondedDevices.toList()
        .map { device ->
            val address = runCatching { device.address }.getOrElse {
                Logger("getBTBondedDevices").e(it, "get BT address")
                ""
            }
            var alias = kotlin.runCatching {
                device.alias
            }.getOrElse { error ->
                XLog.e("Get bt device alias error", error)
                ""
            }
            if (TextUtils.isEmpty(alias)) {
                alias = device.name
            }
            // device.aliasName
            // No virtual method getAliasName()Ljava/lang/String; in class Landroid/bluetooth/BluetoothDevice

            val batteryLevel = device.batteryLevel

            val isConnected = device.isConnected

            BTBondedDevice.newBuilder()
                .setAddress(address)
                .setAlias(alias)
                .setBatteryLevel(batteryLevel)
                .setIsConnected(isConnected)
                .build()
        }
}
