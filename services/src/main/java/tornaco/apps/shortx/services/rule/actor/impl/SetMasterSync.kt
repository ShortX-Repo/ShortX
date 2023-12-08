package tornaco.apps.shortx.services.rule.actor.impl

import android.content.ContentResolver
import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object SetMasterSync : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.SetMasterSync::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d =
            action unpack_ tornaco.apps.shortx.core.proto.action.SetMasterSync::class.java
        val sync: OnOffToggle = d.sync
        val isSyncCurrent = ContentResolver.getMasterSyncAutomatically()
        val isToSync: Boolean = when (sync) {
            OnOffToggle.OnOffToggle_On -> {
                true
            }

            OnOffToggle.OnOffToggle_Off -> {
                false
            }

            OnOffToggle.OnOffToggle_Toggle -> {
                !isSyncCurrent
            }

            OnOffToggle.UNRECOGNIZED -> {
                error("UNRECOGNIZED")
            }
        }
        logger.d("isToSync: $isToSync")
        ContentResolver.setMasterSyncAutomatically(isToSync)
    }
}