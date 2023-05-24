package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.action.WriteGlobalVar
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.rule.toType
import tornaco.apps.shortx.core.rule.toVarValueNotList
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object WriteGlobalVar : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WriteGlobalVar::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ WriteGlobalVar::class.java
        shortXContext.rule.getGlobalVarByName(d.varName)?.let { globalVar ->
            val type = globalVar.type.toType()
            val newValue =
                d.valueAsString.inflateContextMap(contextData).toVarValueNotList(type)
            logger.d("WriteGlobalVar, newValue: $newValue")
            newValue?.let {
                shortXContext.rule.addGlobalVar(
                    GlobalVar.newBuilder(globalVar).setVar(it).build()
                )
            }
        }
    }
}