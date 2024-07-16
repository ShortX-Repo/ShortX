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
import tornaco.apps.shortx.core.util.fallbackOnEmpty
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

        shortXContext.rule.publishFactChecked(UsbDeviceAttached.getDefaultInstance()) {
            FactPublishData(
                reason = "Device Attached",
                fact = UsbDeviceAttached.newBuilder()
                    .setDeviceName(device.deviceName).build(),
                initialContextDataLazy = {
                    mapOf(
                        ContextDataMapping.UsbDeviceAttachDetach.deviceName.name to device.deviceName.fallbackOnEmpty(
                            ""
                        ),
                        ContextDataMapping.UsbDeviceAttachDetach.vendorId.name to device.vendorId.toString()
                            .fallbackOnEmpty(""),
                        ContextDataMapping.UsbDeviceAttachDetach.productId.name to device.productId.toString()
                            .fallbackOnEmpty(""),
                        ContextDataMapping.UsbDeviceAttachDetach.deviceClass.name to device.deviceClass.toString()
                            .fallbackOnEmpty(""),
                        ContextDataMapping.UsbDeviceAttachDetach.deviceSubclass.name to device.deviceSubclass.toString()
                            .fallbackOnEmpty(""),
                        ContextDataMapping.UsbDeviceAttachDetach.deviceProtocol.name to device.deviceProtocol.toString()
                            .fallbackOnEmpty(""),
                    )
                }
            )
        }
    }

    private fun handleDeviceDetached(device: UsbDevice) {
        logger.p("handleDeviceDetached: $device")

        shortXContext.rule.publishFactChecked(UsbDeviceAttached.getDefaultInstance()) {
            FactPublishData(
                reason = "Device Detached",
                fact = UsbDeviceDetached.newBuilder()
                    .setDeviceName(device.deviceName).build(),
                initialContextDataLazy = {
                    mapOf(
                        ContextDataMapping.UsbDeviceAttachDetach.deviceName.name to device.deviceName.fallbackOnEmpty(
                            ""
                        ),
                        ContextDataMapping.UsbDeviceAttachDetach.vendorId.name to device.vendorId.toString()
                            .fallbackOnEmpty(""),
                        ContextDataMapping.UsbDeviceAttachDetach.productId.name to device.productId.toString()
                            .fallbackOnEmpty(""),
                        ContextDataMapping.UsbDeviceAttachDetach.deviceClass.name to device.deviceClass.toString()
                            .fallbackOnEmpty(""),
                        ContextDataMapping.UsbDeviceAttachDetach.deviceSubclass.name to device.deviceSubclass.toString()
                            .fallbackOnEmpty(""),
                        ContextDataMapping.UsbDeviceAttachDetach.deviceProtocol.name to device.deviceProtocol.toString()
                            .fallbackOnEmpty(""),
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