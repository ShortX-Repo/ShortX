package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.services.rule.cond.Cond

object AppIsRunning : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.AppIsRunning::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            var reason: String? = null
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.AppIsRunning::class.java

            val requiredPkgList = if (data.pkgSetsList.isNotEmpty()) {
                data.pkgSetsList.flatMap {
                    shortXContext.pkgSet.getPkgSetByLabel(it)?.appPkgsList ?: emptyList()
                }
            } else {
                data.pkgsList
            }

            val res = if (requiredPkgList.isEmpty()) {
                logger.w("requiredPkgList is empty, return false.")
                false
            } else {
                if (data.op == ConditionOperator.ANY) {
                    requiredPkgList.any {
                        with(shortXContext.appState) { it.isRunning() }
                    }
                } else {
                    var res = true
                    requiredPkgList.forEach {
                        val isRunning = with(shortXContext.appState) {
                            it.isRunning()
                        }
                        if (!isRunning) {
                            res = false
                            reason = "${it.appPkgToString()} is NOT running."
                            return@forEach
                        }
                    }
                    res
                }
            }
            if (data.isInvert) {
                !res
            } else {
                res
            }.toResult(reason)
        }
    }
}