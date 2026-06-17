package tornaco.apps.shortx.ui.takephoto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.BaseActivity
import tornaco.apps.shortx.ui.takephoto.TakePhotoActivityStarter.EXTRA_REQUEST_CODE
import tornaco.apps.shortx.ui.takephoto.TakePhotoActivityStarter.EXTRA_TARGET_FILE_PATH
import tornaco.apps.shortx.ui.takephoto.TakePhotoActivityStarter.RESULT_FILE_PATH
import tornaco.apps.shortx.ui.takephoto.TakePhotoActivityStarter.RESULT_FILE_URI
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TakePhotoActivity : BaseActivity() {
    private val logger = Logger("TakePhotoActivity")

    private var requestCode = 0
    private var targetFilePath = ""
    private var photoUri: Uri? = null
    private var photoFile: File? = null

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        resolveIntent()
        super.onCreate(savedInstanceState)

        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    onPhotoTaken()
                } else {
                    onPhotoCancelled()
                }
            }

        launchCamera()
    }

    @Composable
    override fun Content() {
    }

    private fun resolveIntent() {
        requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, Int.MIN_VALUE)
        require(requestCode >= 0) { "Bad requestCode: $requestCode" }
        targetFilePath = intent.getStringExtra(EXTRA_TARGET_FILE_PATH).orEmpty()
    }

    private fun launchCamera() {
        val file = createPhotoFile()
        photoFile = file
        photoUri = FileProvider.getUriForFile(this, applicationContext.packageName, file)
        logger.i("Launching camera, photoUri=$photoUri, file=${file.absolutePath}")
        takePictureLauncher.launch(photoUri!!)
    }

    private fun createPhotoFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: filesDir
        dir.mkdirs()
        return File(dir, "ShortX_Photo_$timestamp.jpg")
    }

    private fun onPhotoTaken() {
        val file = photoFile
        if (file == null || !file.exists()) {
            logger.e("Photo file does not exist")
            sendResultAndFinish("", "")
            return
        }

        val finalFile = if (targetFilePath.isNotEmpty()) {
            val dest = File(targetFilePath)
            dest.parentFile?.mkdirs()
            file.copyTo(dest, overwrite = true)
            file.delete()
            dest
        } else {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val shortxDir = File(picturesDir, "ShortX")
            shortxDir.mkdirs()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val dest = File(shortxDir, "Photo_$timestamp.jpg")
            file.copyTo(dest, overwrite = true)
            file.delete()
            dest
        }

        logger.i("Photo saved to: ${finalFile.absolutePath}")
        sendResultAndFinish(finalFile.absolutePath, photoUri?.toString().orEmpty())
    }

    private fun onPhotoCancelled() {
        logger.w("Photo cancelled by user")
        photoFile?.delete()
        sendResultAndFinish("", "")
    }

    private fun sendResultAndFinish(filePath: String, fileUri: String) {
        shortXManager.setStartActivityResult(
            requestCode,
            if (filePath.isNotEmpty()) Activity.RESULT_OK else Activity.RESULT_CANCELED,
            bundleOf(
                RESULT_FILE_PATH to filePath,
                RESULT_FILE_URI to fileUri
            )
        )
        setResult(if (filePath.isNotEmpty()) RESULT_OK else RESULT_CANCELED)
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        onPhotoCancelled()
    }
}
