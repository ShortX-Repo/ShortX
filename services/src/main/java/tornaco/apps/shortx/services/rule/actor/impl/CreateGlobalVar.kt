package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.action.CreateGlobalVar
import tornaco.apps.shortx.core.proto.gv.GlobalVar
import tornaco.apps.shortx.core.proto.gv.GlobalVarCreatedBy_Rule
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object CreateGlobalVar : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.CreateGlobalVar::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Unit = with(params) {
        val d = action unpack_ CreateGlobalVar::class.java
        val srcId = params.evaluateContext.srcId
        logger.d("CreateGlobalVar: ${d.globalVar}, srcId: $srcId")
        d.globalVar?.let {
            shortXContext.rule.addGlobalVar(
                GlobalVar.newBuilder(it)
                    .setCreatedBy(
                        GlobalVarCreatedBy_Rule.newBuilder().setSrcId(srcId).build().pack_()
                    )
                    .build()
            )
        }
    }
}