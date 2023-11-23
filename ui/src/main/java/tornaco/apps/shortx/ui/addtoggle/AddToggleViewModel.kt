package tornaco.apps.shortx.ui.addtoggle

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildProp
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.proto.toStringPair
import tornaco.apps.shortx.core.proto.toggles.Toggle
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.util.DateUtils
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.maskAll
import tornaco.apps.shortx.ui.addrule.action.ActionContentStateHandler
import tornaco.apps.shortx.ui.addrule.action.model.toAction
import tornaco.apps.shortx.ui.addrule.action.model.toProtoAction
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.settings.pref.Pref
import java.util.*
import javax.inject.Inject

fun defaultNewToggleId() = "toggle-" + UUID.randomUUID().toString()

data class AddToggleState(
    val id: String = defaultNewToggleId(),
    val title: String = "",
    val description: String = "",
    val versionCode: Long = 1L,
    val isEditToggle: Boolean = false,
    val createTime: Long = System.currentTimeMillis(),
    val toggleChanged: Boolean = false,
    val isEnabled: Boolean = false,
    val evaluatingActions: List<String> = emptyList(),
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AddToggleViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<AddToggleState>(context, { AddToggleState() }) {

    private val _toggleSaved = MutableSharedFlow<String>()
    val toggleSaved = _toggleSaved.asSharedFlow()

    val pref by lazy { Pref(context) }

    val actionOnEnabledStateHandler = ActionContentStateHandler()
    val actionOnDisabledStateHandler = ActionContentStateHandler()
    val actionOnDeletedStateHandler = ActionContentStateHandler()

    fun setTitle(title: String) {
        if (title != state.value.title) {
            updateState {
                copy(title = title, toggleChanged = true)
            }
        }
    }

    fun setDescription(description: String) {
        if (description != state.value.description) {
            updateState {
                copy(description = description, toggleChanged = true)
            }
        }
    }

    // Save with token to check subscribe state.
    fun save(token: String?) {
        logger.p("save with token: ${token?.maskAll()}")
        viewModelScope.launch {
            val authorPref = pref.shareAuthorInfo.first()

            // Hooks
            val actionsOnEnabled = actionOnEnabledStateHandler.actions
            val actionsOnDisabled = actionOnDisabledStateHandler.actions
            val actionsOnDeleted = actionOnDeletedStateHandler.actions

            val toggle = Toggle.newBuilder()
                .setId(state.value.id)
                .setCreateTime(state.value.createTime)
                .setLastUpdateTime(System.currentTimeMillis())
                .setTitle(
                    state.value.title.fallbackOnEmpty(
                        DateUtils.formatHHMMSS(
                            System.currentTimeMillis()
                        )
                    )
                )
                .setDescription(state.value.description.fallbackOnEmpty(""))
                .setIsEnabled(state.value.isEnabled)
                .setAuthor(authorPref)
                .setVersionCode(state.value.versionCode)
                .addAllActionsOnEnabled(actionsOnEnabled.map {
                    it.toProtoAction().pack_()
                })
                .addAllActionsOnDisabled(actionsOnDisabled.map {
                    it.toProtoAction().pack_()
                })
                .addAllActionsOnDeleted(actionsOnDeleted.map {
                    it.toProtoAction().pack_()
                })
                .addAllExtras(mutableListOf<StringPair>().apply {
                    if (!token.isNullOrEmpty()) {
                        add((BuildProp.APP_PACKAGE_NAME to token).toStringPair())
                    }
                })
                .build()

            shortXManager.addToggle(toggle)
            _toggleSaved.emit(toggle.id)
        }
    }

    fun delete(toggleId: String) {
        viewModelScope.launch {
            shortXManager.deleteToggle(toggleId)
        }
    }

    fun initFromToggleId(toggleId: String?) {
        toggleId?.let { id ->
            viewModelScope.launch {
                shortXManager.getToggleById(id)?.let { toggle ->
                    logger.d("initFromToggle: $toggle")
                    initFromToggle(toggle)
                }
            }
        }
    }

    fun initFromToggle(toggle: Toggle?) {
        viewModelScope.launch {
            toggle?.let { tg ->
                logger.d("initFromToggle: $tg")
                updateState {
                    copy(
                        id = tg.id,
                        title = tg.title,
                        description = tg.description,
                        versionCode = tg.versionCode,
                        isEditToggle = true,
                        createTime = tg.createTime,
                        isEnabled = tg.isEnabled,
                    )
                }

                actionOnEnabledStateHandler.init(
                    actions = tg.actionsOnEnabledList.mapNotNull {
                        it.toAction(
                            shortXManager
                        )
                    })

                actionOnDisabledStateHandler.init(
                    actions = tg.actionsOnDisabledList.mapNotNull {
                        it.toAction(
                            shortXManager
                        )
                    })

                actionOnDeletedStateHandler.init(
                    actions = tg.actionsOnDeletedList.mapNotNull {
                        it.toAction(
                            shortXManager
                        )
                    })
            }
        }
    }
}