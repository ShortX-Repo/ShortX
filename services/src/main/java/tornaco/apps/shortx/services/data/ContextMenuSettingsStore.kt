package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseContextMenuSettingsStore

class ContextMenuSettingsStore(val scope: CoroutineScope) :
    BaseContextMenuSettingsStore(
        scope = scope,
        storeFile = Dirs.dataStoreFile("ContextMenuSettings")
    ) {
}