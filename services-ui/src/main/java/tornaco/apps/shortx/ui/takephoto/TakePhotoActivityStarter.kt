package tornaco.apps.shortx.ui.takephoto

import android.content.Context
import android.content.Intent
import tornaco.apps.shortx.core.BuildPropInternal

object TakePhotoActivityStarter {
    const val EXTRA_REQUEST_CODE = "extra.request.code"
    const val EXTRA_TARGET_FILE_PATH = "extra.target.file.path"
    const val RESULT_FILE_PATH = "result.file.path"
    const val RESULT_FILE_URI = "result.file.uri"

    @JvmStatic
    fun start(context: Context, requestCode: Int, targetFilePath: String) {
        val starter = Intent("")
            .setPackage(BuildPropInternal.APP_PACKAGE_NAME)
            .setClassName(
                BuildPropInternal.APP_PACKAGE_NAME,
                "tornaco.apps.shortx.ui.takephoto.TakePhotoActivity"
            )
            .putExtra(EXTRA_REQUEST_CODE, requestCode)
            .putExtra(EXTRA_TARGET_FILE_PATH, targetFilePath)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(starter)
    }
}
