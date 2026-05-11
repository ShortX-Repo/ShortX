package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object AddAppsToPkgSetByPkg : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.AddAppsToPkgSetByPkg::class.java
    }

    override suspend fun handle(params: HandleParams): Actor.Res = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.AddAppsToPkgSetByPkg::class.java
        val pkgSet = requireMutablePkgSet(data.pkgSetLabel)
        val realAppPkgs = data.pkgAndUsersList.toRealApps(params)
        if (realAppPkgs.isEmpty()) return Actor.Res.EMPTY

        val builder = PkgSet.newBuilder(pkgSet)
        var changed = false
        realAppPkgs.forEach { appPkg ->
            if (!pkgSet.appPkgsList.contains(appPkg)) {
                builder.addAppPkgs(appPkg)
                changed = true
            }
        }
        if (changed) {
            shortXContext.pkgSet.updatePkgSet(builder.build())
        }
        Actor.Res.EMPTY
    }
}

object RemoveAppsFromPkgSetByPkg : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.RemoveAppsFromPkgSetByPkg::class.java
    }

    override suspend fun handle(params: HandleParams): Actor.Res = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.RemoveAppsFromPkgSetByPkg::class.java
        val pkgSet = requireMutablePkgSet(data.pkgSetLabel)
        val appsToRemove = data.pkgAndUsersList.toRealApps(params).toSet()
        if (appsToRemove.isEmpty()) return Actor.Res.EMPTY

        val updatedApps = pkgSet.appPkgsList.filterNot { it in appsToRemove }
        if (updatedApps.size != pkgSet.appPkgsCount) {
            shortXContext.pkgSet.updatePkgSet(
                PkgSet.newBuilder(pkgSet)
                    .clearAppPkgs()
                    .addAllAppPkgs(updatedApps)
                    .build()
            )
        }
        Actor.Res.EMPTY
    }
}

private fun HandleParams.requireMutablePkgSet(label: String): PkgSet {
    val pkgSet = requireNotNull(shortXContext.pkgSet.getPkgSetByLabel(label)) {
        "Pkg set: $label not found."
    }
    require(!pkgSet.isPrebuilt) {
        "Pkg set: $label is prebuilt and can't be modified."
    }
    return pkgSet
}
