package tornaco.apps.shortx.ui.addda

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.App
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ContextViewModel
import java.text.Collator
import java.util.*
import javax.inject.Inject

data class AddDirectActionState(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val actions: List<Action> = emptyList(),
    val isEditDirectAction: Boolean = false,
    val daChanged: Boolean = false,
    val installedApps: List<App> = emptyList(),
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AddDirectActionViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    ContextViewModel<AddDirectActionState>(context) {

    private val _daSaved = MutableSharedFlow<String>()
    val daSaved = _daSaved.asSharedFlow()

    override fun initState(): AddDirectActionState {
        return AddDirectActionState()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                shortXManager.getInstalledApps()
                    .sortedWith { o1, o2 ->
                        Collator.getInstance(Locale.CHINESE).compare(o1.label, o2.label)
                    }.sortedBy { it.category.ordinal }
            }
            updateState {
                copy(installedApps = apps)
            }
        }
    }

    fun setTitle(title: String) {
        if (title != state.value.title) {
            updateState {
                copy(title = title, daChanged = true)
            }
        }
    }

    fun setDescription(description: String) {
        if (description != state.value.description) {
            updateState {
                copy(description = description, daChanged = true)
            }
        }
    }


    fun addAction(action: Action) {
        updateState {
            copy(actions = actions.toMutableList().apply {
                add(action)
            }, daChanged = true)
        }
    }

    fun removeAction(action: Action) {
        updateState {
            copy(actions = actions.toMutableList().apply {
                remove(action)
            }, daChanged = true)
        }
    }

    fun sortActions(actions: List<Action>) {
        updateState {
            copy(actions = actions, daChanged = true)
        }
    }

    fun save() {
        viewModelScope.launch {
            val actions = state.value.actions

            val protoActions = actions.map {
                it.toProtoAction().pack_()
            }
            val da = DirectAction.newBuilder()
                .setId(state.value.id)
                .setLastUpdateTime(System.currentTimeMillis())
                .setTitle(state.value.title.fallbackOnEmpty(Date().toString()))
                .apply {
                    if (state.value.description.isNotEmpty()) {
                        description = state.value.description
                    }
                }
                .addAllActions(protoActions)
                .build()

            shortXManager.addDirectAction(da)
            _daSaved.emit(da.id)
        }
    }

    fun delete(daId: String) {
        viewModelScope.launch {
            shortXManager.deleteDirectAction(daId)
        }
    }

    fun initFromDirectActionId(daId: String?) {
        daId?.let { id ->
            viewModelScope.launch {
                shortXManager.getDirectActionById(id)?.let { da ->
                    updateState {
                        copy(
                            id = da.id,
                            title = da.title,
                            description = da.description,
                            actions = da.actionsList.mapNotNull { it.toAction(shortXManager) },
                            isEditDirectAction = true,
                        )
                    }
                }
            }
        }
    }
}