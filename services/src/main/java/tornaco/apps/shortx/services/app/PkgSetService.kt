package tornaco.apps.shortx.services.app

import android.os.RemoteCallbackList
import tornaco.apps.shortx.core.PrebuiltStablePkgSetLabel
import tornaco.apps.shortx.core.PrebuiltUserPkgSetLabel
import tornaco.apps.shortx.core.proto.common.AppCategory
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.core.rule.IPkgSetObserver
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.PkgSetsStore
import tornaco.apps.shortx.services.systemServiceScope

class PkgSetService(shortXContext: ShortXContext) : SystemService(shortXContext, "PkgSetService") {
    private val pkgSetsStore: PkgSetsStore by lazy {
        PkgSetsStore(systemServiceScope)
    }
    private val pkgSetObs = RemoteCallbackList<IPkgSetObserver>()

    private val prebuiltStablePkgSets = listOf(
        PrebuiltStablePkgSet.User,
        PrebuiltStablePkgSet.System,
        PrebuiltStablePkgSet.XposedModule,
        PrebuiltStablePkgSet.All,
        PrebuiltStablePkgSet.Disabled,
        PrebuiltStablePkgSet.Suspended,
    )

    private val prebuiltUserPkgSets = listOf(
        PrebuiltUserPkgSet.Game
    )

    override fun systemReady() {
        super.systemReady()
        pkgSetsStore.readBlocking()

        prebuiltUserPkgSets.forEach {
            if (getPkgSetByLabel(it.label.name) == null) {
                logger.i("Auto install user pkg set: ${it.label}")
                addPkgSet(it.getPkgSet(shortXContext))
            }
        }

        shortXContext.appState.onPackageAdded { added ->
            // Prebuilt.
            broadcastPkgSetChanged(PrebuiltStablePkgSet.User.label.name)
            broadcastPkgSetChanged(PrebuiltStablePkgSet.All.label.name)
            // User set.
            shortXContext.pkgSet.getAllPkgSets(false).filter {
                it.appPkgsList.contains(added.pkg)
            }.forEach {
                broadcastPkgSetChanged(it.label)
            }
        }
        shortXContext.appState.onPackageRemoved { removed ->
            // Prebuilt.
            broadcastPkgSetChanged(PrebuiltStablePkgSet.User.label.name)
            broadcastPkgSetChanged(PrebuiltStablePkgSet.All.label.name)

            shortXContext.pkgSet.getAllPkgSets(false).filter {
                it.appPkgsList.contains(removed.pkg)
            }.forEach {
                broadcastPkgSetChanged(it.label)
            }
        }
    }

    fun addPkgSet(set: PkgSet): Boolean {
        if (getPkgSetByLabel(set.label) != null) {
            logger.w("PkgSet with label: ${set.label} already exists.")
            return false
        }
        pkgSetsStore.addPkgSet(set)

        broadcastPkgSetChanged(set.label)

        return true
    }

    private fun broadcastPkgSetChanged(label: String) {
        logger.d("broadcastPkgSetChanged- $label")
        pkgSetObs.broadcast {
            kotlin.runCatching { it.onAddOrUpdate(label) }
                .onFailure { logger.e(it, "Call onAddOrUpdate") }
        }
    }

    fun deletePkgSet(label: String) {
        pkgSetsStore.deletePkgSet(label)

        pkgSetObs.broadcast {
            kotlin.runCatching { it.onDelete(label) }
                .onFailure { logger.e(it, "Call onDelete") }
        }
    }

    fun getPkgSetByLabel(label: String): PkgSet? {
        return prebuiltStablePkgSets.firstOrNull { it.label.name == label }?.getPkgSet?.invoke(
            shortXContext
        )
            ?: pkgSetsStore.getPkgSet(label)
    }

    fun getAllPkgSets(withPrebuilt: Boolean = true): List<PkgSet> {
        return pkgSetsStore.getAllPkgSets().toMutableList().apply {
            if (withPrebuilt) {
                prebuiltStablePkgSets.forEach {
                    add(it.getPkgSet(shortXContext))
                }
            }
        }
    }

    fun registerPkgSetObs(obs: IPkgSetObserver) {
        pkgSetObs.register(obs)
    }

    fun unregisterPkgSetObs(obs: IPkgSetObserver) {
        pkgSetObs.unregister(obs)
    }
}

sealed class PrebuiltUserPkgSet(
    val label: PrebuiltUserPkgSetLabel,
    val getPkgSet: (ShortXContext) -> PkgSet
) {
    data object Game : PrebuiltUserPkgSet(
        PrebuiltUserPkgSetLabel.Game,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(false)
                .setLabel(PrebuiltUserPkgSetLabel.Game.name)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.isGame }.map { it.pkg })
                .build()
        }
    )

}

sealed class PrebuiltStablePkgSet(
    val label: PrebuiltStablePkgSetLabel,
    val getPkgSet: (ShortXContext) -> PkgSet
) {
    data object All : PrebuiltStablePkgSet(
        PrebuiltStablePkgSetLabel.All,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(PrebuiltStablePkgSetLabel.All.name)
                .addAllAppPkgs(shortXContext.appState.getInstalledApps().map { it.pkg })
                .build()
        }
    )

    data object System : PrebuiltStablePkgSet(
        PrebuiltStablePkgSetLabel.System,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(PrebuiltStablePkgSetLabel.System.name)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.category == AppCategory.System }.map { it.pkg })
                .build()
        }
    )

    data object User : PrebuiltStablePkgSet(
        PrebuiltStablePkgSetLabel.User,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(PrebuiltStablePkgSetLabel.User.name)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.category == AppCategory.User }.map { it.pkg })
                .build()
        }
    )

    data object XposedModule : PrebuiltStablePkgSet(
        PrebuiltStablePkgSetLabel.XposedModule,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(PrebuiltStablePkgSetLabel.XposedModule.name)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.isXposedMod }.map { it.pkg })
                .build()
        }
    )

    data object Disabled : PrebuiltStablePkgSet(
        PrebuiltStablePkgSetLabel.Disabled,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(PrebuiltStablePkgSetLabel.Disabled.name)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.isDisabled }.map { it.pkg })
                .build()
        }
    )

    data object Suspended : PrebuiltStablePkgSet(
        PrebuiltStablePkgSetLabel.Suspended,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(PrebuiltStablePkgSetLabel.Suspended.name)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.isSuspend }.map { it.pkg })
                .build()
        }
    )
}