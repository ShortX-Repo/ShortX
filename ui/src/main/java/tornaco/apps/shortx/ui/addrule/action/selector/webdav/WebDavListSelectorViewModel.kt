package tornaco.apps.shortx.ui.addrule.action.selector.webdav

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.common.WebDavProfile
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class WebDavListSelectorState(
    val id: String = defaultNewActionId(),
    val url: String = "",
    val depth: String = "",
    val profile: WebDavProfile? = null
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class WebDavListSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<WebDavListSelectorState>(context, {
        WebDavListSelectorState()
    }) {

    fun init(currentAction: Action.WebDavList?) {
        currentAction?.let {
            updateState {
                copy(
                    id = currentAction.id,
                    url = currentAction.url,
                    profile = currentAction.profile,
                    depth = currentAction.depth
                )
            }
        }
    }

    fun inputUrl(url: String) {
        updateState {
            copy(url = url)
        }
    }

    fun inputDepth(depth: String) {
        updateState {
            copy(depth = depth)
        }
    }

    fun setProfile(profile: WebDavProfile) {
        updateState {
            copy(profile = profile)
        }
    }
}