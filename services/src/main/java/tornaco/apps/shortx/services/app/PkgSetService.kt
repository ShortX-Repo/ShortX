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

    fun addPkgSet(set: PkgSet) {
        pkgSetsStore.addPkgSet(set)
    }

    fun deletePkgSet(id: String) {
        pkgSetsStore.deletePkgSet(id)
    }

    fun getPkgSetById(id: String): PkgSet? {
        return prebuiltPkgSets.firstOrNull { it.id == id }?.getPkgSet?.invoke(shortXContext)
            ?: pkgSetsStore.getPkgSetById(id)
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
    val id: String,
    val getPkgSet: (ShortXContext) -> PkgSet
) {
    companion object {
        const val ID_ALL = "prebuilt.all"
        const val ID_SYSTEM = "prebuilt.system"
        const val ID_USER = "prebuilt.user"
    }

    object All : PrebuiltPkgSet(
        ID_ALL,
        { shortXContext ->
            PkgSet.newBuilder().setId(ID_ALL)
                .setIsPrebuilt(true)
                .setLabel("All")
                .addAllAppPkgs(shortXContext.appState.getInstalledApps().map { it.pkg })
                .build()
        }
    )

    object System : PrebuiltPkgSet(
        ID_SYSTEM,
        { shortXContext ->
            PkgSet.newBuilder().setId(ID_SYSTEM)
                .setIsPrebuilt(true)
                .setLabel("System")
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.category == AppCategory.System }.map { it.pkg })
                .build()
        }
    )

    object User : PrebuiltPkgSet(
        ID_USER,
        { shortXContext ->
            PkgSet.newBuilder().setId(ID_USER)
                .setIsPrebuilt(true)
                .setLabel("User")
                .addAllAppPkgs(
                    shortXContext.appState.getInstalledApps()
                        .filter { it.category == AppCategory.User }.map { it.pkg })
                .build()
        }
    )
}