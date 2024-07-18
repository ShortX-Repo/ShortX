package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.Context
import android.graphics.Bitmap
import tornaco.apps.shortx.core.ocr.IOCR
import tornaco.apps.shortx.core.rule.action.ByteArrayWrapper
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.util.ExtAppClassHelper
import util.Reflections

class OCRImpl(private val context: Context) : IOCR.Stub() {
    private val logger = Logger("OCRImpl")

    private val apiIns by lazy {
        ExtAppClassHelper.newInstance(
            context,
            "tornaco.apps.shortx.ext.api.ocr.ShortXPaddleApi"
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun detect(
        bitmap: Bitmap,
        numThread: Int,
        useSlim: Boolean
    ): List<ByteArrayWrapper> {
        logger.d("apiIns: $apiIns")
        val result = (Reflections.callMethod(
            apiIns,
            "detect",
            bitmap,
            numThread,
            useSlim
        ) as List<ByteArray>).map {
            ByteArrayWrapper(it)
        }
        logger.d("result: $result")
        return result
    }
}