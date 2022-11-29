package tornaco.apps.shortx.ui.addrule

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Tag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.Pkg
import tornaco.apps.shortx.core.proto.action.TakeScreenshot
import tornaco.apps.shortx.core.proto.condition.CurrentPkg
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.ProtoMessage
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.unKnownPkg
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.ui.base.BaseViewModel
import java.util.*
import javax.inject.Inject

data class AddRuleState(
    val title: String = "",
    val description: String = "",
    val fact: Fact? = null,
    val selectableFacts: List<Fact> = emptyList(),
    val conditions: List<Condition> = emptyList(),
    val selectableConditions: List<Condition> = emptyList(),
    val actions: List<Action> = emptyList(),
    val selectableActions: List<Action> = emptyList(),
)

sealed class Fact(val label: () -> String, val icon: ImageVector) {
    data class KeyEvent(val keyCode: Int) : Fact({ "KeyEvent $keyCode" }, Icons.Filled.Key)
    data class ActivityStarted(val name: String) :
        Fact({ "Activity Started $name" }, Icons.Filled.Tag)
}

fun Fact.toProtoFact(): ProtoMessage {
    return when (this) {
        is Fact.KeyEvent -> {
            tornaco.apps.shortx.core.proto.fact.KeyEvent.newBuilder().setKeyCode(keyCode).build()
        }
        is Fact.ActivityStarted -> TODO()
    }
}

sealed class Condition(val label: (Context) -> String, val icon: ImageVector) {
    data class CurrentPkg(val pkg: Pkg) : Condition({ "$pkg" }, Icons.Filled.Key)
}

fun Condition.toProtoCondition(): ProtoMessage {
    return when (this) {
        is Condition.CurrentPkg -> {
            CurrentPkg.newBuilder().setPkgName(this.pkg.pkgName).build()
        }
    }
}

sealed class Action(val label: (Context) -> String, val icon: ImageVector) {
    object TakeScreenshot : Action({ "Take Screenshot" }, Icons.Filled.Key)
}

fun Action.toProtoAction(): ProtoMessage {
    return when (this) {
        is Action.TakeScreenshot -> {
            TakeScreenshot.newBuilder().build()
        }
    }
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AddRuleViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    BaseViewModel<AddRuleState>(context) {

    private val _ruleSaved = MutableSharedFlow<String>()
    val ruleSaved = _ruleSaved.asSharedFlow()

    override fun initState(): AddRuleState {
        return AddRuleState(
            selectableFacts = listOf(
                Fact.KeyEvent(KeyEvent.KEYCODE_UNKNOWN),
                Fact.ActivityStarted("N/A")),
            selectableConditions = listOf(
                Condition.CurrentPkg(unKnownPkg)
            ),
            selectableActions = listOf(
                Action.TakeScreenshot
            )
        )
    }

    fun setTitle(title: String) {
        updateState {
            copy(title = title)
        }
    }

    fun setDescription(description: String) {
        updateState {
            copy(description = description)
        }
    }

    fun setFact(fact: Fact?) {
        updateState {
            copy(fact = fact)
        }
    }

    fun addCondition(condition: Condition) {
        updateState {
            copy(conditions = conditions.toMutableList().apply {
                add(condition)
            })
        }
    }

    fun addAction(action: Action) {
        updateState {
            copy(actions = actions.toMutableList().apply {
                add(action)
            })
        }
    }

    fun save() {
        viewModelScope.launch {
            val fact = state.value.fact ?: return@launch
            val conditions = state.value.conditions
            val actions = state.value.actions

            val protoFact = fact.toProtoFact().pack_()
            val protoConditions = conditions.map {
                it.toProtoCondition().pack_()
            }
            val protoActions = actions.map {
                it.toProtoAction().pack_()
            }
            val rule =
                Rule.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setLastUpdateTime(System.currentTimeMillis())
                    .setFact(protoFact)
                    .setTitle(state.value.title.fallbackOnEmpty(Date().toString()))
                    .setDescription(state.value.description.fallbackOnEmpty("No description"))
                    .addAllConditions(protoConditions)
                    .addAllActions(protoActions)
                    .build()

            shortXManager.addRule(rule)
            _ruleSaved.emit(rule.id)
        }
    }
}