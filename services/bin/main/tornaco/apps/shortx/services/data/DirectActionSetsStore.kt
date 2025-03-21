package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseDirectActionSetsStore

class DirectActionSetsStore(val scope: CoroutineScope) :
    BaseDirectActionSetsStore(scope = scope, storeFile = Dirs.dataStoreFile("dasets")) {
}