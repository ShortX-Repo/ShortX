package tornaco.apps.shortx.core.res

import android.annotation.SuppressLint
import android.content.Context
import com.squareup.picasso3.Picasso
import com.squareup.picasso3.Request
import com.squareup.picasso3.RequestHandler
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.BitmapUtils
import tornaco.apps.shortx.core.util.Logger
import java.io.File

object PicassoInitializer {
    private lateinit var appContext: Context
    private lateinit var picassoCacheDir: File

    @SuppressLint("StaticFieldLeak")
    private lateinit var appResources: AppResources

    private val appIconRequestHandler = object : RequestHandler() {
        private val logger = Logger("appIconRequestHandler")

        override fun canHandleRequest(data: Request): Boolean {
            return data.uri?.toString()?.startsWith("app://") ?: false
        }

        override fun load(picasso: Picasso, request: Request, callback: Callback) {
            kotlin.runCatching {
                val pkgName = request.uri.toString().replace("app://", "").trim()
                logger.d("pkgName: $pkgName")
                callback.onSuccess(
                    Result.Bitmap(
                        requireNotNull(
                            shortXManager.getAppIcon(
                                pkgName,
                                myUserId
                            )
                        ) { "Get app icon: $pkgName" },
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
                val iconName = request.uri.toString().replace("remix://", "")
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

    private val base64IconRequestHandler = object : RequestHandler() {
        private val logger = Logger("base64IconRequestHandler")

        override fun canHandleRequest(data: Request): Boolean {
            return data.uri?.toString()?.startsWith("base64://") ?: false
        }

        override fun load(picasso: Picasso, request: Request, callback: Callback) {
            kotlin.runCatching {
                val base64 = request.uri.toString().replace("base64://", "")
                callback.onSuccess(
                    Result.Bitmap(
                        requireNotNull(BitmapUtils.base64ToBitmap(base64)) {
                            "Fail to decode base64: $base64"
                        },
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
        Picasso.Builder(appContext).cacheDir(picassoCacheDir)
            .addRequestHandler(appIconRequestHandler)
            .addRequestHandler(remixIconRequestHandler)
            .addRequestHandler(base64IconRequestHandler)
            .build()
    }

    fun install(context: Context, cacheDir: File, appPkgName: String) {
        appContext = context
        picassoCacheDir = cacheDir
        appResources = AppResources(context, appPkgName)
    }

    fun get() = picasso
}