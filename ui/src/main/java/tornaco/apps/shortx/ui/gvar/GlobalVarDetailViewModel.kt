package tornaco.apps.shortx.ui.gvar

import android.content.ComponentName
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.gv.BoolListVar
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.Int64ListVar
import tornaco.apps.shortx.core.proto.gv.StringListVar
import tornaco.apps.shortx.core.rule.*
import tornaco.apps.shortx.core.shortXManager
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.afterNavDelay


sealed interface TextHint {
    object Plain : TextHint
    data class AppComponent(val appPkg: AppPkg) : TextHint
}

data class ValueItem(val value: String, val hint: TextHint = TextHint.Plain)

data class GlobalVarDetailState(
    val globalVar: GlobalVar? = null,
    val valueItems: List<ValueItem> = emptyList(),
    val isList: Boolean = false,
    val isLoading: Boolean = false
)

class GlobalVarDetailViewModel : StateViewModel<GlobalVarDetailState>(GlobalVarDetailState()) {
    private val logger = Logger("GlobalVarDetailViewModel")

    fun loadState(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadStateInternal(id)
        }
    }

    private suspend fun loadStateInternal(id: String) {
        updateState { copy(isLoading = true) }
        afterNavDelay()

        val v = shortXManager.getGlobalVarById(id)
        val valueItems = v?.valueList() ?: emptyList()
        val isList = v?.isList() ?: false

        val valueItemsWithHint = valueItems.map {
            val appComponent =
                kotlin.runCatching { ComponentName.unflattenFromString(it) }.getOrNull()
            val hint = if (appComponent != null) {
                TextHint.AppComponent(
                    AppPkg.newBuilder().setPkgName(appComponent.packageName).build()
                )
            } else TextHint.Plain
            ValueItem(it, hint)
        }
        updateState {
            copy(
                globalVar = v,
                valueItems = valueItemsWithHint,
                isList = isList,
                isLoading = false
            )
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


    fun addOrUpdateValue(value: List<String>) {
        viewModelScope.launch {
            addOrUpdateValueInternal(value)
        }
    }

    private suspend fun addOrUpdateValueInternal(value: List<String>) {
        logger.d("addOrUpdateValue: $value")
        if (state.value.isList) {
            val current = state.value.globalVar
            current?.let {
                val newVarProtoAny: ProtoAny? = when (it.type.toType()) {
                    GlobalVarType.IntList -> {
                        value.mapNotNull { kotlin.runCatching { it.toLong() }.getOrNull() }
                            .let { longV ->
                                val data = if (it.`var`.`is`(Int64ListVar::class.java)) {
                                    it.`var` unpack_ Int64ListVar::class.java
                                } else {
                                    Int64ListVar.newBuilder().build()
                                }
                                Int64ListVar.newBuilder(data).addAllValues(longV).build().pack_()
                            }
                    }

                    GlobalVarType.BoolList -> {
                        value.mapNotNull { kotlin.runCatching { it.toBoolean() }.getOrNull() }
                            .let { boolV ->
                                val data = if (it.`var`.`is`(BoolListVar::class.java)) {
                                    it.`var` unpack_ BoolListVar::class.java
                                } else {
                                    BoolListVar.newBuilder().build()
                                }
                                BoolListVar.newBuilder(data).addAllValues(boolV).build().pack_()
                            }
                    }
                    GlobalVarType.StringList -> {
                        val data = if (it.`var`.`is`(StringListVar::class.java)) {
                            it.`var` unpack_ StringListVar::class.java
                        } else {
                            StringListVar.newBuilder().build()
                        }
                        logger.d("before add to StringList: $value --> current: $data")
                        StringListVar.newBuilder(data).addAllValues(value).build().pack_()
                    }
                    else -> {
                        null
                    }
                }

                shortXManager.addGlobalVar(
                    GlobalVar.newBuilder(it).setVar(newVarProtoAny).build()
                )
                loadStateInternal(it.id)
            }
        } else {
            val current = state.value.globalVar
            current?.let {
                shortXManager.addGlobalVar(
                    GlobalVar.newBuilder(it).setVar(value.first().
                    toVarValueNotList(current.type.toType()))
                        .build()
                )
                loadStateInternal(it.id)
            }
        }
    }
}