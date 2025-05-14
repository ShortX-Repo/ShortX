package tornaco.apps.shortx.ui.gvar

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.I18N
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.GlobalVarCreatedBy_Rule
import tornaco.apps.shortx.core.proto.gv.GlobalVarCreatedBy_User
import tornaco.apps.shortx.core.proto.toProto
import tornaco.apps.shortx.core.rule.IGlobalVarObserver
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.select
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import tornaco.apps.shortx.ui.main.model.DirectActionUM
import tornaco.apps.shortx.ui.main.model.RuleUM
import tornaco.apps.shortx.ui.main.model.daToDirectActionUM
import tornaco.apps.shortx.ui.main.model.ruleToRuleUM
import tornaco.apps.shortx.ui.util.ClipboardUtils
import java.nio.charset.Charset
import javax.inject.Inject

data class GlobalVarUiItem(val globalVar: GlobalVar, val createdBy: CreatedBy)
data class GlobalVarGroup(val createdBy: CreatedBy, val vars: List<GlobalVarUiItem>)

sealed interface CreatedBy {
    fun getLabel(i18n: I18N): String

    data object Unknown : CreatedBy {
        override fun getLabel(i18n: I18N): String {
            return "Unknown"
        }
    }

    data object User : CreatedBy {
        override fun getLabel(i18n: I18N): String {
            return i18n["ui.global.var.created.by.user"]
        }
    }

    data class DA(val da: DirectActionUM) : CreatedBy {
        override fun getLabel(i18n: I18N): String {
            return i18n["ui.global.var.created.by.rule", mapOf("rule" to da.title)]
        }
    }

    data class Rule(val rule: RuleUM) : CreatedBy {
        override fun getLabel(i18n: I18N): String {
            return i18n["ui.global.var.created.by.rule", mapOf("rule" to rule.title)]
        }
    }
}

data class GlobalVarListState(
    val globalVars: List<GlobalVarUiItem> = emptyList(),
    val groups: List<GlobalVarGroup> = emptyList(),
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class GlobalVarListViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<GlobalVarListState>(context, { GlobalVarListState() }) {

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
            val globalVars = shortXManager.getAllGlobalVars().map {
                val createdByProto: ProtoAny? = it.hasCreatedBy().select(it.createdBy, null)
                val createdBy: CreatedBy = createdByProto.protoAnyToCreateBy()
                GlobalVarUiItem(it, createdBy)
            }
            val groups = globalVars.groupBy { it.createdBy }
                .map {
                    GlobalVarGroup(it.key, it.value)
                }
            updateState {
                copy(globalVars = globalVars, groups = groups)
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
            while (state.value.globalVars.any { it.globalVar.name == checkedName }) {
                checkedName += "(2)"
            }
            GlobalVar.newBuilder(externalVar).setName(checkedName).build()
        }.getOrNull()
        return gv
    }

    private fun ProtoAny?.protoAnyToCreateBy(): CreatedBy {
        if (this == null) {
            return CreatedBy.Unknown
        }
        if (this is_ GlobalVarCreatedBy_User::class.java) {
            return CreatedBy.User
        }

        if (this is_ GlobalVarCreatedBy_Rule::class.java) {
            val srcId = (this unpack_ GlobalVarCreatedBy_Rule::class.java).srcId
            val ruleOrNull = shortXManager.getRuleById(srcId)
            if (ruleOrNull != null) {
                return CreatedBy.Rule(ruleToRuleUM(ruleOrNull, emptyList()))
            } else {
                val daOrNull = shortXManager.getDirectActionById(srcId)
                if (daOrNull != null) {
                    return CreatedBy.DA(daToDirectActionUM(daOrNull, emptyList()))
                }
            }
            return CreatedBy.Unknown
        } else {
            return CreatedBy.Unknown
        }
    }
}