package tornaco.apps.shortx.ui.addrule.action.selector

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.common.UserInfo
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import java.util.*
import javax.inject.Inject

data class UserState(
    val allUsers: List<UserInfo> = emptyList()
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class UserSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<UserState>(
        context, { UserState() }
    ) {

    override fun resetState() {
        super.resetState()
        updateState(UserState())
    }

    fun loadUsers() = viewModelScope.launch {
        updateState { copy(allUsers = shortXManager.getAllUsers()) }
    }
}