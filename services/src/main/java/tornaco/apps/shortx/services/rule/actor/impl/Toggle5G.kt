package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.OnOffToggle
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams

object Toggle5G : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.Toggle5G::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val data = action unpack_ tornaco.apps.shortx.core.proto.action.Toggle5G::class.java
        val onOff: OnOffToggle = data.onOff
        val switcher = getFiveGSwitcher()
        val isCurrent5G = switcher.is5GEnabled()
        when (onOff) {
            OnOffToggle.OnOffToggle_On -> {
                switcher.set5GEnabled(true)
            }

            OnOffToggle.OnOffToggle_Off -> {
                switcher.set5GEnabled(false)
            }

            OnOffToggle.OnOffToggle_Toggle -> {
                switcher.set5GEnabled(!isCurrent5G)
            }

            OnOffToggle.UNRECOGNIZED -> {
                throw Error("UNRECOGNIZED")
            }
        }
    }

    private fun getFiveGSwitcher(): FiveGSwitcher {
        return object : FiveGSwitcher {
            override fun is5GEnabled(): Boolean {
                TODO("Not yet implemented")
            }

            override fun set5GEnabled(enable: Boolean) {
                TODO("Not yet implemented")
            }
        }
    }

    interface FiveGSwitcher {
        fun is5GEnabled(): Boolean
        fun set5GEnabled(enable: Boolean)
    }
}