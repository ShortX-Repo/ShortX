package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.audioManager
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object SetRingerMode : Actor {
    
        return tornaco.apps.shortx.core.proto.action.SetRingerMode::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.SetRingerMode::class.java
        
        // Handle switch mode (value = 3) by cycling through normal, vibrate, silent
        if (data.mode.number == 3) {
            val currentMode = androidContext.audioManager.ringerMode
            val nextMode = when (currentMode) {
                0 -> 1 // silent -> vibrate
                1 -> 2 // vibrate -> normal
                2 -> 0 // normal -> silent
                else -> 2 // default to normal
            }
            androidContext.audioManager.ringerModeInternal = nextMode
        } else {
            // For normal, vibrate, silent modes, use the value directly
            androidContext.audioManager.ringerModeInternal = data.mode.number
        }
        
        Actor.Res.EMPTY
    }
}