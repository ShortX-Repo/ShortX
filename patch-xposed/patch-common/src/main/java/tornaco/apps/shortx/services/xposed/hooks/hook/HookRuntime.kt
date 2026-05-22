package tornaco.apps.shortx.services.xposed.hooks.hook

import java.lang.reflect.Member

interface HookAdapter {
    fun hookBefore(member: Member, callback: (ShortXHookParam) -> Unit)

    fun hookAfter(member: Member, callback: (ShortXHookParam) -> Unit)
}

object HookRuntime {
    private val legacyAdapter = LegacyHookAdapter()
    private val api101Adapter = Api101HookAdapter()

    var mode: Mode = Mode.Legacy
        private set

    val adapter: HookAdapter
        get() = when (mode) {
            Mode.Legacy -> legacyAdapter
            Mode.Api101 -> api101Adapter
        }

    fun useLegacy() {
        mode = Mode.Legacy
    }

    fun useApi101() {
        mode = Mode.Api101
    }

    enum class Mode {
        Legacy,
        Api101,
    }
}
