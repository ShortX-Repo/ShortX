package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseWebDavProfileStore

class WebDavProfileStore(
    val scope: CoroutineScope,
    encKey: String
) :
    BaseWebDavProfileStore(
        scope = scope,
        encKey = encKey,
        storeFile = Dirs.dataStoreFile("WebDavProfiles")
    ) {
}