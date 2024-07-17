package tornaco.apps.shortx.services.xposed.hooks.hook.systemui

import android.content.Context
import tornaco.apps.shortx.core.ocr.Paddle
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.util.ExtAppAssetsHelper

class OCRImpl(private val context: Context) {
    private val logger = Logger("OCRImpl")

    fun copyModels() {
        val outDir = context.cacheDir.absolutePath
        logger.d("copyModels outDir: $outDir")
        Paddle.allModels.forEach {
            ExtAppAssetsHelper.copyAssets(context, it, outDir)
        }
        logger.i("copyModels success.")
    }
}