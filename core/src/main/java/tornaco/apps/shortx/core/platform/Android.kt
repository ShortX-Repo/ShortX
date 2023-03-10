package tornaco.apps.shortx.core.platform

object PackageNames {
    const val android = "android"
}

object Methods {
    const val start = "start"
    const val systemReady = "systemReady"
    const val filterInputEvent = "filterInputEvent"
    const val reportEvent = "reportEvent"
    const val getService = "getService"
    const val onTransact = "onTransact"
    const val print = "print"
    const val startListening = "startListening"
}

object ClassNames {
    const val AMS = "com.android.server.am.ActivityManagerService"
    const val INPUT_MANAGER_SERVICE = "com.android.server.inputmethod.InputMethodManagerService"
}

object Fields {
    const val mContext = "mContext"
}

fun String.lifecycleClassName() = "$this\$Lifecycle"