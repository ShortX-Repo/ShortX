package tornaco.apps.shortx.services.rule.actor.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.gv.LocalVar
import tornaco.apps.shortx.core.proto.gv.StringVar
import tornaco.apps.shortx.core.rule.addOrUpdateValue
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object WriteLocalVar : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.WriteLocalVar::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        val d = action unpack_ tornaco.apps.shortx.core.proto.action.WriteLocalVar::class.java
        val existingLocalVar = localVars.getByName(d.varName)
        val localVar = existingLocalVar ?: require(d.autoCreateIfMissing) {
            "Local var: ${d.varName} not exists. pls create first.\nCurrent local vars: ${localVars.all.joinToString { it.name }}"
        }.let {
            LocalVar.newBuilder()
                .setName(d.varName)
                .setType(StringVar.getDefaultInstance().pack_())
                .build()
        }
        val cv = RuleContextAndVars(params)
        val newValue =
            d.valueAsString.compileContextAndVars(cv)
        val updatedLocalVar = localVar.addOrUpdateValue(listOf(newValue))
        if (existingLocalVar == null) {
            localVars.add(updatedLocalVar)
        } else {
            localVars.update(updatedLocalVar)
        }
        Actor.Res.EMPTY
    }
}