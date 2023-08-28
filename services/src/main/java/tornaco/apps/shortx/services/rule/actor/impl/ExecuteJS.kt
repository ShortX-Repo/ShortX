package tornaco.apps.shortx.services.rule.actor.impl

import com.faendir.rhino_android.RhinoAndroidHelper
import com.google.protobuf.Message
import org.mozilla.javascript.ImporterTopLevel
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.action.ExecuteMVEL
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.Interceptors
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ExecuteJS : Actor {
    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ExecuteJS::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        require(Interceptors.checkExecuteMVEL(params.shortXContext, params.androidContext))
        val d = params.action unpack_ ExecuteMVEL::class.java
        val rhinoAndroidHelper = RhinoAndroidHelper(params.androidContext)
        val rhinoContext = rhinoAndroidHelper.enterContext()
        rhinoContext.optimizationLevel = 1
        val scope = ImporterTopLevel(rhinoContext)
        val result = rhinoContext.evaluateString(scope, "1 + 9", "<hello_world>", 1, null).apply {
            params.contextData.add(ContextDataMapping.ExecuteJS.jsRet.name, this.toString())
        }
        logger.d(
            "evaluateString, res: $result" +
                    ""
        )
    }
}