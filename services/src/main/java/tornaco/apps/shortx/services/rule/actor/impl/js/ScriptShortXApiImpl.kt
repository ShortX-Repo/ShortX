package tornaco.apps.shortx.services.rule.actor.impl.js

import android.content.Context
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.proto.action.WriteGlobalVarOp
import tornaco.apps.shortx.core.proto.da.DirectAction
import tornaco.apps.shortx.core.proto.rule.ContextSrcEvent
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.proto.rule.Rule
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.script.ScriptShortXApi
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.dummyHandlerParamsForAction
import tornaco.apps.shortx.services.rule.actor.impl.WriteGlobalVar
import tornaco.apps.shortx.services.rule.nextEvaluateContextSerialNumber
import java.util.UUID

@Suppress("MemberVisibilityCanBePrivate")
@DoNotStrip
class ScriptShortXApiImpl(
    private val shortXContext: ShortXContext,
    private val context: Context,
    private val evaluateContext: EvaluateContext
) : ScriptShortXApi {
    override fun readGlobalVar(varName: String): Any {
        val globalVar = requireNotNull(shortXContext.rule.getGlobalVarByName(varName)) {
            "Global var: $varName not exists."
        }
        return globalVar.run {
            if (isList()) {
                valueList()
            } else {
                valueString()
            }
        }
    }

    override fun writeGlobalVar(
        varName: String,
        varValue: String?
    ) {
        writeGlobalVarWithOp(varName, varValue, WriteGlobalVarOp.WriteGlobalVarOp_Override.number)
    }

    override fun writeGlobalVarWithOp(
        varName: String,
        varValue: String?,
        op: Int
    ) {
        runBlocking {
            WriteGlobalVar.handle(
                dummyHandlerParamsForAction(
                    androidContext = context, shortXContext = shortXContext,
                    evaluateContext = evaluateContext,
                    action = tornaco.apps.shortx.core.proto.action.WriteGlobalVar.newBuilder()
                        .setId(UUID.randomUUID().toString())
                        .setNote("From Script.")
                        .setVarName(varName)
                        .setValueAsString(varValue?.toString().orEmpty())
                        .setOp(WriteGlobalVarOp.forNumber(op))
                        .build()
                        .pack_()
                )
            )
        }
    }

    override fun queryRuleIdByTitleAndDescription(
        title: String,
        description: String?
    ): List<String> {
        return shortXContext.rule.getAllRules().filter {
            it.title.contains(title) && (description.isNullOrEmpty() || it.description.contains(
                description
            ))
        }.map {
            it.id
        }
    }

    override fun queryDAIdByTitleAndDescription(title: String, description: String?): List<String> {
        return shortXContext.rule.getAllDirectAction().filter {
            it.title.contains(title) && (description.isNullOrEmpty() || it.description.contains(
                description
            ))
        }.map {
            it.id
        }
    }

    override fun queryRuleIdByTitle(title: String): List<String> {
        return shortXContext.rule.getAllRules().filter {
            it.title.contains(title)
        }.map {
            it.id
        }
    }

    override fun queryDAIdByTitle(title: String): List<String> {
        return shortXContext.rule.getAllDirectAction().filter {
            it.title.contains(title)
        }.map {
            it.id
        }
    }

    override fun queryRuleById(id: String): Rule? {
        return shortXContext.rule.getRuleById(id)
    }

    override fun queryDAById(id: String): DirectAction? {
        return shortXContext.rule.getDirectActionById(id)
    }

    override fun executeDAById(id: String) {
        shortXContext.rule.executeDirectionActionById(
            context = EvaluateContext.newBuilder()
                .setSrcId(id)
                .setSrcTitle("Script(MVEL/JS)")
                .setSrcEvent(ContextSrcEvent.ContextSrc_DirectAction)
                .setSerialNumber(nextEvaluateContextSerialNumber())
                .build(),
            id = id,
            parameterInputs = emptyList()
        )
    }

    override fun enableDisableRuleById(id: String, enable: Boolean) {
        shortXContext.rule.setRuleEnabled(id, enable)
    }
}