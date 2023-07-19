package tornaco.apps.shortx.ui.addda

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.main.parseShareContentDA
import java.net.URL
import java.util.*
import javax.inject.Inject

fun defaultNewDAId() = "DA" + UUID.randomUUID().toString()

data class AddDirectActionState(
    val id: String = defaultNewDAId(),
    val title: String = "",
    val description: String = "",
    val isEditDirectAction: Boolean = false,
    val createTime: Long = System.currentTimeMillis(),
    val daChanged: Boolean = false,
    val copiedAction: Action? = null,
    // For online.
    val onlineDALoadState: LoadState<Unit> = LoadState.Success(Unit)
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AddDirectActionViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<AddDirectActionState>(context, { AddDirectActionState() }) {

    private val _daSaved = MutableSharedFlow<String>()
    val daSaved = _daSaved.asSharedFlow()

    val actionContentStateHandler = ActionContentStateHandler()

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

    fun save() {
        viewModelScope.launch {
            val actions = actionContentStateHandler.state.value.actions

            val protoActions = actions.map {
                it.toProtoAction().pack_()
            }
            val da = DirectAction.newBuilder()
                .setId(state.value.id)
                .setCreateTime(state.value.createTime)
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
            initFromDirectAction(shortXManager.getDirectActionById(id))
        }
    }

    fun initFromDirectAction(action: DirectAction?) {
        action?.let { da ->
            logger.d("initFromDirectAction: $da")

            actionContentStateHandler.init(da.actionsList.mapNotNull {
                it.toAction(
                    shortXManager
                )
            })

            updateState {
                copy(
                    id = da.id,
                    title = da.title,
                    description = da.description,
                    isEditDirectAction = true,
                    createTime = da.createTime
                )
            }
        }
    }

    fun initFromUrl(url: String?) {
        url?.let { _ ->
            logger.d("initFromUrl: $url")
            viewModelScope.launch {
                updateState { copy(onlineDALoadState = LoadState.Loading) }
                withContext(Dispatchers.IO) {
                    if (BuildProp.APP_IS_DEBUG) {
                        delay(1000)
                    }
                    runCatching {
                        val content = URL(url).readText()
                        val da = parseShareContentDA(content)
                        updateState {
                            copy(onlineDALoadState = LoadState.Success(Unit))
                        }
                        requireNotNull(da) { "Fail to parse Direct action: \n$content" }
                        initFromDirectAction(da)
                    }.onFailure {
                        logger.e(it)
                        updateState {
                            copy(onlineDALoadState = LoadState.Error(it))
                        }
                    }
                }
            }
        }
    }
}