package tornaco.apps.shortx.services.android

import tornaco.apps.shortx.core.util.Logger

object ServiceHolder {
    private val logger = Logger("ServiceHolder")

    var inputManagerService: Any? = null
        set(value) {
            field = value
            logger.w("Set inputManagerService: $value")
        }
}