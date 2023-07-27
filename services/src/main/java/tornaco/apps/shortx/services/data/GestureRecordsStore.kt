package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseGestureRecordsStore

class GestureRecordsStore(val scope: CoroutineScope) :
    BaseGestureRecordsStore(scope = scope, storeFile = Dirs.dataStoreFile("gestureRecords")) {
}