package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.URLUtil
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.FileHelper
import tornaco.apps.shortx.core.util.wallpaperManager
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File
import java.io.FileInputStream

object SetWallpaper : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetWallpaper::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.SetWallpaper::class.java
        val url = d.fileUrl.inflateContextMap(contextData)
            .replaceGlobalVarValues { shortXContext.rule.getAllGlobalVars() }
            .replaceLocalVarValues(localVars)
        logger.d("url: $url")
        val isContent = URLUtil.isContentUrl(url)
        val realUrl = if (isContent) {
            FileHelper.getRealPathFromURI(androidContext, Uri.parse(url))
        } else {
            url
        }
        val tmpFile = File(Dirs.baseServerCacheDir(), "wallpaper.png")
        tmpFile.parentFile.mkdir()

        requireNotNull(
            shortXContext.shell.executeCommand(
                "cp $realUrl $tmpFile",
                false
            )
        ) { "Copy file fail - Shell res is null" }
            .also {
                require(it.code == 0) { "Copy file fail: $it" }
            }

        val changeOwner = "chown system:system ${tmpFile.absolutePath}"
        requireNotNull(
            shortXContext.shell.executeCommand(
                changeOwner,
                false
            )
        ) { "chown file fail." }
            .also {
                require(it.code == 0) { "chown file fail." }
            }

        logger.d("Set with: $tmpFile")
        FileInputStream(tmpFile).use {
            val wallpaperManager = androidContext.wallpaperManager
            val rawBitmap = BitmapFactory.decodeStream(it)
            val wallpaperBitmap = if (d.crop) {
                logger.i("crop wallpaper image.")
                cropBitmapToSize(
                    rawBitmap,
                    wallpaperManager.desiredMinimumWidth,
                    wallpaperManager.desiredMinimumHeight
                )
            } else {
                logger.i("using raw wallpaper image.")
                rawBitmap
            }

            wallpaperManager.setBitmap(
                wallpaperBitmap,
                null,
                true,
                WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
            )

            wallpaperBitmap.recycle()
            rawBitmap.recycle()
        }
    }

    private fun cropBitmapToSize(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        if (originalWidth <= targetWidth && originalHeight <= targetHeight) {
            return bitmap
        }

        val left = (originalWidth - targetWidth) / 2
        val top = (originalHeight - targetHeight) / 2

        return Bitmap.createBitmap(bitmap, left, top, targetWidth, targetHeight)
    }
}