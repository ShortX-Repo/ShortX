package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.EvaluateContextVar
import tornaco.apps.shortx.core.proto.condition.GlobalVarOp
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.cond.Cond

object EvaluateContextVar : Cond {
    override fun condClass(): Class<out Message> = EvaluateContextVar::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data = condition unpack_ EvaluateContextVar::class.java
            val targetVarName = data.varName
            val currentVarValueOrNull = param.contextData.get(targetVarName)?.toString()

            val cv = RuleContextAndVars(param)
            val payloadValue = data.payload.value.compileContextAndVars(cv)

            val res = evaluate(currentVarValueOrNull, data.op, payloadValue)

            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult(
                "Current value: $currentVarValueOrNull"
            )
        }
    }

    fun evaluate(
        currentVarValueOrNull: String?,
        op: GlobalVarOp,
        payloadValue: String
    ) = when (op) {
        GlobalVarOp.GreaterThan -> {
            val currentVarValueAsDouble = currentVarValueOrNull?.toDoubleOrNull()
            val payloadDouble = payloadValue.toDoubleOrNull()
            currentVarValueAsDouble != null && payloadDouble != null && currentVarValueAsDouble > payloadDouble
        }

        GlobalVarOp.GreaterThanOrEQ -> {
            val currentVarValueAsDouble = currentVarValueOrNull?.toDoubleOrNull()
            val payloadDouble = payloadValue.toDoubleOrNull()
            currentVarValueAsDouble != null && payloadDouble != null && currentVarValueAsDouble >= payloadDouble
        }

        GlobalVarOp.LessThanOrEQ -> {
            val currentVarValueAsDouble = currentVarValueOrNull?.toDoubleOrNull()
            val payloadDouble = payloadValue.toDoubleOrNull()
            currentVarValueAsDouble != null && payloadDouble != null && currentVarValueAsDouble <= payloadDouble
        }

        GlobalVarOp.LessThan -> {
            val currentVarValueAsDouble = currentVarValueOrNull?.toDoubleOrNull()
            val payloadDouble = payloadValue.toDoubleOrNull()
            currentVarValueAsDouble != null && payloadDouble != null && currentVarValueAsDouble < payloadDouble
        }

        GlobalVarOp.EqualTo -> {
            currentVarValueOrNull == payloadValue
        }

        GlobalVarOp.Contains -> {
            currentVarValueOrNull?.contains(payloadValue) ?: false
        }

        GlobalVarOp.DoesNotContain -> {
            currentVarValueOrNull == null || !currentVarValueOrNull.contains(payloadValue)
        }

        GlobalVarOp.NotSet -> {
            currentVarValueOrNull == null
        }

        GlobalVarOp.HasBeenSet -> {
            currentVarValueOrNull != null
        }

        GlobalVarOp.IsEmpty -> {
            currentVarValueOrNull.isNullOrEmpty()
        }

        GlobalVarOp.IsNotEmpty -> {
            !currentVarValueOrNull.isNullOrEmpty()
        }

        GlobalVarOp.IsExists -> {
            !currentVarValueOrNull.isNullOrEmpty()
        }

        GlobalVarOp.IsNotExists -> {
            currentVarValueOrNull.isNullOrEmpty()
        }

        GlobalVarOp.UNRECOGNIZED -> {
            false
        }
    }
}