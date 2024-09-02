package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.AccelerometerSensor
import tornaco.apps.shortx.core.proto.fact.ShakeDevice
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.sensorManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData
import kotlin.math.sqrt

class AccelerometerSensorTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger, SensorEventListener {
    private val logger = Logger("AccelerometerSensorTrigger")

    private var shakeThreshold = 12f // 摇晃的加速度阈值，可以根据需要调整
    private var lastShakeTime: Long = 0

    private var accelerometerSensor: Sensor? = null

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
        logger.d("Unregister accelerometer sensor")
        context.sensorManager.unregisterListener(this)
        shortXContext.watchDog.removeSensorUsage(Sensor.TYPE_ACCELEROMETER)
    }

    private fun enableOrDisableSensor() {
        val sensorSrc = mutableListOf<String>()
        shortXContext.rule.forEachEnabledFacts { src, factProtoAny ->
            if (factProtoAny is_ ShakeDevice::class.java) {
                val fact = factProtoAny unpack_ ShakeDevice::class.java
                if (!fact.isDisabled) {
                    sensorSrc.add(src)
                }
            } else if (factProtoAny is_ AccelerometerSensor::class.java) {
                val fact = factProtoAny unpack_ AccelerometerSensor::class.java
                if (!fact.isDisabled) {
                    sensorSrc.add(src)
                }
            }
        }
        logger.d("sensorSrc: $sensorSrc")

        if (sensorSrc.isNotEmpty()) {
            logger.d("Register Accelerometer sensor")
            val acc = context.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            acc?.also { sensor ->
                context.sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
                shortXContext.watchDog.onSensorUsage(Sensor.TYPE_ACCELEROMETER, sensorSrc)
            }
            this.accelerometerSensor = acc
        } else {
            unRegister()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        logger.d("onSensorChanged- $x $y $z")

        shortXContext.rule.publishFactChecked(
            AccelerometerSensor.getDefaultInstance()
        ) {
            FactPublishData(
                reason = "Acc onSensorChanged",
                fact = AccelerometerSensor.newBuilder()
                    .setValueX(x)
                    .setValueY(y)
                    .setValueZ(z)
                    .build()
            ) {
                mapOf(
                    ContextDataMapping.AccelerometerSensor.accX.name to x,
                    ContextDataMapping.AccelerometerSensor.accY.name to y,
                    ContextDataMapping.AccelerometerSensor.accZ.name to z,
                )
            }
        }

        // 计算当前加速度的模长
        val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        // 计算加速度偏离标准重力加速度的变化量
        val delta = acceleration - SensorManager.GRAVITY_EARTH

        // 判断加速度变化是否超过阈值
        if (delta > shakeThreshold) {
            val currentTime = System.currentTimeMillis()
            // 防止连续检测到多次摇晃
            if (currentTime - lastShakeTime > 500) {
                lastShakeTime = currentTime
                onDeviceShaked()
            }
        }
    }

    private fun onDeviceShaked() {
        logger.d("onDeviceShaked")
        shortXContext.rule.publishFactChecked(
            ShakeDevice.getDefaultInstance()
        ) {
            FactPublishData(
                reason = "onDeviceShaked",
                fact = ShakeDevice.getDefaultInstance()
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}