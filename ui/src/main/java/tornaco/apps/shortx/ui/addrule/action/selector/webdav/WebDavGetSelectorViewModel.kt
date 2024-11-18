package tornaco.apps.shortx.ui.addrule.action.selector.webdav

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tornaco.apps.shortx.core.proto.common.HttpRequestHeader
import tornaco.apps.shortx.core.proto.common.WebDavProfile
import tornaco.apps.shortx.ui.addrule.action.model.Action
import tornaco.apps.shortx.ui.addrule.action.model.defaultNewActionId
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class WebDavGetSelectorState(
    val id: String = defaultNewActionId(),
    val url: String = "",
    val destPath: String = "",
    val headers: List<HttpRequestHeader> = emptyList(),
    val profile: WebDavProfile? = null
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class WebDavGetSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<WebDavGetSelectorState>(context, {
        WebDavGetSelectorState()
    }) {

    fun init(currentAction: Action.WebDavGet?) {
        currentAction?.let {
            updateState {
                copy(
                    id = currentAction.id,
                    url = currentAction.url,
                    destPath = currentAction.destPath,
                    profile = currentAction.profile,
                    headers = currentAction.headers
                )
            }
        }
    }

    fun inputUrl(url: String) {
        updateState {
            copy(url = url)
        }
    }

    fun inputDest(destPath: String) {
        updateState {
            copy(destPath = destPath)
        }
    }

    fun setProfile(profile: WebDavProfile) {
        updateState {
            copy(profile = profile)
        }
    }
}