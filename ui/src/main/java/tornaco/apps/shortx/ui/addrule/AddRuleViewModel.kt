package tornaco.apps.shortx.ui.addrule

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Tag
import androidx.compose.ui.graphics.vector.ImageVector
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.ui.base.BaseViewModel
import javax.inject.Inject

data class AddRuleState(val fact: Fact? = null, val selectableFacts: List<Fact> = emptyList())

sealed class Fact(val label: () -> String, val icon: ImageVector) {
    data class KeyEvent(val keyCode: Int) : Fact({ "KeyEvent $keyCode" }, Icons.Filled.Key)
    data class ActivityStarted(val name: String) :
        Fact({ "Activity Started $name" }, Icons.Filled.Tag)
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class AddRuleViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    BaseViewModel<AddRuleState>(context) {

    override fun initState(): AddRuleState {
        return AddRuleState(selectableFacts = listOf(
            Fact.KeyEvent(0),
            Fact.ActivityStarted(""),
        ))
    }

    fun setFact(fact: Fact?) {
        updateState {
            copy(fact = fact)
        }
    }

}