package tornaco.apps.shortx.services.rule.actor.impl

import android.annotation.SuppressLint
import android.view.KeyEvent
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.MediaPlaybackAction
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ctrl.Device
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object MediaPlayback : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.MediaPlayback::class.java
    }

    @Suppress("MoveVariableDeclarationIntoWhen")
    @SuppressLint("MissingPermission")
    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.MediaPlayback::class.java
        logger.d("MediaPlayback: ${d.action}")
        val action: MediaPlaybackAction = d.action

        val keyCode: Int = when (action) {
            MediaPlaybackAction.MediaPlaybackAction_Play -> {
                KeyEvent.KEYCODE_MEDIA_PLAY
            }

            MediaPlaybackAction.MediaPlaybackAction_Pause -> {
                KeyEvent.KEYCODE_MEDIA_PAUSE
            }

            MediaPlaybackAction.MediaPlaybackAction_SkipToNext -> {
                KeyEvent.KEYCODE_MEDIA_NEXT
            }

            MediaPlaybackAction.MediaPlaybackAction_SkipToPrevious -> {
                KeyEvent.KEYCODE_MEDIA_PREVIOUS
            }

            MediaPlaybackAction.MediaPlaybackAction_FastForward -> {
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
            }

            MediaPlaybackAction.MediaPlaybackAction_Rewind -> {
                KeyEvent.KEYCODE_MEDIA_REWIND
            }

            MediaPlaybackAction.MediaPlaybackAction_Stop -> {
                KeyEvent.KEYCODE_MEDIA_STOP
            }

            MediaPlaybackAction.UNRECOGNIZED -> {
                throw Error("UNRECOGNIZED")
            }
        }

        logger.d("MediaPlaybackAction -> keyCode: $keyCode")

        device.pressReleaseKeycode(
            keyCode,
            Device.INJECT_MODE_ASYNC
        )
    }
}