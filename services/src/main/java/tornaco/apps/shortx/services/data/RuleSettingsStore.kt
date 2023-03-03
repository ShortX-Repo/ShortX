package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseRuleSettingsStore

class RuleSettingsStore(scope: CoroutineScope) :
    BaseRuleSettingsStore(
        scope,
        Dirs.dataStoreFile("rules")
    )