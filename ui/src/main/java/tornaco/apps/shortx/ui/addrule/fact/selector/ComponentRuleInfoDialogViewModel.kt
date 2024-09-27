package tornaco.apps.shortx.ui.addrule.fact.selector

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.ui.base.LoadState
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import java.net.URL
import javax.inject.Inject


data class ComponentRuleInfoState(
    val infoState: LoadState<RemoteRuleInfo> = LoadState.Loading
)

@DoNotStrip
data class RemoteRuleInfo(
    val team: String?,
    val description: String,
    val contributors: List<String>,
    val relativeUrl: String?
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ComponentRuleInfoDialogViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<ComponentRuleInfoState>(context, { ComponentRuleInfoState() }) {
    private val gson by lazy { Gson() }

    fun load(url: String) {
        updateState { copy(infoState = LoadState.Loading) }
        viewModelScope.launch {
            val info = withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    gson.fromJson(
                        URL(url).readText(),
                        RemoteRuleInfo::class.java
                    )
                }.getOrElse {
                    logger.e(it, "load fail")
                    null
                }
            }
            logger.d("info: $info")
            if (info == null) {
                updateState { copy(infoState = LoadState.Error()) }
            } else {
                updateState { copy(infoState = LoadState.Success(info)) }
            }
        }
    }
}