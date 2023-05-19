package tornaco.apps.shortx.services.app

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
        return prebuiltPkgSets.firstOrNull { it.label == label }?.getPkgSet?.invoke(shortXContext)
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
    val label: String,
    val getPkgSet: (ShortXContext) -> PkgSet
) {
    companion object {
        const val LABEL_ALL = "All"
        const val LABEL_SYSTEM = "System"
        const val LABEL_USER = "User"
    }

    object All : PrebuiltPkgSet(
        LABEL_ALL,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(LABEL_ALL)
                .addAllAppPkgs(shortXContext.appState.getInstalledApps().map { it.pkg })
                .build()
        }
    )

    object System : PrebuiltPkgSet(
        LABEL_SYSTEM,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(LABEL_SYSTEM)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.category == AppCategory.System }.map { it.pkg })
                .build()
        }
    )

    object User : PrebuiltPkgSet(
        LABEL_USER,
        { shortXContext ->
            PkgSet.newBuilder()
                .setIsPrebuilt(true)
                .setLabel(LABEL_USER)
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.category == AppCategory.User }.map { it.pkg })
                .build()
        }
    )
}