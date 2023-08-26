package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.proto.action.WriteGlobalVar
import tornaco.apps.shortx.core.proto.action.WriteGlobalVarOp
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.rule.addOrUpdateValue
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.replaceGlobalVarValues
import tornaco.apps.shortx.core.rule.replaceLocalVarValues
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
        val globalVar = requireNotNull(shortXContext.rule.getGlobalVarByName(d.varName)) {
            "Global var: ${d.varName} not exists. pls create first."
        }
        val op: WriteGlobalVarOp = d.op
        logger.d("$op -> ${globalVar.name}")
        val newValue =
            d.valueAsString.inflateContextMap(contextData)
                .replaceLocalVarValues(localVars)
                .replaceGlobalVarValues {
                    shortXContext.rule.getAllGlobalVars()
                }
        when (op) {
            WriteGlobalVarOp.WriteGlobalVarOp_AppendToLast -> {
                globalVar.addOrUpdateValue(listOf(newValue))
            }

            WriteGlobalVarOp.WriteGlobalVarOp_AppendToFirst -> {
                globalVar.addOrUpdateValue(listOf(newValue))
            }

            WriteGlobalVarOp.WriteGlobalVarOp_Override -> {

            }

            WriteGlobalVarOp.WriteGlobalVarOp_DeleteValue -> {

            }

            WriteGlobalVarOp.WriteGlobalVarOp_DeleteLast -> {

            }

            WriteGlobalVarOp.WriteGlobalVarOp_DeleteFirst -> {

            }

            WriteGlobalVarOp.WriteGlobalVarOp_Clear -> {

            }

            WriteGlobalVarOp.WriteGlobalVarOp_Reverse -> {

            }

            WriteGlobalVarOp.WriteGlobalVarOp_Shuffle -> {

            }

            WriteGlobalVarOp.UNRECOGNIZED -> {
                throw UnsupportedOperationException("WriteGlobalVar: UNRECOGNIZED op")
            }
        }
    }

    private fun GlobalVar.throwIfNotList(op: WriteGlobalVarOp) {
        require(isList()) {
            "$op can only be applied to List type."
        }
    }
}