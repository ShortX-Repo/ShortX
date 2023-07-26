package tornaco.apps.shortx.services.rule.fact.trigger

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.text.TextUtils
import com.elvishew.xlog.XLog
import kotlinx.coroutines.flow.MutableStateFlow
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.common.BTBondedDevice
import tornaco.apps.shortx.core.proto.common.OnOffAny
import tornaco.apps.shortx.core.proto.fact.BTConnectedTo
import tornaco.apps.shortx.core.proto.fact.BTDisconnectedFrom
import tornaco.apps.shortx.core.proto.fact.BTStatusChanged
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.util.bluetooth.BluetoothCallback
import tornaco.apps.shortx.services.util.bluetooth.CachedBluetoothDevice
import tornaco.apps.shortx.services.util.bluetooth.LocalBluetoothManager


class BTStateTrigger(override val context: Context, override val shortXContext: ShortXContext) :
    Trigger {
    private val logger = Logger("BTStateTrigger")
    private val _connectedDevices: MutableStateFlow<List<BTBondedDevice>> =
        MutableStateFlow(emptyList())

    private var btManager: LocalBluetoothManager? = null

    private fun checkConnectionState() {
        val eventConnectedDevices =
            btManager?.cachedDeviceManager?.cachedDevicesCopy?.filter { it.isConnected }
                ?.map { BTHelper.btDeviceToProtoDevice(it) }
                ?: emptyList()
        logger.d("eventConnectedDevices: ${eventConnectedDevices.map { it.deviceToString() }}")

        val currentBoundDevices = _connectedDevices.value
        logger.d("currentBoundDevices: ${currentBoundDevices.map { it.deviceToString() }}")

        val newConnectedDevices = eventConnectedDevices.filter { ed ->
            !currentBoundDevices.any { it.address == ed.address }
        }
        logger.d("newConnectedDevices ---->: ${newConnectedDevices.map { it.deviceToString() }}")

        val disconnectedDevices = currentBoundDevices.filter { cd ->
            !eventConnectedDevices.any { it.address == cd.address }
        }
        logger.d("disconnectedDevices ---->: ${disconnectedDevices.map { it.deviceToString() }}")

        _connectedDevices.value = eventConnectedDevices

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
        LocalBluetoothManager.getInstance(
            context
        ) { _, bluetoothManager ->
            logger.d("onBluetoothManagerInitialized: $bluetoothManager")
            btManager = bluetoothManager
            bluetoothManager.eventManager.registerCallback(object : BluetoothCallback {
                override fun onBluetoothStateChanged(bluetoothState: Int) {
                    logger.d("onBluetoothStateChanged: $bluetoothState")
                    if (bluetoothState == BluetoothAdapter.STATE_OFF) {
                        shortXContext.rule.publishFact(
                            "btStateChanged to btStateOff",
                            BTStatusChanged.newBuilder()
                                .setOoa(OnOffAny.Off)
                                .build()
                        )
                    }
                    if (bluetoothState == BluetoothAdapter.STATE_ON) {
                        shortXContext.rule.publishFact(
                            "btStateChanged to btStateOn",
                            BTStatusChanged.newBuilder()
                                .setOoa(OnOffAny.On)
                                .build()
                        )
                    }

                }

                override fun onScanningStateChanged(started: Boolean) {
                    logger.d("onScanningStateChanged: $started")
                }

                override fun onDeviceAdded(cachedDevice: CachedBluetoothDevice?) {
                    logger.d("onDeviceAdded: ${cachedDevice?.name}")
                }

                override fun onDeviceDeleted(cachedDevice: CachedBluetoothDevice?) {
                    logger.d("onDeviceDeleted: ${cachedDevice?.name}")
                }

                override fun onDeviceBondStateChanged(
                    cachedDevice: CachedBluetoothDevice?,
                    bondState: Int
                ) {
                    logger.d("onDeviceBondStateChanged: bondState: $bondState, ${cachedDevice?.name}")
                }

                override fun onConnectionStateChanged(
                    cachedDevice: CachedBluetoothDevice?,
                    state: Int
                ) {
                    logger.d("onConnectionStateChanged: state: $state, ${cachedDevice?.name}")
                    checkConnectionState()
                }

                override fun onActiveDeviceChanged(
                    activeDevice: CachedBluetoothDevice?,
                    bluetoothProfile: Int
                ) {
                    logger.d("onActiveDeviceChanged: bluetoothProfile: $bluetoothProfile, ${activeDevice?.name}")
                    checkConnectionState()
                }

                override fun onAudioModeChanged() {
                    logger.d("onAudioModeChanged")
                }
            })
        }
    }

    override fun unRegister() {
    }
}

@SuppressLint("MissingPermission")
object BTHelper {
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


    @SuppressLint("NewApi")
    fun btDeviceToProtoDevice(device: CachedBluetoothDevice): BTBondedDevice {
        val address = runCatching { device.address }.getOrElse {
            Logger("btDeviceToProtoDevice").e(it, "get BT address")
            ""
        }
        val alias = device.name
        val batteryLevel = device.batteryLevel

        val isConnected = device.isConnected

        return BTBondedDevice.newBuilder()
            .setAddress(address)
            .setAlias(alias)
            .setBatteryLevel(batteryLevel)
            .setIsConnected(isConnected)
            .build()
    }
}

fun BTBondedDevice.deviceToString() =
    "${this.alias} - ${this.address} - connected: ${this.isConnected}"
