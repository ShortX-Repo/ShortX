package tornaco.apps.shortx.services.rule.actor.impl

import android.graphics.BitmapFactory
import com.google.protobuf.Message
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File

object ParseQRCode : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ParseQRCode::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.ParseQRCode::class.java
        val contextAndVars = RuleContextAndVars(params)
        val compiledImagePath = d.imagePath.fallbackOnEmpty("").compileContextAndVars(contextAndVars)

        require(compiledImagePath.isNotEmpty()) { "Image path is empty." }
        val file = File(compiledImagePath)
        require(file.exists()) { "Image file does not exist: $compiledImagePath" }

        val bitmap = BitmapFactory.decodeFile(compiledImagePath)
            ?: error("Failed to decode image: $compiledImagePath")

        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.recycle()

        val binaryBitmap = BinaryBitmap(HybridBinarizer(RGBLuminanceSource(width, height, pixels)))
        val result = MultiFormatReader().decode(binaryBitmap)
        val qrText = result.text

        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.ParseQRCode.qrCodeText.name)
                .fallbackOnEmpty { ContextDataMapping.ParseQRCode.qrCodeText.name },
            qrText
        )

        Actor.Res(qrText)
    }
}
