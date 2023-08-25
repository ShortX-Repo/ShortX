package tornaco.apps.shortx.services.rule.fact.trigger

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import com.elvishew.xlog.XLog
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.BTBondedDevice
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.BTConnectedTo
import tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom
import tornaco.apps.shortx.core.proto.fact.BTStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.fact.trigger.BTHelper.btDeviceToProtoDevice
import tornaco.apps.shortx.services.util.SafeBroadcastReceiver


class BTStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("BTStateTrigger")
    override fun register() {
        val receiver = object : SafeBroadcastReceiver(shortXContext) {
            @SuppressLint("MissingPermission")
            override fun handleReceive(context: Context, intent: Intent) {
                val action = intent.action
                logger.d("handleReceive: $action")

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
                    } else if (state == BluetoothAdapter.STATE_ON) {
                        shortXContext.rule.publishFact(
                            "btStateChanged to btStateOn",
                            BTStatusChanged.newBuilder()
                                .setOoa(OnOffAny.On)
                                .build()
                        )
                    }
                } else if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                    val device =
                        btDeviceToProtoDevice(
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE
                            )
                        )

                    logger.d("ACL connected: ${device.deviceToString()}")

                    BTHelper._connectedDevices.value =
                        BTHelper._connectedDevices.value.toMutableList().apply {
                            add(device)
                        }

                    shortXContext.rule.publishFact(
                        reason = "BT device connected",
                        fact = BTConnectedTo.newBuilder().addAllDevice(listOf(device.alias))
                            .build(),
                        mapOf(
                            ContextDataMapping.BTDeviceConnected.btDeviceAlias.name to device.alias,
                            ContextDataMapping.BTDeviceConnected.btDeviceAddress.name to device.address,
                            ContextDataMapping.BTDeviceConnected.btDeviceBatteryLevel.name to device.batteryLevel
                        )
                    )
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                    val device =
                        btDeviceToProtoDevice(
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE
                            )
                        )
                    logger.d("ACL disconnected: ${device.deviceToString()}")

                    BTHelper._connectedDevices.value =
                        BTHelper._connectedDevices.value.toMutableList().apply {
                            remove(device)
                        }

                    shortXContext.rule.publishFact(
                        reason = "BT device disconnected",
                        fact = BTDisconnectedFrom.newBuilder().addAllDevice(
                            listOf(device.alias)
                        ).build(),
                        mapOf(
                            ContextDataMapping.BTDeviceDisConnected.btDeviceAlias.name to device.alias,
                            ContextDataMapping.BTDeviceDisConnected.btDeviceAddress.name to device.address,
                        )
                    )
                }
            }
        }

        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        context.registerReceiver(receiver, filter)
    }

    override fun unRegister() {
    }
}

@SuppressLint("MissingPermission")
object BTHelper {
    private val logger = Logger("BTHelper")

    // Cache.
    val _connectedDevices: MutableStateFlow<List<BTBondedDevice>> =
        MutableStateFlow(emptyList())

    fun getBTBondedDevices() = BluetoothAdapter.getDefaultAdapter().bondedDevices.toList()
        .map { device ->
            btDeviceToProtoDevice(device)
        }

    @SuppressLint("NewApi")
    fun btDeviceToProtoDevice(device: BluetoothDevice): BTBondedDevice {
        val address = runCatching { device.address }.getOrElse {
            Logger("btDeviceToProtoDevice").e(it, "get BT address")
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

        return BTBondedDevice.newBuilder()
            .setAddress(address)
            .setAlias(alias)
            .setBatteryLevel(batteryLevel)
            .setIsConnected(isConnected)
            .build()
    }

    fun getAllConnectedDevices(context: Context): List<BluetoothDevice> {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        val connectedDevices = mutableListOf<BluetoothDevice>()

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            return connectedDevices
        }

        val profiles = intArrayOf(
            BluetoothProfile.A2DP,
            BluetoothProfile.HEADSET,
            BluetoothProfile.HEALTH
            // Add other Bluetooth profiles you're interested in
        )

        for (profileId in profiles) {
            bluetoothAdapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                    if (profile == profileId) {
                        connectedDevices.addAll(proxy.connectedDevices)
                    }
                }

                override fun onServiceDisconnected(profile: Int) {
                }
            }, profileId)
        }

        return connectedDevices
    }


    fun <T> getProfileProxy(context: Context, profile: Int): T {
        var proxy: BluetoothProfile?
        runBlocking {
            val bluetoothManager = context.getSystemService(BluetoothManager::class.java)!!
            val bluetoothAdapter = bluetoothManager.adapter

            val flow = callbackFlow {
                val serviceListener =
                    object : BluetoothProfile.ServiceListener {
                        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                            trySendBlocking(proxy)
                        }

                        override fun onServiceDisconnected(profile: Int) {}
                    }

                bluetoothAdapter.getProfileProxy(context, serviceListener, profile)

                awaitClose {}
            }
            proxy = withTimeoutOrNull(5_000) { flow.first() }
        }
        if (proxy == null) {
            logger.w("profile proxy $profile is null")
        }
        @Suppress("UNCHECKED_CAST")
        return proxy!! as T
    }
}

fun BTBondedDevice.deviceToString() =
    "${this.alias} - ${this.address} - connected: ${this.isConnected}"


