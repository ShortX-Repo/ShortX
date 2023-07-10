/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tornaco.apps.shortx.services.audio.recorder;

import static android.content.Context.MEDIA_PROJECTION_SERVICE;
import static tornaco.apps.shortx.services.audio.recorder.ScreenRecordingAudioSource.INTERNAL;
import static tornaco.apps.shortx.services.audio.recorder.ScreenRecordingAudioSource.MIC_AND_INTERNAL;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.MediaStore;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import tornaco.apps.shortx.services.data.Dirs;

/**
 * Recording screen and mic/internal audio
 */
public class ScreenMediaRecorder extends MediaProjection.Callback {
    private static final String TAG = "ScreenMediaRecorder";

    private File mTempAudioFile;
    private MediaProjection mMediaProjection;
    private int mUser;
    private ScreenInternalAudioRecorder mAudio;
    private ScreenRecordingAudioSource mAudioSource;
    private final Handler mHandler;

    private Context mContext;
    ScreenMediaRecorderListener mListener;

    public ScreenMediaRecorder(Context context, Handler handler,
                               int user, ScreenRecordingAudioSource audioSource,
                               ScreenMediaRecorderListener listener) {
        mContext = context;
        mHandler = handler;
        mUser = user;
        mListener = listener;
        mAudioSource = audioSource;
    }

    private void prepare(String fileName) throws IOException, RemoteException, RuntimeException {
        //Setup media projection
        IBinder b = ServiceManager.getService(MEDIA_PROJECTION_SERVICE);
        IMediaProjectionManager mediaService =
                IMediaProjectionManager.Stub.asInterface(b);
        IMediaProjection proj = null;
        proj = mediaService.createProjection(mUser, mContext.getPackageName(),
                MediaProjectionManager.TYPE_SCREEN_CAPTURE, false);
        IMediaProjection projection = IMediaProjection.Stub.asInterface(proj.asBinder());
        mMediaProjection = new MediaProjection(mContext, projection);
        mMediaProjection.registerCallback(this, mHandler);

        if (mAudioSource == INTERNAL ||
                mAudioSource == MIC_AND_INTERNAL) {
            mTempAudioFile = new File(Dirs.INSTANCE.baseServerRecordingDir(), fileName);
            mAudio = new ScreenInternalAudioRecorder(mTempAudioFile.getAbsolutePath(),
                    mMediaProjection, mAudioSource == MIC_AND_INTERNAL);
        }

    }

    /**
     * Start screen recording
     */
    public void start(String fileName) throws IOException, RemoteException, RuntimeException {
        Log.d(TAG, "start recording");
        prepare(fileName);
        recordInternalAudio();
    }

    /**
     * End screen recording, throws an exception if stopping recording failed
     */
    public void end() throws IOException {
        Closer closer = new Closer();

        // MediaRecorder might throw RuntimeException if stopped immediately after starting
        // We should remove the recording in this case as it will be invalid
        closer.register(mMediaProjection::stop);
        closer.register(this::stopInternalAudioRecording);

        closer.close();

        mMediaProjection = null;

        Log.d(TAG, "end recording");
    }

    @Override
    public void onStop() {
        Log.d(TAG, "The system notified about stopping the projection");
        mListener.onStopped();
    }

    private void stopInternalAudioRecording() {
        if (mAudioSource == INTERNAL || mAudioSource == MIC_AND_INTERNAL) {
            mAudio.end();
            mAudio = null;
        }
    }

    private void recordInternalAudio() throws IllegalStateException {
        if (mAudioSource == INTERNAL || mAudioSource == MIC_AND_INTERNAL) {
            mAudio.start();
        }
    }

    /**
     * Store recorded audio
     */
    public SavedRecording save() throws IOException {
        String fileName = mTempAudioFile.getName();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/aac");
        values.put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Audio.Media.DATE_TAKEN, System.currentTimeMillis());

        ContentResolver resolver = mContext.getContentResolver();
        Uri collectionUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri itemUri = resolver.insert(collectionUri, values);

        Log.d(TAG, itemUri.toString());

        OutputStream os = resolver.openOutputStream(itemUri, "w");
        Files.copy(mTempAudioFile.toPath(), os);
        os.close();
        if (mTempAudioFile != null) mTempAudioFile.delete();
        return new SavedRecording(itemUri);
    }

    /**
     * Release the resources without saving the data
     */
    protected void release() {
        if (mTempAudioFile != null) {
            mTempAudioFile.delete();
        }
    }

    /**
     * Object representing the recording
     */
    public static class SavedRecording {

        private final Uri mUri;

        protected SavedRecording(Uri uri) {
            mUri = uri;
        }

        public Uri getUri() {
            return mUri;
        }
    }

    public interface ScreenMediaRecorderListener {
        /**
         * Called when the recording stopped by the system.
         * For example, this might happen when doing partial screen sharing of an app
         * and the app that is being captured is closed.
         */
        void onStopped();
    }

    /**
     * Allows to register multiple {@link Closeable} objects and close them all by calling
     * {@link Closer#close}. If there is an exception thrown during closing of one
     * of the registered closeables it will continue trying closing the rest closeables.
     * If there are one or more exceptions thrown they will be re-thrown at the end.
     * In case of multiple exceptions only the first one will be thrown and all the rest
     * will be printed.
     */
    private static class Closer implements Closeable {
        private final List<Closeable> mCloseables = new ArrayList<>();

        void register(Closeable closeable) {
            mCloseables.add(closeable);
        }

        @Override
        public void close() throws IOException {
            Throwable throwable = null;

            for (int i = 0; i < mCloseables.size(); i++) {
                Closeable closeable = mCloseables.get(i);

                try {
                    closeable.close();
                } catch (Throwable e) {
                    if (throwable == null) {
                        throwable = e;
                    } else {
                        e.printStackTrace();
                    }
                }
            }

            if (throwable != null) {
                if (throwable instanceof IOException) {
                    throw (IOException) throwable;
                }

                if (throwable instanceof RuntimeException) {
                    throw (RuntimeException) throwable;
                }

                throw (Error) throwable;
            }
        }
    }
}
