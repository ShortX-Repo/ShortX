package tornaco.apps.shortx.services.rule.actor.impl

import android.graphics.BitmapFactory
import com.google.protobuf.Message
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

object ParseQRCode : Actor {
    private val tmpFileId = AtomicInteger(0)

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ParseQRCode::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ParseQRCode::class.java
        val contextAndVars = RuleContextAndVars(params)
        val compiledImagePath =
            d.imagePath.fallbackOnEmpty("").compileContextAndVars(contextAndVars)

        require(compiledImagePath.isNotEmpty()) { "Image path is empty." }
        val srcFile = File(compiledImagePath)

        val tmpDir = File(Dirs.baseServerCacheDir(), "qrcode").apply { mkdirs() }
        val tmpFile = File(tmpDir, "parse_${tmpFileId.incrementAndGet()}")
        CopyFile.copyFile(
            shortXContext = shortXContext,
            srcFile = srcFile,
            destFile = tmpFile,
            deleteSrcOnSuccess = false,
            owner = "1000.1000"
        )

        try {
            val bitmap = BitmapFactory.decodeFile(tmpFile.absolutePath)
                ?: error("Failed to decode image: $compiledImagePath")

            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            bitmap.recycle()

            val binaryBitmap =
                BinaryBitmap(HybridBinarizer(RGBLuminanceSource(width, height, pixels)))
            val result = MultiFormatReader().decode(binaryBitmap)
            val qrText = result.text

            params.contextData.add(
                d.customContextDataKey.forKey(ContextDataMapping.ParseQRCode.qrCodeText.name)
                    .fallbackOnEmpty { ContextDataMapping.ParseQRCode.qrCodeText.name },
                qrText
            )

            Actor.Res(qrText)
        } finally {
            // Keep for debug.
            if (!BuildPropInternal.APP_IS_DEBUG) {
                tmpFile.delete()
            }
        }
    }
}
