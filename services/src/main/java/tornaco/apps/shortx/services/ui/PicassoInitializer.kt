package tornaco.apps.shortx.services.ui

import android.content.Context
import com.squareup.picasso3.Picasso
import tornaco.apps.shortx.services.data.Dirs

object PicassoInitializer {
    private lateinit var appContext: Context

    private val picasso: Picasso by lazy {
        Picasso.Builder(appContext).cacheDir(Dirs.picassoCacheDir()).build()
    }

    fun install(context: Context) {
        appContext = context
    }

    fun get() = picasso
}