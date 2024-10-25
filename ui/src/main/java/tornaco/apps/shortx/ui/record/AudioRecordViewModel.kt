package tornaco.apps.shortx.ui.record

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.AudioRecordFile
import tornaco.apps.shortx.core.util.FileHelper
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import javax.inject.Inject

data class AudioRecordState(
    val isLoading: Boolean = false,
    val records: List<AudioRecordFile> = emptyList()
)

sealed interface ExportResult {
    data class Success(val path: String) : ExportResult
    data class Failed(val error: String) : ExportResult
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AudioRecordViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<AudioRecordState>(context, { AudioRecordState() }) {
    private val _exportPerformed = MutableSharedFlow<ExportResult>()
    val exportPerformed = _exportPerformed.asSharedFlow()

    private val appRecordFileDir = File(context.filesDir, "records").apply {
        mkdirs()
    }

    fun loadState() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val records = withContext(Dispatchers.IO) {
                shortXManager.getAllAudioRecordFiles().forEach { exportToShortXApp(it) }
                val files = appRecordFileDir
                    .walk(FileWalkDirection.TOP_DOWN)
                    .filter { it.isFile }
                    .map {
                        AudioRecordFile.newBuilder()
                            .setName(it.name)
                            .setDuration(getAudioDuration(it))
                            .setPath(it.absolutePath)
                            .setDate(it.lastModified())
                            .setFileSize(Files.size(it.toPath()))
                            .build()
                    }.filterNotNull()
                    .sortedByDescending { it.date }
                    .toList()
                files
            }
            updateState { copy(isLoading = false, records = records) }
        }
    }

    fun delete(file: AudioRecordFile) {
        shortXManager.deleteAudioRecordFile(file)
        File(appRecordFileDir, file.name).delete()
        loadState()
    }

    fun rename(file: AudioRecordFile, newName: String) {
        shortXManager.renameAudioRecordFile(file, newName)
        File(appRecordFileDir, file.name).renameTo(File(appRecordFileDir, newName))
        loadState()
    }

    fun export(file: AudioRecordFile) {
        viewModelScope.launch {
            runCatching {
                val tmpFile = File(
                    context.cacheDir,
                    "ShortX-Audio-Record-${System.currentTimeMillis()}"
                ).apply { createNewFile() }
                val pfd =
                    ParcelFileDescriptor.open(tmpFile, ParcelFileDescriptor.MODE_READ_WRITE)

                shortXManager.exportAudioRecordFile(file, pfd)

                val resolver = context.contentResolver
                // Find all audio files on the primary external storage device.
                // On API <= 28, use VOLUME_EXTERNAL instead.
                val audioCollection = MediaStore.Audio.Media
                    .getContentUri(
                        if (OsUtils.isQOrAbove()) {
                            MediaStore.VOLUME_EXTERNAL_PRIMARY
                        } else {
                            MediaStore.VOLUME_EXTERNAL
                        }
                    )

                val audioDetails = ContentValues().apply {
                    put(MediaStore.Audio.Media.MIME_TYPE, "audio/aac")
                    put(MediaStore.Audio.Media.DISPLAY_NAME, file.name)
                    put(MediaStore.Audio.Media.IS_PENDING, 1)
                }

                val contentUri = requireNotNull(
                    resolver.insert(
                        audioCollection,
                        audioDetails
                    )
                ) { "Fail insert songContentUri" }

                resolver.openFileDescriptor(contentUri, "w", null).use { mediaPFD ->
                    // Write data into the pending audio file.
                    val output = FileOutputStream(mediaPFD!!.fileDescriptor)
                    output.use { out ->
                        tmpFile.inputStream().use {
                            it.copyTo(out)
                        }
                    }
                }

                // Now that we're finished, release the "pending" status, and allow other apps
                // to play the audio track.
                audioDetails.clear()
                audioDetails.put(MediaStore.Audio.Media.IS_PENDING, 0)
                resolver.update(contentUri, audioDetails, null, null)

                tmpFile.delete()
                val path = FileHelper.getRealPathFromURI(context, contentUri)
                _exportPerformed.emit(ExportResult.Success(path))
            }.getOrElse {
                logger.e(it, "export")
                _exportPerformed.emit(ExportResult.Failed(it.message ?: "UNKNOWN ERR"))
            }
        }
    }

    private fun exportToShortXApp(file: AudioRecordFile) {
        runCatching {
            val appRecordFile = File(
                appRecordFileDir,
                file.name
            )
            if (appRecordFile.exists()) {
                return@runCatching
            } else {
                appRecordFile.createNewFile()
            }
            logger.d("Pull audio file: $appRecordFile")
            val pfd =
                ParcelFileDescriptor.open(appRecordFile, ParcelFileDescriptor.MODE_READ_WRITE)
            shortXManager.exportAudioRecordFile(file, pfd)
            pfd.close()
        }.getOrElse {
            logger.e(it, "exportToShortXApp")
        }
    }

    private fun getAudioDuration(file: File): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return durationStr?.toLong() ?: 0L
    }
}