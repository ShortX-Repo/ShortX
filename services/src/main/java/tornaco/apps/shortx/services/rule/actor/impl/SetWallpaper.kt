package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.URLUtil
import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.wallpaperManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File
import java.io.FileInputStream

object SetWallpaper : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetWallpaper::class.java
    }

    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.SetWallpaper::class.java
        val url = d.fileUrl.inflateContextMap(contextData)
            .replaceGlobalVarValues { shortXContext.rule.getAllGlobalVars() }
            .replaceLocalVarValues(localVars)
        logger.d("url: $url")
        val isContent = URLUtil.isContentUrl(url)
        if (isContent) {
            logger.d("setStream using contentResolver")
            androidContext.contentResolver.openInputStream(Uri.parse(url)).use {
                androidContext.wallpaperManager.setStream(it)
            }
        } else {
            logger.d("setStream using FileInputStream")
            FileInputStream(File(url)).use {
                androidContext.wallpaperManager.setStream(it)
            }
        }
    }

}