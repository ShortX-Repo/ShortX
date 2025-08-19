package tornaco.apps.shortx.core;

interface IAudioRecordingListener {
    oneway void onStartRecording();
    oneway void onStopRecording();
}