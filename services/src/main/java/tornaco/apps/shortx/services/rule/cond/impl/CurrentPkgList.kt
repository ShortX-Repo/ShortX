package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.proto.condition.CurrentPkgList
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.services.ShortXContext
import tornaco.apps.shortx.services.rule.cond.Cond

object CurrentPkgList : Cond {
    override fun condClass(): Class<out Message> = CurrentPkgList::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val currentPkg = condition unpack_ CurrentPkgList::class.java
            val requiredPkgList = if (currentPkg.pkgSetsList.isNotEmpty()) {
                currentPkg.pkgSetsList.flatMap {
                    shortXContext.pkgSet.getPkgSetByLabel(it)?.appPkgsList ?: emptyList()
                }
            } else {
                currentPkg.pkgsList
            }
            evaluate(requiredPkgList, currentPkg.op, param.shortXContext, currentPkg.isInvert)
        }
    }

    internal fun evaluate(
        requiredPkgList: List<AppPkg>,
        op: ConditionOperator,
        shortXContext: ShortXContext,
        isInvert: Boolean
    ): Cond.Result {
        var reason: String? = null

        val res = if (requiredPkgList.isEmpty()) {
            logger.w("requiredPkgList is empty, return false.")
            false
        } else {
            if (op == ConditionOperator.ALL) {
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
        return if (isInvert) {
            !res
        } else {
            res
        }.toResult(reason)
    }
}