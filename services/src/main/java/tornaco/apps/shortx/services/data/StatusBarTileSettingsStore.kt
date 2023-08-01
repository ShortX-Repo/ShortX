package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseStatusBarSettingsStore

class StatusBarTileSettingsStore(val scope: CoroutineScope) :
    BaseStatusBarSettingsStore(
        scope = scope,
        storeFile = Dirs.dataStoreFile("StatusBarTileSettings")
    ) {
}