package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object RemoveNotificationForPackage : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.RemoveNotificationForPackage::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data =
            action unpack_ tornaco.apps.shortx.core.proto.action.RemoveNotificationForPackage::class.java
        val requiredPkgList = if (data.pkgSetsList.isNotEmpty()) {
            data.pkgSetsList.flatMap {
                shortXContext.pkgSet.getPkgSetByLabel(it)?.appPkgsList ?: emptyList()
            }
        } else {
            data.appPkgList
        }

        shortXContext.notification.removeNotificationForPackage(requiredPkgList)
    }
}