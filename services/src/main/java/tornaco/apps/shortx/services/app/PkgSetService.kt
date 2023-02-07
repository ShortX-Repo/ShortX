package tornaco.apps.shortx.services.app

import tornaco.apps.shortx.core.proto.pkgset.PkgSet
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.data.PkgSetsStore
import tornaco.apps.shortx.services.systemServiceScope

class PkgSetService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val pkgSetsStore: PkgSetsStore by lazy {
        PkgSetsStore(systemServiceScope)
    }

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
        return pkgSetsStore.getPkgSetById(id)
    }

    fun getAllPkgSets(): List<PkgSet> {
        return pkgSetsStore.getAllPkgSets()
    }
}