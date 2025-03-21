package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BasePkgSetsStore

class PkgSetsStore(val scope: CoroutineScope) :
    BasePkgSetsStore(scope = scope, storeFile = Dirs.dataStoreFile("pkgsets")) {
}