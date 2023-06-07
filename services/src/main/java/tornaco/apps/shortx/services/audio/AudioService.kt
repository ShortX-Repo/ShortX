package tornaco.apps.shortx.services.audio

import android.media.AudioManager
import android.media.AudioPlaybackConfiguration
import android.media.AudioPlaybackConfiguration.PLAYER_STATE_STARTED
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.util.OsUtils
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.SystemService
import tornaco.apps.shortx.services.launchSafety
import tornaco.apps.shortx.services.systemServiceScope

sealed class FocusEvent(val target: AppPkg) {
    class Gain(target: AppPkg) : FocusEvent(target)
    class Lost(target: AppPkg) : FocusEvent(target)
}

class AudioService(shortXContext: ShortXContext) : SystemService(shortXContext) {
    private val currentPlayingAppPkgs = mutableListOf<AppPkg>()

    private val _focusEvent = MutableSharedFlow<FocusEvent>()
    val focusEvent = _focusEvent.asSharedFlow()

    override fun systemReady() {
        super.systemReady()
        registerListener()
    }

    private fun registerListener() {
        if (OsUtils.isOOrAbove()) {
            kotlin.runCatching {
                context.audioManager.registerAudioPlaybackCallback(object :
                    AudioManager.AudioPlaybackCallback() {
                    override fun onPlaybackConfigChanged(configs: List<AudioPlaybackConfiguration>?) {
                        super.onPlaybackConfigChanged(configs)
                        configs?.let { handlePlaybackConfigChanged(it) }
                    }
                }, handler)
            }.onFailure {
                shortXContext.watchDog.onFatalError(it, "registerAudioPlaybackCallback")
            }
        }
    }

    private fun handlePlaybackConfigChanged(configs: List<AudioPlaybackConfiguration>) =
        kotlin.runCatching {
            logger.d("handlePlaybackConfigChanged: $configs")
            val appToPlayerStates = configs.mapNotNull { configuration ->
                val uid = configuration.clientUid
                val appOrNull = shortXContext.appState.uidToApp(uid)
                logger.d("Playback state: ${configuration.playerState}, app: $appOrNull")
                appOrNull?.let { it to configuration.playerState }
            }
            logger.d(
                "appToPlayerStates: ${
                    appToPlayerStates.map {
                        "${it.first.label} - ${
                            playerStateToString(
                                it.second
                            )
                        }"
                    }
                }"
            )

            val playingApps = appToPlayerStates.filter { it.second == PLAYER_STATE_STARTED }
            logger.d("playingApps: ${playingApps.map { it.first.label }}")

            val playingPkgs = playingApps.map { it.first.pkg }.distinct()

            val gainPkgs = playingPkgs.subtract(currentPlayingAppPkgs)
            val losingPkgs = currentPlayingAppPkgs.subtract(playingPkgs)

            logger.d("gainPkgs: $gainPkgs")
            logger.d("losingPkgs: $losingPkgs")

            systemServiceScope.launchSafety {
                gainPkgs.forEach { _focusEvent.emit(FocusEvent.Gain(it)) }
            }
            systemServiceScope.launchSafety {
                losingPkgs.forEach { _focusEvent.emit(FocusEvent.Lost(it)) }
            }

            currentPlayingAppPkgs.clear()
            currentPlayingAppPkgs.addAll(playingPkgs)
        }.onFailure {
            shortXContext.watchDog.onFatalError(it, "handlePlaybackConfigChanged")
        }
}

/** @hide
 */
private fun playerStateToString(@AudioPlaybackConfiguration.PlayerState state: Int): String {
    return when (state) {
        AudioPlaybackConfiguration.PLAYER_STATE_UNKNOWN -> "PLAYER_STATE_UNKNOWN"
        AudioPlaybackConfiguration.PLAYER_STATE_RELEASED -> "PLAYER_STATE_RELEASED"
        AudioPlaybackConfiguration.PLAYER_STATE_IDLE -> "PLAYER_STATE_IDLE"
        PLAYER_STATE_STARTED -> "PLAYER_STATE_STARTED"
        AudioPlaybackConfiguration.PLAYER_STATE_PAUSED -> "PLAYER_STATE_PAUSED"
        AudioPlaybackConfiguration.PLAYER_STATE_STOPPED -> "PLAYER_STATE_STOPPED"
        AudioPlaybackConfiguration.PLAYER_UPDATE_DEVICE_ID -> "PLAYER_UPDATE_DEVICE_ID"
        else -> "invalid state $state"
    }
}