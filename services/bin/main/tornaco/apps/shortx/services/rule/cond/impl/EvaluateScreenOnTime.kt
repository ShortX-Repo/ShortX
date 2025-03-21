package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.ScreenOnTimeStartFrom
import tornaco.apps.shortx.core.proto.condition.EvaluateScreenOnTime
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.cond.Cond

object EvaluateScreenOnTime : Cond {
    override fun condClass(): Class<out Message> = EvaluateScreenOnTime::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data = condition unpack_ EvaluateScreenOnTime::class.java
            val currentVarValueOrNull =
                if (data.from == ScreenOnTimeStartFrom.ScreenOnTimeStartFrom_LastScreenOff) {
                    shortXContext.user.screenOnTimeFromLastScreenOffInSeconds.get().toString()
                } else {
                    shortXContext.user.screenOnTimeFromSystemReadyInSeconds.get().toString()
                }
            logger.d("currentVarValueOrNull: $currentVarValueOrNull")

            val cv = RuleContextAndVars(param)
            val payloadValue = data.payload.value.compileContextAndVars(cv)

            val res = EvaluateContextVar.evaluate(
                currentVarValueOrNull = currentVarValueOrNull,
                op = data.op,
                payloadValue = payloadValue
            )

            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult(
                "Current value: $currentVarValueOrNull"
            )
        }
    }
}