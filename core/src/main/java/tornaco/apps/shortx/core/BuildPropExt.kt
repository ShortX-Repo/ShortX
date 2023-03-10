package tornaco.apps.shortx.core

inline fun doIfDebug(action: () -> Unit) {
    if (BuildProp.APP_IS_DEBUG) {
        action()
    }
}