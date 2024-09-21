package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.StatusBarClipState
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.ActionEvaluator
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.statusbar.StatusBarChipEvent
import java.util.concurrent.atomic.AtomicReference

object ShowStatusBarChip : Actor {
    private val scope by lazy { CoroutineScope(SupervisorJob()) }
    private val actionRef = AtomicReference<HandleParams?>(null)

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowStatusBarChip::class.java
    }

    override fun init(androidContext: Context, shortXContext: ShortXContext) {
        super.init(androidContext, shortXContext)
        scope.launch {
            shortXContext.statusbar.statusBarChipEvent.collectLatest {
                logger.d("statusBarChipEvent: $it")
                val hp = actionRef.get() ?: return@collectLatest
                val d =
                    hp.action unpack_ tornaco.apps.shortx.core.proto.action.ShowStatusBarChip::class.java

                when (it) {
                    is StatusBarChipEvent.Click -> {
                        run loop@{
                            if (d.clickActionList.isNullOrEmpty().not()) {
                                d.clickActionList.forEach {
                                    with(hp) {
                                        val evaRes = recursiveExecute(
                                            evaluateContext,
                                            it,
                                            contextData,
                                            localVars,
                                            parameterInputs
                                        )
                                        if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                                            logger.e(evaRes.error, "evaluate fail")
                                            if (evaRes.shouldContinue) {
                                                logger.d("Action on error is continue execute.")
                                            } else {
                                                return@loop
                                            }
                                        } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                                            val scope = evaRes.scope
                                            logger.w("evaluate break! $scope")
                                            scope.rethrowOrReturnLoop {
                                                return@loop
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is StatusBarChipEvent.LongClick -> {
                        run loop@{
                            if (d.longClickActionList.isNullOrEmpty().not()) {
                                d.longClickActionList.forEach {
                                    with(hp) {
                                        val evaRes = recursiveExecute(
                                            evaluateContext,
                                            it,
                                            contextData,
                                            localVars,
                                            parameterInputs
                                        )
                                        if (evaRes is ActionEvaluator.EvaluateResult.Fail) {
                                            logger.e(evaRes.error, "evaluate fail")
                                            if (evaRes.shouldContinue) {
                                                logger.d("Action on error is continue execute.")
                                            } else {
                                                return@loop
                                            }
                                        } else if (evaRes is ActionEvaluator.EvaluateResult.Break) {
                                            val scope = evaRes.scope
                                            logger.w("evaluate break! $scope")
                                            scope.rethrowOrReturnLoop {
                                                return@loop
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun handle(
        params: HandleParams
    ) = with(params) {
        logger.d("Show.")
        withContext(Dispatchers.Main) {
            val d =
                action unpack_ tornaco.apps.shortx.core.proto.action.ShowStatusBarChip::class.java
            val cv = RuleContextAndVars(params)

            shortXContext.statusbar.updateStatusBarClip(
                StatusBarClipState.newBuilder()
                    .setId(evaluateContext.srcId)
                    .setIcon(d.icon.compileContextAndVars(cv))
                    .setText(d.text.compileContextAndVars(cv))
                    .addAllLocalVar(localVars.all)
                    .addAllFuncParameterInputs(parameterInputs)
                    .build()
            )
            actionRef.set(params)
        }
    }
}