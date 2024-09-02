package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.LightSensor
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.sensorManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData

class LightSensorTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger, SensorEventListener {
    private val logger = Logger("LightSensorTrigger")
    private var lightSensor: Sensor? = null

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

    @Synchronized
    override fun unRegister() {
        logger.d("Unregister light sensor")
        context.sensorManager.unregisterListener(this)
        shortXContext.watchDog.removeSensorUsage(Sensor.TYPE_LIGHT)
        lightSensor = null
    }

    @Synchronized
    private fun enableOrDisableSensor() {
        val sensorSrc = mutableListOf<String>()
        shortXContext.rule.forEachEnabledFacts { src, factProtoAny ->
            if (factProtoAny is_ LightSensor::class.java) {
                val fact = factProtoAny unpack_ LightSensor::class.java
                if (!fact.isDisabled) {
                    sensorSrc.add(src)
                }
            }
        }
        logger.d("sensorSrc: $sensorSrc")

        if (sensorSrc.isNotEmpty()) {
            if (this.lightSensor != null) {
                logger.w("Already registered. won't register again.")
                return
            }
            logger.d("Register light sensor")
            val lightSensor = context.sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            lightSensor?.also { sensor ->
                context.sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                shortXContext.watchDog.onSensorUsage(Sensor.TYPE_LIGHT, sensorSrc)
            }
            this.lightSensor = lightSensor
        } else {
            unRegister()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val lightIntensity = it.values[0] // 获取光线强度
            logger.d("onSensorChanged: $lightIntensity")
            shortXContext.rule.publishFactChecked(
                LightSensor.getDefaultInstance()
            ) {
                FactPublishData(
                    reason = "Light onSensorChanged",
                    fact = LightSensor.newBuilder().setValue(lightIntensity)
                        .build(),
                    initialContextDataLazy = {
                        mapOf(
                            ContextDataMapping.LightSensor.lightIntensity.name to lightIntensity.toString()
                        )
                    }
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}