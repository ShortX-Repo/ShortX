package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StartAppProcessByPkg : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StartAppProcessByPkg::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.StartAppProcessByPkg::class.java
        val realAppPkgs = d.pkgAndUsersList.toRealApps(params)
        shortXContext.appState.startAppProcess(realAppPkgs, "Action Eva- StartAppProcessByPkg")
    }
}