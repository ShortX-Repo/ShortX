package tornaco.apps.shortx.services.rule.cond.impl

import com.google.protobuf.Message
import tornaco.apps.shortx.core.os.myUserId
import tornaco.apps.shortx.core.proto.common.AppPkg
import tornaco.apps.shortx.core.proto.common.StringPair
import tornaco.apps.shortx.core.rule.compileContextAndVars
import tornaco.apps.shortx.core.rule.unpack_
import tornaco.apps.shortx.services.rule.RuleContextAndVars
import tornaco.apps.shortx.services.rule.cond.Cond

object CurrentPkgListByPkg : Cond {
    override fun condClass(): Class<out Message> =
        tornaco.apps.shortx.core.proto.condition.CurrentPkgListByPkg::class.java

    override suspend fun evaluate(param: Cond.CondParam): Cond.Result {
        return with(param) {
            val currentPkg =
                condition unpack_ tornaco.apps.shortx.core.proto.condition.CurrentPkgListByPkg::class.java
            val requiredPkgList = currentPkg.pkgAndUsersList.toRealApps(param)
            CurrentPkgList.evaluate(
                requiredPkgList,
                currentPkg.op,
                shortXContext,
                currentPkg.isInvert
            )
        }
    }
}

fun List<StringPair>.toRealApps(params: Cond.CondParam): List<AppPkg> {
    val cv = RuleContextAndVars(params)
    return this.mapNotNull { stringPair ->
        val app = params.shortXContext.appState.pkgToApp(
            AppPkg.newBuilder()
                .setPkgName(
                    stringPair.first.compileContextAndVars(cv)
                )
                .setUserId(stringPair.second.compileContextAndVars(cv).toIntOrNull() ?: myUserId)
                .build()
        )
        app?.pkg
    }
}