package tornaco.apps.shortx.services.rule.actor.impl

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Environment
import android.widget.Toast
import com.google.protobuf.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.os.ScreenCap
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.SelectScreenArea.startSelectScreenArea
import tornaco.apps.shortx.services.util.MediaUtil
import java.io.File

object AreaScreenshot : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.AreaScreenshot::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.AreaScreenshot::class.java
        val area = startSelectScreenArea(shortXContext)
        logger.d("area: $area")
        val rect: Rect? = area?.getRect(androidContext)
        logger.d("rect: $rect")

        requireNotNull(rect) {
            "No Screen Rect selected."
        }

        delay(100)

        val screenCap = ScreenCap(androidContext)
        val screen: Bitmap = try {
            screenCap.cap()
        } finally {
            screenCap.waitUntilDestroyed()
        }
        logger.d("screen: $screen")

        if (BuildPropInternal.APP_IS_DEBUG) {
            runCatching {
                saveScreenBitmapToFile(
                    screen,
                    Dirs.baseServerCacheDir(),
                    "screen"
                )
            }
        }

        val cropped =
            if (area is AreaSelection.FullScreen) screen else BitmapUtils.cropBitmap(screen, rect)
        logger.d("cropped: $cropped")
        requireNotNull(cropped) { "Cropped is null." }

        val tmpFile = saveScreenBitmapToFile(cropped, Dirs.screenshotCacheDir())
        cropped.recycle()
        screen.recycle()

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

        val fileAndUri = MediaUtil.scanFile(androidContext, destFile.absolutePath)
        logger.i("fileAndUri: $fileAndUri")

        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.AreaScreenshot.screenshotFilePath.name)
                .fallbackOnEmpty { ContextDataMapping.AreaScreenshot.screenshotFilePath.name },
            fileAndUri.first
        )
        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.AreaScreenshot.screenshotFileUri.name)
                .fallbackOnEmpty { ContextDataMapping.AreaScreenshot.screenshotFileUri.name },
            fileAndUri.second
        )

        withContext(Dispatchers.Main) {
            Toast.makeText(
                androidContext,
                "\uD83D\uDCF1: ${destFile.toRelativeString(rootDir)}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}