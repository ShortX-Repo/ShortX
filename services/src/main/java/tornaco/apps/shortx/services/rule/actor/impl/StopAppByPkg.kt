package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object StopAppByPkg : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.StopAppByPkg::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.StopAppByPkg::class.java
        val realAppPkgs = d.pkgAndUsersList.toRealApps(params)
        shortXContext.appState.forceStopApp(realAppPkgs, "Action Eva- StopAppByPkg")
        Actor.Res.EMPTY
    }
}

fun List<StringPair>.toRealApps(params: HandleParams): List<AppPkg> {
    val cv = RuleContextAndVars(params)
    return this.mapNotNull { stringPair ->
        val app = params.shortXContext.appState.pkgToApp(
            AppPkg.newBuilder()
                .setPkgName(stringPair.first.compileContextAndVars(cv))
                .setUserId(stringPair.second.compileContextAndVars(cv).toIntOrNull() ?: myUserId)
                .build()
        )
        app?.pkg
    }
}