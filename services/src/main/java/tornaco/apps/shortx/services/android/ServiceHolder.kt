package tornaco.apps.shortx.services.android

import tornaco.apps.shortx.core.util.Logger

object ServiceHolder {
    private val logger = Logger("ServiceHolder")

    var inputManagerService: Any? = null
        set(value) {
            field = value
            logger.w("Set inputManagerService: $value")
        }

    var windowManagerService: Any? = null
        set(value) {
            field = value
            logger.w("Set windowManagerService: $value")
        }

    var statusBarManagerService: Any? = null
        set(value) {
            field = value
            logger.w("Set statusBarManagerService: $value")
        }

    var displayPolicy: Any? = null
        set(value) {
            field = value
            logger.w("Set displayPolicy: $value")
        }

    var keyCombinationManager: Any? = null
        set(value) {
            field = value
            logger.w("Set keyCombinationManager: $value")
        }
    var singleKeyGestureDetector: Any? = null
        set(value) {
            field = value
            logger.w("Set singleKeyGestureDetector: $value")
        }
}