package tornaco.apps.shortx.services.ui

import android.content.Context
import com.squareup.picasso3.Picasso
import com.squareup.picasso3.Request
import com.squareup.picasso3.RequestHandler
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXService
import tornaco.apps.shortx.services.data.Dirs

object PicassoInitializer {
    private lateinit var appContext: Context

    private val appIconRequestHandler = object : RequestHandler() {
        private val logger = Logger("appIconRequestHandler")

        override fun canHandleRequest(data: Request): Boolean {
            return data.uri?.toString()?.startsWith("app://") ?: false
        }

        override fun load(picasso: Picasso, request: Request, callback: Callback) {
            kotlin.runCatching {
                val pkgName = request.uri!!.toString().replace("app://", "")
                logger.d("pkgName: $pkgName")
                callback.onSuccess(
                    Result.Bitmap(
                        ShortXService.serviceBinder.getAppIcon(pkgName, 0),
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
            .build()
    }

    fun install(context: Context) {
        appContext = context
    }

    fun get() = picasso
}