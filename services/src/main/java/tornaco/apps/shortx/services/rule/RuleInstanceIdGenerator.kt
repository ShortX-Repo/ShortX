package tornaco.apps.shortx.services.rule

import android.content.Context
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.rule.RuleInstanceIdGenerator_JS
import tornaco.apps.shortx.core.proto.rule.RuleInstanceIdGenerator_MVEL
import tornaco.apps.shortx.core.proto.rule.RuleInstanceIdGenerator_Static
import tornaco.apps.shortx.core.proto.rule.RuleInstanceIdGenerator_String
import tornaco.apps.shortx.core.rule.ProtoAny
import tornaco.apps.shortx.core.rule.RuleContextAndVars
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.is_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.Logger
import tornaco.apps.shortx.core.logOnDebug
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.impl.ExecuteJS
import tornaco.apps.shortx.services.rule.actor.impl.MVELExecutor
import tornaco.apps.shortx.services.rule.cond.impl.MatchJS

object RuleInstanceIdGenerator {
    private val logger = Logger("RuleInstanceIdGenerator")

    fun ProtoAny.generate(
        context: Context,
        shortXContext: ShortXContext,
        contextData: ContextData,
    ): String? {
        // RuleInstanceIdGenerator
        logger.d("generate: contextData= ${contextData.map}")

        if (this is_ RuleInstanceIdGenerator_Static::class.java) {
            val d = this unpack_ RuleInstanceIdGenerator_Static::class.java
            return d.insId
        }

        if (this is_ RuleInstanceIdGenerator_String::class.java) {
            val d = this unpack_ RuleInstanceIdGenerator_String::class.java
            val contextAndVars =
                RuleContextAndVars(
                    localVars = LocalVars.obtain(),
                    contextData = contextData
                ) { shortXContext.rule.getAllGlobalVars() }
            return d.insId.compileContextAndVars(contextAndVars).apply {
                logger.d("generate: RuleInstanceIdGenerator_String: $this")
            }
        }

        if (this is_ RuleInstanceIdGenerator_MVEL::class.java) {
            val d = this unpack_ RuleInstanceIdGenerator_MVEL::class.java
            val res = MVELExecutor(
                context,
                shortXContext,
                d.expression.inflateContextMap(contextData.map.mapValues { entry ->
                    // Wrap all data with ""
                    entry.value?.let {
                        if (it is String) "\"$it\"" else it
                    }
                }).apply {
                    logger.d("RuleInstanceIdGenerator_MVEL, inflated expression: $this")
                }).invoke(vars = contextData.map, localVars = LocalVars.obtain())
            return res.toString()
        }

        if (this is_ RuleInstanceIdGenerator_JS::class.java) {
            val d = this unpack_ RuleInstanceIdGenerator_JS::class.java
            val resString =
                ExecuteJS.executeJS(
                    context,
                    shortXContext,
                    contextData,
                    LocalVars.obtain(),
                    d.expression
                )
            MatchJS.logger.d("RuleInstanceIdGenerator_JS, resString: $resString")
            return resString
        }

        return null
    }
}