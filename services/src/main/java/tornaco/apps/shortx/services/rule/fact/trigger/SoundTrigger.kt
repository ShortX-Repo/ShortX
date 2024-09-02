package tornaco.apps.shortx.services.rule.fact.trigger

import android.content.Context
import android.media.MediaRecorder
import tornaco.apps.shortx.core.ShortXManager.Companion.Sensors.SENSOR_TYPE_SOUND
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.Sound
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData
import kotlin.math.log10

class SoundTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("SoundTrigger")

    private var mediaRecorder: MediaRecorder? = null

    // 定义声音检测的时间间隔
    private val soundCheckInterval: Long = 1000
    private val soundCheckHandler by lazy { newHandlerOfNewThread("ShortX-Sound") }

    override fun onAnyChanged() {
        super.onAnyChanged()
        enableOrDisableSensor()
    }

    override fun register() {
        enableOrDisableSensor()
    }

    override fun unRegister() {
        logger.d("Unregister sound listener")
        mediaRecorder?.apply {
            stop()
            reset()
            release()
        }
        mediaRecorder = null
        soundCheckHandler.removeCallbacksAndMessages(null)
        shortXContext.watchDog.removeSensorUsage(SENSOR_TYPE_SOUND)
    }

    private fun enableOrDisableSensor() {
        val sensorSrc = mutableListOf<String>()
        shortXContext.rule.forEachEnabledFacts { src, factProtoAny ->
            if (factProtoAny is_ Sound::class.java) {
                val fact = factProtoAny unpack_ Sound::class.java
                if (!fact.isDisabled) {
                    sensorSrc.add(src)
                }
            }
        }
        logger.d("sensorSrc: $sensorSrc")

        if (sensorSrc.isNotEmpty()) {
            logger.d("Register sound sensor")

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile("/dev/null") // 输出文件设为空，数据仅用于获取分贝
                prepare()
                start()
            }

            shortXContext.watchDog.onSensorUsage(SENSOR_TYPE_SOUND, sensorSrc)

            soundCheckHandler.postDelayed(object : Runnable {
                override fun run() {
                    mediaRecorder?.let {
                        val maxAmplitude = it.maxAmplitude // 获取最大振幅
                        val decibels: Double = 20 * log10(maxAmplitude.toDouble()) // 转换为分贝
                        logger.d("Current sound level: $decibels dB")

                        shortXContext.rule.publishFactChecked(
                            Sound.getDefaultInstance()
                        ) {
                            FactPublishData(
                                reason = "Sound",
                                fact = Sound.newBuilder().setValue(decibels.toFloat())
                                    .build(),
                                initialContextDataLazy = {
                                    mapOf(
                                        ContextDataMapping.Sound.decibels.name to decibels
                                    )
                                }
                            )
                        }
                    }
                    soundCheckHandler.postDelayed(this, soundCheckInterval)
                }
            }, soundCheckInterval)

        } else {
            unRegister()
        }
    }
}