package tornaco.apps.shortx.services.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.media.AudioPlaybackConfiguration.PLAYER_STATE_STARTED
import android.media.MediaRecorder
import android.media.RingtoneManager
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.os.RemoteCallbackList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.IAudioRecordingListener
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.AudioRecordFile
import tornaco.apps.shortx.core.proto.common.AudioSource
import tornaco.apps.shortx.core.proto.common.Ringtone
import tornaco.apps.shortx.core.proto.nameAndExt
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.audio.omrecorder.AudioRecordConfig
import tornaco.apps.shortx.services.audio.omrecorder.OmRecorder
import tornaco.apps.shortx.services.audio.omrecorder.PullTransport
import tornaco.apps.shortx.services.audio.omrecorder.PullableSource
import tornaco.apps.shortx.services.audio.recorder.ScreenMediaRecorder
import tornaco.apps.shortx.services.audio.recorder.ScreenRecordingAudioSource
import tornaco.apps.shortx.services.data.Dirs.baseServerRecordingDir
import tornaco.apps.shortx.services.enforceCallingPermissions
import tornaco.apps.shortx.services.runClearCallingIdentity
import tornaco.apps.shortx.services.systemServiceScope
import tornaco.apps.shortx.services.watchdog.launchSafety
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files


sealed class FocusEvent(val target: AppPkg) {
    class Gain(target: AppPkg) : FocusEvent(target)
    class Lost(target: AppPkg) : FocusEvent(target)
}

class AudioService(shortXContext: ShortXContext) : SystemService(shortXContext, "AudioService") {
    private val currentPlayingAppPkgs = mutableListOf<AppPkg>()

    private val _focusEvent = MutableSharedFlow<FocusEvent>()
    val focusEvent = _focusEvent.asSharedFlow()

    fun AppPkg.hasFocus() = currentPlayingAppPkgs.contains(this)

    private val recorderSync = "SYNC.RECORDER"

    private var currentRecorder: IRecorder? = null

    private val recordListeners = RemoteCallbackList<IAudioRecordingListener>()

    val isRecording: Boolean get() = synchronized(recorderSync) { currentRecorder != null }

    private val playingRingtones = mutableListOf<android.media.Ringtone>()

    override fun systemReady() {
        super.systemReady()
        registerListener()
    }

    fun getStreamMinVolume(type: Int): Int = runClearCallingIdentity {
        if (OsUtils.isPOrAbove()) {
            context.audioManager.getStreamMinVolume(type)
        } else 0
    } ?: 0

    fun getStreamMaxVolume(type: Int): Int = runClearCallingIdentity {
        context.audioManager.getStreamMaxVolume(type)
    } ?: 0

    fun getStreamVolume(type: Int): Int = runClearCallingIdentity {
        context.audioManager.getStreamVolume(type)
    } ?: 0

    fun startRecording(
        src: AudioSource,
        fileNamePrefix: String
    ) {
        synchronized(recorderSync) {
            require(currentRecorder == null) {
                "Already recording!"
            }
            if (src == AudioSource.Mic) {
                startRecordingMic(fileNamePrefix)
            } else {
                startRecordingInternalOrBoth(src, fileNamePrefix)
            }
        }
    }

    private fun startRecordingMic(fileNamePrefix: String) {
        val ps = PullableSource.Default(
            AudioRecordConfig.Default(
                MediaRecorder.AudioSource.MIC,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO, 44100
            )
        )
        val fileName =
            "${fileNamePrefix.fallbackOnEmpty("")}-${DateUtils.formatForFileName(System.currentTimeMillis())}.aac"
        val outputFile = File(baseServerRecordingDir(), fileName)

        val checkedFile = if (outputFile.exists()) {
            File(outputFile.absolutePath + "-2")
        } else {
            outputFile
        }
        checkedFile.parentFile.mkdirs()
        logger.logOnDebug("startRecordingMic, checkedFile: $checkedFile")

        val recorder = OmRecorder.wav(
            PullTransport.Default(ps),
            checkedFile
        )
        recorder.startRecording()
        currentRecorder = OmRecorderWrapper(recorder)
    }

    private fun startRecordingInternalOrBoth(src: AudioSource, fileNamePrefix: String) {
        val source = when (src) {
            AudioSource.Mic -> {
                ScreenRecordingAudioSource.MIC
            }

            AudioSource.Internal -> {
                ScreenRecordingAudioSource.INTERNAL
            }

            AudioSource.MicAndInternal -> {
                ScreenRecordingAudioSource.MIC_AND_INTERNAL
            }

            else -> {
                throw IllegalArgumentException("Unsupported source: $src")
            }
        }

        val mediaRecorder = ScreenMediaRecorder(
            context,
            handler,
            myUserId,
            source
        ) {
            logger.i("ScreenMediaRecorder stop.")
        }
        runCatching {
            val fileName =
                "${fileNamePrefix.fallbackOnEmpty("")}-${DateUtils.formatForFileName(System.currentTimeMillis())}.aac"
            mediaRecorder.start(fileName)
            currentRecorder = ScreenMediaRecorderWrapper(mediaRecorder)
            logger.d("Recording started: $currentRecorder, fileName: $fileName")
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

    fun stopRecording() = systemServiceScope.launchSafety("stopRecording") {
        synchronized(recorderSync) {
            currentRecorder?.let {
                runCatching {
                    it.stop()
                }.onFailure {
                    logger.e(it, "stopRecording")
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
        return baseServerRecordingDir()
            .walk(FileWalkDirection.TOP_DOWN)
            .filter { it.isFile }
            .map { file ->
                AudioRecordFile.newBuilder()
                    .setName(file.nameWithoutExtension)
                    .setExt(file.extension)
                    .setPath(file.absolutePath)
                    .setDate(file.lastModified())
                    .setFileSize(Files.size(file.toPath()))
                    .build()
            }.toList()
    }

    fun deleteAudioRecordFile(file: AudioRecordFile) {
        enforceCallingPermissions()
        logger.i("deleteAudioRecordFile: $file")
        val fileToDel = File(baseServerRecordingDir(), file.nameAndExt)
        if (!fileToDel.delete()) {
            logger.w("Unable to delete file: $fileToDel")
        }
    }

    fun renameAudioRecordFile(file: AudioRecordFile, name: String) {
        enforceCallingPermissions()
        logger.i("renameAudioRecordFile: $file $name")
        val fileToRen = File(baseServerRecordingDir(), file.nameAndExt)
        val fileNew = File(baseServerRecordingDir(), name + "." + file.ext)
        if (!fileToRen.renameTo(fileNew)) {
            logger.w("Unable to rename file: $fileToRen $fileNew")
        }
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

    // java.util.ConcurrentModificationException
    //	at java.util.LinkedHashMap$LinkedHashIterator.nextNode(LinkedHashMap.java:760)
    //	at java.util.LinkedHashMap$LinkedEntryIterator.next(LinkedHashMap.java:793)
    //	at java.util.LinkedHashMap$LinkedEntryIterator.next(LinkedHashMap.java:791)
    //	at java.util.HashMap.putMapEntries(HashMap.java:511)
    //	at java.util.LinkedHashMap.<init>(LinkedHashMap.java:408)
    //	at oa.b0.y4(Unknown Source:7)
    //	at oa.b0.v4(Unknown Source:14)
    //	at ih.f0.a(Unknown Source:2)
    //	at ih.y.E(Unknown Source:41)
    //	at kh.h.l(Unknown Source:131)
    //	at kh.f.onPlaybackConfigChanged(Unknown Source:7)
    //	at android.media.AudioManager$ServiceEventHandlerDelegate$1.handleMessage(AudioManager.java:3811)
    //	at android.os.Handler.dispatchMessage(Handler.java:106)
    //	at android.os.Looper.loopOnce(Looper.java:226)
    //	at android.os.Looper.loop(Looper.java:313)
    //	at android.os.HandlerThread.run(HandlerThread.java:67)
    @Synchronized
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

            systemServiceScope.launchSafety("FocusEvent1") {
                gainPkgs.forEach { _focusEvent.emit(FocusEvent.Gain(it)) }
            }
            systemServiceScope.launchSafety("FocusEvent2") {
                losingPkgs.forEach { _focusEvent.emit(FocusEvent.Lost(it)) }
            }

            currentPlayingAppPkgs.clear()
            currentPlayingAppPkgs.addAll(playingPkgs)
        }.onFailure {
            shortXContext.watchDog.onFatalError(it, "handlePlaybackConfigChanged")
        }


    // java.lang.SecurityException: Given calling package android does not match caller's uid 10162
    // at android.os.Parcel.createException(Parcel.java:2072)
    // at android.os.Parcel.readException(Parcel.java:2040)
    // at android.os.Parcel.readException(Parcel.java:1988)
    // at tornaco.apps.shortx.core.IShortX$Stub$Proxy.getAllRingtones(IShortX.java:6515)
    // at tornaco.apps.shortx.core.ShortXManager.getAllRingtones(ShortXManager.kt:1194)
    // at tornaco.apps.shortx.ui.ringtone.RingtoneListViewModel$loadState$1.invokeSuspend(RingtonePickerViewModel.kt:30)
    // at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
    // at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
    // at kotlinx.coroutines.internal.LimitedDispatcher$Worker.run(LimitedDispatcher.kt:115)
    // at kotlinx.coroutines.scheduling.TaskImpl.run(Tasks.kt:103)
    // at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:584)
    // at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:793)
    // at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:697)
    // at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:684)
    //     Caused by: android.os.RemoteException: Remote stack trace:
    // at com.android.server.am.ActivityManagerService.getContentProvider(ActivityManagerService.java:7311)
    // at android.app.ActivityThread.acquireProvider(ActivityThread.java:6566)
    // at android.app.ContextImpl$ApplicationContentResolver.acquireUnstableProvider(ContextImpl.java:2725)
    // at android.content.ContentResolver.acquireUnstableProvider(ContentResolver.java:2120)
    // at android.content.ContentResolver.query(ContentResolver.java:930)
    fun getAllRingtones(): List<Ringtone> {
        val res = mutableListOf<Ringtone>()
        runBlocking {
            systemServiceScope.launchSafety("getAllRingtones") {
                val all = RingtoneLoader(context).load(RingtoneManager.TYPE_ALARM) + RingtoneLoader(
                    context
                ).load(
                    RingtoneManager.TYPE_NOTIFICATION
                ) + RingtoneLoader(context).load(RingtoneManager.TYPE_RINGTONE)
                res.addAll(all)
            }.join()
        }
        return res
    }

    fun playRingtone(ringtone: Ringtone) {
        synchronized(playingRingtones) {
            playingRingtones.removeAll {
                it.stop()
                true
            }

            val androidRingtone = RingtoneManager.getRingtone(context, Uri.parse(ringtone.uri))
            requireNotNull(androidRingtone) {
                "Ringtone not exists: ${ringtone.title}"
            }
            androidRingtone.play()
            playingRingtones.add(androidRingtone)
        }
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