package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object ServiceIsRunning : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.ServiceIsRunning::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.ServiceIsRunning::class.java

            val services = data.servicesList
            logger.d("Checking services: ${data.op} $services")

            val res = if (services.isEmpty()) {
                logger.w("servicesList is empty, return false.")
                false
            } else {
                if (data.op == ConditionOperator.ANY) {
                    services.any {
                        with(shortXContext.appState) { it.isServiceRunning() }
                    }
                } else {
                    services.all {
                        with(shortXContext.appState) { it.isServiceRunning() }
                    }
                }
            }
            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult()
        }
    }
}