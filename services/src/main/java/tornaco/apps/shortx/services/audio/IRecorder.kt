package tornaco.apps.shortx.services.audio

import tornaco.apps.shortx.services.audio.omrecorder.Recorder
import tornaco.apps.shortx.services.audio.recorder.ScreenMediaRecorder

interface IRecorder {
    fun stop()
}

class ScreenMediaRecorderWrapper(private val rec: ScreenMediaRecorder) : IRecorder {
    override fun stop() {
        // ---------------------
        // content provider: content://media/external_primary/audio/media/1000000242
        // at android.content.ContentResolver.openAssetFileDescriptor(ContentResolver.java:1901)
        // at android.content.ContentResolver.openOutputStream(ContentResolver.java:1562)
        // at tornaco.apps.shortx.services.audio.recorder.ScreenMediaRecorder.save(ScreenMediaRecorder.java:158)
        // at tornaco.apps.shortx.services.audio.AudioService$stopRecording$1.invokeSuspend(AudioService.kt:116)
        // at tornaco.apps.shortx.services.audio.AudioService$stopRecording$1.invoke(Unknown Source:8)
        // at tornaco.apps.shortx.services.audio.AudioService$stopRecording$1.invoke(Unknown Source:4)
        // at tornaco.apps.shortx.services.WatchDogKt$launchSafety$1.invokeSuspend(WatchDog.kt:49)
        // at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
        // at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
        // at android.os.Handler.handleCallback(Handler.java:942)
        // at android.os.Handler.dispatchMessage(Handler.java:99)
        // at android.os.Looper.loopOnce(Looper.java:201)
        // at android.os.Looper.loop(Looper.java:288)
        // at android.os.HandlerThread.run(HandlerThread.java:67)
        // -----------------------
        // it.save()
        rec.end()
    }
}

class OmRecorderWrapper(private val rec: Recorder) : IRecorder {
    override fun stop() {
        rec.stopRecording()
    }
}