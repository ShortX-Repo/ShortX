package tornaco.apps.shortx.services.rule.actor.mvel.handle.impl

import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.mvel.handle.IInput

class InputImpl(private val shortXContext: ShortXContext) : IInput {
    override fun injectKey(code: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLastKey(): Int {
        return shortXContext.input.lastInputKey.first
    }

    override fun getLastKeyTime(): Long {
        return shortXContext.input.lastInputKey.second
    }
}