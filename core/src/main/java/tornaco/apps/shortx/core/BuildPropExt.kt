package tornaco.apps.shortx.core

inline fun onDebugBuild(action: () -> Unit) {
    if (BuildProp.APP_IS_DEBUG) {
        action()
    }
}