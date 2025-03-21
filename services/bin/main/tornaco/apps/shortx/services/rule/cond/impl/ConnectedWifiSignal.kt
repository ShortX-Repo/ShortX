package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.IntOp
import tornaco.apps.shortx.core.proto.condition.ConnectedWifiSignal
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object ConnectedWifiSignal : Cond {
    override fun condClass(): Class<out Message> = ConnectedWifiSignal::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val currentLevel = shortXContext.network.wifiSignalState.level

            val requiredSignal = condition unpack_ ConnectedWifiSignal::class.java
            val res = matchIntOp(
                op = requiredSignal.op ?: IntOp.UNRECOGNIZED,
                currentValue = currentLevel,
                expectedValue = requiredSignal.level
            )

            if (requiredSignal.isInvert) {
                !res
            } else {
                res
            }.toResult("Current value: $currentLevel")
        }
    }
}

fun matchIntOp(op: IntOp, currentValue: Int, expectedValue: Int): Boolean {
    return when (op) {
        IntOp.IntGreaterThan -> {
            currentValue > expectedValue
        }

        IntOp.IntGreaterThanOrEQ -> {
            currentValue >= expectedValue
        }

        IntOp.IntLessThan -> {
            currentValue < expectedValue
        }

        IntOp.IntLessThanOrEQ -> {
            currentValue <= expectedValue
        }

        IntOp.IntEqualTo -> {
            currentValue == expectedValue
        }

        IntOp.UNRECOGNIZED -> {
            false
        }
    }
}