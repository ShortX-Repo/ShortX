package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.proto.condition.CurrentPkgList
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.cond.Cond

object CurrentPkgList : Cond {
    override fun condClass(): Class<out Message> = CurrentPkgList::class.java

    override fun evaluate(param: Cond.CondParam): Boolean {
        return with(param) {
            val currentPkg = condition unpack_ CurrentPkgList::class.java
            val state = shortXContext.appState.currentFgComponentName.value
            val requiredPkgList = if (currentPkg.pkgSetsList.isNotEmpty()) {
                currentPkg.pkgSetsList.flatMap {
                    shortXContext.pkgSet.getPkgSetByLabel(it)?.appPkgsList ?: emptyList()
                }
            } else {
                currentPkg.pkgsList
            }
            requiredPkgList.any {
                it.pkgName == state.pkg.pkgName && it.userId == state.pkg.userId
            }
        }
    }
}