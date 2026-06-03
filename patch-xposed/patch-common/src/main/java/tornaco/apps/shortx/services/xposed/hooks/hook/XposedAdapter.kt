package tornaco.apps.shortx.services.xposed.hooks.hook

import java.lang.reflect.Member

interface XposedAdapter {

    fun log(message: String)

    fun log(message: String, throwable: Throwable)

    fun hookBefore(member: Member, callback: (ShortXHookParam) -> Unit)

    fun hookAfter(member: Member, callback: (ShortXHookParam) -> Unit)
}

object XposedRuntime {
    private var adapter: XposedAdapter? = null

    var isSystemServer: Boolean = false
        private set

    val current: XposedAdapter
        get() = checkNotNull(adapter) { "XposedAdapter is not initialized" }

    fun init(adapter: XposedAdapter, systemServer: Boolean = false) {
        this.adapter = adapter
        isSystemServer = systemServer
    }
}