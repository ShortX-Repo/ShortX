package tornaco.apps.shortx.services.rule.actor.impl

import android.content.Context
import com.google.protobuf.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import tornaco.apps.shortx.core.proto.common.StatusBarClipState
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.actor.Actor
import tornaco.apps.shortx.services.rule.actor.HandleParams
import tornaco.apps.shortx.services.rule.actor.impl.util.runSubActions
import tornaco.apps.shortx.services.statusbar.StatusBarChipEvent
import tornaco.apps.shortx.services.watchdog.launchSafety
import java.util.concurrent.atomic.AtomicReference

object ShowStatusBarChip : Actor {
    private val scope by lazy { CoroutineScope(SupervisorJob()) }
    private val actionRef = AtomicReference<HandleParams?>(null)

    override fun actionsCanHandle(): Class<out Message> {
        return tornaco.apps.shortx.core.proto.action.ShowStatusBarChip::class.java
    }

    override fun init(androidContext: Context, shortXContext: ShortXContext) {
        super.init(androidContext, shortXContext)
        scope.launchSafety {
            shortXContext.statusbar.statusBarChipEvent.collectLatest {
                runCatching {
                    logger.d("statusBarChipEvent: $it")
                    val hp = actionRef.get() ?: return@collectLatest
                    val d =
                        hp.action unpack_ tornaco.apps.shortx.core.proto.action.ShowStatusBarChip::class.java

                    when (it) {
                        is StatusBarChipEvent.Click -> {
                            with(hp) {
                                runSubActions(d.clickActionList)
                            }
                        }

                        is StatusBarChipEvent.LongClick -> {
                            with(hp) {
                                runSubActions(d.longClickActionList)
                            }
                        }
                    }
                }.onFailure {
                    logger.e(it, "Handle chip event")
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