package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.SetAppEnabled
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.pkgSetIdsToPkgs


object SetAppEnabled : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetAppEnabled::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ SetAppEnabled::class.java
        val pkgs = if (d.pkgSetsList.isNotEmpty()) {
            d.pkgSetsList.pkgSetIdsToPkgs(shortXContext)
        } else {
            d.appPkgList
        }
        shortXContext.appState.setAppEnabled(pkgs, d.enable, "Action Eva- SetAppEnabled")
    }
}