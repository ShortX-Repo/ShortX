package tornaco.apps.shortx.services.rule.actor.impl

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Environment
import android.widget.Toast
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.os.ScreenCap
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.SelectScreenArea.startSelectScreenArea
import tornaco.apps.shortx.services.util.MediaScannerUtil
import java.io.File

object AreaScreenshot : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.AreaScreenshot::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val rect: Rect? = startSelectScreenArea(shortXContext)

        requireNotNull(rect) {
            "No Screen Rect selected."
        }

        val screenCap = ScreenCap(androidContext)
        val screen: Bitmap = try {
            screenCap.cap()
        } finally {
            screenCap.destroy()
        }
        logger.d("screen: $screen")

        val cropped = BitmapUtils.cropBitmap(screen, rect)
        logger.d("cropped: $cropped")

        val tmpFile = saveScreenBitmapToFile(cropped, Dirs.screenshotCacheDir())

        val currentPkgName =
            shortXContext.appState.currentFrontPkgs().firstOrNull()?.pkgName.orEmpty()

        val rootDir = Environment.getExternalStorageDirectory()
        val mediaDir =
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Screenshots"
            )
        val destFile = File(
            mediaDir,
            "ScreenShot-${currentPkgName}-${DateUtils.formatForFileName(System.currentTimeMillis())}.png"
        )
        CopyFile.copyFile(
            shortXContext,
            tmpFile,
            destFile,
            true
        )
        logger.i("Screenshot saved: $destFile")

        MediaScannerUtil.scanFile(androidContext, destFile.absolutePath)

        withContext(Dispatchers.Main) {
            Toast.makeText(
                androidContext,
                "\uD83D\uDCF1: ${destFile.toRelativeString(rootDir)}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}