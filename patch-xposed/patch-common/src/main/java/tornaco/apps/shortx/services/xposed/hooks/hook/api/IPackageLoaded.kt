package tornaco.apps.shortx.services.xposed.hooks.hook.api

interface IPackageLoaded {
    /**
     * Called from the LSPosed API 101 package-load callback only.
     * Package and process names are the values reported by API 101; there is no legacy fallback.
     */
    @Throws(Throwable::class)
    fun onPackageLoaded(param: Param) {
    }

    data class Param(
        val packageName: String,
        val processName: String?,
        val classLoader: ClassLoader
    )
}
