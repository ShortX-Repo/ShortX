package tornaco.apps.shortx.services.app

import tornaco.apps.shortx.core.PrebuiltPkgSetLabel
import tornaco.apps.shortx.core.proto.common.AppCategory
import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.PkgSetsStore
import tornaco.apps.shortx.services.systemServiceScope

class PkgSetService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val pkgSetsStore: PkgSetsStore by lazy {
        PkgSetsStore(systemServiceScope)
    }

    private val prebuiltPkgSets = listOf(
        PrebuiltPkgSet.User,
        PrebuiltPkgSet.System,
        PrebuiltPkgSet.Game,
        PrebuiltPkgSet.XposedModule,
        PrebuiltPkgSet.All,
    )

    override fun systemReady() {
        super.systemReady()
        pkgSetsStore.readBlocking()
    }

    fun addPkgSet(set: PkgSet): Boolean {
        if (getPkgSetByLabel(set.label) != null) {
            logger.w("PkgSet with label: ${set.label} already exists.")
            return false
        }
        pkgSetsStore.addPkgSet(set)
        return true
    }

    fun deletePkgSet(label: String) {
        pkgSetsStore.deletePkgSet(label)
    }

    fun getPkgSetByLabel(label: String): PkgSet? {
        return prebuiltPkgSets.firstOrNull { it.label.name == label }?.getPkgSet?.invoke(
            shortXContext
        )
            ?: pkgSetsStore.getPkgSet(label)
    }

    fun getAllPkgSets(): List<PkgSet> {
        return pkgSetsStore.getAllPkgSets().toMutableList().apply {
            prebuiltPkgSets.forEach {
                add(it.getPkgSet(shortXContext))
            }
        }
    }
}


sealed class PrebuiltPkgSet(
    val label: PrebuiltPkgSetLabel,
    val getPkgSet: (ShortXContext) -> PkgSet
) {
    object All : PrebuiltPkgSet(
        PrebuiltPkgSetLabel.All,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(PrebuiltPkgSetLabel.All.name)
                .addAllAppPkgs(shortXContext.appState.getInstalledApps().map { it.pkg })
                .build()
        }
    )

    object System : PrebuiltPkgSet(
        PrebuiltPkgSetLabel.System,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(PrebuiltPkgSetLabel.System.name)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.category == AppCategory.System }.map { it.pkg })
                .build()
        }
    )

    object User : PrebuiltPkgSet(
        PrebuiltPkgSetLabel.User,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(PrebuiltPkgSetLabel.User.name)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.category == AppCategory.User }.map { it.pkg })
                .build()
        }
    )

    object Game : PrebuiltPkgSet(
        PrebuiltPkgSetLabel.Game,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(PrebuiltPkgSetLabel.Game.name)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.isGame }.map { it.pkg })
                .build()
        }
    )

    object XposedModule : PrebuiltPkgSet(
        PrebuiltPkgSetLabel.XposedModule,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(PrebuiltPkgSetLabel.XposedModule.name)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.isXposedMod }.map { it.pkg })
                .build()
        }
    )
}