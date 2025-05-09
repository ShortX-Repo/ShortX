package tornaco.apps.shortx.services.rule.actor.impl.js

import android.content.Context
import kotlinx.coroutines.runBlocking
import tornaco.apps.shortx.core.annotations.DoNotStrip
import tornaco.apps.shortx.core.proto.action.WriteGlobalVarOp
import tornaco.apps.shortx.core.proto.rule.EvaluateContext
import tornaco.apps.shortx.core.rule.isList
import tornaco.apps.shortx.core.rule.pack_
import tornaco.apps.shortx.core.rule.valueList
import tornaco.apps.shortx.core.rule.valueString
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.actor.dummyHandlerParamsForAction
import tornaco.apps.shortx.services.rule.actor.impl.WriteGlobalVar
import java.util.UUID

@Suppress("MemberVisibilityCanBePrivate")
@DoNotStrip
class ScriptShortXApi(
    private val shortXContext: ShortXContext,
    private val context: Context,
    private val evaluateContext: EvaluateContext
) {
    // Return String for primitive type.
    // Return List<String> for List type.
    fun readGlobalVar(varName: String): Any {
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

    fun writeGlobalVar(
        varName: String,
        varValue: String?
    ) {
        writeGlobalVarWithOp(varName, varValue, WriteGlobalVarOp.WriteGlobalVarOp_Override.number)
    }

    // Op value: one of below op enum numbers, e.g. 3 for override.
    //    WriteGlobalVarOp_Auto(0),
    //    WriteGlobalVarOp_AppendToLast(1),
    //    WriteGlobalVarOp_AppendToFirst(2),
    //    WriteGlobalVarOp_Override(3),
    //    WriteGlobalVarOp_DeleteValue(4),
    //    WriteGlobalVarOp_DeleteLast(5),
    //    WriteGlobalVarOp_DeleteFirst(6),
    //    WriteGlobalVarOp_Clear(7),
    //    WriteGlobalVarOp_Reverse(8),
    //    WriteGlobalVarOp_Shuffle(9),
    //    WriteGlobalVarOp_Plus1(10),
    //    WriteGlobalVarOp_Minus1(11),
    //    WriteGlobalVarOp_Invert(12),
    //    WriteGlobalVarOp_PlusDelta(13),
    //    WriteGlobalVarOp_MinusDelta(14),
    //    WriteGlobalVarOp_RemoveAtIndex(15),
    fun writeGlobalVarWithOp(
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
}