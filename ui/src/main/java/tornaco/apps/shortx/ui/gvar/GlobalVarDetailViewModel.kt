package tornaco.apps.shortx.ui.gvar

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.gv.BoolListVar
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.Int64ListVar
import tornaco.apps.shortx.core.proto.gv.StringListVar
import tornaco.apps.shortx.core.rule.*
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.ui.base.StateViewModel

data class GlobalVarDetailState(
    val globalVar: GlobalVar? = null,
    val valueItems: List<String> = emptyList(),
    val isList: Boolean = false
)

class GlobalVarDetailViewModel : StateViewModel<GlobalVarDetailState>() {
    override fun initState(): GlobalVarDetailState {
        return GlobalVarDetailState()
    }

    fun loadState(id: String) {
        viewModelScope.launch {
            val v = shortXManager.getGlobalVarById(id)
            val valueItems = v?.valueList() ?: emptyList()
            val isList = v?.isList() ?: false
            updateState { copy(globalVar = v, valueItems = valueItems, isList = isList) }
        }
    }

    fun updateName(name: String) {
        viewModelScope.launch {
            val current = state.value.globalVar
            current?.let {
                shortXManager.addGlobalVar(GlobalVar.newBuilder(it).setName(name).build())
                loadState(it.id)
            }
        }
    }

    fun addOrUpdateValue(value: String) {
        if (state.value.isList) {
            val current = state.value.globalVar
            current?.let {


                val newVarProtoAny: ProtoAny? = when (it.type.toType()) {
                    GlobalVarType.IntList -> {
                        kotlin.runCatching { value.toLong() }.getOrNull()?.let { longV ->
                            val data = if (it.`var`.`is`(Int64ListVar::class.java)) {
                                it.`var` unpack_ Int64ListVar::class.java
                            } else {
                                Int64ListVar.newBuilder().build()
                            }
                            Int64ListVar.newBuilder(data).addValues(longV).build().pack_()
                        }
                    }

                    GlobalVarType.BoolList -> {
                        kotlin.runCatching { value.toBoolean() }.getOrNull()?.let { boolV ->
                            val data = if (it.`var`.`is`(BoolListVar::class.java)) {
                                it.`var` unpack_ BoolListVar::class.java
                            } else {
                                BoolListVar.newBuilder().build()
                            }
                            BoolListVar.newBuilder(data).addValues(boolV).build().pack_()
                        }
                    }
                    GlobalVarType.StringList -> {
                        val data = if (it.`var`.`is`(StringListVar::class.java)) {
                            it.`var` unpack_ StringListVar::class.java
                        } else {
                            StringListVar.newBuilder().build()
                        }
                        StringListVar.newBuilder(data).addValues(value).build().pack_()
                    }
                    else -> {
                        null
                    }
                }

                shortXManager.addGlobalVar(
                    GlobalVar.newBuilder(it).setVar(newVarProtoAny).build()
                )
                loadState(it.id)
            }
        } else {
            val current = state.value.globalVar
            current?.let {
                shortXManager.addGlobalVar(
                    GlobalVar.newBuilder(it).setVar(value.toVarValue(current.type.toType())).build()
                )
                loadState(it.id)
            }
        }
    }
}