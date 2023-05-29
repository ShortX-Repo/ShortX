package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import org.mvel2.MVEL
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.mvel.handle.Handle

class MVELExecutor(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val expression: String
) {
    private val logger = Logger("MVELExecutor")

    operator fun invoke(vars: Map<String, Any?> = emptyMap()): Any? {
        val varsMap: Map<String, Any?> = mutableMapOf<String, Any?>().apply {
            put("context", context)
            putAll(vars)
            Handle.inject(context, shortXContext, this)

            shortXContext.rule.getAllGlobalVars().forEach {
                put(
                    GLOBAL_VAR_REF_PREFIX + it.name,
                    if (it.isList()) {
                        it.valueList()
                    } else {
                        it.valueString()
                    }
                )
            }
        }
        logger.w("Execute: $expression")
        return MVEL.executeExpression(MVEL.compileExpression(expression), varsMap)
    }
}
