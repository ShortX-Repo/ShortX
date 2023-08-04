package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.services.rule.cond.Cond

object AppHasNotification : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.AppHasNotification::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            var reason: String? = null
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.AppHasNotification::class.java

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
                val allN = shortXContext.notification.getActiveNotifications()
                logger.p("allN: ${allN.joinToString(System.lineSeparator())}")

                if (data.op == ConditionOperator.ANY) {
                    requiredPkgList.any {
                        with(shortXContext.notification) { it.hasNotification(allN) }
                    }
                } else {
                    var res = true
                    requiredPkgList.forEach {
                        val hasN = with(shortXContext.notification) {
                            it.hasNotification(allN)
                        }
                        if (!hasN) {
                            res = false
                            reason = "${it.appPkgToString()} has no notification"
                            return@forEach
                        }
                    }
                    res
                }
            }

            Cond.Result(
                if (data.isInvert) {
                    !res
                } else {
                    res
                },
                reason
            )
        }
    }
}