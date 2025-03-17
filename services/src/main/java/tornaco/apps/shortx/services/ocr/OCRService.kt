package tornaco.apps.shortx.services.ocr

import android.os.RemoteCallbackList
import tornaco.apps.shortx.core.ocr.IOCR
import tornaco.apps.shortx.core.proto.common.OCREngine
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.Settings
import tornaco.apps.shortx.services.data.read
import tornaco.apps.shortx.services.data.write

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
            "requireOCR, no registered ocr.\n" +
                    "Did you select SystemUI in Xposed?\n" +
                    "请确保已在Xposed中勾选SystemUI"
        }
        val first = ocrStubs.getRegisteredCallbackItem(0)
        return first
    }

    fun setOCREngine(engine: Int) {
        store.write(Settings.IntSettings.OCREngine, engine)
    }

    fun getOCREngine(): OCREngine {
        return OCREngine.forNumber(store.read(Settings.IntSettings.OCREngine))
    }
}
