package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.context.ContextDataMapping
import tornaco.apps.shortx.core.proto.condition.ConditionOperator
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.core.util.appPkgToString
import tornaco.apps.shortx.services.rule.cond.Cond

object AppHasAudioFocus : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.AppHasAudioFocus::class.java

    override suspend fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val data =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.AppHasAudioFocus::class.java

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
                        with(shortXContext.audio) { it.hasFocus() }
                    }
                } else {
                    var res = true
                    requiredPkgList.forEach {
                        val hasFocus = with(shortXContext.audio) { it.hasFocus() }
                        if (!hasFocus) {
                            res = false
                            contextData.add(
                                ContextDataMapping.Condition.conditionMatchReason.name,
                                "${it.appPkgToString()} has NO focus"
                            )
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
            }
        }
    }
}