package tornaco.apps.shortx.services.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.media.AudioPlaybackConfiguration.PLAYER_STATE_STARTED
import android.media.MediaRecorder
import android.os.RemoteCallbackList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import tornaco.apps.shortx.core.IAudioRecordingListener
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.audio.omrecorder.AudioRecordConfig
import tornaco.apps.shortx.services.audio.omrecorder.OmRecorder
import tornaco.apps.shortx.services.audio.omrecorder.PullTransport
import tornaco.apps.shortx.services.audio.omrecorder.PullableSource
import tornaco.apps.shortx.services.audio.omrecorder.Recorder
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope
import java.io.File


sealed class FocusEvent(val target: AppPkg) {
    class Gain(target: AppPkg) : FocusEvent(target)
    class Lost(target: AppPkg) : FocusEvent(target)
}

class AudioService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val currentPlayingAppPkgs = mutableListOf<AppPkg>()

    private val _focusEvent = MutableSharedFlow<FocusEvent>()
    val focusEvent = _focusEvent.asSharedFlow()

    fun AppPkg.hasFocus() = currentPlayingAppPkgs.contains(this)

    private val recorderSync = "SYNC.RECORDER"
    private var currentRecorder: Recorder? = null
    private val recordListeners = RemoteCallbackList<IAudioRecordingListener>()

    val isRecording: Boolean get() = synchronized(recorderSync) { currentRecorder != null }

    override fun systemReady() {
        super.systemReady()
        registerListener()
    }

    fun startRecording(
        source: Int = MediaRecorder.AudioSource.REMOTE_SUBMIX,
        outputFile: File
    ) {
        synchronized(recorderSync) {
            require(currentRecorder == null) {
                "Already recording!"
            }

            val src = PullableSource.Default(
                AudioRecordConfig.Default(
                    source, AudioFormat.ENCODING_PCM_16BIT,
                    AudioFormat.CHANNEL_IN_MONO, 44100
                )
            )

            val checkedFile = if (outputFile.exists()) {
                File(outputFile.absolutePath + "-2")
            } else {
                outputFile
            }
            logger.p("startRecording, checkedFile: $checkedFile")

            val recorder = OmRecorder.wav(
                PullTransport.Default(src),
                checkedFile
            )
            recorder.startRecording()

            currentRecorder = recorder

            recordListeners.broadcast {
                runCatching { it.onStartRecording() }.onFailure {
                    logger.e(
                        it,
                        "Call onStartRecording"
                    )
                }
            }
        }
    }

    fun stopRecording() {
        synchronized(recorderSync) {
            currentRecorder?.stopRecording()
            currentRecorder = null

            recordListeners.broadcast {
                runCatching { it.onStopRecording() }.onFailure {
                    logger.e(
                        it,
                        "Call onStopRecording"
                    )
                }
            }
        }
    }

    fun registerAudioRecordingObs(obs: IAudioRecordingListener) {
        recordListeners.register(obs)
    }

    fun unregisterAudioRecordingObs(obs: IAudioRecordingListener) {
        recordListeners.unregister(obs)
    }

    private fun registerListener() {
        if (OsUtils.isOOrAbove()) {
            kotlin.runCatching {
                context.audioManager.registerAudioPlaybackCallback(object :
                    AudioManager.AudioPlaybackCallback() {
                    override fun onPlaybackConfigChanged(configs: List<AudioPlaybackConfiguration>?) {
                        super.onPlaybackConfigChanged(configs)
                        configs?.let { handlePlaybackConfigChanged(it) }
                    }
                }, handler)
            }.onFailure {
                shortXContext.watchDog.onFatalError(it, "registerAudioPlaybackCallback")
            }
        }
    }

    private fun handlePlaybackConfigChanged(configs: List<AudioPlaybackConfiguration>) =
        kotlin.runCatching {
            configs.forEach {
                logger.d(
                    "Config-> Uid: ${it.clientUid} - isActive: ${it.isActive} - playerType: ${it.playerType} - playerState: ${
                        playerStateToString(
                            it.playerState
                        )
                    } - audioAttributes: ${it.audioAttributes}"
                )
            }

            val appToPlayerStates = configs.mapNotNull { configuration ->
                val uid = configuration.clientUid
                val appOrNull = shortXContext.appState.uidToApp(uid)
                appOrNull?.let { it to configuration.playerState }
            }
            logger.d(
                "appToPlayerStates: ${
                    appToPlayerStates.map {
                        "${it.first.label} - ${
                            playerStateToString(
                                it.second
                            )
                        }"
                    }
                }"
            )

            val playingApps = appToPlayerStates.filter { it.second == PLAYER_STATE_STARTED }
            logger.d("playingApps: ${playingApps.map { it.first.label }}")

            val playingPkgs = playingApps.map { it.first.pkg }.distinct()

            val gainPkgs = playingPkgs.subtract(currentPlayingAppPkgs)
            val losingPkgs = currentPlayingAppPkgs.subtract(playingPkgs)

            logger.d("gainPkgs: $gainPkgs")
            logger.d("losingPkgs: $losingPkgs")

            systemServiceScope.launchSafety {
                gainPkgs.forEach { _focusEvent.emit(FocusEvent.Gain(it)) }
            }
            systemServiceScope.launchSafety {
                losingPkgs.forEach { _focusEvent.emit(FocusEvent.Lost(it)) }
            }

            currentPlayingAppPkgs.clear()
            currentPlayingAppPkgs.addAll(playingPkgs)
        }.onFailure {
            shortXContext.watchDog.onFatalError(it, "handlePlaybackConfigChanged")
        }
}

/** @hide
 */
private fun playerStateToString(@AudioPlaybackConfiguration.PlayerState state: Int): String {
    return when (state) {
        AudioPlaybackConfiguration.PLAYER_STATE_UNKNOWN -> "PLAYER_STATE_UNKNOWN"
        AudioPlaybackConfiguration.PLAYER_STATE_RELEASED -> "PLAYER_STATE_RELEASED"
        AudioPlaybackConfiguration.PLAYER_STATE_IDLE -> "PLAYER_STATE_IDLE"
        PLAYER_STATE_STARTED -> "PLAYER_STATE_STARTED"
        AudioPlaybackConfiguration.PLAYER_STATE_PAUSED -> "PLAYER_STATE_PAUSED"
        AudioPlaybackConfiguration.PLAYER_STATE_STOPPED -> "PLAYER_STATE_STOPPED"
        AudioPlaybackConfiguration.PLAYER_UPDATE_DEVICE_ID -> "PLAYER_UPDATE_DEVICE_ID"
        else -> "invalid state $state"
    }
}