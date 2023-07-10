package tornaco.apps.shortx.ui.record

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.AudioRecordFile
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.io.File
import javax.inject.Inject

data class AudioRecordState(
    val isLoading: Boolean = false,
    val records: List<AudioRecordFile> = emptyList()
)

val exportDirType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    Environment.DIRECTORY_RECORDINGS
} else {
    Environment.DIRECTORY_DOWNLOADS
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AudioRecordViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<AudioRecordState>(context, { AudioRecordState() }) {

    fun loadState() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val records = withContext(Dispatchers.IO) {
                shortXManager.getAllAudioRecordFiles()
            }
            updateState { copy(isLoading = false, records = records) }
        }
    }

    fun delete(file: AudioRecordFile) {
        shortXManager.deleteAudioRecordFile(file)
        loadState()
    }

    fun export(file: AudioRecordFile) {
        val output = File(
            Environment.getExternalStoragePublicDirectory(exportDirType),
            File(file.path).name
        ).apply { createNewFile() }
        val pfd =
            ParcelFileDescriptor.open(output, ParcelFileDescriptor.MODE_READ_WRITE)

        shortXManager.exportAudioRecordFile(file, pfd)

        runCatching {
            MediaScannerConnection.scanFile(
                context, arrayOf<String>(output.absolutePath), null
            ) { path, _ -> logger.i("MediaScanner scanned recording $path") }
        }
    }
}