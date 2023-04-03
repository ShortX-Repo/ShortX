package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.store.BaseGlobalVarStore

class GlobalVarStore(
    scope: CoroutineScope,
    private val onAddOrUpdate: (globalVar: GlobalVar) -> Unit,
    private val onDelete: (globalVar: GlobalVar) -> Unit,
) : BaseGlobalVarStore(
    scope = scope,
    storeFile = Dirs.dataStoreFile("globalVars")
) {
    override fun addGlobalVar(globalVar: GlobalVar) {
        super.addGlobalVar(globalVar)
        onAddOrUpdate(globalVar)
    }

    override fun deleteGlobalVar(varId: String): GlobalVar? {
        val res = super.deleteGlobalVar(varId)
        res?.let { onDelete(it) }
        return res
    }
}