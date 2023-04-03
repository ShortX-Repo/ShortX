package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseDirectActionSettingsStore

class DirectActionSettingsStore(
    scope: CoroutineScope,
) : BaseDirectActionSettingsStore(scope = scope, storeFile = Dirs.dataStoreFile("das"))