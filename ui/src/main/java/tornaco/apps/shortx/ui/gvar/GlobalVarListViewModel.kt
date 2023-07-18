package tornaco.apps.shortx.ui.gvar

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.toProto
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.util.ClipboardUtils
import java.nio.charset.Charset
import javax.inject.Inject

data class GlobalVarListState(
    val globalVars: List<GlobalVar> = emptyList()
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class GlobalVarListViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<GlobalVarListState>(context, { GlobalVarListState() }) {
    private val globalVarObs = object : IGlobalVarObserver.Stub() {
        override fun onAddOrUpdate(id: String?) {
            loadState()
        }

        override fun onDelete(id: String?) {
            loadState()
        }
    }

    init {
        shortXManager.registerGlobalVarObs(globalVarObs)
    }

    fun loadState() {
        viewModelScope.launch(Dispatchers.IO) {
            val globalVars = shortXManager.getAllGlobalVars()
            viewModelScope.launch {
                updateState {
                    copy(globalVars = globalVars)
                }
            }
        }
    }

    fun createGlobalVar(globalVar: GlobalVar) {
        shortXManager.addGlobalVar(globalVar)
        loadState()
    }

    fun delete(name: String) {
        viewModelScope.launch {
            shortXManager.deleteGlobalVar(name)
            loadState()
        }
    }

    fun importFromClipboard() {
        val content = ClipboardUtils.readClipboard(context)
        if (content != null) {
            content.toImportGVOrNull()?.let {
                shortXManager.addGlobalVar(it)
                loadState()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterGlobalVarObs(globalVarObs)
    }

    fun importFromUri(uri: Uri) {
        val text = runCatching {
            context.contentResolver.openInputStream(uri)?.use {
                it.bufferedReader(Charset.defaultCharset()).readText()
            }
        }.getOrElse {
            logger.e(it, "importFromUri")
            null
        }
        text?.toImportGVOrNull()?.let {
            shortXManager.addGlobalVar(it)
            loadState()
        }
    }

    private fun String.toImportGVOrNull(): GlobalVar? {
        val gv = kotlin.runCatching {
            val externalVar =
                GlobalVar.parseFrom(GlobalVar.newBuilder().toProto(this).toByteArray())
            var checkedName = externalVar.name
            while (state.value.globalVars.any { it.name == checkedName }) {
                checkedName += "(2)"
            }
            GlobalVar.newBuilder(externalVar).setName(checkedName).build()
        }.getOrNull()
        return gv
    }
}