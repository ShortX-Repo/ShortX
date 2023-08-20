package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import kotlinx.coroutines.delay
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object LaunchAppByPkg : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.LaunchAppByPkg::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.LaunchAppByPkg::class.java

        val realAppPkgs = d.pkgAndUsersList.toRealApps(params)
        realAppPkgs.forEach {
            delay(600)
            shortXContext.appState.launchApp(it)
        }
    }
}