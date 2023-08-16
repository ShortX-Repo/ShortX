package tornaco.apps.shortx.ui.ocr

import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import tornaco.apps.shortx.core.util.Logger


object Ocr {
    private val logger = Logger("TessOcr")
    private var api: TessBaseAPI? = null

    /**
     *  Given path must contain subdirectory `tessdata` where are `*.traineddata` language files
     *  The path must be directly readable by the app
     * */
    fun install(dataPath: String): Boolean {
        // Create TessBaseAPI instance (this internally creates the native Tesseract instance)
        val tess = TessBaseAPI()
        // Initialize API for specified language
        // (can be called multiple times during Tesseract lifetime)
        if (!tess.init(dataPath, "eng")) {
            // could be multiple languages, like "eng+deu+fra"
            // Error initializing Tesseract (wrong/inaccessible data path or not existing language file(s))
            // Release the native Tesseract instance
            logger.w("init fail.")
            tess.recycle()
            return false
        }
        api = tess
        logger.d("install success.")
        return true
    }

    fun getText(bitmap: Bitmap): String {
        val api = requireNotNull(api) {
            "TessAPI is not installed."
        }
        api.setImage(bitmap)
        return api.utF8Text
    }
}