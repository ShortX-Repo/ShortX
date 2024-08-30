package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.ProximitySensor
import tornaco.apps.shortx.core.proto.fact.ProximitySensorState
import tornaco.apps.shortx.core.proto.fact.ProximitySensorStateChanged
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.sensorManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData

class ProximitySensorTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger, SensorEventListener {
    private val logger = Logger("ProximitySensorTrigger")

    private var proximitySensor: Sensor? = null

    override fun onAnyChanged() {
        super.onAnyChanged()
        enableOrDisableSensor()
    }

    override fun register() {
        enableOrDisableSensor()
    }

    override fun onScreenOff() {
        super.onScreenOff()
        unRegister()
    }

    override fun onScreenOn() {
        super.onScreenOn()
        enableOrDisableSensor()
    }

    override fun unRegister() {
        logger.d("Unregister light sensor")
        context.sensorManager.unregisterListener(this)
        shortXContext.watchDog.removeSensorUsage(Sensor.TYPE_PROXIMITY)
    }

    private fun enableOrDisableSensor() {
        val sensorSrc = mutableListOf<String>()
        shortXContext.rule.forEachEnabledFacts { src, factProtoAny ->
            if (factProtoAny is_ ProximitySensor::class.java) {
                val fact = factProtoAny unpack_ ProximitySensor::class.java
                if (!fact.isDisabled) {
                    sensorSrc.add(src)
                }
            } else if (factProtoAny is_ ProximitySensorStateChanged::class.java) {
                val fact = factProtoAny unpack_ ProximitySensorStateChanged::class.java
                if (!fact.isDisabled) {
                    sensorSrc.add(src)
                }
            }
        }
        logger.d("sensorSrc: $sensorSrc")

        if (sensorSrc.isNotEmpty()) {
            logger.d("Register Proximity sensor")
            val prox = context.sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
            prox?.also { sensor ->
                context.sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                shortXContext.watchDog.onSensorUsage(Sensor.TYPE_PROXIMITY, sensorSrc)
            }
            this.proximitySensor = prox
        } else {
            unRegister()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val distance = it.values[0]
            logger.d("onSensorChanged: $distance, maximumRange: ${proximitySensor?.maximumRange}")

            // 检查设备是否接近
            val proximitySensorState = if (distance < (proximitySensor?.maximumRange ?: 0f)) {
                // 设备接近
                ProximitySensorState.ProximitySensorState_Approaching
            } else {
                // 设备远离
                ProximitySensorState.ProximitySensorState_Departing
            }

            shortXContext.rule.publishFactChecked(
                ProximitySensorStateChanged.getDefaultInstance()
            ) {
                FactPublishData(
                    reason = "Proximity onSensorChanged",
                    fact = ProximitySensorStateChanged.newBuilder().setState(proximitySensorState)
                        .build()
                )
            }

            shortXContext.rule.publishFactChecked(
                ProximitySensor.getDefaultInstance()
            ) {
                FactPublishData(
                    reason = "Proximity onSensorChanged",
                    fact = ProximitySensor.newBuilder().setValue(distance).build(),
                    initialContextDataLazy = {
                        mapOf(
                            ContextDataMapping.ProximitySensor.distance.name to distance.toString(),
                            ContextDataMapping.ProximitySensor.maximumRange.name to (proximitySensor?.maximumRange
                                ?: 0).toString(),
                        )
                    }
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}