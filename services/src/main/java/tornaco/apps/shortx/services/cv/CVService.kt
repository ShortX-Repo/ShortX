package tornaco.apps.shortx.services.cv

import tornaco.apps.shortx.core.cv.ICV
import tornaco.apps.shortx.core.util.RemoteCallbackListExt
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService

class CVService(shortXContext: ShortXContext) :
    SystemService(shortXContext, "CVService") {
    private val cvStubs = RemoteCallbackListExt<ICV>()

    fun attachCV(cv: ICV?) {
        logger.w("attachCV: $cv")
        cv?.let {
            cvStubs.register(it)
        }
    }

    fun requireCV(): ICV {
        val itemCount = cvStubs.registeredCallbackCount
        require(itemCount > 0) {
            "requireCV, no registered cv."
        }
        val first = cvStubs.getRegisteredCallbackItem(0)
        return first
    }
}
