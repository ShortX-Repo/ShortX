package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseFunctionSettingsStore

class FunctionSettingsStore(
    scope: CoroutineScope,
) : BaseFunctionSettingsStore(scope = scope, storeFile = Dirs.dataStoreFile("functions"))