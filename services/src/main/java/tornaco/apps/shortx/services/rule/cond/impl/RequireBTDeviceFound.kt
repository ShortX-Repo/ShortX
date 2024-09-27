package tornaco.apps.shortx.services.rule.cond.impl

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.proto.common.BTBondedDevice
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond
import tornaco.apps.shortx.services.rule.fact.trigger.BTHelper

object RequireBTDeviceFound : Cond {
    private val scanResults = mutableSetOf<BTBondedDevice>()
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            logger.logOnDebug("onScanResult: $result")
            result?.device?.let {
                val dev = BTHelper.btDeviceToProtoDevice(it)
                scanResults.add(dev)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            logger.logOnDebug("onBatchScanResults: $results")
            results?.mapNotNull { it.device }?.forEach {
                val dev = BTHelper.btDeviceToProtoDevice(it)
                scanResults.add(dev)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            logger.w("onScanFailed: $errorCode")
        }
    }

    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireBTDeviceFound::class.java

    @SuppressLint("MissingPermission")
    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.RequireBTDeviceFound::class.java
            val scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

            scanResults.clear()
            scanner.startScan(scanCallback)

            delay(data.timeout.toLongOrNull() ?: 5000)

            logger.logOnDebug("scanResults: $scanResults")

            val hasFound = scanResults.any {
                it.alias == data.device && (data.address.isNullOrEmpty() || data.address == it.address)
            }
            logger.d("Has found target device? $hasFound")
            scanner.stopScan(scanCallback)

            val res = hasFound
            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult()
        }
    }
}