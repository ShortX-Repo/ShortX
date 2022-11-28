package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.ui.base.BaseViewModel
import java.util.*
import javax.inject.Inject


data class Rule(val label: String, val updateTime: String)

data class MainState(val rules: List<Rule> = emptyList())

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(@ApplicationContext private val context: Context) :
    BaseViewModel<MainState>(context) {

    override fun initState(): MainState {
        return MainState()
    }

    fun loadRules() {
        viewModelScope.launch {
            val rules = shortXManager.getAllRules().map {
                Rule(label = it.id, updateTime = Date(it.lastUpdateTime).toString())
            }
            updateState {
                copy(rules = rules)
            }
        }
    }
}