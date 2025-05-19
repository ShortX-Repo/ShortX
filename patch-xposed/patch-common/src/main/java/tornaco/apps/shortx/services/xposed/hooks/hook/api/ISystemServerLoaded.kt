package tornaco.apps.shortx.services.xposed.hooks.hook.api


interface ISystemServerLoaded {
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