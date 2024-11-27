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

data class WebDavPutSelectorState(
    val id: String = defaultNewActionId(),
    val url: String = "",
    val localFilePath: String = "",
    val contentType: String = "",
    val profile: WebDavProfile? = null
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class WebDavPutSelectorViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<WebDavPutSelectorState>(context, {
        WebDavPutSelectorState()
    }) {

    fun init(currentAction: Action.WebDavPut?) {
        currentAction?.let {
            updateState {
                copy(
                    id = currentAction.id,
                    url = currentAction.url,
                    localFilePath = currentAction.localFilePath,
                    profile = currentAction.profile,
                    contentType = currentAction.contentType
                )
            }
        }
    }

    fun inputUrl(url: String) {
        updateState {
            copy(url = url)
        }
    }

    fun inputLocalFilePath(path: String) {
        updateState {
            copy(localFilePath = path)
        }
    }

    fun inputContentType(contentType: String) {
        updateState {
            copy(contentType = contentType)
        }
    }

    fun setProfile(profile: WebDavProfile) {
        updateState {
            copy(profile = profile)
        }
    }
}