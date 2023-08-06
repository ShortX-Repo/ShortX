package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.fact.Notification
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
                    requiredPkgList.any { requiredPkg ->
                        val matchedN = allN.firstOrNull { notification ->
                            notification.appsList.any {
                                it == requiredPkg
                            }
                        }

                        if (matchedN != null) {
                            contextData.add(matchedN.contextMap())
                        }
                        matchedN != null
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

    private fun Notification.contextMap() = mapOf(
        ContextDataMapping.AppHasNotification.matchedAppHasNotificationTitle.name to title,
        ContextDataMapping.AppHasNotification.matchedAppHasNotificationContentText.name to contentText,
        ContextDataMapping.AppHasNotification.matchedAppHasNotificationPkgName.name to appsList.first().pkgName,
        ContextDataMapping.AppHasNotification.matchedAppHasNotificationUserId.name to appsList.first().userId,
        ContextDataMapping.AppHasNotification.matchedAppHasNotificationNotificationTag.name to tag,
    )
}