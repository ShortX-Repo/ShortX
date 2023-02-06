package tornaco.apps.shortx.services.rule.actor

import android.content.Context
import org.mvel2.MVEL
import org.mvel2.integration.VariableResolverFactory
import org.mvel2.integration.impl.MapVariableResolverFactory
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.mvel.handle.Handle

class MVELExecutor(
    private val context: Context,
    private val shortXContext: ShortXContext,
    private val expression: String
) {
    private val logger = Logger("MVELExecutor")

    operator fun invoke(vars: Map<String, Any> = emptyMap()): Any {
        val varsMap: Map<String, Any> = mutableMapOf<String, Any>().apply {
            put("context", context)
            putAll(vars)
            Handle.inject(context, shortXContext, this)
        }
        val factory: VariableResolverFactory = MapVariableResolverFactory(varsMap)
        logger.w("Execute: $expression")
        return MVEL.eval(expression, factory)
    }
}