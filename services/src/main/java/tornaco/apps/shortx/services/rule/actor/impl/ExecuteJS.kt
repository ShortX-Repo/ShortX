package tornaco.apps.shortx.services.rule.actor.impl

import com.faendir.rhino_android.RhinoAndroidHelper
import com.google.protobuf.Message
import org.mozilla.javascript.Context
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.ScriptableObject
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.LOCAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.Interceptors
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams


object ExecuteJS : Actor {
    private val rhinoAndroidHelper by lazy { RhinoAndroidHelper(Dirs.jsCacheDir()) }

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ExecuteJS::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ) {
        require(Interceptors.checkExecuteMVEL(params.shortXContext, params.androidContext))
        val d = params.action unpack_ tornaco.apps.shortx.core.proto.action.ExecuteJS::class.java
        val rhinoContext = rhinoAndroidHelper.enterContext()
        rhinoContext.optimizationLevel = 1
        rhinoContext.initStandardObjects()

        val scope = ImporterTopLevel(rhinoContext)
        val wrappedAndroidContext = Context.javaToJS(params.androidContext, scope)
        ScriptableObject.putProperty(scope, "context", wrappedAndroidContext)

        params.contextData.map.forEach {
            ScriptableObject.putConstProperty(scope, it.key, it.value)
        }
        params.shortXContext.rule.getAllGlobalVars().forEach {
            ScriptableObject.putConstProperty(
                scope,
                GLOBAL_VAR_REF_PREFIX + it.name,
                if (it.isList()) {
                    it.valueList()
                } else {
                    it.valueString()
                }
            )
        }
        params.localVars.all.forEach {
            ScriptableObject.putConstProperty(
                scope,
                LOCAL_VAR_REF_PREFIX + it.name,
                if (it.isList()) {
                    it.valueList()
                } else {
                    it.valueString()
                }
            )
        }

        val expression = d.expression.inflateContextMap(params.contextData.map.mapValues { entry ->
            // Wrap all data with ""
            entry.value?.let {
                "\"$it\""
            }
        }).apply {
            logger.d("inflated expression: $this")
        }

        runCatching {
            rhinoContext.evaluateString(scope, expression, "<shortx_js>", 1, null)
        }.onSuccess {
            params.contextData.add(ContextDataMapping.ExecuteJS.jsRet.name, Context.toString(it))
            Context.exit()
        }.onFailure {
            logger.e(it, "JS")
            Context.exit()
            throw IllegalStateException(it)
        }
    }
}