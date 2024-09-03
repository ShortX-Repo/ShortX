package tornaco.apps.shortx.services.rule.fact.trigger

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import tornaco.apps.shortx.core.ShortXManager.Companion.Sensors.SENSOR_TYPE_SOUND
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.fact.Sound
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.FactPublishData
import tornaco.apps.shortx.services.watchdog.launchSafety
import kotlin.math.log10
import kotlin.math.sqrt

private const val SAMPLE_RATE = 1000L

class SoundTrigger(
    override val context: Context,
    override val shortXContext: ShortXContext
) :
    Trigger {
    private val logger = Logger("SoundTrigger")

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val bufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private val scope = CoroutineScope(SupervisorJob())

    override fun onAnyChanged() {
        super.onAnyChanged()
        enableOrDisableSensor()
    }

    override fun register() {
        enableOrDisableSensor()
    }

    override fun unRegister() {
        logger.d("Unregister sound listener")
        stopListening()
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
            if (isRecording) {
                logger.w("Already recording. won't start again.")
                return
            }
            logger.d("Register sound sensor")
            shortXContext.watchDog.onSensorUsage(SENSOR_TYPE_SOUND, sensorSrc)
            startListening()
        } else {
            unRegister()
        }
    }

    @SuppressLint("MissingPermission")
    @Synchronized
    private fun startListening() {
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord?.startRecording()
        isRecording = true

        val buffer = ShortArray(bufferSize)

        scope.launchSafety {
            while (isRecording && isActive) {
                val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (readSize > 0) {
                    // 计算 RMS 值
                    val rms = calculateRMS(buffer, readSize)
                    // 将 RMS 转化为分贝值
                    val decibels = (20 * log10(rms)).toInt()
                    logger.d("Decibels: $decibels dB")

                    shortXContext.rule.publishFactChecked(
                        Sound.getDefaultInstance()
                    ) {
                        FactPublishData(
                            reason = "Sound",
                            fact = Sound.newBuilder().setValue(decibels.toFloat()).build(),
                            initialContextDataLazy = {
                                mapOf(
                                    ContextDataMapping.Sound.decibels.name to decibels
                                )
                            }
                        )
                    }

                    delay(SAMPLE_RATE)
                }
            }
        }
    }

    private fun calculateRMS(buffer: ShortArray, readSize: Int): Double {
        var sum = 0.0
        for (i in 0 until readSize) {
            sum += buffer[i] * buffer[i]
        }
        return sqrt(sum / readSize)
    }

    @Synchronized
    private fun stopListening() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        shortXContext.watchDog.removeSensorUsage(SENSOR_TYPE_SOUND)
    }
}