package tornaco.apps.shortx.services.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseSettingsStore


class SettingsStore(
    context: Context,
    name: String,
    scope: CoroutineScope
) : BaseSettingsStore(context, name, Dirs.dataStoreFile(name), scope)