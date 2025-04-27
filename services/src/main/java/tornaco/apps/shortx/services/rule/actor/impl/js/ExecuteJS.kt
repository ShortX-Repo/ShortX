package tornaco.apps.shortx.services.rule.actor.impl.js

import com.faendir.rhino_android.RhinoAndroidHelper
import com.google.protobuf.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import org.mozilla.javascript.Context
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.ScriptableObject
import tornaco.apps.shortx.core.context.ContextData
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.context.ContextMapUtil.inflateContextMap
import tornaco.apps.shortx.core.context.forKey
import tornaco.apps.shortx.core.localvar.LocalVars
import tornaco.apps.shortx.core.proto.common.CoroutineContext
import tornaco.apps.shortx.core.proto.func.FuncParameterInput
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.FUNC_PARAM_REF_PREFIX
import tornaco.apps.shortx.core.rule.GLOBAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.LOCAL_VAR_REF_PREFIX
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.core.util.fallbackOnEmpty
import tornaco.apps.shortx.core.util.newHandlerOfNewThread
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.data.Dirs
import tornaco.apps.shortx.services.rule.Interceptors
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.ScriptActionEvaluator


object ExecuteJS : Actor {
    private val handlerDispatcher: CoroutineDispatcher by lazy { newHandlerOfNewThread("ShortXJS").asCoroutineDispatcher() }

    private val rhinoAndroidHelper by lazy { RhinoAndroidHelper(Dirs.jsCacheDir()) }

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ExecuteJS::class.java
    }

    override suspend fun handle(
        params: HandleParams
    ): Actor.Res {
        require(Interceptors.checkExecuteMVEL(params.shortXContext, params.androidContext))
        val d = params.action unpack_ tornaco.apps.shortx.core.proto.action.ExecuteJS::class.java

        logger.d("Before execute JS: ${Thread.currentThread()}")

        // In-case they want to handle UI.
        val coroutineContext = when (d.context ?: CoroutineContext.CoroutineContext_Default) {
            CoroutineContext.CoroutineContext_Default -> {
                Dispatchers.Default
            }

            CoroutineContext.CoroutineContext_IO -> {
                Dispatchers.IO
            }

            CoroutineContext.CoroutineContext_UI -> {
                handlerDispatcher
            }

            CoroutineContext.UNRECOGNIZED -> {
                Dispatchers.Default
            }
        }
        logger.d("Choose coroutineContext: $coroutineContext")
        val res = withContext(coroutineContext) {
            executeJS(
                evaluateContext = params.evaluateContext,
                androidContext = params.androidContext,
                shortXContext = params.shortXContext,
                contextData = params.contextData,
                localVars = params.localVars,
                parameterInputs = params.parameterInputs,
                rawExpression = d.expression
            )
        }

        params.contextData.add(
            d.customContextDataKey.forKey(ContextDataMapping.ExecuteJS.jsRet.name)
                .fallbackOnEmpty(ContextDataMapping.ExecuteJS.jsRet.name),
            res
        )

        return Actor.Res(res)
    }

    fun executeJS(
        androidContext: android.content.Context,
        shortXContext: ShortXContext,
        contextData: ContextData,
        localVars: LocalVars,
        parameterInputs: List<FuncParameterInput>,
        rawExpression: String,
        evaluateContext: EvaluateContext? = null,
    ): String {
        val rhinoContext = rhinoAndroidHelper.enterContext()
        rhinoContext.optimizationLevel = 1
        rhinoContext.initStandardObjects()

        val scope = ImporterTopLevel(rhinoContext)
        val wrappedAndroidContext = Context.javaToJS(androidContext, scope)
        ScriptableObject.putProperty(scope, "context", wrappedAndroidContext)

        val scriptActionEvaluator = ScriptActionEvaluator(
            shortXContext = shortXContext,
            evaluateContext = evaluateContext ?: EvaluateContext.getDefaultInstance(),
            initialContextData = contextData.map.mapValues { it.value ?: "" },
            localVars = localVars,
            parameterInputs = parameterInputs
        )
        val actionEvaluator = Context.javaToJS(scriptActionEvaluator, scope)
        ScriptableObject.putProperty(scope, "actionEvaluator", actionEvaluator)

        val wrappedConsole = Context.javaToJS(KotlinConsole, scope)
        ScriptableObject.putProperty(scope, "console", wrappedConsole)

        contextData.map.forEach {
            ScriptableObject.putConstProperty(scope, it.key, it.value)
        }
        shortXContext.rule.getAllGlobalVars().forEach {
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
        localVars.all.forEach {
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
        parameterInputs.forEach {
            ScriptableObject.putConstProperty(
                scope,
                FUNC_PARAM_REF_PREFIX + it.name,
                it.value
            )
        }

        val expression = rawExpression.inflateContextMap(contextData.map.mapValues { entry ->
            // Wrap all data with ""
            entry.value?.let {
                "\"$it\""
            }
        }).apply {
            logger.d("inflated expression: $this")
        }

        return runCatching {
            logger.d("In execute JS: ${Thread.currentThread()}")
            val obj = rhinoContext.evaluateString(scope, expression, "<shortx_js>", 1, null)
            Context.toString(obj)
        }.onSuccess {
            logger.d(it)
            Context.exit()
        }.onFailure {
            logger.e(it, "JS")
            Context.exit()
        }.getOrThrow()
    }
}