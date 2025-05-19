package tornaco.apps.shortx.services.xposed.hooks.hook.api

interface IPackageLoaded {
    @Throws(Throwable::class)
    fun onPackageLoaded(param: Param) {
    }

    data class Param(
        val packageName: String,
        val processName: String?,
        val classLoader: ClassLoader
    )
}