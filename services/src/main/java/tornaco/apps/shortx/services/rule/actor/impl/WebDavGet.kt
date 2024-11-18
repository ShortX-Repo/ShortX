package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import java.io.File
import java.io.FileOutputStream

object WebDavGet : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WebDavGet::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        with(params) {
            val data =
                action unpack_ tornaco.apps.shortx.core.proto.action.WebDavGet::class.java
            val cv = RuleContextAndVars(params)
            withContext(Dispatchers.IO) {
                val url = data.url.compileContextAndVars(cv)
                val headers = data.headersList.associate { it.key to it.value }
                val profile = shortXContext.rule.getAllWebDavProfiles()
                    .first { it.profileId == data.profileId.compileContextAndVars(cv) }
                val sardine: Sardine = OkHttpSardine()
                sardine.setCredentials(profile.userName, profile.password)
                val inputStream = sardine.get(url, headers)
                logger.d("input: $inputStream")
                val tmpFile = File(Dirs.baseServerTmpDir(), "download.tmp").apply {
                    parentFile?.mkdirs()
                }
                logger.d("tmpFile: $tmpFile")
                inputStream.use { input ->
                    FileOutputStream(tmpFile).use { out ->
                        input.copyTo(out)
                    }
                }
                logger.d("Write success")


            }
        }
    }
}