package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import com.thegrizzlylabs.sardineandroid.Sardine
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object WebDavList : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WebDavList::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res {
        return with(params) {
            val data =
                action unpack_ tornaco.apps.shortx.core.proto.action.WebDavList::class.java
            val cv = RuleContextAndVars(params)
            withContext(Dispatchers.IO) {
                val url = data.url.compileContextAndVars(cv)
                val depth = data.depth.compileContextAndVars(cv).toIntOrNull() ?: 1
                val profile = shortXContext.rule.getAllWebDavProfiles()
                    .first { it.profileId == data.profileId.compileContextAndVars(cv) }
                val sardine: Sardine = OkHttpSardine()
                sardine.setCredentials(profile.userName, profile.password)
                val resources = sardine.list(url, depth)
                val paths = resources.map { it.path }
                logger.d("paths: $paths")
                val names = resources.map { it.name }
                logger.d("names: $names")
                contextData.add(
                    data.customContextDataKey.forKey(ContextDataMapping.WebDavList.webdavResources.name)
                        .fallbackOnEmpty(ContextDataMapping.WebDavList.webdavResources.name),
                    resources
                )
                contextData.add(
                    data.customContextDataKey.forKey(ContextDataMapping.WebDavList.webdavPaths.name)
                        .fallbackOnEmpty(ContextDataMapping.WebDavList.webdavPaths.name),
                    paths
                )
                contextData.add(
                    data.customContextDataKey.forKey(ContextDataMapping.WebDavList.webdavNames.name)
                        .fallbackOnEmpty(ContextDataMapping.WebDavList.webdavNames.name),
                    names
                )

                Actor.Res(paths.joinToString())
            }
        }
    }
}