package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseDynamicShortcutSettingsStore

class DynamicShortcutSettingsStore(val scope: CoroutineScope) :
    BaseDynamicShortcutSettingsStore(
        scope = scope,
        storeFile = Dirs.dataStoreFile("DynamicShortcutSettings")
    ) {
}