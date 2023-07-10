package tornaco.apps.shortx.services.audio

import android.app.ActivityThread
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.media.AudioPlaybackConfiguration.PLAYER_STATE_STARTED
import android.media.MediaRecorder
import android.os.ParcelFileDescriptor
import android.os.RemoteCallbackList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import tornaco.apps.shortx.core.IAudioRecordingListener
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.AudioRecordFile
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.audio.recorder.ScreenMediaRecorder
import tornaco.apps.shortx.services.audio.recorder.ScreenRecordingAudioSource
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.enforceCallingPermissions
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files


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
    private var currentRecorder: ScreenMediaRecorder? = null
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

            logger.d("ActivityThread.currentPackageName: ${ActivityThread.currentPackageName()}")
            logger.d("ActivityThread: ${ActivityThread.currentActivityThread()}")
            requireNotNull(ActivityThread.currentPackageName()) { "ActivityThread.currentPackageName() is null." }

            val mediaRecorder = ScreenMediaRecorder(context,
                handler,
                myUserId,
                ScreenRecordingAudioSource.INTERNAL,
                object : ScreenMediaRecorder.ScreenMediaRecorderListener {
                    override fun onInfo(mr: MediaRecorder?, what: Int, extra: Int) {

                    }

                    override fun onStopped() {
                    }
                }
            )
            runCatching {
                mediaRecorder.start()
                currentRecorder = mediaRecorder
                logger.d("Recording started: $currentRecorder")
                recordListeners.broadcast {
                    runCatching { it.onStartRecording() }.onFailure {
                        logger.e(
                            it,
                            "Call onStartRecording"
                        )
                    }
                }
            }.onFailure {
                logger.e(it, "Fail start recording.")
                currentRecorder = null
            }

        }
    }

    fun stopRecording() {
        synchronized(recorderSync) {
            currentRecorder?.let {
                it.end()
                it.save().apply {
                    logger.d("Saved: ${this.uri}")
                }
            }
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

    fun getAllAudioRecordFiles(): List<AudioRecordFile> {
        return Dirs.baseServerRecordingDir()
            .walk(FileWalkDirection.TOP_DOWN)
            .filter { it.isFile }
            .map {
                AudioRecordFile.newBuilder().setName(it.nameWithoutExtension)
                    .setPath(it.absolutePath).setDate(it.lastModified())
                    .setFileSize(Files.size(it.toPath()))
                    .build()
            }.toList()
    }

    fun deleteAudioRecordFile(file: AudioRecordFile) {
        enforceCallingPermissions()
        logger.i("deleteAudioRecordFile: $file")
        File(file.path).delete()
    }

    fun exportAudioRecordFile(file: AudioRecordFile, pfd: ParcelFileDescriptor) {
        logger.i("exportAudioRecordFile: $file")
        pfd.use {
            val output = FileOutputStream(pfd.fileDescriptor)
            output.use { out ->
                File(file.path).inputStream().use {
                    it.copyTo(out)
                }
            }
        }
        logger.i("exportAudioRecordFile success")
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