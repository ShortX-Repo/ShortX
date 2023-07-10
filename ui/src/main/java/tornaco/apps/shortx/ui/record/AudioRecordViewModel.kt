package tornaco.apps.shortx.ui.record

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
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
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.io.File
import java.io.FileOutputStream
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
    ContextViewModel<AudioRecordState>(context, { AudioRecordState() }) {
    private val _exportPerformed = MutableSharedFlow<ExportResult>()
    val exportPerformed = _exportPerformed.asSharedFlow()

    fun loadState() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val records = withContext(Dispatchers.IO) {
                shortXManager.getAllAudioRecordFiles().sortedByDescending { it.date }
            }
            updateState { copy(isLoading = false, records = records) }
        }
    }

    fun delete(file: AudioRecordFile) {
        shortXManager.deleteAudioRecordFile(file)
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
}