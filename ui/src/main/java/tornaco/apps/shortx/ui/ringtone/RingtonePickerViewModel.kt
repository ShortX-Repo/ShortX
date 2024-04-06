package tornaco.apps.shortx.ui.ringtone

import android.annotation.SuppressLint
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.proto.common.Ringtone
import tornaco.apps.shortx.ui.base.ShortXContextViewModel
import javax.inject.Inject

data class RingtoneListState(
    val alarms: List<Ringtone> = emptyList(),
    val notifications: List<Ringtone> = emptyList(),
    val ringtones: List<Ringtone> = emptyList(),
    val isLoading: Boolean = false
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class RingtoneListViewModel @Inject constructor(@ApplicationContext context: Context) :
    ShortXContextViewModel<RingtoneListState>(context, { RingtoneListState() }) {
    private var playingRingtone: android.media.Ringtone? = null

    fun loadState() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val all = shortXManager.getAllRingtones()
            logger.d("all: ${all.joinToString { it.title }}")
            val alarms = all.filter { it.type == RingtoneManager.TYPE_ALARM }
            val notifications = all.filter { it.type == RingtoneManager.TYPE_NOTIFICATION }
            val ringtones = all.filter { it.type == RingtoneManager.TYPE_RINGTONE }
            updateState {
                copy(
                    alarms = alarms,
                    notifications = notifications,
                    ringtones = ringtones,
                    isLoading = false
                )
            }
        }
    }

    fun play(ringtone: Ringtone) {
        playingRingtone?.stop()
        playingRingtone = RingtoneManager.getRingtone(context, Uri.parse(ringtone.uri))
        playingRingtone?.play()
    }

    override fun onCleared() {
        super.onCleared()
        playingRingtone?.stop()
    }
}