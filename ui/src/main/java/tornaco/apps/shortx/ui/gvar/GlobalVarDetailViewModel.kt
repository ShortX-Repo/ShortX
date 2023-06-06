package tornaco.apps.shortx.ui.gvar

import android.content.ComponentName
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.rule.addOrUpdateValue
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.valueList
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

class GlobalVarDetailViewModel : StateViewModel<GlobalVarDetailState>({ GlobalVarDetailState() }) {
    private val logger = Logger("GlobalVarDetailViewModel")

    fun loadState(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadStateInternal(id)
        }
    }

    private suspend fun loadStateInternal(id: String) {
        updateState { copy(isLoading = true) }
        afterNavDelay()

        val v = shortXManager.getGlobalVarByName(id)
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
                shortXManager.deleteGlobalVar(it.name)
                shortXManager.addGlobalVar(GlobalVar.newBuilder(it).setName(name).build())
                loadState(name)
            }
        }
    }

    fun addOrUpdateValue(value: List<String>) {
        viewModelScope.launch {
            state.value.globalVar?.addOrUpdateValue(value)
            state.value.globalVar?.name?.let { loadStateInternal(it) }
        }
    }
}