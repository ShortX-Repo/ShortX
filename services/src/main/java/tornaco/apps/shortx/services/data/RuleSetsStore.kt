package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.store.BaseRuleSetsStore

class RuleSetsStore(val scope: CoroutineScope) :
    BaseRuleSetsStore(scope = scope, storeFile = Dirs.dataStoreFile("rulesets")) {
}