package tornaco.apps.shortx.services.data

import kotlinx.coroutines.CoroutineScope
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.store.BaseGlobalVarStore

class GlobalVarStore(
    scope: CoroutineScope,
    encKey: String,
    private val onAddOrUpdate: (globalVar: GlobalVar) -> Unit,
    private val onDelete: (globalVar: GlobalVar) -> Unit,
) : BaseGlobalVarStore(
    scope = scope,
    storeFile = Dirs.dataStoreFile("globalVars"),
    encKey
) {
    override fun addGlobalVar(globalVar: GlobalVar) {
        if (getGlobalVarByName(globalVar.name) == globalVar) return
        super.addGlobalVar(globalVar)
        onAddOrUpdate(globalVar)
    }

    override fun deleteGlobalVar(varId: String): GlobalVar? {
        val res = super.deleteGlobalVar(varId)
        res?.let { onDelete(it) }
        return res
    }

}