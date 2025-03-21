package tornaco.apps.shortx.core

import tornaco.apps.shortx.core.util.Logger

inline fun Logger.logOnDebug(logProvider: () -> Any?) {
    if (BuildPropInternal.APP_IS_DEBUG) {
        d(logProvider())
    }
}


fun Logger.logOnDebug(log: Any?) {
    if (BuildPropInternal.APP_IS_DEBUG) {
        d(log)
    }
}