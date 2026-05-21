package tornaco.apps.shortx.services.xposed.hooks.hook.api


interface ISystemServerLoaded {
    /**
     * Called from the LSPosed API 101 system-server callback only.
     * The supplied ClassLoader is the API 101 system-server loader used for hook registration.
     */
    @Throws(Throwable::class)
    fun onSystemServerLoaded(param: Param) {
    }

    data class Param(
        /**
         * The ClassLoader used for this package.
         */
        val classLoader: ClassLoader
    )
}
