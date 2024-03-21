package tornaco.apps.shortx.services.rule.cond.impl

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import com.google.protobuf.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond
import tornaco.apps.shortx.services.rule.fact.trigger.BTHelper

object RequireBTDeviceFound : Cond {
    private val stopScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            logger.d("STOPCALLBACK onScanResult: $result")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            logger.d("STOPCALLBACK onBatchScanResults: $results")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            logger.d("STOPCALLBACK onScanFailed: $errorCode")
        }
    }

    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.RequireBTDeviceFound::class.java

    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.RequireBTDeviceFound::class.java
            val scanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

            val scanResults = mutableSetOf<String>()
            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    super.onScanResult(callbackType, result)
                    logger.p("onScanResult: $result")
                    result?.device?.let {
                        val dev = BTHelper.btDeviceToProtoDevice(it)
                        scanResults.add(dev.alias)
                    }
                }

                override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                    super.onBatchScanResults(results)
                    logger.p("onBatchScanResults: $results")
                    results?.mapNotNull { it.device }?.forEach {
                        val dev = BTHelper.btDeviceToProtoDevice(it)
                        scanResults.add(dev.alias)
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    super.onScanFailed(errorCode)
                    logger.w("onScanFailed: $errorCode")
                }
            }
            scanner.startScan(scanCallback)

            delay(5000)

            logger.p("scanResults: $scanResults")

            val hasFound = scanResults.contains(data.device)
            logger.d("Has found target device? $")
            scanner.stopScan(stopScanCallback)

            val res = hasFound
            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult()
        }
    }
}