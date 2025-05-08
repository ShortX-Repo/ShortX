package tornaco.apps.shortx.services.xposed.hooks.hook.api

interface IPackageLoaded {
    @Throws(Throwable::class)
    fun onPackageLoaded(param: Param) {
    }

    object Param
}