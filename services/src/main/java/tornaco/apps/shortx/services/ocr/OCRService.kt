package tornaco.apps.shortx.services.ocr

import android.os.RemoteCallbackList
import tornaco.apps.shortx.core.ocr.IOCR
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService

class OCRService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "OCRService") {
    private val ocrStubs = RemoteCallbackList<IOCR>()

    fun attachOCR(ocr: IOCR?) {
        logger.w("attachOCR: $ocr")
        ocr?.let {
            ocrStubs.register(it)
        }
    }

    fun requireOCR(): IOCR {
        val itemCount = ocrStubs.registeredCallbackCount
        require(itemCount > 0) {
            "requireOCR, no registered ocr."
        }
        val first = ocrStubs.getRegisteredCallbackItem(0)
        return first
    }
}
