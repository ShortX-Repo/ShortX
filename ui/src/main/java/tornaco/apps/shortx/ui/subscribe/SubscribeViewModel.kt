package tornaco.apps.shortx.ui.subscribe

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tornaco.android.sec.net.S
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.ui.base.StateViewModel
import tornaco.apps.shortx.ui.base.UiState
import tornaco.apps.shortx.ui.base.data
import tornaco.apps.shortx.ui.base.error
import tornaco.apps.shortx.ui.subscribe.code.Repo
import tornaco.apps.shortx.ui.subscribe.code.SubscriptionConfig
import tornaco.apps.shortx.ui.subscribe.code.VerifyResult
import tornaco.apps.shortx.ui.subscribe.util.isGPToken
import tornaco.apps.shortx.ui.subscribe.util.setLVLToken
import javax.inject.Inject

sealed interface SubscriptionSource {
    object GooglePlay : SubscriptionSource
    data class ActivationCode(val code: String) : SubscriptionSource
}

data class SubscriptionState(
    val isSubscribed: Boolean = false,
    val source: SubscriptionSource? = null,
    val subscriptionConfigState: UiState<SubscriptionConfig> = UiState.Loading
)

enum class CodeVerifyEvent {
    Verifying,
    Success,
    Fail,
    NetworkError,
    Error,
}

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class SubscribeViewModel @Inject constructor(@ApplicationContext val context: Context) :
    StateViewModel<SubscriptionState>({ SubscriptionState() }) {
    private val logger = Logger("ShortX")

    private val repo by lazy { Repo(context) }

    private val _codeVerifyEvent = MutableSharedFlow<CodeVerifyEvent>()
    val codeVerifyEvent = _codeVerifyEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            LVLStateHolder.container.stateFlow.collectLatest {
                logger.p("LVLStateHolder.container.stateFlow changed")
                loadState()
            }
        }
    }

    fun loadState() {
        logger.p("loadState")
        val subscribeState = LVLStateHolder.container.stateFlow.value
        val isSubscribed = subscribeState.isSubscribed
        val token = subscribeState.token

        logger.p("loadState, isSubscribed: $isSubscribed")

        var source: SubscriptionSource? = null
        if (isSubscribed && token != null) {
            source = if (token.isGPToken()) {
                SubscriptionSource.GooglePlay
            } else {
                SubscriptionSource.ActivationCode(token)
            }
        }
        updateState {
            copy(isSubscribed = isSubscribed, source = source)
        }

        // Load config
        if (state.value.subscriptionConfigState !is UiState.Loaded<SubscriptionConfig>) viewModelScope.launch {
            updateState { copy(subscriptionConfigState = UiState.Loading) }
            delay(2000)
            repo.getSubscriptionConfig().let {
                if (it.isSuccessful()) {
                    updateState { copy(subscriptionConfigState = UiState.Loaded(it.data!!)) }
                } else if (it.isError()) {
                    updateState { copy(subscriptionConfigState = UiState.Error(it.error)) }
                }
            }
        }
    }

    fun verify(code: String) {
        viewModelScope.launch {
            _codeVerifyEvent.emit(CodeVerifyEvent.Verifying)
            delay(5834)
            when (val res = repo.verify(code)) {
                is VerifyResult.Fail -> {
                    _codeVerifyEvent.emit(CodeVerifyEvent.Fail)
                }

                VerifyResult.NetworkError -> {
                    _codeVerifyEvent.emit(CodeVerifyEvent.NetworkError)
                }

                is VerifyResult.Error -> {
                    _codeVerifyEvent.emit(CodeVerifyEvent.Error)
                }

                is VerifyResult.Success -> {
                    S.c(code, res.data.k)
                    tornaco.apps.shortx.ui.util.verifyOtp(code, requireNotNull(res.data.i),
                        onRes = {
                            setLVLToken(code)
                            viewModelScope.launch {
                                _codeVerifyEvent.emit(CodeVerifyEvent.Success)
                            }
                        },
                        onError = {
                            viewModelScope.launch {
                                _codeVerifyEvent.emit(CodeVerifyEvent.Error)
                            }
                        })
                }
            }
        }
    }
}