package tornaco.apps.shortx.services.ui

import android.annotation.SuppressLint
import android.content.Context
import com.squareup.picasso3.Picasso
import com.squareup.picasso3.Request
import com.squareup.picasso3.RequestHandler
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.res.AppResources
import tornaco.apps.shortx.core.res.Remix
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.data.Dirs

object PicassoInitializer {
    private lateinit var appContext: Context

    @SuppressLint("StaticFieldLeak")
    private lateinit var appResources: AppResources

    private val appIconRequestHandler = object : RequestHandler() {
        private val logger = Logger("appIconRequestHandler")

        override fun canHandleRequest(data: Request): Boolean {
            return data.uri?.toString()?.startsWith("app://") ?: false
        }

        override fun load(picasso: Picasso, request: Request, callback: Callback) {
            kotlin.runCatching {
                val pkgName = request.uri!!.toString().replace("app://", "").trim()
                logger.d("pkgName: $pkgName")
                callback.onSuccess(
                    Result.Bitmap(
                        ShortXService.serviceBinder.getAppIcon(pkgName, myUserId),
                        Picasso.LoadedFrom.DISK
                    )
                )
            }.onFailure {
                logger.e(it, "load error")
                callback.onError(it)
            }
        }
    }

    private val remixIconRequestHandler = object : RequestHandler() {
        private val logger = Logger("remixIconRequestHandler")

        override fun canHandleRequest(data: Request): Boolean {
            return data.uri?.toString()?.startsWith("remix://") ?: false
        }

        override fun load(picasso: Picasso, request: Request, callback: Callback) {
            kotlin.runCatching {
                val iconName = request.uri!!.toString().replace("remix://", "")
                val resName = Remix.allFlat[iconName]
                logger.d("iconName: $iconName, resName: $resName")
                callback.onSuccess(
                    Result.Bitmap(
                        BitmapUtils.getBitmap(appContext, appResources.getDrawable(resName))!!,
                        Picasso.LoadedFrom.DISK
                    )
                )
            }.onFailure {
                logger.e(it, "load error")
                callback.onError(it)
            }
        }
    }

    private val picasso: Picasso by lazy {
        Picasso.Builder(appContext).cacheDir(Dirs.picassoCacheDir())
            .addRequestHandler(appIconRequestHandler)
            .addRequestHandler(remixIconRequestHandler)
            .build()
    }

    fun install(context: Context) {
        appContext = context
        appResources = AppResources(context, BuildPropInternal.APP_PACKAGE_NAME)
    }

    fun get() = picasso
}