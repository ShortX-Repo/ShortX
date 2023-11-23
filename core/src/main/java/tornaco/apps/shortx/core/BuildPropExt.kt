package tornaco.apps.shortx.core

inline fun onDebugBuild(action: () -> Unit) {
    if (BuildProp.APP_IS_DEBUG) {
        action()
    }
}

inline fun onReleaseBuild(action: () -> Unit) {
    if (!BuildProp.APP_IS_DEBUG) {
        action()
    }
}