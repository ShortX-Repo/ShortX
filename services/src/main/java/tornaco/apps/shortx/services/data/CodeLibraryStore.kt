package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseCodeLibraryStore

class CodeLibraryStore(val scope: CoroutineScope) :
    BaseCodeLibraryStore(scope = scope, storeFile = Dirs.dataStoreFile("codeLibrary")) {
}