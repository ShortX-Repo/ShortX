package tornaco.apps.shortx.ui.main

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tornaco.apps.shortx.core.BuildPropInternal
import tornaco.apps.shortx.core.IAudioRecordingListener
import tornaco.apps.shortx.ui.base.ContextViewModel
import tornaco.apps.shortx.ui.main.model.MainState
import tornaco.apps.shortx.ui.settings.pref.Pref
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
open class MainViewModel @Inject constructor(@ApplicationContext context: Context) :
    ContextViewModel<MainState>(context, { MainState() }) {
    private val pref by lazy { Pref(context) }

    private val audioRecordingObs = object : IAudioRecordingListener.Stub() {
        override fun onStartRecording() {
            logger.d("audioRecordingObs, onStartRecording")
            updateState { copy(isAudioRecording = shortXManager.isAudioRecording()) }
        }

        override fun onStopRecording() {
            logger.d("audioRecordingObs, onStopRecording")
            updateState { copy(isAudioRecording = shortXManager.isAudioRecording()) }
        }
    }

    init {
        updateState {
            copy(
                isActivated = shortXManager.isInstalled,
                isFrameworkUpdated = shortXManager.isFrameworkUpdated(BuildPropInternal.BUILD_FINGERPRINT),
                isAudioRecording = shortXManager.isAudioRecording()
            )
        }

        shortXManager.registerAudioRecordingObs(audioRecordingObs)

        viewModelScope.launch {
            pref.privacyStatementAcceptedFlow.collectLatest {
                logger.d("isPrivacyStatementAccepted changed: $it")
                updateState { copy(isPrivacyStatementAccepted = it) }
            }
        }

        viewModelScope.launch {
            pref.ignoreFrameworkUpdate.collectLatest {
                logger.d("ignoreFrameworkUpdate changed: $it")
                updateState { copy(isIgnoreFrameworkUpdate = it) }
            }
        }

        viewModelScope.launch {
            pref.hideSubscribeInfoFlow.collectLatest {
                updateState { copy(isHideSubscribeInfo = it) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        shortXManager.unregisterAudioRecordingObs(audioRecordingObs)
    }


    fun reboot() {
        shortXManager.reboot()
    }

    fun acceptPrivacyStatement() {
        viewModelScope.launch {
            pref.setPrivacyStatementAccepted(true)
        }
    }
}