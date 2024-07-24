package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import tornaco.apps.shortx.core.compat.ContextCompat
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.UsbDeviceAttached
import tornaco.apps.shortx.core.proto.fact.UsbDeviceDetached
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.usbManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData

class UsbDeviceStateTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) : Trigger {
    private val logger = Logger("UsbDeviceStateTrigger")

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            val usbManager = this@UsbDeviceStateTrigger.context.usbManager

            when (action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    device?.let {
                        handleDeviceAttached(it)
                    }
                }

                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    device?.let {
                        handleDeviceDetached(it)
                    }
                }
            }
        }
    }

    private fun handleDeviceAttached(device: UsbDevice) {
        logger.p("handleDeviceAttached: $device")
        // UsbDevice[mName=/dev/bus/usb/001/003,mVendorId=2316,mProductId=4096,mClass=0,mSubclass=0,
        // mProtocol=0,mManufacturerName=Samsung,mProductName=Flash Drive FIT,mVersion=11.00,
        // mSerialNumberReader=com.android.server.usb.UsbSerialReader@f5e450d, mHasAudioPlayback=false,
        // mHasAudioCapture=false, mHasMidi=false, mHasVideoCapture=false, mHasVideoPlayback=false,
        // mConfigurations=[
        // UsbConfiguration[mId=1,mName=null,mAttributes=128,mMaxPower=150,mInterfaces=[
        //
        // UsbInterface[mId=0,mAlternateSetting=0,mName=null,mClass=8,mSubclass=6,mProtocol=80,mEndpoints=[
        // UsbEndpoint[mAddress=1,mAttributes=2,mMaxPacketSize=512,mInterval=0]
        // UsbEndpoint[mAddress=130,mAttributes=2,mMaxPacketSize=512,mInterval=0]]]]

        val productName = device.productName
        val manufacturerName = device.manufacturerName

        shortXContext.rule.publishFactChecked(UsbDeviceAttached.getDefaultInstance()) {
            FactPublishData(
                reason = "Device Attached",
                fact = UsbDeviceAttached.newBuilder()
                    .setDeviceName(device.deviceName).build(),
                initialContextDataLazy = {
                    mapOf(
                        ContextDataMapping.UsbDeviceAttachDetach.productName.name to productName.orEmpty(),
                        ContextDataMapping.UsbDeviceAttachDetach.manufacturerName.name to manufacturerName.orEmpty(),
                    )
                }
            )
        }
    }

    private fun handleDeviceDetached(device: UsbDevice) {
        logger.p("handleDeviceDetached: $device")
        val productName = device.productName
        val manufacturerName = device.manufacturerName
        shortXContext.rule.publishFactChecked(UsbDeviceAttached.getDefaultInstance()) {
            FactPublishData(
                reason = "Device Detached",
                fact = UsbDeviceDetached.newBuilder()
                    .setDeviceName(device.deviceName).build(),
                initialContextDataLazy = {
                    mapOf(
                        ContextDataMapping.UsbDeviceAttachDetach.productName.name to productName.orEmpty(),
                        ContextDataMapping.UsbDeviceAttachDetach.manufacturerName.name to manufacturerName.orEmpty(),
                    )
                }
            )
        }
    }

    override fun register() {
        val filter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    override fun unRegister() {
        context.unregisterReceiver(receiver)
    }
}