package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.CurrentPkgList
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.services.rule.cond.Cond

object CurrentPkgList : Cond {
    override fun condClass(): Class<out Message> = CurrentPkgList::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            var reason: String? = null
            val currentPkg = condition unpack_ CurrentPkgList::class.java
            val requiredPkgList = if (currentPkg.pkgSetsList.isNotEmpty()) {
                currentPkg.pkgSetsList.flatMap {
                    shortXContext.pkgSet.getPkgSetByLabel(it)?.appPkgsList ?: emptyList()
                }
            } else {
                currentPkg.pkgsList
            }

            val res = if (requiredPkgList.isEmpty()) {
                logger.w("requiredPkgList is empty, return false.")
                false
            } else {
                if (currentPkg.op == ConditionOperator.ALL) {
                    var res = true
                    requiredPkgList.forEach {
                        val isF = with(shortXContext.appState) {
                            it.isFront()
                        }
                        if (!isF) {
                            res = false
                            reason = "${it.appPkgToString()} is NOT front."
                            return@forEach
                        }
                    }
                    res
                } else {
                    requiredPkgList.any {
                        with(shortXContext.appState) {
                            it.isFront()
                        }
                    }
                }
            }
            if (currentPkg.isInvert) {
                !res
            } else {
                res
            }.toResult(reason)
        }
    }
}