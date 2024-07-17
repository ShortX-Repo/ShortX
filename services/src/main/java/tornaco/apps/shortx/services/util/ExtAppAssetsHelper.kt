package tornaco.apps.shortx.services.util


import android.content.Context
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.util.Logger
import java.io.File
import java.io.FileOutputStream


object ExtAppAssetsHelper {
    private val logger = Logger("AppAssetsHelper")

    fun copyAssets(androidContext: Context, path: String, outDir: String) {
        val appRes = AppResources(androidContext, BuildPropInternal.SHORTX_EXT_APP_PKG_NAME)
        val appContext = appRes.appContext
        val assets = appContext.assets
        assets.open(path).use {
            it.copyTo(FileOutputStream(File(outDir, File(path).name)))
        }
    }
}