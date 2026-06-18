package tornaco.apps.shortx.services.rule.actor.impl

import android.graphics.Bitmap
import android.os.Environment
import com.google.protobuf.Message
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.util.MediaUtil
import java.io.File
import java.io.FileOutputStream

object GenerateQRCode : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.GenerateQRCode::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.GenerateQRCode::class.java
        val contextAndVars = RuleContextAndVars(params)
        val compiledText = d.text.fallbackOnEmpty("").compileContextAndVars(contextAndVars)
        val compiledTargetDir = d.targetDirPath.fallbackOnEmpty("").compileContextAndVars(contextAndVars)

        require(compiledText.isNotEmpty()) { "Text to encode is empty." }

        val size = 512
        val hints = mapOf(EncodeHintType.MARGIN to 1, EncodeHintType.CHARACTER_SET to "UTF-8")
        val bitMatrix = QRCodeWriter().encode(compiledText, BarcodeFormat.QR_CODE, size, size, hints)
        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            for (x in 0 until size) {
                pixels[y * size + x] = if (bitMatrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
            }
        }
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, size, 0, 0, size, size)

        val fileName = "QRCode_${DateUtils.formatForFileName(System.currentTimeMillis())}.png"

        val tmpDir = File(Dirs.baseServerCacheDir(), "qrcode").apply { mkdirs() }
        val tmpFile = File(tmpDir, fileName)
        FileOutputStream(tmpFile).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()

        val destDir = if (compiledTargetDir.isNotEmpty()) {
            File(compiledTargetDir)
        } else {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "ShortX"
            )
        }
        val destFile = File(destDir, fileName)

        CopyFile.copyFile(
            shortXContext,
            tmpFile,
            destFile,
            true
        )

        val fileAndUri = MediaUtil.scanFile(androidContext, destFile.absolutePath)
        val finalPath = fileAndUri.first
        val finalUri = fileAndUri.second.toString()

        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.GenerateQRCode.qrCodeFilePath.name)
                .fallbackOnEmpty { ContextDataMapping.GenerateQRCode.qrCodeFilePath.name },
            finalPath
        )
        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.GenerateQRCode.qrCodeFileUri.name)
                .fallbackOnEmpty { ContextDataMapping.GenerateQRCode.qrCodeFileUri.name },
            finalUri
        )

        Actor.Res(finalPath)
    }
}
