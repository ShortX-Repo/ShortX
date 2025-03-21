package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseToggleSettingsStore

class ToggleSettingsStore(
    scope: CoroutineScope,
) : BaseToggleSettingsStore(scope = scope, storeFile = Dirs.dataStoreFile("toggles"))